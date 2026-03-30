import { existsSync, statSync, readdirSync, unlinkSync } from "fs";
import { join } from "path";
import { env } from "../env.js";
import { forceRotation, getCurrentFileInfo } from "./log-rotation.js";

export interface CleanupResult {
  filesDeleted: number;
  emptyFilesDeleted: number;
  largeFiles: number;
  bytesFreed: number;
  totalSizeBytes: number;
  sizeExceeded: boolean;
  errors: number;
  operationsRotated: boolean;
  operationsRotationMessage: string;
}

const CURRENT_LOG_FILES = new Set(["simple.log", "operations.md", "application.log", "errors.log"]);

export function performCleanup(): CleanupResult {
  const result: CleanupResult = {
    filesDeleted: 0,
    emptyFilesDeleted: 0,
    largeFiles: 0,
    bytesFreed: 0,
    totalSizeBytes: 0,
    sizeExceeded: false,
    errors: 0,
    operationsRotated: false,
    operationsRotationMessage: "",
  };

  const logsDir = env.LOG_PATH;
  if (!existsSync(logsDir)) return result;

  try {
    const fileInfo = getCurrentFileInfo("operations.md");
    if (fileInfo.needsRotation) {
      const rotationResult = forceRotation("operations.md");
      if (rotationResult.success) {
        result.operationsRotated = true;
        result.operationsRotationMessage = rotationResult.message;
      } else {
        result.errors++;
      }
    }

    const cutoff = Date.now() - env.LOG_CLEANUP_MAX_AGE_DAYS * 24 * 60 * 60 * 1000;
    const files = readdirSync(logsDir);

    for (const file of files) {
      const filePath = join(logsDir, file);
      try {
        const stats = statSync(filePath);
        if (!stats.isFile()) continue;

        result.totalSizeBytes += stats.size;

        if (stats.mtimeMs < cutoff && !CURRENT_LOG_FILES.has(file)) {
          unlinkSync(filePath);
          result.filesDeleted++;
          result.bytesFreed += stats.size;
          continue;
        }

        if (stats.size === 0 && !CURRENT_LOG_FILES.has(file)) {
          unlinkSync(filePath);
          result.emptyFilesDeleted++;
          continue;
        }

        if (stats.size > 10 * 1024 * 1024 && !CURRENT_LOG_FILES.has(file)) {
          result.largeFiles++;
        }
      } catch {
        result.errors++;
      }
    }

    const maxSizeBytes = env.LOG_CLEANUP_MAX_SIZE_MB * 1024 * 1024;
    if (result.totalSizeBytes > maxSizeBytes) {
      result.sizeExceeded = true;
    }
  } catch {
    result.errors++;
  }

  return result;
}

import { existsSync, statSync, renameSync, writeFileSync } from "fs";
import { join } from "path";
import { env } from "../env.js";

const LOG_FILES = ["simple.log", "operations.md", "application.log", "errors.log"];

export interface FileInfo {
  exists: boolean;
  sizeMb: number;
  sizeBytes: number;
  needsRotation: boolean;
  lastModified: Date | null;
}

export interface RotationResult {
  success: boolean;
  message: string;
  originalSize?: number;
  archivedAs?: string;
}

export function getCurrentFileInfo(filename = "operations.md"): FileInfo {
  const filePath = join(env.LOG_PATH, filename);
  if (!existsSync(filePath)) {
    return { exists: false, sizeMb: 0, sizeBytes: 0, needsRotation: false, lastModified: null };
  }
  const stats = statSync(filePath);
  const sizeMb = stats.size / (1024 * 1024);
  return {
    exists: true,
    sizeMb,
    sizeBytes: stats.size,
    needsRotation: sizeMb > env.LOG_ROTATION_MAX_SIZE_MB,
    lastModified: stats.mtime,
  };
}

export function forceRotation(filename = "operations.md"): RotationResult {
  const filePath = join(env.LOG_PATH, filename);
  if (!existsSync(filePath)) {
    return { success: false, message: `Arquivo ${filename} não encontrado` };
  }
  try {
    const stats = statSync(filePath);
    const date = new Date().toISOString().replace(/[:.]/g, "-").substring(0, 19);
    const ext = filename.includes(".") ? filename.substring(filename.lastIndexOf(".")) : "";
    const base = filename.includes(".") ? filename.substring(0, filename.lastIndexOf(".")) : filename;
    const archivedName = `${base}.${date}${ext}`;
    const archivedPath = join(env.LOG_PATH, archivedName);

    renameSync(filePath, archivedPath);
    writeFileSync(filePath, "");

    return {
      success: true,
      message: `Rotação concluída: ${filename} -> ${archivedName}`,
      originalSize: stats.size,
      archivedAs: archivedName,
    };
  } catch (e) {
    return { success: false, message: `Erro na rotação: ${e instanceof Error ? e.message : String(e)}` };
  }
}

export function forceAllLogsRotation(): Record<string, RotationResult> {
  const results: Record<string, RotationResult> = {};
  for (const file of LOG_FILES) {
    const info = getCurrentFileInfo(file);
    if (info.exists && info.sizeBytes > 0) {
      results[file] = forceRotation(file);
    } else {
      results[file] = { success: true, message: "Arquivo vazio ou inexistente, rotação não necessária" };
    }
  }
  return results;
}

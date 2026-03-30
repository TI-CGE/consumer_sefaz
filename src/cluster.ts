import { spawn } from "bun";

const cpuCount = navigator.hardwareConcurrency || 1;
const requestedWorkers = Number(process.env.CLUSTER_WORKERS || "0");
const workerCount = requestedWorkers > 0 ? requestedWorkers : cpuCount;

if (workerCount <= 1) {
  console.log("[cluster] CLUSTER_WORKERS<=1, iniciando processo único");
  await import("./index");
  process.exit(0);
}

const baseEnv = {
  ...process.env,
  CLUSTER_ENABLED: "true",
};

const children = Array.from({ length: workerCount }, (_, index) =>
  spawn({
    cmd: ["bun", "run", "src/index.ts"],
    env: {
      ...baseEnv,
      CLUSTER_ROLE: index === 0 ? "leader" : "worker",
    },
    stdout: "inherit",
    stderr: "inherit",
    stdin: "inherit",
  })
);

console.log(`[cluster] iniciados ${workerCount} processos (${workerCount - 1} workers + 1 leader)`);

function shutdownAll() {
  for (const child of children) {
    try {
      child.kill();
    } catch {
      // noop
    }
  }
}

process.on("SIGINT", shutdownAll);
process.on("SIGTERM", shutdownAll);
process.on("exit", shutdownAll);

await Promise.all(children.map((child) => child.exited));

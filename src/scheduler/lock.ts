let receitaRunning = false;

export function tryAcquireReceitaLock(): boolean {
  if (receitaRunning) return false;
  receitaRunning = true;
  return true;
}

export function releaseReceitaLock() {
  receitaRunning = false;
}

export function isReceitaRunning(): boolean {
  return receitaRunning;
}

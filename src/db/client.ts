import pg from "pg";
import { env } from "../env.js";

const { Pool } = pg;

export const pool = new Pool({
  host: env.DB_HOST,
  port: env.DB_PORT,
  database: env.DB_NAME,
  user: env.DB_USER,
  password: env.DB_PASSWORD,
  max: env.DB_POOL_MAX,
  // Conexão deve abrir sob demanda; evita manter sessão inativa por longos períodos.
  min: 0,
  connectionTimeoutMillis: env.DB_CONNECTION_TIMEOUT,
  idleTimeoutMillis: env.DB_IDLE_TIMEOUT,
});

pool.on("error", (err: any) => {
  // O servidor pode encerrar conexões ociosas do pool (57P05). Não é falha funcional do consumo.
  if (err?.code === "57P05" || String(err?.message ?? "").includes("idle-session timeout")) {
    console.warn("Conexão ociosa do pool encerrada pelo PostgreSQL (idle-session timeout).");
    return;
  }
  console.error("Unexpected error on idle database client", err);
});

export async function query(text: string, params?: unknown[]) {
  const start = Date.now();
  const result = await pool.query(text, params);
  const duration = Date.now() - start;
  if (duration > 3000) {
    console.warn(`Slow query (${duration}ms): ${text.substring(0, 100)}...`);
  }
  return result;
}

export async function testConnection(): Promise<boolean> {
  try {
    const client = await pool.connect();
    await client.query("SELECT 1");
    client.release();
    return true;
  } catch {
    return false;
  }
}

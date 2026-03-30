import { z } from "zod";

const envSchema = z.object({
  APP_NAME: z.string().default("SEFAZ Transparency Consumer"),
  NODE_ENV: z.enum(["development", "production", "docker"]).default("development"),
  PORT: z.coerce.number().default(8083),
  CONTEXT_PATH: z.string().default("/"),

  DB_HOST: z.string().default("172.28.65.26"),
  DB_PORT: z.coerce.number().default(5432),
  DB_NAME: z.string().default("setc@bd"),
  DB_USER: z.string().default("gideon"),
  DB_PASSWORD: z.string().default("setc@2025"),
  DB_POOL_MAX: z.coerce.number().default(10),
  DB_POOL_MIN: z.coerce.number().default(0),
  DB_CONNECTION_TIMEOUT: z.coerce.number().default(60000),
  DB_IDLE_TIMEOUT: z.coerce.number().default(300000),

  SEFAZ_TOKEN_URL: z.string().default("https://sso.apps.sefaz.se.gov.br/auth/realms/externo/protocol/openid-connect/token"),
  SEFAZ_CLIENT_ID: z.string().default("87f72053"),
  SEFAZ_CLIENT_SECRET: z.string().default("6d1009a431e1f3b50c4141ca0c5e267b"),

  LOG_PATH: z.string().default("./logs"),
  LOG_ROTATION_MAX_SIZE_MB: z.coerce.number().default(3),
  LOG_CLEANUP_ENABLED: z.preprocess((v) => v === "true" || v === true, z.boolean()).default(true),
  LOG_CLEANUP_MAX_AGE_DAYS: z.coerce.number().default(7),
  LOG_CLEANUP_MAX_SIZE_MB: z.coerce.number().default(500),
  LOG_LEVEL: z.string().default("info"),

  CLUSTER_ENABLED: z.preprocess((v) => v === "true" || v === true, z.boolean()).default(false),
  CLUSTER_WORKERS: z.coerce.number().int().min(0).default(0),
  CLUSTER_ROLE: z.enum(["single", "leader", "worker"]).default("single"),

  SCHEDULER_ENABLED: z.preprocess((v) => v === "true" || v === true, z.boolean()).default(true),
  SCHEDULER_STARTUP_DELAY_SECONDS: z.coerce.number().default(10),
  SCHEDULER_TEST_ON_STARTUP: z.preprocess((v) => v === "true" || v === true, z.boolean()).default(false),
  SCHEDULER_PRODUCTION_ENABLED: z.preprocess((v) => v === "true" || v === true, z.boolean()).default(true),
});

export type Env = z.infer<typeof envSchema>;

function loadEnv(): Env {
  const result = envSchema.safeParse(process.env);
  if (!result.success) {
    console.error("Invalid environment variables:", result.error.flatten().fieldErrors);
    process.exit(1);
  }
  return result.data;
}

export const env = loadEnv();

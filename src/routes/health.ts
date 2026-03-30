import { Hono } from "hono";
import { testConnection } from "../db/client.js";
import { getToken } from "../auth/token-service.js";

export const healthRoutes = new Hono();

healthRoutes.get("/", async (c) => {
  try {
    const dbOk = await testConnection();
    return c.json({
      status: "UP",
      timestamp: new Date().toISOString(),
      database: dbOk ? "OK" : "UNREACHABLE",
      tokenService: "OK",
    });
  } catch (e) {
    return c.json({ status: "DOWN", error: e instanceof Error ? e.message : String(e), timestamp: new Date().toISOString() }, 500);
  }
});

healthRoutes.get("/token-test", async (c) => {
  try {
    const token = await getToken();
    return c.json({
      status: "SUCCESS",
      tokenLength: token.length,
      tokenPrefix: token.substring(0, 10) + "...",
      timestamp: new Date().toISOString(),
    });
  } catch (e) {
    return c.json({ status: "ERROR", error: e instanceof Error ? e.message : String(e), timestamp: new Date().toISOString() }, 500);
  }
});

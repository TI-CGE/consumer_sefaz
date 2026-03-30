import { Hono } from "hono";
import { getToken, getTokenStatus, testConnectivity, forceTokenRenewal } from "../auth/token-service.js";

export const tokenRoutes = new Hono();

tokenRoutes.get("/status", async (c) => {
  return c.json({ status: getTokenStatus(), timestamp: new Date().toISOString() });
});

tokenRoutes.get("/connectivity", async (c) => {
  const connected = await testConnectivity();
  return c.json({ connected, timestamp: new Date().toISOString() });
});

tokenRoutes.post("/renew", async (c) => {
  try {
    const token = await forceTokenRenewal();
    return c.json({
      status: "SUCCESS",
      tokenPreview: token.substring(0, 10) + "...",
      timestamp: new Date().toISOString(),
    });
  } catch (e) {
    return c.json({ status: "ERROR", error: e instanceof Error ? e.message : String(e) }, 500);
  }
});

tokenRoutes.get("/get", async (c) => {
  try {
    const token = await getToken();
    return c.json({
      status: "SUCCESS",
      tokenPreview: token.substring(0, 10) + "...",
      timestamp: new Date().toISOString(),
    });
  } catch (e) {
    return c.json({ status: "ERROR", error: e instanceof Error ? e.message : String(e) }, 500);
  }
});

tokenRoutes.get("/diagnostic", async (c) => {
  const connected = await testConnectivity();
  const status = getTokenStatus();
  let tokenTest: { success: boolean; tokenLength?: number; error?: string };
  try {
    const token = await getToken();
    tokenTest = { success: true, tokenLength: token.length };
  } catch (e) {
    tokenTest = { success: false, error: e instanceof Error ? e.message : String(e) };
  }
  return c.json({ connectivity: connected, tokenStatus: status, tokenTest, timestamp: new Date().toISOString() });
});

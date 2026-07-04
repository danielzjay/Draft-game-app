/**
 * Firebase Cloud Function proxy for the Relworx Payments API.
 *
 * NOTE: the app's PaymentApiService.kt currently calls a PHP backend (see /server/relworx.php)
 * instead of this file — this Cloud Function is left here as an alternative if you'd rather
 * host on Firebase than on PHP/shared hosting. Only one of the two should actually be wired up;
 * whichever you use, PAYMENT_BACKEND_BASE_URL in your .env must point at it.
 *
 * WHY THIS EXISTS:
 * Relworx auth is one secret Bearer API key for your whole merchant account — it is NOT safe
 * to put inside an Android app, because anyone can decompile the APK and extract it, then use
 * it to move money through YOUR account. This function is the only place the key should live.
 * The app calls this function; this function calls Relworx.
 *
 * SETUP:
 *   1. npm install -g firebase-tools   (if you don't have it)
 *   2. cd into this /server folder, run: firebase init functions   (choose your existing
 *      "draughts-combat1" project, JavaScript, no ESLint if you want it simpler)
 *   3. Move this file's contents into the generated functions/index.js
 *   4. Store your real Relworx key as a secret (never hardcode it):
 *        firebase functions:secrets:set RELWORX_API_KEY
 *      (paste your key when prompted)
 *   5. Set RELWORX_ACCOUNT_NO below to your real business account number, or also store it as
 *      a secret if you'd rather not hardcode it.
 *   6. Deploy: firebase deploy --only functions
 *   7. Copy the deployed URL (looks like
 *      https://us-central1-draughts-combat1.cloudfunctions.net/relworxProxy) into your app's
 *      .env as PAYMENT_BACKEND_BASE_URL — include a trailing slash.
 */

const { onRequest } = require("firebase-functions/v2/https");
const { defineSecret } = require("firebase-functions/params");
const logger = require("firebase-functions/logger");

const RELWORX_API_KEY = defineSecret("RELWORX_API_KEY");
const RELWORX_BASE_URL = "https://payments.relworx.com/api";

async function callRelworx(path, method, apiKey, body, query) {
  let url = `${RELWORX_BASE_URL}/${path}`;
  if (query) {
    url += "?" + new URLSearchParams(query).toString();
  }
  const response = await fetch(url, {
    method,
    headers: {
      "Content-Type": "application/json",
      "Accept": "application/vnd.relworx.v2",
      "Authorization": `Bearer ${apiKey}`,
    },
    body: body ? JSON.stringify(body) : undefined,
  });
  const data = await response.json();
  return { status: response.status, data };
}

exports.relworxProxy = onRequest(
  { secrets: [RELWORX_API_KEY], cors: false },
  async (req, res) => {
    // TODO before going live: verify a Firebase Auth ID token here (req.headers.authorization)
    // so random strangers can't hit your proxy and spam Relworx through your account.
    const apiKey = RELWORX_API_KEY.value();
    const path = req.path.replace(/^\//, ""); // e.g. "mobile-money/validate"

    try {
      let result;
      if (path === "mobile-money/validate" && req.method === "POST") {
        result = await callRelworx("mobile-money/validate", "POST", apiKey, req.body);
      } else if (path === "mobile-money/request-payment" && req.method === "POST") {
        result = await callRelworx("mobile-money/request-payment", "POST", apiKey, req.body);
      } else if (path === "mobile-money/send-payment" && req.method === "POST") {
        result = await callRelworx("mobile-money/send-payment", "POST", apiKey, req.body);
      } else if (path === "mobile-money/check-request-status" && req.method === "GET") {
        result = await callRelworx(
          "mobile-money/check-request-status",
          "GET",
          apiKey,
          null,
          { internal_reference: req.query.internal_reference, account_no: req.query.account_no }
        );
      } else {
        res.status(404).json({ success: false, message: "Unknown payment route" });
        return;
      }
      res.status(result.status).json(result.data);
    } catch (err) {
      logger.error("Relworx proxy error", err);
      res.status(502).json({ success: false, message: "Payment gateway unreachable" });
    }
  }
);

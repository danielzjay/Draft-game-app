<?php
header("Content-Type: application/json");

// CONFIG
// TODO: move these two values out of source code entirely once this is on the server —
// use real environment variables (e.g. via your host's control panel or a .env loader) instead
// of hardcoding them here. Anyone who can read this file can drain your Relworx account.
$RELWORX_API_KEY = "57a27b9c98ddab.sTChnINUfFRQNGWB545_2Q";
$RELWORX_ACCOUNT_NO = "REL77D1451BB8";
$RELWORX_BASE_URL = "https://payments.relworx.com/api";

$action = $_GET['action'] ?? '';

$allowed = [
    "validate" => [
        "method" => "POST",
        "endpoint" => "mobile-money/validate"
    ],
    "request-payment" => [
        "method" => "POST",
        "endpoint" => "mobile-money/request-payment"
    ],
    "send-payment" => [
        "method" => "POST",
        "endpoint" => "mobile-money/send-payment"
    ],
    "check-status" => [
        "method" => "GET",
        "endpoint" => "mobile-money/check-request-status"
    ]
];

if (!isset($allowed[$action])) {
    http_response_code(404);
    echo json_encode([
        "success" => false,
        "message" => "Unknown payment route"
    ]);
    exit;
}

$route = $allowed[$action];
$url = $RELWORX_BASE_URL . "/" . $route["endpoint"];

/**
 * Optional GET query params
 */
if ($route["method"] === "GET") {
    $query = http_build_query([
        "internal_reference" => $_GET["internal_reference"] ?? "",
        "account_no" => $_GET["account_no"] ?? $RELWORX_ACCOUNT_NO
    ]);

    $url .= "?" . $query;
}

/**
 * CORRECT AUTH: Relworx uses Bearer token authentication (see
 * https://payments.relworx.com/docs/authentication/) — it does NOT recognize custom
 * X-API-KEY / X-ACCOUNT-NO headers. That mismatch is exactly what was causing every request
 * to come back 401 Unauthorized: the API was rejecting the request before it even looked at
 * the body, since it never received a token in a header format it understands.
 */
$headers = [
    "Authorization: Bearer $RELWORX_API_KEY",
    "Accept: application/vnd.relworx.v2",
    "Content-Type: application/json"
];

$ch = curl_init($url);

curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

/**
 * POST BODY
 */
if ($route["method"] === "POST") {

    $body = file_get_contents("php://input");
    $decoded = json_decode($body, true);

    // account_no belongs in the body for request-payment/send-payment, per Relworx's docs —
    // but NOT for validate, which only takes {"msisdn": "..."}. Only inject it where it's
    // actually expected, so validate's payload matches the docs exactly.
    if ($action !== "validate" && is_array($decoded) && !isset($decoded["account_no"])) {
        $decoded["account_no"] = $RELWORX_ACCOUNT_NO;
        $body = json_encode($decoded);
    }

    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, $body);
}

$response = curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);

if (curl_errno($ch)) {
    http_response_code(502);
    echo json_encode([
        "success" => false,
        "message" => curl_error($ch)
    ]);
    exit;
}

curl_close($ch);

http_response_code($httpCode);
echo $response;
?>

<?php
/**
 * PHP proxy for the Relworx Payments API — matches the endpoints the Android app now calls
 * (relworx.php?action=validate / request-payment / send-payment / check-status).
 *
 * WHY THIS EXISTS: Relworx auth is one secret Bearer API key for your whole merchant account.
 * It must NEVER be embedded in the Android app — anyone can decompile an APK and pull it out,
 * then move money through YOUR account. This script is the only place the key should live;
 * upload it to your own web hosting (the same domain you set as PAYMENT_BACKEND_BASE_URL in
 * .env), and the app calls THIS script instead of Relworx directly.
 *
 * SETUP:
 *   1. Upload this file to your host, e.g. https://yourdomain.com/api/relworx.php
 *   2. Set RELWORX_API_KEY as a real environment variable on your host (cPanel -> Setup Python/
 *      Node App or .htaccess / php.ini `env[RELWORX_API_KEY]`, or your host's env panel).
 *      Do NOT hardcode it in this file, and do NOT commit a version of this file that has it
 *      filled in to git.
 *   3. Set RELWORX_ACCOUNT_NO the same way, or hardcode it below if you don't mind it being
 *      semi-public (it's just an account identifier, not a secret — unlike the API key).
 *   4. In your app's .env: PAYMENT_BACKEND_BASE_URL=https://yourdomain.com/api/
 *      (must end in a trailing slash, pointing at the FOLDER containing this file).
 *   5. Before going live: add real auth here (e.g. verify a Firebase Auth ID token passed in
 *      an Authorization header) so random strangers can't hit this script and spend through
 *      your Relworx account. Left as a TODO below — this script is not safe to expose publicly
 *      as-is beyond a development/testing phase.
 */

header('Content-Type: application/json');

$RELWORX_API_KEY = getenv('RELWORX_API_KEY');
$RELWORX_BASE_URL = 'https://payments.relworx.com/api';

if (!$RELWORX_API_KEY) {
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'Server is missing RELWORX_API_KEY.']);
    exit;
}

// TODO before going live: verify the caller is really your signed-in app user, e.g.:
// $authHeader = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
// if (!verifyFirebaseIdToken($authHeader)) { http_response_code(401); exit; }

$action = $_GET['action'] ?? '';
$method = $_SERVER['REQUEST_METHOD'];

function callRelworx($path, $method, $apiKey, $baseUrl, $body = null, $query = null) {
    $url = $baseUrl . '/' . $path;
    if ($query) {
        $url .= '?' . http_build_query($query);
    }
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, [
        'Content-Type: application/json',
        'Accept: application/vnd.relworx.v2',
        'Authorization: Bearer ' . $apiKey,
    ]);
    if ($body !== null) {
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($body));
    }
    $response = curl_exec($ch);
    $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);
    return ['status' => $status ?: 502, 'body' => $response];
}

$requestBody = json_decode(file_get_contents('php://input'), true);

switch (true) {
    case $action === 'validate' && $method === 'POST':
        $result = callRelworx('mobile-money/validate', 'POST', $RELWORX_API_KEY, $RELWORX_BASE_URL, $requestBody);
        break;

    case $action === 'request-payment' && $method === 'POST':
        $result = callRelworx('mobile-money/request-payment', 'POST', $RELWORX_API_KEY, $RELWORX_BASE_URL, $requestBody);
        break;

    case $action === 'send-payment' && $method === 'POST':
        $result = callRelworx('mobile-money/send-payment', 'POST', $RELWORX_API_KEY, $RELWORX_BASE_URL, $requestBody);
        break;

    case $action === 'check-status' && $method === 'GET':
        $result = callRelworx('mobile-money/check-request-status', 'GET', $RELWORX_API_KEY, $RELWORX_BASE_URL, null, [
            'internal_reference' => $_GET['internal_reference'] ?? '',
            'account_no' => $_GET['account_no'] ?? '',
        ]);
        break;

    default:
        http_response_code(404);
        echo json_encode(['success' => false, 'message' => 'Unknown payment route']);
        exit;
}

http_response_code($result['status']);
echo $result['body'];

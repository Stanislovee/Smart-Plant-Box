<?php
header('Content-Type: application/json');
require '../vendor/autoload.php';
include '../config.php';

use Firebase\JWT\JWT;
use Firebase\JWT\Key;

$headers = getallheaders();

if (!isset($headers['Authorization'])) {
    http_response_code(401);
    echo json_encode(["success" => false, "message" => "Authorization token required"]);
    exit;
}

list($type, $token) = explode(" ", $headers['Authorization'], 2);
if ($type !== "Bearer" || empty($token)) {
    http_response_code(401);
    echo json_encode(["success" => false, "message" => "Invalid token format"]);
    exit;
}

try {
    JWT::decode($token, new Key(JWT_SECRET, 'HS256'));
} catch (Exception $e) {
    http_response_code(401);
    echo json_encode(["success" => false, "message" => "Invalid token"]);
    exit;
}

if ($_SERVER["REQUEST_METHOD"] === "POST") {
    $input = json_decode(file_get_contents("php://input"), true);
    if (isset($input["key"]) && isset($input["email"])) {
        $key = trim($input["key"]);
        $email = trim($input["email"]);
        $stmt = $pdo->prepare("SELECT email FROM devices WHERE `key` = ? AND email = ?");
        $stmt->execute([$key, $email]);
        if ($stmt->fetch()) {
            $update_stmt = $pdo->prepare("UPDATE devices SET email = NULL WHERE `key` = ?");
            $update_stmt->execute([$key]);
            echo json_encode(["success" => true, "message" => "Device unbound"]);
        } else {
            echo json_encode(["success" => false, "message" => "Device not bound to this email"]);
        }
    } else {
        echo json_encode(["success" => false, "message" => "Missing key or email"]);
    }
} else {
    http_response_code(405);
    echo json_encode(["success" => false, "message" => "Method not allowed"]);
}

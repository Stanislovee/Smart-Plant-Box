<?php
header('Content-Type: application/json');
require '../vendor/autoload.php';
include '../config.php';

use \Firebase\JWT\JWT;
use \Firebase\JWT\Key;

$headers = getallheaders();
$token = isset($headers['Authorization']) ? str_replace("Bearer ", "", $headers['Authorization']) : null;

if ($token) {
    try {
        // Use JWT_SECRET fro config.php
        $decoded = JWT::decode($token, new Key(JWT_SECRET, 'HS256'));
        echo json_encode([
            "success" => true,
            "message" => "Token is valid.",
            "data" => $decoded->data
        ]);
    } catch (Exception $e) {
        http_response_code(401);
        echo json_encode(["success" => false, "message" => "Invalid token: " . $e->getMessage()]);
    }
} else {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Token not provided."]);
}

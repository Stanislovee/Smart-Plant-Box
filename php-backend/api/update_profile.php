<?php
header('Content-Type: application/json');
include '../config.php';
require '../vendor/autoload.php';

use \Firebase\JWT\JWT;
use \Firebase\JWT\Key;

$data = json_decode(file_get_contents("php://input"), true);
$headers = getallheaders();
$token = isset($headers['Authorization']) ? str_replace("Bearer ", "", $headers['Authorization']) : null;

if (!$token) {
    http_response_code(401);
    echo json_encode(["success" => false, "message" => "Token required"]);
    exit();
}

try {
    $decoded = JWT::decode($token, new Key(JWT_SECRET, 'HS256'));
    $user_id = $decoded->data->id;
    
    $name = isset($data['name']) ? trim($data['name']) : null;
    $password = isset($data['password']) ? $data['password'] : null;

    $update_fields = [];
    $params = ['id' => $user_id];

    if ($name) {
        $update_fields[] = "Name = :name";
        $params['name'] = $name;
    }

    if ($password) {
        $update_fields[] = "Password = :password";
        $params['password'] = password_hash($password, PASSWORD_BCRYPT);
    }

    if (empty($update_fields)) {
        http_response_code(400);
        echo json_encode(["success" => false, "message" => "No fields to update"]);
        exit();
    }

    $sql = "UPDATE users SET " . implode(", ", $update_fields) . " WHERE id = :id";
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);

    echo json_encode(["success" => true, "message" => "Profile updated successfully"]);
} catch (Exception $e) {
    http_response_code(401);
    echo json_encode(["success" => false, "message" => "Invalid token"]);
}

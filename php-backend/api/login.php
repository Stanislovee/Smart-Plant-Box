<?php
header('Content-Type: application/json');
include '../config.php';
require '../vendor/autoload.php';

use \Firebase\JWT\JWT;
use \Firebase\JWT\Key;

$data = json_decode(file_get_contents("php://input"), true);

if (!empty($data['Email']) && !empty($data['Password'])) {
    $email = strtolower(trim($data['Email']));
    $password = $data['Password'];

    $stmt = $pdo->prepare("SELECT * FROM users WHERE Email = :email");
    $stmt->execute(['email' => $email]);

    if ($stmt->rowCount() > 0) {
        $user = $stmt->fetch();
        if (password_verify($password, $user['Password'])) {
            $payload = [
                "iat" => time(),
                "exp" => time() + 3600,
                "data" => [
                    "id" => $user['id'],
                    "email" => $user['Email'],
                    "name" => $user['Name']
                ]
            ];
            $jwt = JWT::encode($payload, JWT_SECRET, 'HS256');
            echo json_encode(["success" => true, "message" => "Login successful", "token" => $jwt]);
        } else {
            http_response_code(401);
            echo json_encode(["success" => false, "message" => "Incorrect password"]);
        }
    } else {
        http_response_code(404);
        echo json_encode(["success" => false, "message" => "User not found"]);
    }
} else {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Email and password required"]);
}

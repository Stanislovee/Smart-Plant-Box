<?php
header('Content-Type: application/json');
include '../config.php';

$data = json_decode(file_get_contents("php://input"), true);
$email = isset($data["email"]) ? strtolower(trim($data["email"])) : null;

if (empty($email)) {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Email required"]);
    exit();
}

$stmt = $pdo->prepare("SELECT Name FROM users WHERE Email = :email");
$stmt->execute(['email' => $email]);
$user = $stmt->fetch();

if ($user) {
    echo json_encode(["success" => true, "name" => $user["Name"]]);
} else {
    http_response_code(404);
    echo json_encode(["success" => false, "message" => "User not found"]);
}

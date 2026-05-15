<?php
header('Content-Type: application/json');
include '../config.php';

$data = json_decode(file_get_contents("php://input"), true);

if (!empty($data['Name']) && !empty($data['Email']) && !empty($data['Password'])) {
    $name = trim($data['Name']);
    $email = strtolower(trim($data['Email']));
    $password = password_hash($data['Password'], PASSWORD_BCRYPT);

    $stmt = $pdo->prepare("SELECT * FROM users WHERE Email = :email");
    $stmt->execute(['email' => $email]);

    if ($stmt->rowCount() > 0) {
        http_response_code(409);
        echo json_encode(["success" => false, "message" => "Email already exists"]);
        exit;
    }

    $stmt = $pdo->prepare("INSERT INTO users (Name, Email, Password) VALUES (:name, :email, :password)");
    $stmt->execute(['name' => $name, 'email' => $email, 'password' => $password]);

    http_response_code(201);
    echo json_encode(["success" => true, "message" => "Registration successful"]);
} else {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "All fields required"]);
}

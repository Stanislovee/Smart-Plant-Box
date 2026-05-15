
<?php
header('Content-Type: application/json');
include '../config.php';

$data = json_decode(file_get_contents("php://input"), true);

if (!empty($data['email']) && !empty($data['code'])) {
    $email = strtolower(trim($data['email']));
    $code = $data['code'];

    $stmt = $pdo->prepare("SELECT reset_code FROM users WHERE Email = :email");
    $stmt->execute(['email' => $email]);
    $user = $stmt->fetch();

    if ($user && $user['reset_code'] === $code) {
        echo json_encode(["success" => true, "message" => "Code verified"]);
    } else {
        http_response_code(400);
        echo json_encode(["success" => false, "message" => "Invalid code"]);
    }
} else {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Email and code required"]);
}

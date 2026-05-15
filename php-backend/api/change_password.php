
<?php
header('Content-Type: application/json');
include '../config.php';

$data = json_decode(file_get_contents("php://input"), true);

if (!empty($data['email']) && !empty($data['code']) && !empty($data['newPassword'])) {
    $email = strtolower(trim($data['email']));
    $code = $data['code'];
    $newPassword = password_hash($data['newPassword'], PASSWORD_BCRYPT);

    $stmt = $pdo->prepare("SELECT reset_code FROM users WHERE Email = :email");
    $stmt->execute(['email' => $email]);
    $user = $stmt->fetch();

    if ($user && $user['reset_code'] === $code) {
        $stmt = $pdo->prepare("UPDATE users SET Password = :password, reset_code = NULL WHERE Email = :email");
        $stmt->execute(['password' => $newPassword, 'email' => $email]);
        echo json_encode(["success" => true, "message" => "Password changed successfully"]);
    } else {
        http_response_code(400);
        echo json_encode(["success" => false, "message" => "Invalid code"]);
    }
} else {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Email, code, and newPassword required"]);
}

<?php
header('Content-Type: application/json');
include '../config.php';
require '../vendor/autoload.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

$data = json_decode(file_get_contents("php://input"), true);

if (!empty($data['email'])) {
    $email = strtolower(trim($data['email']));
    
    $stmt = $pdo->prepare("SELECT * FROM users WHERE Email = :email");
    $stmt->execute(['email' => $email]);
    
    if ($stmt->rowCount() === 0) {
        http_response_code(404);
        echo json_encode(["success" => false, "message" => "User not found"]);
        exit;
    }

    $code = sprintf("%04d", rand(0, 9999));
    $stmt = $pdo->prepare("UPDATE users SET reset_code = :code WHERE Email = :email");
    $stmt->execute(['code' => $code, 'email' => $email]);

    $mail = new PHPMailer(true);
    try {
        $mail->isSMTP();
        $mail->Host = 'smtp.gmail.com';
        $mail->SMTPAuth = true;
        $mail->Username = 'your_email@gmail.com';  // Змініть на ваш email
        $mail->Password = 'your_app_password';     // Змініть на ваш пароль додатку
        $mail->SMTPSecure = PHPMailer::ENCRYPTION_STARTTLS;
        $mail->Port = 587;

        $mail->setFrom('your_email@gmail.com', 'Smart Plant Box');
        $mail->addAddress($email);
        $mail->isHTML(true);
        $mail->Subject = 'Password Reset Code';
        $mail->Body = "Your password reset code is: <b>$code</b>";

        if ($mail->send()) {
            echo json_encode(["success" => true, "message" => "Reset code sent"]);
        } else {
            http_response_code(500);
            echo json_encode(["success" => false, "message" => "Failed to send email"]);
        }
    } catch (Exception $e) {
        http_response_code(500);
        echo json_encode(["success" => false, "message" => "Email error: " . $mail->ErrorInfo]);
    }
} else {
    http_response_code(400);
    echo json_encode(["success" => false, "message" => "Email required"]);
}

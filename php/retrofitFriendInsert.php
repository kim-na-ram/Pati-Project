<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST')
{
    include("dbcon.php");

    $email = $_POST['email'];
    $friend_email = $_POST['friend_email'];

    if($email == '' || $friend_email == '') {
        echo json_encode(array(
            "status" => "false",
            "message" => "필수 인자가 부족합니다"),
            JSON_UNESCAPED_UNICODE
        );
    } else {

        try {

            $stmt = $con->prepare('INSERT INTO friend_tb VALUES(:email, :friend_email)');
            $stmt->bindParam(':email', $email);
            $stmt->bindParam(':friend_email', $friend_email);

            if($stmt->execute()) {
                echo json_encode(
                    array(
                        "status" => "true",
                        "message" => "성공"
                        ), JSON_UNESCAPED_UNICODE
                    );
            }
            else {
                echo json_encode(
                    array(
                    "status" => "false",
                    "message" => "실패"
                    ), JSON_UNESCAPED_UNICODE
                );
            }

        } catch(PDOException $e) {
            die("Database error: " . $e->getMessage());
        }
    }
}
?>


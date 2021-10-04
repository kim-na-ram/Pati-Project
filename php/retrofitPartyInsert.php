<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST')
{
    include("dbcon.php");

    $request_email = $_POST['request_email'];
    $receive_email = $_POST['receive_email'];

    if($request_email == '' || $receive_email == '') {
        echo json_encode(array(
            "status" => "false",
            "message" => "필수 인자가 부족합니다"),
            JSON_UNESCAPED_UNICODE
        );
    } else {

        try {

            $stmt = $con->prepare('INSERT INTO party_tb VALUES(:request_email, :receive_email)');
            $stmt->bindParam(':request_email', $request_email);
            $stmt->bindParam(':receive_email', $receive_email);

            if($stmt->execute()) {
                echo json_encode(
                    array(
                        "status" => "true",
                        "message" => "파티 요청 성공"
                        ), JSON_UNESCAPED_UNICODE
                    );
            }
            else {
                echo json_encode(
                    array(
                    "status" => "false",
                    "message" => "파티 요청 실패"
                    ), JSON_UNESCAPED_UNICODE
                );
            }

        } catch(PDOException $e) {
            die("Database error: " . $e->getMessage());
        }
    }
}
?>


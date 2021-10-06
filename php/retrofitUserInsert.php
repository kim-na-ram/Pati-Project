<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST')
{
    include("dbcon.php");

    $email = $_POST['email'];
    $password = $_POST['password'];
    $user_name = $_POST['user_name'];
    $gender= $_POST['gender'];
    $picture = $_POST['picture'];

    if($email == '' || $password == '' || $user_name == '' || $gender == '') {
        echo json_encode(array(
            "status" => "false",
	    "message" => "필수 인자가 부족합니다"),
	    JSON_UNESCAPED_UNICODE
        );
    } else {

        try {

            $stmt = $con->prepare('INSERT INTO user_tb(email, password, user_name, gender, picture) VALUES(:email, :password, :user_name, :gender, :picture)');
            $stmt->bindParam(':email', $email);
            $stmt->bindParam(':password', $password);
            $stmt->bindParam(':user_name', $user_name);
            $stmt->bindParam(':gender', $gender);
            $stmt->bindParam(':picture', $picture);

            $gt_stmt = $con->prepare('INSERT INTO tendency_tb(email) VALUES(:email)');
            $gt_stmt->bindParam(':email', $email);

            $tt_stmt = $con->prepare('INSERT INTO game_tb(email) VALUES(:email)');
            $tt_stmt->bindParam(':email', $email);

            if($stmt->execute() && $gt_stmt->execute() && $tt_stmt->execute()) {
                echo json_encode(
                    array(
                        "status" => "true",
                        "message" => "회원가입 성공"
                        ), JSON_UNESCAPED_UNICODE
                    );
            }
            else {
                echo json_encode(
                    array(
                    "status" => "false",
                    "message" => "회원가입 실패"
                    ), JSON_UNESCAPED_UNICODE
                );
            }

        } catch(PDOException $e) {
            die("Database error: " . $e->getMessage());
        }
    }
}
?>


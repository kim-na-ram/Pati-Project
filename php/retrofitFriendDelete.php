<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST')
{
    include("dbcon.php");

    $email = $_POST['email'];
    $friend_email = $_POST['friend_email'];

    if($email == '' && $friend_email == '') {
        echo json_encode(array(
            "status" => "false",
            "message" => "필수 인자가 부족합니다"),
            JSON_UNESCAPED_UNICODE
        );
    } else {

        try {
            
            $delete_stmt = $con->prepare('DELETE FROM friend_tb WHERE email LIKE :email AND friend_email LIKE :friend_email');
            $delete_stmt->bindParam(':email', $email);
            $delete_stmt->bindParam(':friend_email', $friend_email);
            $delete_stmt->execute();
            
            
            $select_stmt = $con->prepare('SELECT * FROM friend_tb WHERE email LIKE :email AND friend_email LIKE :friend_email');
            $select_stmt->bindParam(':email', $email);
            $select_stmt->bindParam(':friend_email', $friend_email);
            $select_stmt->execute();


            if($select_stmt->rowCount() == 0) {
                echo json_encode(
                    array(
                        "status" => "true",
                        "message" => "요청 삭제 성공"
                        ), JSON_UNESCAPED_UNICODE
                    );
            }
            else {
                echo json_encode(
                    array(
                    "status" => "false",
                    "message" => "요청 삭제 실패"
                    ), JSON_UNESCAPED_UNICODE
                );
            }

        } catch(PDOException $e) {
            die("Database error: " . $e->getMessage());
        }
    }
}
?>

<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST')
{
    include("dbcon.php");

    $request_email = $_POST['request_email'];
    $receive_email = $_POST['receive_email'];

    if($request_email == '' && $receive_email == '') {
        echo json_encode(array(
            "status" => "false",
            "message" => "필수 인자가 부족합니다"),
            JSON_UNESCAPED_UNICODE
        );
    } else {

        try {
            
            $delete_stmt = $con->prepare('DELETE FROM party_tb WHERE request_email LIKE :request_email AND receive_email LIKE :receive_email');
            $delete_stmt->bindParam(':request_email', $request_email);
            $delete_stmt->bindParam(':receive_email', $receive_email);
            $delete_stmt->execute();
            
            
            $select_stmt = $con->prepare('SELECT * FROM party_tb WHERE request_email LIKE :request_email AND receive_email LIKE :receive_email');
            $select_stmt->bindParam(':request_email', $request_email);
            $select_stmt->bindParam(':receive_email', $receive_email);
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

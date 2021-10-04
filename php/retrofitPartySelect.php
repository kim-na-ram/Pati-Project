<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST')
{
    include("dbcon.php");

    $email = $_POST['email'];

    if($email == '') {
        echo json_encode(array(
            "status" => "false",
            "message" => "필수 인자가 부족합니다"),
            JSON_UNESCAPED_UNICODE
        );
    } else {

        try {

            $sql="select email, user_name, picture from user_tb where email in (select request_email from party_tb where receive_email like '$email')";
            $stmt = $con->prepare($sql);
            $stmt->execute();

            if ($stmt->rowCount() == 0) {
                echo json_encode(
                    array(
                        "status" => "false",
                        "message" => "검색 실패"
                        ), JSON_UNESCAPED_UNICODE
                    );

            } else {
                
                $result = array();

                while($row=$stmt->fetch(PDO::FETCH_ASSOC)) {

                    extract($row);

                    array_push($result, array(
                        "status" => "true",
                        "message" => "검색 성공",
                        "email"=>$row["email"],
                        "user_name"=>$row["user_name"],
                        "picture"=>$row["picture"]
                    ));
                }

                echo json_encode($result);

            }

        } catch(PDOException $e) {
            die("Database error: " . $e->getMessage());
        }
    }
}
?>

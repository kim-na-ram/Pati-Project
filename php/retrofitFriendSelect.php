
<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST')
{
    include("dbcon.php");

    $email = $_POST['email'];

    if($email == '') {
        
        $result = array();

        array_push($result, array(
            "status" => "false",
            "message" => "필수 인자 부족"
        ));
                
        echo json_encode($result);

    } else {

        try {

            $sql="select email, user_name, picture from user_tb where email in (select friend_email from friend_tb where email like '$email')";
            $stmt = $con->prepare($sql);
            $stmt->execute();

            if ($stmt->rowCount() == 0) {
                    
                $result = array();

                array_push($result, array(
                        "status" => "false",
                        "message" => "검색 실패"
                ));
                
                echo json_encode($result);

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

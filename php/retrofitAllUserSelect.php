<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST')
{
    include("dbcon.php");

    $email = $_POST['email'];
    $preferred_gender = $_POST['preferred_gender'];

    if($email == '') {
        echo json_encode(array(
            "status" => "false",
            "message" => "필수 인자가 부족합니다"),
            JSON_UNESCAPED_UNICODE
        );
    } else {

        try {
            
            $sql="select * from user_tb U, game_tb G, tendency_tb T where U.email = G.email and U.email = T.email and U.email != '$email' and gender $preferred_gender";
            $stmt = $con->prepare($sql);
            $stmt->execute();
            
            if ($stmt->rowCount() == 0) {
                # 실패 처리 필요
	    } else {

		$result = array();

                while($row=$stmt->fetch(PDO::FETCH_ASSOC)) {

                    extract($row);

		    array_push($result, array(
			"email"=>$row["email"],
                        "user_name"=>$row["user_name"],
			"gender"=>$row["gender"],
			"self_pr"=>$row["self_pr"],
                        "game_name"=>$row["game_namie"],
                        "picture"=>$row["picture"],
                        "game0"=>$row["LOL"],
                        "game1"=>$row["OverWatch"],
                        "game2"=>$row["BattleGround"],
                        "game3"=>$row["SuddenAttack"],
                        "game4"=>$row["FIFAOnline4"],
                        "game5"=>$row["LostArk"],
                        "game6"=>$row["MapleStory"],
                        "game7"=>$row["Cyphers"],
                        "game8"=>$row["StarCraft"],
                        "game9"=>$row["DungeonandFighter"]
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


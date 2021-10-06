<?php

if ($_SERVER['REQUEST_METHOD'] == 'POST')
{
    include("dbcon.php");

    $email = $_POST['email'];
    $picture = $_POST['picture'];
    $user_name = $_POST['user_name'];
    $game_name = $_POST['game_name'];
    $self_pr = $_POST['self_pr'];
    $purpose = $_POST['purpose'];
    $voice = $_POST['voice'];
    $men = $_POST['men'];
    $women = $_POST['women'];
    $game_mode = $_POST['game_mode'];
    $game0 = $_POST['game0'];
    $game1 = $_POST['game1'];
    $game2 = $_POST['game2'];
    $game3 = $_POST['game3'];
    $game4 = $_POST['game4'];
    $game5 = $_POST['game5'];
    $game6 = $_POST['game6'];
    $game7 = $_POST['game7'];
    $game8 = $_POST['game8'];
    $game9 = $_POST['game9'];


    if($email == '' || $user_name == '') {
        echo json_encode(array(
            "status" => "false",
            "message" => "필수 인자가 부족합니다"),
            JSON_UNESCAPED_UNICODE
        );
    } else {

        try {
            
            $stmt = $con->prepare("UPDATE user_tb SET picture=:picture, user_name=:user_name, game_name=:game_name, self_pr=:self_pr WHERE email=:email");
            $stmt->bindParam(':picture', $picture);
            $stmt->bindParam(':user_name', $user_name);
            $stmt->bindParam(':game_name', $game_name);
            $stmt->bindParam(':self_pr', $self_pr);
            $stmt->bindParam(':email', $email);
            
            $tt_stmt = $con->prepare("UPDATE tendency_tb SET purpose=$purpose, voice=$voice, men=$men, women=$women, game_mode=$game_mode");
            
            $gt_stmt = $con->prepare("UPDATE game_tb SET LOL=$game0, OverWatch=$game1, BattleGround=$game2, SuddenAttack=$game3, FIFAOnline4=$game4, LostArk=$game5, MapleStory=$game6, Cyphers=$game7, StarCraft=$game8, DungeonandFighter=$game9 WHERE email='$email'");

            if($stmt->execute() && $gt_stmt->execute() && $tt_stmt->execute()) {
                echo json_encode(
                    array(
                        "status" => "true",
                        "message" => "정보 업데이트 성공"
                        ), JSON_UNESCAPED_UNICODE
                    );
            }
            else {
                echo json_encode(
                    array(
                    "status" => "false",
                    "message" => "정보 업데이트 실패"
                    ), JSON_UNESCAPED_UNICODE
                );
            }

        } catch(PDOException $e) {
            die("Database error: " . $e->getMessage());
        }
    }
}
?>


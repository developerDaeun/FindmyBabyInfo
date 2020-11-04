<?php

$conn=mysqli_connect("localhost","root","alswl300","mydb");

if(!$conn){ //db연결 되지 않은 경우
    echo "데이터베이스 연결이 되지 않았습니다";
    exit(); //php 탈출
}   
else{
    //echo "연결 완료"
}
mysqli_set_charset($conn,"utf8");

$id=$_POST["id"];
$pw=$_POST["pw"];   

//아이디 존재하는지 확인하는 쿼리문
$result_id=mysqli_query($conn,"select * from member where id='".$id."'");   
//일치하는 아이디 비밀번호 있는지 확인하는 쿼리문
$result_pw=mysqli_query($conn,"select * from member where id='".$id."'and pw='".$pw."'");

if(mysqli_num_rows($result_id)==0){ //아이디 존재하지 않는 경우
    echo "RESULT_ID_NULL";
}   
else if(mysqli_num_rows($result_pw)==0){ //비밀번호 틀린 경우
    echo "RESULT_PW_NULL";
}   
else{ //아이디 비밀번호 존재하는 경우
    echo "RESULT_OK";
}   


mysqli_close($conn);    //db연결 해제
?>
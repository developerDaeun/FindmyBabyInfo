<?php
$conn=mysqli_connect("localhost","root","alswl300","mydb");

if(!$conn){
    echo "데이터베이스 연결 실패";
    exit(); //php 종료
}

mysqli_set_charset($conn,"utf-8");

$id=$_POST["id"];
$pw=$_POST["pw"];

//아이디 비밀번호 일치하는지 확인하는 쿼리문
$result=mysqli_query($conn,"select * from member where id='".$id."'and pw='".$pw."'");
if(mysqli_num_rows($result)==0){  //비밀번호 틀린 경우
    echo "NOT_FOUND";
}  
else{
    mysqli_query($conn,"delete from member where id='".$id."' and pw='".$pw."'");
    echo "RESULT_OK";
}   

mysqli_close($conn);
?>
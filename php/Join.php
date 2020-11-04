<?php
$conn=mysqli_connect("localhost","root","alswl300","mydb");

if(!$conn){
    echo "데이터베이스 연결 실패";
    exit(); //php 종료
}

mysqli_set_charset($conn,"utf-8");

$id=$_POST["id"];
$pw=$_POST["pw"];
$name=$_POST["name"];
$num=$_POST["num"];

//이미 해당 아이디 존재하는지 체크
$result=mysqli_query($conn,"select * from member where id='".$id."'");
//전화번호 중복체크
$result_number=mysqli_query($conn,"select * from member where num='".$num."'");

if(mysqli_num_rows($result)>0){
    echo "EXIST_ID";
}   //데이터 존재하는 경우
else if(mysqli_num_rows($result_number)>0){
    echo "EXIST_NUMBER";
}
else{
    mysqli_query($conn,"insert into member values('".$id."','".$pw."','".$name."','".$num."')");
    echo "INSERT_OK";
}

mysqli_close($conn);
?>
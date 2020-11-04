<?php
$conn=mysqli_connect("localhost","root","alswl300","mydb");

if(!$conn){
    echo "데이터베이스 연결 실패";
    exit(); //php 종료
}

mysqli_set_charset($conn,"utf-8");

$id=$_POST["id"];
$name=$_POST["name"];
$num=$_POST["num"];

if(mysqli_query($conn, "update member set name='".$name."', num='".$num."' where id='".$id."'")){
    echo "RESULT_OK";
}
else echo "FAIL";

mysqli_close($conn);
?>
<?php
$conn=mysqli_connect("localhost","root","alswl300","mydb");

if(!$conn){
    echo "데이터베이스 연결 실패";
    exit(); //php 종료
}

mysqli_set_charset($conn,"utf-8");

$flag=$_POST["flag"];
$cno=$_POST["cno"];

if($flag=="delete"){
    $result=mysqli_query($conn,"delete from child_information where cno='".$cno."'");
    echo "DELETE_OK";
}
else if($flag=="update"){
    $name=$_POST["name"];
    $gender=$_POST["gender"];
    $age=$_POST["age"];
    $result=mysqli_query($conn,"update child_information set name='".$name."', gender='".$gender."', age='".$age."' where cno='".$cno."'");
    echo "UPDATE_OK";
}
else{
    echo "ERROR";
}

mysqli_close($conn);
?>
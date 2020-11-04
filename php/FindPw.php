<?php
$conn=mysqli_connect("localhost","root","alswl300","mydb");

if(!$conn){
    echo "데이터베이스 연결 실패";
    exit();
}

mysqli_set_charset($conn,"UTF-8");

$id=$_POST["id"];
$name=$_POST["name"];
$num=$_POST["num"]; 

$result=mysqli_query($conn,"select * from member where name='".$name."'and num='".$num."' and id='".$id."'");
if(mysqli_num_rows($result)==0){
    echo "NOT_FOUND";
}  
else{
    echo "FOUND";
}  

mysqli_close($conn);
?>
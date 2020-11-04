<?php
$conn=mysqli_connect("localhost","root","alswl300","mydb");

if(!$conn){
    echo "데이터베이스 연결 실패";
    exit();
}

mysqli_set_charset($conn,"UTF-8");

$name=$_POST["name"];
$num=$_POST["num"];

$result=mysqli_query($conn,"select * from member where name='".$name."'and num='".$num."'");

if(mysqli_num_rows($result)==0){    //조건에 맞는 데이터 없는 경우
    echo "RESULT_NULL";
}   
else{   //조건에 맞는 데이터 찾은 경우
    while($row=mysqli_fetch_assoc($result)){
        //echo "실행";
        $id=$row["id"];
    }
    echo $id;
}   

mysqli_close($conn);
?>
<?php
$conn=mysqli_connect("localhost","root","alswl300","mydb");

if(!$conn){
    echo "데이터베이스 연결 실패";
    exit(); //php 종료
}

mysqli_set_charset($conn,"utf-8");

$id=$_POST["id"];
$pw=$_POST["pw"];

$result=mysqli_query($conn,"select * from member where id='".$id."'and pw='".$pw."'");
if(mysqli_num_rows($result)>0){
    $data=array();
    $row=$result->fetch_array(MYSQLI_ASSOC);
    extract($row);
            array_push($data, 
                array('name'=>$name,
                'num'=>$num
            ));
    //$json=json_encode($data,JSON_PRETTY_PRINT+JSON_UNESCAPTED_UNICODE);
    $json=json_encode($data);
    echo $json;
}
else echo "NOT_FOUND";

mysqli_close($conn);
?>
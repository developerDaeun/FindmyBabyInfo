<?php
$conn=mysqli_connect("localhost","root","alswl300","mydb");

if(!$conn){
    echo "데이터베이스 연결 실패";
    exit(); //php 종료
}

mysqli_set_charset($conn,"utf-8");

$id=$_POST["id"];

$result=mysqli_query($conn,"select * from child_information where id='".$id."'");
if(mysqli_num_rows($result)>0){
    $data=array();
    while($row=$result->fetch_array(MYSQLI_ASSOC)){
        extract($row);
    
            array_push($data, 
                array( 'cno'=>$cno,
                'name'=>$name,
                'gender'=>$gender,
                'age'=>$age
            ));
    }
    $json=json_encode(array("childInfo"=>$data),JSON_PRETTY_PRINT+JSON_UNESCAPTED_UNICODE);
    echo $json;
}
else echo "NO_DATA";

mysqli_close($conn);
?>
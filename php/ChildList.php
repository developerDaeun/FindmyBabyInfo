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

        $imgQuery=mysqli_query($conn, "select * from child_image where id='".$id."' and cno='".$cno."'");
        $imgRow=mysqli_fetch_assoc($imgQuery);
        extract($imgRow);
        $imgData=array();   //이미지 경로를 저장하는 배열
        for($i=0; $i<$imgNo; $i++){
            array_push($imgData, $imgRow["img".$i]);
        }

        array_push($data, 
            array( 'cno'=>$cno,
                'name'=>$name,
                'gender'=>$gender,
                'age'=>$age,
                'path'=>$imgData
        ));
    }
    $json=json_encode(array("childInfo"=>$data),JSON_PRETTY_PRINT+JSON_UNESCAPTED_UNICODE);
    echo $json;
}
else echo "NO_DATA";

mysqli_close($conn);
?>
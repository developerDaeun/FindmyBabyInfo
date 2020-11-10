<?php
$conn=mysqli_connect("localhost","root","alswl300","mydb");

if(!$conn){
    echo "데이터베이스 연결 실패";
    exit(); //php 종료
}

mysqli_set_charset($conn,"utf-8");

$json=$_POST["data"];
$jsondata=json_decode($json);
//$jsondata=json_decode($json, true);
//print_r($jsondata);

//$id=$jsondata['id'];
//$name=$jsondata['name'];
//$gender=$jsondata['gender'];
//$age=$jsondata['age'];
//$path=$jsondata['path']   //$path를 배열 형식으로 받아오기 가능?

$id=$jsondata->id;
$name=$jsondata->name;
$gender=$jsondata->gender;
$age=$jsondata->age;
$path=$jsondata->path;  //string 타입
$path=substr($path,1,-1);   //$path의 [] 제거
$path_arr=explode(", ", $path);


//이미 해당 이름 존재하는지 체크
$result=mysqli_query($conn,"select *from child_information where name='".$name."'");
//cno은 현재 등록된 max(no)+1 값으로 지정
$maxNo=mysqli_query($conn,"select *from child_information where id='".$id."' ORDER BY cno DESC");
$num=mysqli_fetch_array($maxNo);
$cno=(int)$num["cno"]+1;

if(mysqli_num_rows($result)>0){ //데이터가 이미 존재하는 경우
    echo "EXIST_NAME";
}
else{
    mysqli_query($conn, "insert into child_information values('".$cno."','".$id."','".$name."','".$gender."','".$age."')");
    //이미지 경로 저장
    mysqli_query($conn, "insert into child_image values('".$id."','".$cno."','".count($path_arr)."',null, null, null, null, null)");
    for($i=0; $i<count($path_arr);$i++){
        mysqli_query($conn, "update child_image set img".$i."='$path_arr[$i]' where id='".$id."' and cno='".$cno."'");
    }
    echo "RESULT_OK";
}

mysqli_close($conn);
?>
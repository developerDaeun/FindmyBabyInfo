package com.example.softwareproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChildInfo extends ToolBar {
    private String IP="192.168.0.22";
    private String no, id, name, gender, age;
    EditText childName, childAge;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2;
    Button delete, update;
    private String flag="delete";
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Intent get_Intent=getIntent();
        id=get_Intent.getStringExtra("id"); //ChilList 액티비티에서 id값 받아옴
        name=get_Intent.getStringExtra("name");
        gender=get_Intent.getStringExtra("gender");
        age=get_Intent.getStringExtra("age");
        no=get_Intent.getStringExtra("no");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_info);

        findById();

        //기존에 등록된 정보 표시
        childName.setText(name);
        childAge.setText(age);
        if(gender.equals("남")){
            radioButton1.setChecked(true);
            radioButton2.setChecked(false);
        }
        else{
            radioButton1.setChecked(false);
            radioButton2.setChecked(true);
        }

        //나이 입력창에서 엔터 키 입력 시 키보드 내리기
        childAge.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN)&&(keyCode==KeyEvent.KEYCODE_ENTER)){
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);   //키보드 내리기
                }
                return false;
            }
        });

        //삭제버튼을 누른 경우
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag="delete";
                android_to_php atp=new android_to_php();
                atp.execute("http://"+IP+"/ChildInfo.php", flag, no);    //AsyncTask 실행시켜서 백그라운드 작업 수행
            }
        });

        //수정버튼을 누른 경우
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag="update";
                String strChildName=childName.getText().toString();
                String strChildAge=childAge.getText().toString();
                String strChildGender="";
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.boy:
                        strChildGender = "남";
                        break;
                    case R.id.girl:
                        strChildGender = "여";
                        break;
                    default:
                        //Toast.makeText(getApplicationContext(), "성별을 선택하세요", Toast.LENGTH_SHORT).show();
                        break;
                }
                android_to_php atp=new android_to_php();
                atp.execute("http://"+IP+"/ChildInfo.php", flag, no, strChildName, strChildGender, strChildAge);    //AsyncTask 실행시켜서 백그라운드 작업 수행
            }
        });
    }

    private void findById(){
        childName=(EditText)findViewById(R.id.childName);
        childAge=(EditText)findViewById(R.id.childAge);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        radioButton1=(RadioButton)findViewById(R.id.boy);
        radioButton2=(RadioButton)findViewById(R.id.girl);
        delete=(Button)findViewById(R.id.delete);
        update=(Button)findViewById(R.id.update);
        imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    }

    //EditText 외 배경 클릭 시 키보드 내리기
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(imm.isAcceptingText()) { //키보드가 올라와있는 경우
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);   //키보드 내리기
        }
        return true;
        //return super.onTouchEvent(event);
    }

    class android_to_php extends AsyncTask<String,Void,String> {    //<doInBackground 매개변수 자료형, onProgressUpdate 자료형, onPostExecute 자료형>
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog=ProgressDialog.show(ChildInfo.this,"처리 중입니다.",null,true,true);
        }   //스레드 실행(php로 데이터 넘기는 스레드) 전 동작

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();   //프로그레스바 종료!!

        }   //스레드 실행 완료 후 동작

        @Override
        protected String doInBackground(String... strings) { //strings 안에 php 주소 저장됨
            try{
                String Data="";
                if(flag.equals("delete")) {
                    Data = "flag=" + strings[1] + "&no=" + strings[2];
                }
                else{
                    Data="flag="+strings[1]+"&no="+strings[2]+"&name="+strings[3]+"&gender="+strings[4]+"&age="+strings[5];
                }
                URL serverURL=new URL(strings[0]);
                HttpURLConnection httpURLConnection=(HttpURLConnection)serverURL.openConnection();
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.setRequestProperty("Accept-Charset","UTF-8");
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(10000); //타임아웃 시간 지정
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true); //데이터 주고받음 지정해주는소스
                httpURLConnection.connect();

                OutputStream outputStream=httpURLConnection.getOutputStream();
                outputStream.write(Data.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int result_Code=httpURLConnection.getResponseCode();

                InputStream inputStream;

                if(result_Code==HttpURLConnection.HTTP_OK){ //연결 된 경우
                    inputStream=httpURLConnection.getInputStream();
                }
                else{ //연결 되지 않은 경우
                    inputStream=httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader=new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader=new BufferedReader(inputStreamReader);

                StringBuilder sb=new StringBuilder();
                sb.append(bufferedReader.readLine());   //한 줄 읽어옴

                String result=sb.toString().trim();
                System.out.println("Result="+result);
                if(result.equals("DELETE_OK")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder dlg=new AlertDialog.Builder(ChildInfo.this);
                            dlg.setTitle("삭제 완료");
                            dlg.setMessage("삭제되었습니다.");
                            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //progressDialog.dismiss();   //프로그레스바 종료!!
                                    finish();   //로그인으로 돌아감
                                }
                            }); //확인 버튼 누를 시 액티비티 종료하고 로그인 화면으로 돌아감

                            dlg.show();
                        }
                    });
                }
                else if(result.equals("UPDATE_OK")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder dlg=new AlertDialog.Builder(ChildInfo.this);
                            dlg.setTitle("수정 완료");
                            dlg.setMessage("수정이 완료되었습니다.");
                            dlg.setPositiveButton("확인", null); //확인 버튼 누를 시 액티비티 종료하고 로그인 화면으로 돌아감
                            dlg.show();
                        }
                    });

                }
                else{
                    System.out.println("error");
                }


                bufferedReader.close();

                httpURLConnection.disconnect();
            }   //try
            catch(Exception e){
                e.printStackTrace();
                Log.e("오류",e.toString());
            }   //catch
            return null;
        }
    }   //AsyncTask
}

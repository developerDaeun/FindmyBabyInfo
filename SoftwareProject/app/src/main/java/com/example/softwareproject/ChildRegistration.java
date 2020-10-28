package com.example.softwareproject;

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

import androidx.appcompat.app.AlertDialog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChildRegistration extends ToolBar {
    private String id;
    private String IP="192.168.0.22";
    EditText childName, childAge;
    Button add;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Intent get_Intent=getIntent();
        id=get_Intent.getStringExtra("id"); //MainMenu 액티비티에서 id값 받아옴

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_registration);

        findById();

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

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strChildName=childName.getText().toString();
                String strChildAge=childAge.getText().toString();
                String strChildGender="";
                if(strChildName.equals("")){
                    Toast.makeText(getApplicationContext(),"아이 이름을 입력하세요",Toast.LENGTH_SHORT).show();
                }
                else if(strChildAge.equals("")){
                    Toast.makeText(getApplicationContext(),"아이 나이를 입력하세요",Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        Integer age = Integer.parseInt(strChildAge);
                        if(!(age >= 1 && age <= 100)){
                            Toast.makeText(getApplicationContext(), "나이를 1~100 사이로 입력해주세요",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if(!radioButton1.isChecked() && !radioButton2.isChecked()){ //성별이 선택되지 않은 경우
                                Toast.makeText(getApplicationContext(), "성별을 선택하세요", Toast.LENGTH_SHORT).show();
                            }
                            else{
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

                                android_to_php atp = new android_to_php();  //AsyncTask 생성
                                atp.execute("http://"+IP+"/Registration.php", id, strChildName, strChildGender, strChildAge);    //AsyncTask 실행시켜서 백그라운드 작업 수행
                            }
                        }
                    }   //아이 정보 정수로 입력하지 않은 경우 catch에 걸림
                    catch(Exception e){
                        Toast.makeText(getApplicationContext(),"잘못된 형식의 나이 입력입니다",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void findById(){
        childName=(EditText)findViewById(R.id.childName);
        childAge=(EditText)findViewById(R.id.childAge);
        add=(Button)findViewById(R.id.add);
        imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        radioButton1=(RadioButton)findViewById(R.id.boy);
        radioButton2=(RadioButton)findViewById(R.id.girl);
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

            progressDialog=ProgressDialog.show(ChildRegistration.this,"처리중입니다.",null,true,true);
        }   //스레드 실행(php로 데이터 넘기는 스레드) 전 동작

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            childName.setText("");
            childAge.setText("");
            radioGroup.clearCheck();
            progressDialog.dismiss();   //프로그레스바 종료!!

        }   //스레드 실행 완료 후 동작

        @Override
        protected String doInBackground(String... strings) { //strings 안에 php 주소 저장됨
            try{
                String Data="id=" + strings[1] + "&name=" + strings[2] + "&gender=" + strings[3] + "&age=" + strings[4];
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

                bufferedReader.close();

                httpURLConnection.disconnect();

                if(result.equals("EXIST_NAME")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"이미 등록된 아이입니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder dlg=new AlertDialog.Builder(ChildRegistration.this);
                            dlg.setTitle("등록 완료");
                            dlg.setMessage("등록이 완료되었습니다");
                            dlg.setPositiveButton("확인",null);
                            dlg.show();
                        }
                    });
                }
            }   //try
            catch(Exception e){
                e.printStackTrace();
                Log.e("오류",e.toString());
            }   //catch
            return null;
        }
    }   //AsyncTask
}

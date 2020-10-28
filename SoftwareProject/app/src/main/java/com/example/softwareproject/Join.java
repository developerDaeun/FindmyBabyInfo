package com.example.softwareproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Join extends AppCompatActivity {
    EditText id, pw, pwCheck, name, num;
    Button join, back;
    InputMethodManager imm;
    private String IP="192.168.0.22";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        findById();

        //num에서 enter 입력 시 join 버튼 클릭 이벤트
        num.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN)&&(keyCode==KeyEvent.KEYCODE_ENTER)){
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);   //키보드 내리기
                    join.callOnClick();
                }
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strID=id.getText().toString();
                String strPW=pw.getText().toString();
                String strPwCheck=pwCheck.getText().toString();
                String strName=name.getText().toString();
                String strNum=num.getText().toString();

                if(strID.equals("")){   //아이디 미 입력
                    Toast.makeText(getApplicationContext(),"아이디를 입력해 주세요",Toast.LENGTH_SHORT).show();
                }
                else if(strPW.equals("")){  //비밀번호 미 입력
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력해 주세요",Toast.LENGTH_SHORT).show();
                }
                else if(strPwCheck.equals("")){ //비밀번호 확인 미 입력
                    Toast.makeText(getApplicationContext(),"비밀번호 확인을 입력해 주세요",Toast.LENGTH_SHORT).show();
                }
                else if(strName.equals("")){    //이름 미 입력
                    Toast.makeText(getApplicationContext(),"이름을 입력해 주세요",Toast.LENGTH_SHORT).show();
                }
                else if(strNum.equals("")){ //전화번호 미 입력
                    Toast.makeText(getApplicationContext(),"전화번호를 입력해 주세요",Toast.LENGTH_SHORT).show();
                }
                else if(strPwCheck.equals(strPW)){  //비밀번호==비밀번호 확인
                    if (!strNum.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$")) {
                        Toast.makeText(getApplicationContext(), "잘못된 전화번호 형식입니다", Toast.LENGTH_SHORT).show();
                    }   //전화번호를 11자리로 입력하지 않은 경우
                    else {
                        android_to_php atp = new android_to_php();  //AsyncTask 생성
                        atp.execute("http://"+IP+"/Join.php", strID, strPW, strName, strNum);    //AsyncTask 실행시켜서 백그라운드 작업 수행
                    }
                }
                else{   //패스워드 확인 틀린 경우
                    Toast.makeText(getApplicationContext(),"비밀번호가 틀렸습니다",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void findById(){
        id=(EditText)findViewById(R.id.id);
        pw=(EditText)findViewById(R.id.pw);
        pwCheck=(EditText)findViewById(R.id.pwCheck);
        name=(EditText)findViewById(R.id.name);
        num=(EditText)findViewById(R.id.num);
        join=(Button)findViewById(R.id.join);
        back=(Button)findViewById(R.id.back);
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

            progressDialog=ProgressDialog.show(Join.this,"처리중입니다.",null,true,true);
        }   //스레드 실행(php로 데이터 넘기는 스레드) 전 동작

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            progressDialog.dismiss();   //프로그레스바 종료!!

        }   //스레드 실행 완료 후 동작

        @Override
        protected String doInBackground(String... strings) { //strings 안에 php 주소 저장됨
            try{
                String Data="id=" + strings[1] + "&pw=" + strings[2] + "&name=" + strings[3] + "&num=" + strings[4];
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

                if(result.equals("EXIST_ID")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"존재하는 아이디입니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if(result.equals("EXIST_NUMBER")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"존재하는 전화번호입니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder dlg=new AlertDialog.Builder(Join.this);
                            dlg.setTitle("회원가입 완료");
                            dlg.setMessage("회원가입이 완료되었습니다");
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
            }   //try
            catch(Exception e){
                e.printStackTrace();
                Log.e("오류",e.toString());
            }   //catch
            return null;
        }
    }   //AsyncTask
}

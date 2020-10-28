package com.example.softwareproject;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {

    EditText id, pw;
    Button login, join;
    InputMethodManager imm;
    private String IP="192.168.0.22";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findById();

        //pw에서 enter 입력 시 login 버튼 클릭 이벤트
        pw.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()==KeyEvent.ACTION_DOWN)&&(keyCode==KeyEvent.KEYCODE_ENTER)){
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);   //키보드 내리기
                    login.callOnClick();
                }
                return false;
            }
        });

        //로그인 버튼 클릭 이벤트
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strID=id.getText().toString();
                String strPW=pw.getText().toString();
                if(strID.equals("")){ //아이디 미 입력
                    Toast.makeText(getApplication(),"아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                }
                else if(strPW.equals("")){  //비밀번호 미 입력
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력하세요",Toast.LENGTH_SHORT).show();
                }
                else {
                    android_to_php atp = new android_to_php();  //AsyncTask 생성
                    atp.execute("http://"+IP+"/Login.php", strID, strPW);   //AsyncTask 실행시켜서 백그라운드 작업 수행
                }
            }//onClick
        });

        //회원가입 버튼 클릭 시 Join 화면으로 이동
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Join.class);
                startActivity(intent);
            }
        });
    }

    //변수와 위젯 연결 함수
    private void findById(){
        id=(EditText)findViewById(R.id.id);
        pw=(EditText)findViewById(R.id.pw);
        login=(Button)findViewById(R.id.login);
        join=(Button)findViewById(R.id.join);
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

    class android_to_php extends AsyncTask<String,Void,String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() { //스레드 실행 전 동작
            super.onPreExecute();

            progressDialog=ProgressDialog.show(Login.this,"로그인 중입니다",null,true,true);
        }

        @Override
        protected void onPostExecute(String s) { //스레드 실행 완료 후 동작
            super.onPostExecute(s);

            id.setText("");
            pw.setText(""); //로그아웃 후 돌아온 경우
            progressDialog.dismiss();   //프로그레스바 종료
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                String Data="id=" + strings[1] + "&pw=" + strings[2]; //안드로이드로 보낼 데이터 저장
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

                if(result.equals("RESULT_ID_NULL")){ //존재하는 아이디 없는 경우
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"존재하지 않는 아이디입니다.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if(result.equals("RESULT_PW_NULL")){ //비밀번호 틀린 경우
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"비밀번호가 틀렸습니다,",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else if(result.equals("RESULT_OK")){ //아이디와 비밀번호가 일치하는 경우
                    Intent intent=new Intent(getApplicationContext(),MainMenu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("id", strings[1]);    //아이디 다음 액티비티로 넘겨줌
                    startActivity(intent);
                }
            }
            catch(Exception e){
                e.printStackTrace();
                Log.e("오류",e.toString());
            }
            return null;
        }
    }
}

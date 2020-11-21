package org.tensorflow.lite.examples.detection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private EditText id, pw;
    private Button login, join, findId, findPw;
    private InputMethodManager imm;
    private String strID, strPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findById();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strID=id.getText().toString();
                strPW=pw.getText().toString();
                if(strID.equals("")){ //아이디 미 입력
                    Toast.makeText(getApplication(),"아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                }
                else if(strPW.equals("")){  //비밀번호 미 입력
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    String data="id="+strID+"&pw="+strPW;
                    AndroidToPhp atp=new AndroidToPhp();
                    atp.execute("/Login.php",data);
                }
            }
        });

        //회원가입 버튼 클릭 시 회원가입 페이지로 이동
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), Join.class);
                startActivity(intent);
            }
        });

        findId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), FindId.class);
                startActivity(intent);
            }
        });

        findPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), FindPw.class);
                startActivity(intent);
            }
        });
    }

    private void findById(){
        id=(EditText)findViewById(R.id.id);
        pw=(EditText)findViewById(R.id.pw);
        login=(Button)findViewById(R.id.login);
        join=(Button)findViewById(R.id.join);
        findId=(Button)findViewById(R.id.findId);
        findPw=(Button)findViewById(R.id.findPw);
        imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    }

    //배경 클릭 시 키보드 내리기
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(imm.isAcceptingText()) { //키보드가 올라와있는 경우
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);   //키보드 내리기
        }
        return true;
    }

    class AndroidToPhp extends AsyncTask<String, Void, String> { //<doInBackground 매개변수 자료형, onProgressUpdate 자료형, onPostExecute 자료형>

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= ProgressDialog.show(Login.this,"로그인 중입니다.",null,true,true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            id.setText("");
            pw.setText(""); //로그아웃 후 돌아온 경우
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestHandler rh=new RequestHandler();
            String result=rh.sendPostRequest(strings[0], strings[1]);
            if(result.equals("RESULT_ID_NULL")){ //존재하는 아이디 없는 경우
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else if(result.equals("RESULT_PW_NULL")){ //비밀번호 틀린 경우
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"비밀번호가 틀렸습니다,", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else if(result.equals("RESULT_OK")){ //아이디와 비밀번호가 일치하는 경우
                Intent intent=new Intent(getApplicationContext(), MainMenu.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("id", strID);    //아이디 다음 액티비티로 넘겨줌
                startActivity(intent);
            }
            return result;
        }

    }
}

package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Join extends AppCompatActivity {

    private EditText id, pw, pwCheck, name, num;
    private Button join, back;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        findById();

        //돌아가기 버튼을 누르면 Login Activity로 돌아감
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //등록 버튼을 누르면 입력한 정보를 DB에 저장한 후 Login Activity로 돌아감
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
                        String data="id="+strID+"&pw="+strPW+"&name="+strName+"&num="+strNum;
                        AndroidToPhp atp=new AndroidToPhp();
                        atp.execute("/Join.php", data);
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

    //배경 클릭 시 키보드 내리기
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(imm.isAcceptingText()) { //키보드가 올라와있는 경우
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);   //키보드 내리기
        }
        return true;
    }

    class AndroidToPhp extends AsyncTask<String, Void, String>{ //<doInBackground 매개변수 자료형, onProgressUpdate 자료형, onPostExecute 자료형>

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=ProgressDialog.show(Join.this,"처리중입니다.",null,true,true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestHandler rh=new RequestHandler();
            String result=rh.sendPostRequest(strings[0], strings[1]);
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
            return result;
        }

    }
}

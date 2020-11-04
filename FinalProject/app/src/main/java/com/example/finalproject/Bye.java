package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Bye extends ToolBar {

    private String getId;
    private EditText id, pw;
    private Button bye;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent getIntent=getIntent();
        getId=getIntent.getStringExtra("id");  //MyPage로부터 id 받아옴

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bye);

        findById();

        bye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strId=id.getText().toString();
                final String strPw=pw.getText().toString();
                if(strId.equals("")){   //아이디 입력하지 않은 경우
                    Toast.makeText(getApplicationContext(),"아이디를 입력해주세요",Toast.LENGTH_LONG).show();
                }
                else if(strPw.equals("")){   //비밀번호 입력하지 않은 경우
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력해주세요",Toast.LENGTH_LONG).show();
                }
                else if (!strId.equals(getId)){
                    Toast.makeText(getApplicationContext(),"아이디가 다릅니다",Toast.LENGTH_LONG).show();
                }
                else{
                    AlertDialog.Builder dlg=new AlertDialog.Builder(Bye.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                    dlg.setTitle("회원 탈퇴");
                    dlg.setMessage("탈퇴하시겠습니까?");
                    dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String data="id="+strId+"&pw="+strPw;
                            AndroidToPhp atp=new AndroidToPhp();
                            atp.execute("/Bye.php", data);
                        }
                    });
                    dlg.setNegativeButton("취소",null);
                    dlg.show();
                }
            }
        });
    }

    private void findById(){
        id=(EditText)findViewById(R.id.id);
        pw=(EditText)findViewById(R.id.pw);
        bye=(Button)findViewById(R.id.bye);
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
            progressDialog=ProgressDialog.show(Bye.this,"처리 중입니다.",null,true,true);
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
            if(result.equals("NOT_FOUND")){ //비밀번호 일치하지 않는 경우
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"비밀번호를 잘못 입력하셨습니다.",Toast.LENGTH_LONG).show();
                    }
                });
            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder dlg=new AlertDialog.Builder(Bye.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                        dlg.setTitle("탈퇴 완료");
                        dlg.setMessage("회원 탈퇴가 완료되었습니다.");
                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent=new Intent(getApplicationContext(), Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                        dlg.show();
                    }
                });
            }
            return result;
        }
    }
}

package com.example.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PwChange extends Activity {

    private String id;
    private EditText pw, pwCheck;
    private Button ok, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent getIntent=getIntent();
        id=getIntent.getStringExtra("id");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_change);

        findById();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strPw=pw.getText().toString();
                String strPwCheck=pwCheck.getText().toString();
                if(strPw.equals("")){
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력하세요",Toast.LENGTH_SHORT).show();
                }
                else if(strPwCheck.equals("")){
                    Toast.makeText(getApplicationContext(),"비밀번호 확인을 입력하세요",Toast.LENGTH_SHORT).show();
                }
                else if(!strPw.equals(strPwCheck)){
                    Toast.makeText(getApplicationContext(),"비밀번호가 틀렸습니다",Toast.LENGTH_SHORT).show();
                }
                else{
                    String data="id="+id+"&pw="+strPw;
                    AndroidToPhp atp=new AndroidToPhp();
                    atp.execute("/PwChange.php", data);
                }
            }
        });
    }

    private void findById(){
        pw=(EditText)findViewById(R.id.pw);
        pwCheck=(EditText)findViewById(R.id.pwCheck);
        ok=(Button)findViewById(R.id.ok);
        cancel=(Button)findViewById(R.id.cancel);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {    //바깥 레이어 클릭 시 안닫히도록 하는 함수
        //return super.onTouchEvent(event);
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {   //뒤로가기 버튼 눌러도 안닫히도록 하는 함수
        //super.onBackPressed();
        return;
    }

    class AndroidToPhp extends AsyncTask<String, Void, String> { //<doInBackground 매개변수 자료형, onProgressUpdate 자료형, onPostExecute 자료형>

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=ProgressDialog.show(PwChange.this,"처리 중입니다.",null,true,true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pw.setText("");
            pwCheck.setText("");
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestHandler rh=new RequestHandler();
            String result=rh.sendPostRequest(strings[0], strings[1]);
            if(result.equals("RESULT_OK")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder dlg=new AlertDialog.Builder(PwChange.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                        dlg.setTitle("변경 완료");
                        dlg.setMessage("비밀번호 변경이 완료되었습니다");
                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(getApplicationContext(), Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                        dlg.show();
                    }
                });
            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return result;
        }
    }
}

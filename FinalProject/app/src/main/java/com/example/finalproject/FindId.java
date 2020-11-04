package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindId extends AppCompatActivity {

    private EditText name, num;
    private Button search, back;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        findById();

        //돌아가기 버튼 클릭 동작
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strName=name.getText().toString();
                String strNum=num.getText().toString();
                if(strName.equals("")){  //이름 입력되지 않은 경우
                    Toast.makeText(getApplicationContext(),"이름을 입력하세요",Toast.LENGTH_LONG).show();
                }
                else if(strNum.equals("")){ //번호 입력되지 않은 경우
                    Toast.makeText(getApplicationContext(),"전화번호를 입력하세요",Toast.LENGTH_LONG).show();
                }
                else{
                    if (!strNum.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$")) {   //전화번호를 11자리로 입력하지 않은 경우
                        Toast.makeText(getApplicationContext(), "잘못된 전화번호 형식입니다", Toast.LENGTH_LONG).show();
                    }
                    else{
                        String data="name="+strName+"&num="+strNum;
                        AndroidToPhp atp=new AndroidToPhp();
                        atp.execute("/FindId.php", data);
                    }
                }
            }
        });
    }

    //배경 클릭 시 키보드 내리기
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(imm.isAcceptingText()) { //키보드가 올라와있는 경우
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);   //키보드 내리기
        }
        return true;
    }

    private void findById(){
        name=(EditText)findViewById(R.id.name);
        num=(EditText)findViewById(R.id.num);
        search=(Button)findViewById(R.id.search);
        back=(Button)findViewById(R.id.back);
        imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
    }

    class AndroidToPhp extends AsyncTask<String, Void, String> { //<doInBackground 매개변수 자료형, onProgressUpdate 자료형, onPostExecute 자료형>

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=ProgressDialog.show(FindId.this,"처리중입니다.",null,true,true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestHandler rh=new RequestHandler();
            final String result=rh.sendPostRequest(strings[0], strings[1]);
            if(result.equals("RESULT_NULL")){   //해당 정보에 등록된 아이디가 없는 경우
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"아이디가 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                    }
                });
            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                android.app.AlertDialog.Builder dlg=new AlertDialog.Builder(FindId.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                                dlg.setMessage("찾으시는 아이디는 '"+result+"' 입니다");
                                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                                dlg.show();
                            }
                        });
                    }
                });
            }
            return result;
        }
    }
}

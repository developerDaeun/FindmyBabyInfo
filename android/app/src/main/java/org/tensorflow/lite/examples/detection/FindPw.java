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

public class FindPw extends AppCompatActivity {

    private EditText id, name, num;
    private String strId;
    private Button back, search;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pw);

        findById();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strId=id.getText().toString();
                String strName=name.getText().toString();
                String strNum=num.getText().toString();

                if(strId.equals("")){   //아이디 미입력
                    Toast.makeText(getApplicationContext(),"아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                }
                else if(strName.equals("")){    //이름 미입력
                    Toast.makeText(getApplicationContext(),"이름을 입력하세요", Toast.LENGTH_SHORT).show();
                }
                else if(strNum.equals("")){ //전화번호 미입력
                    Toast.makeText(getApplicationContext(),"전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (!strNum.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$")) {
                        Toast.makeText(getApplicationContext(), "잘못된 전화번호 형식입니다", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String data="id="+strId+"&name="+strName+"&num="+strNum;
                        AndroidToPhp atp=new AndroidToPhp();
                        atp.execute("/FindPw.php", data);
                    }
                }
            }
        });
    }

    private void findById(){
        id=(EditText)findViewById(R.id.id);
        name=(EditText)findViewById(R.id.name);
        num=(EditText)findViewById(R.id.num);
        back=(Button)findViewById(R.id.back);
        search=(Button)findViewById(R.id.search);
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
            progressDialog= ProgressDialog.show(FindPw.this,"처리중입니다.",null,true,true);
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
            if(result.equals("NOT_FOUND")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"일치하는 정보가 없습니다.", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else{
                Intent intent=new Intent(getApplicationContext(), PwChange.class);
                intent.putExtra("id", strId);
                startActivity(intent);
            }
            return result;
        }
    }
}

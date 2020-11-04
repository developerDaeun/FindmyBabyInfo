package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyPage extends ToolBar {

    private String getId, getName, getNum;
    private EditText name, num;
    private Button bye, pwChange, update;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Idenfication에서 값을 받아옴
        Intent getIntent=getIntent();
        getId=getIntent.getStringExtra("id");
        getName=getIntent.getStringExtra("name");
        getNum=getIntent.getStringExtra("num");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        findById();

        //등록된 정보 표시
        name.setText(getName);
        num.setText(getNum);

        //정보수정 버튼 클릭 시 현재 입력된 정보로 수정
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strName=name.getText().toString();
                String strNum=num.getText().toString();
                if(strName.equals("")){
                    Toast.makeText(getApplication(),"이름을 입력하세요", Toast.LENGTH_SHORT).show();
                }   //이름 입력되지 않은 경우
                else if(strNum.equals("")){
                    Toast.makeText(getApplicationContext(),"전화번호를 입력하세요",Toast.LENGTH_SHORT).show();
                }   //전화번호가 입력되지 않은 경우
                else if (!strNum.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$")) {
                    Toast.makeText(getApplicationContext(), "잘못된 전화번호 형식입니다", Toast.LENGTH_SHORT).show();
                }   //전화번호를 11자리로 입력하지 않은 경우
                else {
                    String data="id="+getId+"&name="+strName+"&num="+strNum;
                    AndroidToPhp atp=new AndroidToPhp();
                    atp.execute("/MyPage.php", data);
                }
            }
        });

        //비밀번호 변경 버튼 클릭 시 비밀번호 변경 창인 PwChange 액티비티로 이동
        pwChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), PwChange.class);
                intent.putExtra("id",getId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        //회원탈퇴 버튼 클릭 시 재인증 화면으로 이동
        bye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), Bye.class);
                intent.putExtra("id",getId);
                startActivity(intent);
            }
        });
    }

    private void findById(){
        name=(EditText)findViewById(R.id.name);
        num=(EditText)findViewById(R.id.num);
        bye=(Button)findViewById(R.id.bye);
        pwChange=(Button)findViewById(R.id.pwChange);
        update=(Button)findViewById(R.id.update);
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
            progressDialog=ProgressDialog.show(MyPage.this,"처리 중입니다.",null,true,true);
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
            if(result.equals("RESULT_OK")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"수정 완료",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"수정 실패",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return result;
        }
    }
}

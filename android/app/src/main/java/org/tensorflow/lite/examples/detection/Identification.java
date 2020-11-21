package org.tensorflow.lite.examples.detection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Identification extends Activity {

    private EditText pw;
    private Button ok, cancel;
    private String id;
    private String TAG_NAME="name", TAG_NUM="num";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent getIntent=getIntent();
        id=getIntent.getStringExtra("id");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);

        findById();

        //취소 버튼을 누른 경우 동작
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
                if(strPw.equals("")){ //비밀번호를 입력하지 않은 경우
                    Toast.makeText(getApplicationContext(),"비밀번호를 입력하세요", Toast.LENGTH_LONG).show();
                }
                else{
                    String data="id="+id+"&pw="+strPw;
                    AndroidToPhp atp=new AndroidToPhp();
                    atp.execute("/Identification.php", data);
                }
            }
        });
    }

    private void findById(){
        pw=(EditText)findViewById(R.id.pw);
        ok=(Button) findViewById(R.id.ok);
        cancel=(Button)findViewById(R.id.cancel);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {    //바깥 레이어 클릭 시 안닫히도록 하는 함수
        //return super.onTouchEvent(event);
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
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

        ProgressDialog progressDialog=new ProgressDialog(Identification.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= ProgressDialog.show(Identification.this,"확인 중입니다.",null,true,true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pw.setText("");
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestHandler rh=new RequestHandler();
            String result=rh.sendPostRequest(strings[0], strings[1]);
            if(result.equals("NOT_FOUND")){ //비밀번호가 틀린 경우
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //System.out.println("존재함");
                        Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다", Toast.LENGTH_LONG).show();
                    }
                });
            }
            else{
                try{
                    JSONArray jsonArray=new JSONArray(result);
                    JSONObject jsonObject=jsonArray.getJSONObject(0);
                    String name=jsonObject.getString(TAG_NAME);
                    String num=jsonObject.getString(TAG_NUM);

                    Intent intent=new Intent(getApplicationContext(), MyPage.class);
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.putExtra("num", num);
                    startActivity(intent);
                }catch(JSONException e){
                    e.printStackTrace();
                    Log.e("json 오류",e.toString());
                }
            }
            return result;
        }
    }
}

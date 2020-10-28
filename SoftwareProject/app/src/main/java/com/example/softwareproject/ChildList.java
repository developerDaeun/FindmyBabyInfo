package com.example.softwareproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChildList extends ToolBar {
    private String id;
    private String IP="192.168.0.22";
    private List<String> ListMenu = new ArrayList<>();
    private String result;
    private String TAG_JSON="webnautes", TAG_NAME="name", TAG_GENDER="gender", TAG_AGE="age", TAG_NO="no";
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    ListView listview;
    TextView txt;
    Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Intent get_Intent=getIntent();
        id=get_Intent.getStringExtra("id"); //MainMenu 액티비티에서 id값 받아옴

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_list);

        findById();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ChildRegistration.class);
                intent.putExtra("id", id);    //아이디 다음 액티비티로 넘겨줌
                startActivity(intent);
            }
        });

        android_to_php atp = new android_to_php();  //AsyncTask 생성
        atp.execute("http://"+IP+"/ChildList.php", id);    //AsyncTask 실행시켜서 백그라운드 작업 수행

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ListMenu);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject item = jsonArray.getJSONObject(position);
                    String name=item.getString(TAG_NAME);
                    String gender=item.getString(TAG_GENDER);
                    String age=item.getString(TAG_AGE);
                    String no=item.getString(TAG_NO);
                    Intent intent=new Intent(getApplicationContext(),ChildInfo.class);
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.putExtra("gender", gender);
                    intent.putExtra("age", age);
                    intent.putExtra("no", no);
                    startActivity(intent);
                }catch(JSONException e){
                    e.printStackTrace();
                    Log.e("오류",e.toString());
                }
            }
        });
    }

    private void findById(){
        listview=(ListView)findViewById(R.id.listview);
        txt=(TextView)findViewById(R.id.txt);
        add=(Button)findViewById(R.id.add);
    }

    private void addList(){

        try{
            jsonObject=new JSONObject(result);
            jsonArray=jsonObject.getJSONArray(TAG_JSON);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item=jsonArray.getJSONObject(i);
                String name=item.getString(TAG_NAME);
                ListMenu.add(name);
            }
        }catch(JSONException e){
            e.printStackTrace();
            Log.e("오류",e.toString());
        }
    }

    class android_to_php extends AsyncTask<String,Void,String> {    //<doInBackground 매개변수 자료형, onProgressUpdate 자료형, onPostExecute 자료형>
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog=ProgressDialog.show(ChildList.this,"불러오는 중입니다.",null,true,true);
        }   //스레드 실행(php로 데이터 넘기는 스레드) 전 동작

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();   //프로그레스바 종료!!
            if(result!=null) {
                addList();
            }

        }   //스레드 실행 완료 후 동작

        @Override
        protected String doInBackground(String... strings) { //strings 안에 php 주소 저장됨
            try{
                String Data="id=" + strings[1];
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

                String line=sb.toString().trim();
                if(line.equals("NO_DATA")){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txt.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }).start();
                }
                else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    txt.setVisibility(View.GONE);
                                }
                            });
                        }
                    }).start();
                    while((line=bufferedReader.readLine())!=null){
                        sb.append(line);
                    }
                    result=sb.toString().trim();
                }

                bufferedReader.close();

                httpURLConnection.disconnect();
            }   //try
            catch(Exception e){
                e.printStackTrace();
                Log.e("오류",e.toString());
            }   //catch
            return null;
        }
    }   //AsyncTask
}

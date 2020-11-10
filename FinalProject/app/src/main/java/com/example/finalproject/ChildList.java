package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ChildList extends ToolBar {

    private String id;
    private TextView noInfo;    //childList size가 0인 경우 표시
    private ListView listView;
    private List<String> list=new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Button add; //추가 버튼

    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private String TAG_JSON="childInfo", TAG_NAME="name",TAG_GENDER="gender", TAG_AGE="age",TAG_CNO="cno", TAG_PATH="path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Intent getIntent=getIntent();
        id=getIntent.getStringExtra("id"); //MainMenu 액티비티에서 id값 받아옴

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_list);

        findById();

        //추가 버튼 클릭 시 아이 등록 페이지로 이동
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ChildRegistraion.class);
                intent.putExtra("id", id);    //아이디 다음 액티비티로 넘겨줌
                startActivity(intent);
            }
        });

        //listview Item 클릭 시
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long itemId) {
                try{
                    JSONObject item=jsonArray.getJSONObject(position);
                    String name=item.getString(TAG_NAME);
                    String gender=item.getString(TAG_GENDER);
                    String age=item.getString(TAG_AGE);
                    String cno=item.getString(TAG_CNO);
                    String path=item.getString(TAG_PATH);

                    Intent intent=new Intent(getApplicationContext(),ChildInfo.class);
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.putExtra("gender", gender);
                    intent.putExtra("age", age);
                    intent.putExtra("cno", cno);
                    intent.putExtra("path", path);
                    startActivity(intent);
                }catch(JSONException e){
                    e.printStackTrace();
                    Log.e("json 오류", e.toString());
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e("parse 오류", e.toString());
                }
            }
        });

        String data="id="+id;
        AndroidToPhp atp=new AndroidToPhp();
        atp.execute("/ChildList.php",data);
    }

    private void findById(){
        noInfo=(TextView)findViewById(R.id.noInfo);
        listView=(ListView)findViewById(R.id.listView);
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        add=(Button)findViewById(R.id.add);
    }

    //listview에 항목을 추가하는 함수
    private void addList(String result){
        try{
            jsonObject=new JSONObject(result);
            jsonArray=jsonObject.getJSONArray(TAG_JSON);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item=jsonArray.getJSONObject(i);
                String name=item.getString(TAG_NAME);
                list.add(name);
            }
        }catch(JSONException e){
            e.printStackTrace();
            Log.e("json 오류", e.toString());
        }
    }

    class AndroidToPhp extends AsyncTask<String, Void, String> { //<doInBackground 매개변수 자료형, onProgressUpdate 자료형, onPostExecute 자료형>

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=ProgressDialog.show(ChildList.this,"불러오는 중입니다.",null,true,true);
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
            if(result.equals("NO_DATA")){   //등록된 아이 데이터가 없는 경우
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noInfo.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }).start();
            }
            else{   //등록된 데이터가 있는 경우
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                noInfo.setVisibility(View.GONE);
                            }
                        });
                    }
                }).start();
                addList(result);
            }
            return result;
        }

    }
}

package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class ChildInfo extends ToolBar {

    private String cno, id, name, gender, age, path;
    private String[] pathArr;
    private EditText childName, childAge;
    private RadioGroup radioGroup;
    private RadioButton boy, girl;
    private Button delete, update;
    private InputMethodManager imm;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerAdapter adapter;
    private ArrayList<String> dataList;   //RecyclerView에서 표시할 내용

    private String flag="delete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Intent get_Intent=getIntent();
        id=get_Intent.getStringExtra("id"); //ChilList 액티비티에서 id값 받아옴
        name=get_Intent.getStringExtra("name");
        gender=get_Intent.getStringExtra("gender");
        age=get_Intent.getStringExtra("age");
        cno=get_Intent.getStringExtra("cno");
        path=get_Intent.getStringExtra("path");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_info);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        findById();

        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); //Recyclerview를 가로로 설정
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        //path를 dataList에 저장
        path=path.replaceAll("\\\\","");    //\ 문자 제거
        path=path.substring(2, path.length()-2);
        pathArr=path.split("\",\"");    //"," 를 기준으로 문자열 split

        for(int i=0;i<pathArr.length;i++){
            System.out.println("pathArr="+pathArr[i]);
            dataList.add(pathArr[i]);
        }
        adapter.notifyDataSetChanged();

        //기존에 등록된 정보 표시
        childName.setText(name);
        childAge.setText(age);
        if(gender.equals("남")){
            boy.setChecked(true);
            girl.setChecked(false);
        }
        else{
            boy.setChecked(false);
            girl.setChecked(true);
        }

        //삭제버튼을 누른 경우 해당 아이 정보 삭제
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dlg = new AlertDialog.Builder(ChildInfo.this);
                dlg.setTitle("아이 정보 삭제");
                dlg.setMessage("해당 정보를 삭제하시겠습니까?");
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        flag="delete";
                        String data="flag="+flag+"&cno="+cno;
                        AndroidToPhp atp=new AndroidToPhp();
                        atp.execute("/ChildInfo.php", data);
                    }
                });
                dlg.setNegativeButton("취소", null);  //취소 버튼 누른 경우
                dlg.show();
            }
        });

        //수정 버튼을 누른 경우 현재 화면에 입력된 정보대로 아이의 정보 수정
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag="update";
                String strChildName=childName.getText().toString();
                String strChildAge=childAge.getText().toString();
                String strChildGender="";
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.boy:
                        strChildGender = "남";
                        break;
                    case R.id.girl:
                        strChildGender = "여";
                        break;
                    default:
                        //Toast.makeText(getApplicationContext(), "성별을 선택하세요", Toast.LENGTH_SHORT).show();
                        break;
                }

                String data="flag="+flag+"&cno="+cno+"&name="+strChildName+"&gender="+strChildGender+"&age="+strChildAge;
                AndroidToPhp atp=new AndroidToPhp();
                atp.execute("/ChildInfo.php", data);
            }
        });
    }

    private void findById(){
        childName=(EditText)findViewById(R.id.childName);
        childAge=(EditText)findViewById(R.id.childAge);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        boy=(RadioButton)findViewById(R.id.boy);
        girl=(RadioButton)findViewById(R.id.girl);
        delete=(Button)findViewById(R.id.delete);
        update=(Button)findViewById(R.id.update);
        imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        linearLayoutManager=new LinearLayoutManager(this);
        dataList=new ArrayList<>();
        adapter=new RecyclerAdapter(dataList);
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
            progressDialog=ProgressDialog.show(ChildInfo.this,"처리중입니다.",null,true,true);
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

            if(result.equals("DELETE_OK")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder dlg=new AlertDialog.Builder(ChildInfo.this);
                        dlg.setTitle("삭제 완료");
                        dlg.setMessage("삭제되었습니다.");
                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //progressDialog.dismiss();   //프로그레스바 종료!!
                                finish();   //ChildList으로 돌아감
                            }
                        }); //확인 버튼 누를 시 액티비티 종료하고 로그인 화면으로 돌아감

                        dlg.show();
                    }
                });
            }
            else if(result.equals("UPDATE_OK")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder dlg=new AlertDialog.Builder(ChildInfo.this);
                        dlg.setTitle("수정 완료");
                        dlg.setMessage("수정이 완료되었습니다.");
                        dlg.setPositiveButton("확인", null); //확인 버튼 누를 시 액티비티 종료하고 로그인 화면으로 돌아감
                        dlg.show();
                    }
                });

            }
            else{
                System.out.println("error");
            }
            return result;
        }
    }
}

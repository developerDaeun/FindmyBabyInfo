package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ToolBar extends AppCompatActivity {

    protected static String logId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_bar);
    }

    //상속받은 액티비티에 툴바 적용
    @Override
    public void setContentView(int layoutResID) {
        LinearLayout linearLayout=(LinearLayout)getLayoutInflater().inflate(R.layout.activity_tool_bar, null);
        FrameLayout activityContent=(FrameLayout)linearLayout.findViewById(R.id.activityContent);
        getLayoutInflater().inflate(layoutResID, activityContent, true);
        super.setContentView(linearLayout);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //뒤로가기 버튼
        getSupportActionBar().setDisplayShowTitleEnabled(false);    //기존 타이틀이 보이지 않도록 설정
    }


    //메뉴 설정
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolmenu, menu);
        return true;
    }

    //메뉴 클릭 메소드
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mypage:
                Intent intent=new Intent(getApplicationContext(), Identification.class);
                intent.putExtra("id", logId);
                startActivity(intent);
                return true;
            case R.id.logout:
                clickLogout();
                return true;
            case android.R.id.home: //뒤로가기
                clickBackKey();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //뒤로가기 버튼을 누른 경우 동작
    protected void clickBackKey(){
        finish();
    }

    //로그아웃 버튼을 누른 경우 동작
    protected void clickLogout(){
        Intent intent=new Intent(getApplicationContext(), LogoutCheck.class);
        startActivity(intent);
    }
}

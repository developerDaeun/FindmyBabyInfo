package com.example.softwareproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ToolBar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_bar);
    }

    @Override
    public void setContentView(int layoutResID) {
        //super.setContentView(layoutResID);
        LinearLayout linearLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.activity_tool_bar,null);
        FrameLayout activityContent=(FrameLayout)linearLayout.findViewById(R.id.activityContent);
        getLayoutInflater().inflate(layoutResID, activityContent, true);
        super.setContentView(linearLayout);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //뒤로가기 버튼
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존 타이틀 보여지지 않도록 구현
    }

    //메뉴 사용
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolmenu, menu);
        return true;
    }

    //메뉴 클릭 이벤트
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mypage:
                Toast.makeText(getApplicationContext(),"메뉴1 클릭", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout:
                Toast.makeText(getApplicationContext(),"메뉴2 클릭", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
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
}

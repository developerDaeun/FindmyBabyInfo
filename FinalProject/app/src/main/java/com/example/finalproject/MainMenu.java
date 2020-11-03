package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

public class MainMenu extends ToolBar {

    private String id;
    private Button childInfo, findChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Intent getIntent=getIntent();
        id=getIntent.getStringExtra("id");  //Login Activity에서 id값 전달
        logId=id;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        findById();

        //아이정보 버튼 클릭 시 아이정보 리스트 페이지로 이동
        childInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ChildList.class);
                intent.putExtra("id", logId);
                startActivity(intent);
            }
        });

        //아이찾기 버튼 클릭 시 인공지능과 연계
        findChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //추후 구현 예정
            }
        });
    }

    private void findById(){
        childInfo=(Button)findViewById(R.id.childInfo);
        findChild=(Button)findViewById(R.id.findChild);
    }

    @Override
    protected void clickBackKey() {
        super.clickLogout();
    }
}

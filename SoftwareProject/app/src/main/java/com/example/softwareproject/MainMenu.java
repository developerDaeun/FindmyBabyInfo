package com.example.softwareproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends ToolBar {
    private String id;
    Button child, find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Intent get_Intent=getIntent();
        id=get_Intent.getStringExtra("id"); //Login 액티비티에서 id값 받아옴

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        findViewId();   //findViewById

        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ChildList.class);
                intent.putExtra("id", id);    //아이디 다음 액티비티로 넘겨줌
                startActivity(intent);
            }
        });
    }

    public void findViewId(){
        child=(Button)findViewById(R.id.child);
        find=(Button)findViewById(R.id.find);
    }

}

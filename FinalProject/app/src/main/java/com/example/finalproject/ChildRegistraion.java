package com.example.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ChildRegistraion extends ToolBar {

    private String id;
    private EditText childName, childAge;
    private Button imageLoad, add;
    private RadioGroup radioGroup;
    private RadioButton boy, girl;
    private InputMethodManager imm;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerAdapter adapter;
    private ArrayList<String> dataList;   //RecyclerView에서 표시할 내용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Intent getIntent=getIntent();
        id=getIntent.getStringExtra("id");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_registraion);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        findById();

        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL); //Recyclerview를 가로로 설정
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        //이미지 불러오기 버튼 클릭 시 갤러리에서 이미지 받아옴
        imageLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        //등록 버튼 클릭 시 데이터베이스에 아이 정보 등록
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strChildName=childName.getText().toString();
                String strChildAge=childAge.getText().toString();
                String strChildGender="";
                if(strChildName.equals("")){    //이름 미입력
                    Toast.makeText(getApplicationContext(),"아이 이름을 입력하세요",Toast.LENGTH_SHORT).show();
                }
                else if(strChildAge.equals("")){    //나이 미입력
                    Toast.makeText(getApplicationContext(),"아이 나이를 입력하세요",Toast.LENGTH_SHORT).show();
                }
                else{
                    try{
                        Integer age = Integer.parseInt(strChildAge);
                        if(!(age >= 1 && age <= 100)){  //나이가 1~100사이가 아닌 경우
                            Toast.makeText(getApplicationContext(), "나이를 1~100 사이로 입력해주세요",Toast.LENGTH_SHORT).show();
                        }else{
                            if(!boy.isChecked() && !girl.isChecked()){ //성별이 선택되지 않은 경우
                                Toast.makeText(getApplicationContext(), "성별을 선택하세요", Toast.LENGTH_SHORT).show();
                            }
                            else{
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

                                if(dataList.size()==0){
                                    Toast.makeText(getApplicationContext(), "사진을 선택하세요", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    JSONObject jsonObject=new JSONObject();
                                    jsonObject.put("id", id);
                                    jsonObject.put("name", strChildName);
                                    jsonObject.put("gender", strChildGender);
                                    jsonObject.put("age", age);
                                    ArrayList<String> path=new ArrayList<>();
                                    for(int i=0;i<dataList.size();i++){
                                        path.add(dataList.get(i));
                                    }
                                    jsonObject.put("path", path);
                                    String data=jsonObject.toString();

                                    AndroidToPhp atp=new AndroidToPhp();
                                    atp.execute("/Registration.php","data="+data);
                                }
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        Log.e("error", e.toString());
                    }
                }
            }
        });
    }

    private void findById(){
        childName=(EditText)findViewById(R.id.childName);
        childAge=(EditText)findViewById(R.id.childAge);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        boy=(RadioButton)findViewById(R.id.boy);
        girl=(RadioButton)findViewById(R.id.girl);
        imageLoad=(Button)findViewById(R.id.imageLoad);
        add=(Button)findViewById(R.id.add);
        imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        linearLayoutManager=new LinearLayoutManager(this);
        dataList=new ArrayList<>();
        adapter=new RecyclerAdapter(dataList);
    }

    //URI로 부터 sdcard에 저장된 파일명 반환
    private String getRealPathFromURI(Uri contentURI) {
        String result="";
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME);
            cursor.moveToPosition(0);
            result = cursor.getString(idx);
            String []fileName=result.split("\\.");  //.을 기준으로 문자열 자르기
            result=fileName[0];
            cursor.close();
        }
        return result;
    }

    //bitmap 이미지를 내부 저장소에 저장하고 그 경로 반환
    private String saveToInternalStorage(Bitmap bitmap, String imageName){
        ContextWrapper cw=new ContextWrapper(getApplicationContext());
        File dir=cw.getDir("imageDir", Context.MODE_PRIVATE);
        File imgDir=new File(dir, imageName+".jpg");
        FileOutputStream fos=null;
        try{
            fos=new FileOutputStream(imgDir);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }catch(Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                fos.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        return dir.getAbsolutePath();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data == null) {
                System.out.println("사진 선택 취소");
            } else {
                if (data.getClipData() == null) {
                    System.out.println("다중 선택 불가");
                } else {
                    ClipData clipData = data.getClipData();
                    if (clipData.getItemCount() > 5) {
                        Toast.makeText(ChildRegistraion.this, "사진은 5장까지만 선택가능", Toast.LENGTH_SHORT).show();
                    } else {
                        dataList.clear();
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            try {
                                Uri uri = clipData.getItemAt(i).getUri();
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);   //uri->bitmap
                                String imageName = getRealPathFromURI(uri);
                                String imagePath=saveToInternalStorage(bitmap, imageName);
                                dataList.add(imagePath+"/"+imageName+".jpg");
                            }catch (Exception e){
                                e.printStackTrace();
                                Log.e("bitmap 변환 오류",e.toString());
                            }

                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }
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
            progressDialog=ProgressDialog.show(ChildRegistraion.this,"처리중입니다.",null,true,true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            childName.setText("");
            childAge.setText("");
            radioGroup.clearCheck();
            dataList.clear();
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            RequestHandler rh=new RequestHandler();
            String result=rh.sendPostRequest(strings[0], strings[1]);
            if(result.equals("EXIST_NAME")){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"이미 등록된 아이입니다.",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder dlg=new AlertDialog.Builder(ChildRegistraion.this);
                        dlg.setTitle("등록 완료");
                        dlg.setMessage("등록이 완료되었습니다");
                        dlg.setPositiveButton("확인",null);
                        dlg.show();
                    }
                });
            }
            return result;
        }
    }
}

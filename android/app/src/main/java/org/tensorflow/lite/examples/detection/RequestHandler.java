package org.tensorflow.lite.examples.detection;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestHandler {

    private String IP="http://192.168.0.87";
    StringBuilder sb=new StringBuilder();

    public String sendPostRequest(String url, String data){
        try{
            URL serverURL=new URL(IP+url);

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
            outputStream.write(data.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            int result_Code=httpURLConnection.getResponseCode();
            InputStream inputStream;
            if(result_Code== HttpURLConnection.HTTP_OK){ //연결 된 경우
                inputStream=httpURLConnection.getInputStream();
            }
            else{ //연결 되지 않은 경우
                inputStream=httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader=new InputStreamReader(inputStream,"UTF-8");
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            String response="";
            while((response=bufferedReader.readLine())!=null){
                sb.append(response);
            }
        }catch(Exception e){
            e.printStackTrace();
            Log.e("연결 오류",e.toString());
        }
        return sb.toString();
    }
}

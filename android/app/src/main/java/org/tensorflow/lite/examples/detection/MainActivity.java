package org.tensorflow.lite.examples.detection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.examples.detection.customview.OverlayView;
import org.tensorflow.lite.examples.detection.env.ImageUtils;
import org.tensorflow.lite.examples.detection.env.Logger;
import org.tensorflow.lite.examples.detection.env.Utils;
import org.tensorflow.lite.examples.detection.tflite.Classifier;
import org.tensorflow.lite.examples.detection.tflite.YoloV4Classifier;
import org.tensorflow.lite.examples.detection.tracking.MultiBoxTracker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    private String[] pathArr;
    private TextView textView;

    // manifest permission만 하면 안됨
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // manifest permission만 하면 안됨
        verifyStoragePermissions(MainActivity.this);


        final Intent getIntent = getIntent();
        String id = getIntent.getStringExtra("id");  // Login Activity에서 id값 전달
        String path = getIntent.getStringExtra("path");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraButton = findViewById(R.id.cameraButton);
        detectButton = findViewById(R.id.detectButton);
        detectButton2 = findViewById(R.id.detectButton2);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);

        //path를 dataList에 저장
        path = path.replaceAll("\\\\", "");    // 문자 제거
        path = path.substring(2, path.length() - 2);
        pathArr = path.split("\",\"");    // "," 를 기준으로 문자열 split

        cameraButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DetectorActivity.class)));

        detectButton.setOnClickListener(v -> {
            Handler handler = new Handler();

            new Thread(() -> {
                final List<Classifier.Recognition> results = detector.recognizeImage(cropBitmap);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleResult(cropBitmap, results);
                    }
                });
            }).start();
        });

        detectButton2.setOnClickListener(v -> {
            Handler handler2 = new Handler();

            new Thread(() -> {
                final List<Classifier.Recognition> results2 = detector.recognizeImage(cropBitmap2);
                handler2.post(new Runnable() {
                    @Override
                    public void run() {
                        handleResult2(cropBitmap2, results2);
                    }
                });
            }).start();
        });

        //this.sourceBitmap = Utils.getBitmapFromAsset(MainActivity.this, pathArr[0]);
        //this.sourceBitmap = Utils.getBitmapFromAsset("/sdcard/ipwebcam_videos/photo/photo_2020-11-21_19-31-1.jpg");
        //this.sourceBitmap = Utils.getBitmapFromAsset("/sdcard/ipwebcam_videos/circular/circular_2020-11-16_19-57.mp4");
//        this.sourceBitmap = Utils.getBitmapFromAsset(pathArr[0]);   // 영상
//        this.sourceBitmap2 = Utils.getBitmapFromAsset(pathArr[1]);  // 사진


        ArrayList<String> temp;
        temp = findFolder();
        Collections.sort(temp);


        this.sourceBitmap = Utils.getBitmapFromAsset(temp.get(temp.size()-1));   // 영상
        this.sourceBitmap2 = Utils.getBitmapFromAsset(pathArr[0]);  // 사진    // 새로 추가

        this.cropBitmap = Utils.processBitmap(sourceBitmap, TF_OD_API_INPUT_SIZE);
        this.cropBitmap2 = Utils.processBitmap(sourceBitmap2, TF_OD_API_INPUT_SIZE);                // 새로 추가

        this.imageView.setImageBitmap(cropBitmap);
        this.imageView2.setImageBitmap(cropBitmap2);                // 새로 추가

        initBox();          // 건드릴 소스 없음


//        /*-------------------------------------------------------------------------------*/
//        // 동영상 파일 불러오기
//        videoFile = new File("/sdcard/ipwebcam_videos/circular/circular_2020-11-22_15-24.mp4");
//        videoFileUri = Uri.parse(videoFile.toString());
//
//        // instance 생성
//        retriever = new MediaMetadataRetriever();
//
//        // 추출한 bitmap을 담을 array 생성
//        bitmapArrayList = new ArrayList<Bitmap>();
//
//        // 사용할 data source 경로 설정
//        retriever.setDataSource(videoFile.toString());
//
//        // video file의 총 재생시간 얻어오기
//        mediaPlayer = MediaPlayer.create(getBaseContext(), videoFileUri);
//
//        // ex) 60초 영상이면 millisecond = 60000
//        int millisecond = mediaPlayer.getDuration();
//
//        // 1000씩 증가 => 60초 영상이면 1초마다 bitmap을 얻기 위해
//        for (int i = 0; i < millisecond; i += 500) {
//            // getFrameAtTime 함수는 i라는 타임에 bitmap을 얻어와준다.
//            // getFrameAtTime의 첫번째 인자의 unit은 microsecond이다.
//            // 따라서 1000을 곱해줌
//            bitmap = retriever.getFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
//            bitmapArrayList.add(bitmap);
//
//            this.cropBitmap = Utils.processBitmap(bitmapArrayList.get(i/500), TF_OD_API_INPUT_SIZE);
//            this.imageView.setImageBitmap(cropBitmap);
//
//            initBox();
//        }
//
//        // retriever를 다 사용하면 release 해줘야함
//        retriever.release();


    }

    private ArrayList<String> findFolder(){
        ArrayList<String> fName = new ArrayList<String>();
        File files = new File("/sdcard/ipwebcam_videos/photo/");
        if(files.listFiles().length>0){
            for(File file : files.listFiles()){
                fName.add("/sdcard/ipwebcam_videos/photo/" + file.getName());
            }
        }
        files = null;
        return fName;
    }


//    // 새로 추가
//    class BackRunnable implements Runnable {
//        final List<Classifier.Recognition> results2 = detector.recognizeImage(cropBitmap2);
//        public void run(){
//            while(true){
//                try {
//                    handleResult2(cropBitmap2, results2);
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }
//    }


//    public void saveFrames(ArrayList<Bitmap> saveBitmap) throws IOException{
//        String folder = Environment.getExternalStorageDirectory().toString();
//        File saveFolder = new File(folder + "/");
//        if(!saveFolder.exists()){
//            saveFolder.mkdirs();
//        }
//        int i = 1;
//        for (Bitmap b : saveBitmap){
//            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//            b.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
//            File f = new File(saveFolder,("filename"+i+".jpg"));
//
//            f.createNewFile();
//            FileOutputStream fo = new FileOutputStream(f);
//            fo.write(bytes.toByteArray());
//
//            fo.flush();
//            fo.close();
//            i++;
//        }
//        thread.interrupt();
//    }


//    /* Checks if external storage is available to at least read */
//    public boolean isExternalStorageReadable() {
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state) ||
//                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//            return true;
//        }
//        return false;
//    }


    private static final Logger LOGGER = new Logger();

    public static final int TF_OD_API_INPUT_SIZE = 416;

    private static final boolean TF_OD_API_IS_QUANTIZED = false;

    //private static final String TF_OD_API_MODEL_FILE = "yolov4-416-fp32.tflite";
    private static final String TF_OD_API_MODEL_FILE = "yolov4-416-custom-last.tflite";

    //private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/coco.txt";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/custom";

    // Minimum detection confidence to track a detection.
    private static final boolean MAINTAIN_ASPECT = false;
    private Integer sensorOrientation = 90;

    private Classifier detector;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;
    private MultiBoxTracker tracker;
    private OverlayView trackingOverlay;
    private OverlayView trackingOverlay2;

    protected int previewWidth = 0;
    protected int previewHeight = 0;

    private Bitmap sourceBitmap;
    private Bitmap sourceBitmap2;
    private Bitmap cropBitmap;
    private Bitmap cropBitmap2;

    private Button cameraButton, detectButton, detectButton2;
    private ImageView imageView;
    private ImageView imageView2;

    private void initBox() {
        previewHeight = TF_OD_API_INPUT_SIZE;
        previewWidth = TF_OD_API_INPUT_SIZE;
        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        tracker = new MultiBoxTracker(this);
        trackingOverlay = findViewById(R.id.tracking_overlay);
        trackingOverlay2 = findViewById(R.id.tracking_overlay2);            // 새로 추가
        trackingOverlay.addCallback(
                canvas -> tracker.draw(canvas));            // 도화지 생성
        trackingOverlay2.addCallback(                                       // 새로 추가
                canvas -> tracker.draw(canvas));            // 도화지 생성

        tracker.setFrameConfiguration(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, sensorOrientation);

        try {
            detector =
                    YoloV4Classifier.create(
                            getAssets(),
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE,
                            TF_OD_API_IS_QUANTIZED);
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing classifier!");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Classifier could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }
    }


//    private void handleResult(Bitmap bitmap, List<Classifier.Recognition> results) {
//        final Canvas canvas = new Canvas(bitmap);
////        final Paint paint = new Paint();
////        paint.setColor(Color.RED);
////        paint.setStyle(Paint.Style.STROKE);
////        paint.setStrokeWidth(2.0f);
//
//        final List<Classifier.Recognition> mappedRecognitions =
//                new LinkedList<Classifier.Recognition>();
//
//        for (final Classifier.Recognition result : results) {
//            final RectF location = result.getLocation();
//            //System.out.println("location 결과== " + location);
//            if (location != null && result.getConfidence() >= MINIMUM_CONFIDENCE_TF_OD_API) {
//                //canvas.drawRect(location, paint);       // 빨간 네모 박스로만 그리기(지움)
////                for(int i=0; i<2; i++){
////                    location.left = location.left - (float)0;
////                    location.right = location.right - (float)0;
////                }
//                //System.out.println("location 결과2== " + location);
//                cropToFrameTransform.mapRect(location); // bbox 위치를 프레임으로 변환(원래 주석)
//
//                result.setLocation(location);   // result의 bbox 위치 사용(원래 주석)
//                mappedRecognitions.add(result); // title, confidence, bbox 위치 추가(원래 주석)
//            }
//        }
//        tracker.trackResults(mappedRecognitions, new Random().nextInt());   // title, confidence 결과(원래 주석)
//        trackingOverlay.postInvalidate();   // title, confidence 그리기(원래 주석)            // 새로 추가
//        imageView.setImageBitmap(bitmap);                                                   // 새로 추가
//    }

    private void handleResult(Bitmap bitmap, List<Classifier.Recognition> results) {

        final List<Classifier.Recognition> mappedRecognitions =
                new LinkedList<Classifier.Recognition>();

        for (final Classifier.Recognition result : results) {
            final RectF location = result.getLocation();
            System.out.println("location 결과 == " + location);
            System.out.println("location 결과 == " + result.getConfidence());
            if (location != null && result.getConfidence() >= MINIMUM_CONFIDENCE_TF_OD_API) {
                cropToFrameTransform.mapRect(location); // bbox 위치를 프레임으로 변환(원래 주석)

                result.setLocation(location);   // result의 bbox 위치 사용(원래 주석)
                mappedRecognitions.add(result); // title, confidence, bbox 위치 추가(원래 주석)
            }
        }
        tracker.trackResults(mappedRecognitions, new Random().nextInt());   // title, confidence 결과(원래 주석)
        trackingOverlay.postInvalidate();   // title, confidence 그리기(원래 주석)            // 새로 추가
        imageView.setImageBitmap(bitmap);                                                   // 새로 추가

    }

    private void handleResult2(Bitmap bitmap, List<Classifier.Recognition> results) {

        final List<Classifier.Recognition> mappedRecognitions =
                new LinkedList<Classifier.Recognition>();

        for (final Classifier.Recognition result : results) {
            final RectF location = result.getLocation();
            System.out.println("location 결과2 == " + location);
            System.out.println("location 결과2 == " + result.getConfidence());
            if (location != null && result.getConfidence() >= MINIMUM_CONFIDENCE_TF_OD_API) {
                cropToFrameTransform.mapRect(location); // bbox 위치를 프레임으로 변환(원래 주석)

                location.right = location.right + (float)60;

                result.setLocation(location);   // location을 crop에서 frame으로 변환한 값으로 넣기(원래 주석)
                mappedRecognitions.add(result); // title, confidence, bbox 위치 추가(원래 주석)
            }
        }
        tracker.trackResults(mappedRecognitions, new Random().nextInt());   // title, confidence 결과(원래 주석)
        trackingOverlay2.postInvalidate();   // title, confidence 그리기(원래 주석)            // 새로 추가
        imageView2.setImageBitmap(bitmap);                                                   // 새로 추가
    }
}

package com.tsuchiya.ken.playreader;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.widget.ImageView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.view.GestureDetector;
import android.widget.SeekBar;

public class BookActivity extends Activity {

    private List<String> pageList = new ArrayList<String>();
    private File[] files;
    private File Page_L;
    private File Page_R;
    private Bitmap bmp_L;
    private Bitmap bmp_R;

    private int page_num_L;
    private int page_num_R;

    private String PATH_DATA;

    private  SeekBar seekBar;

    private static final int SWIPE_MIN_DISTANCE = 50;               // X軸最低スワイプ距離
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;        // X軸最低スワイプスピード
    private static final int SWIPE_MAX_OFF_PATH = 250;              // Y軸の移動距離　これ以上なら横移動を判定しない
    private GestureDetector mGestureDetector;                       // タッチイベントを処理するためのインタフェース

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_picture);

        Intent intent = getIntent();

        page_num_L = 1;
        page_num_R = 0;

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // String形式で受け取る。
        PATH_DATA = intent.getStringExtra("PATH_DATA");

        Log.d("myTag","path: " +PATH_DATA);

        files = new File(PATH_DATA).listFiles();
        if(files != null){
            searchPageFiles(files);

            PageUpdate();
        }

        // SeekBar
        seekBar = findViewById(R.id.pagebar);
        // 初期値
        seekBar.setProgress(0);
        // 最大値
        seekBar.setMax(pageList.size());

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    //ツマミがドラッグされると呼ばれる
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        page_num_R = progress-1;
                        page_num_L = progress;
                        PageUpdate();
                        Log.d("myTag","progress: "+progress);

                    }

                    //ツマミがタッチされた時に呼ばれる
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    //ツマミがリリースされた時に呼ばれる
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                });

        mGestureDetector = new GestureDetector(this, mOnGestureListener);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        pageList = null;
        Page_L = null;
        bmp_L.recycle();
        Page_R = null;
        bmp_R.recycle();
        Log.d("myTag","back ");
    }

    private int PageUpdate(){
        Log.d("myTag","page_num_L:" + page_num_L + " page_num_R:" + page_num_R);
        if (pageList.size() < 1) return -1;
        if (page_num_L >= pageList.size()) return -2;

        Page_L = new File( PATH_DATA+"/"+pageList.get(page_num_L) );
        bmp_L = BitmapFactory.decodeFile(Page_L.getPath());
        ((ImageView)findViewById(R.id.imageView_L)).setImageBitmap(bmp_L);

        Page_R = new File( PATH_DATA+"/"+pageList.get(page_num_R) );
        bmp_R = BitmapFactory.decodeFile(Page_R.getPath());
        ((ImageView)findViewById(R.id.imageView_R)).setImageBitmap(bmp_R);

        return 0;
    }

    private void GoToNextPage(){
        if (page_num_L < pageList.size()-1) page_num_L += 2;
        if (page_num_R < pageList.size()-1) page_num_R += 2;
        PageUpdate();
    }

    private void GoToPrePage(){
        if (page_num_L > 1) page_num_L -= 2;
        if (page_num_R > 1) page_num_R -= 2;
        PageUpdate();
    }

    // タッチイベント
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    // タッチイベントのリスナー
    private final GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        // フリックイベント
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            try {
                // 移動距離・スピードを出力
                float distance_x = Math.abs((event1.getX() - event2.getX()));
                float velocity_x = Math.abs(velocityX);
                Log.d("myTag","横の移動距離:" + distance_x + " 横の移動スピード:" + velocity_x);

                // Y軸の移動距離が大きすぎる場合
                if (Math.abs(event1.getY() - event2.getY()) > SWIPE_MAX_OFF_PATH) {
                    Log.d("myTag","縦の移動距離が大きすぎ");
                }
                // 開始位置から終了位置の移動距離が指定値より大きい
                // X軸の移動速度が指定値より大きい
                else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Log.d("myTag","右から左");

                    GoToPrePage();
                }
                // 終了位置から開始位置の移動距離が指定値より大きい
                // X軸の移動速度が指定値より大きい
                else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    Log.d("myTag","左から右");

                    GoToNextPage();
                }

            } catch (Exception e) {
                // TODO
            }
            return false;
        }
    };

    private void searchPageFiles(File[] f){
        for(int i = 0; i < f.length; i++){
            if (f[i].isFile()){
                if(f[i].getName().endsWith(".jpg") || f[i].getName().endsWith(".png") || f[i].getName().endsWith(".JPG") || f[i].getName().endsWith(".jpeg")){
                    pageList.add(f[i].getName());
                    Log.d("myTag", "pageList["+ i +"] = "+ f[i].getName() );
                }
            }
        }
    }
}

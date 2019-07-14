package com.tsuchiya.ken.playreader;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Environment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.util.Log;
import android.content.Intent;

public class MainActivity extends Activity {

    final short DEBUG = 1;

    private List<String> volumeList = new ArrayList<String>();      // 本の巻のリスト（フォルダ 名）
    private List<String> bookList = new ArrayList<String>();        // 本の作品名のリスト（フォルダ名）
    private ListView lv;                                            // 左側のリストビュー
    private ListView lv_r;                                          // 右側のリストビュー
    private File[] files;                                           // 本の作品名のリスト（フォルダ）
    private File[] volumes;                                         // 本の巻のリスト（フォルダ ）
    private String sdPath;                                          // 外部ストレージのパス
    private String bookPath;                                        // 本のフォルダのパス
    ArrayAdapter<String> adapter_r;                                 // 右側のリストビュー用のadapter
    ArrayAdapter<String> adapter;                                   // 左側のリストビュー用のadapter
    private String artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("myTag", "Hello tablet P !");

        sdPath = Environment.getExternalStorageDirectory().getPath();
        Log.d("myTag", "sdPath:" + sdPath);
        //sdPath = "/storage/sdcard1";
        sdPath = "/mnt/sdcard/Document";

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        files = new File(sdPath).listFiles();

        if(files != null){

            // 本を探す（作品）
            searchBookFolder(files);

            bookPath = bookList.get(0);
            Log.d("myTag","path: "+sdPath+"/"+bookPath);
            volumes = new File(sdPath+"/"+bookPath).listFiles();
            searchVolumeFolder(volumes);

            lv = (ListView) findViewById(R.id.booklist);
            lv_r = (ListView) findViewById(R.id.books);

            //adapterのインスタンスを作成
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, bookList);
            adapter_r = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, volumeList);

            lv.setAdapter(adapter);
            lv_r.setAdapter(adapter_r);

            // 左側のリストをタップ
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView listView = (ListView) parent;
                    bookPath = (String) listView.getItemAtPosition(position);
                    showItem(bookPath);

                    volumes = new File(sdPath+"/"+bookPath).listFiles();
                    volumeList.clear();
                    searchVolumeFolder(volumes);
                    adapter_r.notifyDataSetChanged();     //Adapterに変更を通知
                    Log.d("myTag", "T:"+bookPath );
                }
            });

            // 右側のリストをタップ
            lv_r.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView listView = (ListView) parent;
                    String item = (String) listView.getItemAtPosition(position);
                    showItem(item);

                    Intent intent = new Intent(getApplication(), BookActivity.class);
                    String PD = sdPath+"/"+bookPath+"/"+ artist+item;

                    intent.putExtra("PATH_DATA", PD);
                    //intent.putExtra(PATH_DATA, sdPath+"/"+bookPath+"/"+item);
                    startActivity(intent);

                    Log.d("myTag", "関数:"+PD);
                }
            });

        }

    }

    // 作品を探す
    private void searchBookFolder(File[] f){
        for(int i = 0; i < f.length; i++){
            if (f[i].isFile() == false){
                // フォルダを作品として登録
                bookList.add(f[i].getName());
                Log.d("myTag", "bookList["+ i +"] = "+ f[i].getName() );
            }
        }
    }

    // 本の巻を探す
    private void searchVolumeFolder(File[] f){
        String str_temp[] ;
        for(int i = 0; i < f.length; i++){
            if (f[i].isFile() == false){
                // 作者名と作品名を[]で分離
                str_temp = f[i].getName().split(Pattern.quote("]"), 0);
                if (str_temp.length > 1){
                    // フォルダを巻として登録
                    artist = str_temp[0]+"]";
                    volumeList.add(str_temp[1]);
                    //volumeList.add(f[i].getName());
                } else {
                    artist = "";
                    volumeList.add(f[i].getName());
                }
                Log.d("myTag", "volumeList["+ i +"] = "+ f[i].getName() );
            }
        }
    }

    public void showItem(String str){
        Toast.makeText(this, "ファイル名:" + str, Toast.LENGTH_SHORT).show();
    }
}


    /*
    //再帰的にディレクトリ内を調べるメソッド
    private void searchMusicFiles(File[] f){
        for(int i = 0; i < files.length; i++){
            if (files[i].isFile()){
                if(files[i].getName().endsWith(".mp3") || files[i].getName().endsWith(".m4a") || files[i].getName().endsWith(".flac")){
                    songList.add(files[i].getName());
                    Log.d("myTag", "songList["+ i +"] = "+ files[i].getName() );
                }
            } else {
                //searchMusicFiles(files[i]); // 再帰
            }
        }
    }*/
    /*
    private void searchVideoFiles(File[] f){
        for(int i = 0; i < files.length; i++){
            if (files[i].isFile()){
                if(files[i].getName().endsWith(".mp4") || files[i].getName().endsWith(".avi") || files[i].getName().endsWith(".mkv")){
                    //movieList.add(files[i].getName());
                    Log.d("myTag", "songList["+ i +"] = "+ songList.get(i) );
                }
            } else {
                //searchMusicFiles(files[i]); // 再帰
            }
        }
    }*/
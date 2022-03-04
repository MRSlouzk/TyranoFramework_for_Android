package jp.tyrano.tyranoplayerframework;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.app.Activity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainActivity extends Activity  {

    private WebView webview ;

    private String base_url ;
    private String base_path ;

    private boolean flag_init = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ステータスバー削除
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        this.webview = (WebView)this.findViewById(R.id.webview_playgame);

        //画面を入れ替えてもリロードさせない
        if(savedInstanceState != null){
            this.webview.restoreState(savedInstanceState);
            return;
        }

        File dataDir = this.getFilesDir();

        final String file_url = "file:///android_asset/index.html";
        final String base_url = "file:///android_asset/";;

        this.base_url = base_url;

        String userAgent = this.webview.getSettings().getUserAgentString();
        this.webview.getSettings().setUserAgentString(userAgent + ";tyranoplayer-android-1.0");
        this.webview.getSettings().setJavaScriptEnabled(true);
        this.webview.getSettings().setAllowFileAccessFromFileURLs(true);
        this.webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        this.webview.getSettings().setDomStorageEnabled(true);
        this.webview.getSettings().setMediaPlaybackRequiresUserGesture(false);
        this.webview.getSettings().setUseWideViewPort(true);
        this.webview.clearCache(true);

        MyJavaScriptInterface obj = new MyJavaScriptInterface(this,this.base_path);
        this.webview.addJavascriptInterface(obj,"appJsInterface");



        try {

                BufferedReader reader ;
                reader = new BufferedReader(new InputStreamReader(getAssets().open("index.html"), "UTF-8"));
                startGame(reader);

        }catch (Exception e) {
            System.out.println("ERROROROROR");
            System.out.print(e.toString());
        }



        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }

            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
                Log.d("dddd",request.toString());

                //エラーが出るので追加した。
                return false;
            }
        });


    }



    @Override
    public void onResume() {

        super.onResume();
        if(this.flag_init ==true) {
            this.webview.loadUrl("javascript:_tyrano_player.resumeAllAudio();");
            this.webview.onResume();
        }else {
            this.flag_init = true;
        }
    }

    @Override
    public void onPause() {

        super.onPause();
        this.webview.loadUrl("javascript:_tyrano_player.pauseAllAudio();");
        this.webview.onPause();
    }

    @Override
    public void onDestroy() {
        if (webview != null) {
            webview.destroy();
            webview = null;
        }
        super.onDestroy();
    }

    //ゲーム開始用
    public void startGame(BufferedReader reader){

        System.out.println("start game!!");

        try{


            String s = "";

            StringBuffer et = new StringBuffer();
            String data;
            while ((data = reader.readLine()) != null) {

                if(data.indexOf("</head>")!=-1){

                    //外部からJSファイルを読み込む
                    et.append("<script type='text/javascript' src='file:///android_asset/tyrano_player.js'></script>");
                    et.append("\n");
                    et.append("</head>");


                }else {
                    et.append(data);
                }

                et.append("\n");
            }

            s = et.toString();

            final String html_str = s;

            System.out.println(html_str);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webview.loadDataWithBaseURL(base_url, html_str, "text/html", "UTF-8",null);
                }
            });

        }catch(Exception e){
            System.out.print("erroror");
            System.out.print(e.toString());
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        this.webview.saveState(outState);
    }
    @Override
    public void onRestoreInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setStorage(String key, String val){

        File dataDir = this.getFilesDir();
        String localPath = dataDir.getAbsolutePath()+ "/";
        //プロジェクトのパスを取得する。
        String save_path = localPath + key +".sav";

        try{

            File file = new File(save_path);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file,false)));
            pw.println(val);
            pw.close();

        }catch(IOException e){
            System.out.println(e);
        }

    }

    public String getStorage(String key){

        File dataDir = this.getFilesDir();
        String localPath = dataDir.getAbsolutePath()+ "/";
        //プロジェクトのパスを取得する。
        String save_path = localPath + key +".sav";

        //ファイルの存在チェック
        String result_str ="";

        try{
            File file = new File(save_path);

            if(!file.exists()){

                System.out.println("file not found!");
                return "";
            }

            BufferedReader br = new BufferedReader(new FileReader(file));

            String str ;
            while((str = br.readLine()) != null){
                result_str += str;
            }

            br.close();
        }catch(FileNotFoundException e){
            System.out.println(e);
        }catch(IOException e){
            System.out.println(e);
        }

        return result_str;
    }

    public void finishGame(){

        final Context context = this;
        this.runOnUiThread(new Runnable() {
            public void run() {

                //本当に戻って良いですか
                new AlertDialog.Builder(context)
                        .setTitle("確認")
                        .setMessage("タイトルに戻ります。よろしいですね？セーブしていない場合、状態は破棄されます。")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // OK が押された
                                finish();
                                startActivity(getIntent());
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }});

    }

    public void openUrl(String url){

        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));

    }



}

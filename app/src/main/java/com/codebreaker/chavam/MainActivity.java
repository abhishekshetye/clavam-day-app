package com.codebreaker.chavam;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    String TAG = "FIRbase";
    List<Video> videos;
    private Button btn, first, newvideo;
    private SharedPreferences prefs;
    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.secondbtn);
        newvideo = (Button) findViewById(R.id.newvideo);
        first = (Button) findViewById(R.id.button2);

        //shared prefs
        prefs = getSharedPreferences("MYPREFS", MODE_PRIVATE);
        size = prefs.getInt("SIZE", 0);



        //ui changes
        newvideo.setVisibility(View.INVISIBLE);
        first.setText(prefs.getString("FN", "first"));
        btn.setText(prefs.getString("SN", "second"));


        //analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("user", "user");
        bundle.putString("opened", "yes");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

        SharedPreferences.Editor editor = prefs.edit();
        int c = prefs.getInt("OPENED", 1) + 1;
        editor.putInt("OPENED", c);
        editor.commit();


        //open counter
        updateTimes();

        if(size <= 1){
            btn.setVisibility(View.INVISIBLE);
        }


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("videos");

        videos = new ArrayList<>();
        final Query myTopPostsQuery = myRef.orderByValue();
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot video: dataSnapshot.getChildren()) {
                    Map<String, Object> mapObj = (Map<String, Object>) video.getValue();
                        Log.d("FIR", mapObj.get("name") + " ");
                        videos.add(new Video((String) mapObj.get("name"), (String) mapObj.get("url")));
                }

                if(videos.size() > size){
                    //newvideo.setVisibility(View.VISIBLE);
                }

                // myTopPostsQuery.removeEventListener(this);

                if(videos.size() >= 2)
                Log.d("FIR", "secondLast ->  " + videos.get(videos.size()-2).getName());

                if(videos.size() >= 1)
                Log.d("FIR", "last -> "  + videos.get(videos.size()-1).getName());

                first.setText(videos.get(videos.size()-1).getName());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("FT", videos.get(videos.size()-1).getUrl());
                editor.putString("FN",videos.get(videos.size()-1).getName() );
                editor.putInt("SIZE", videos.size());

                if(videos.size() < 2){
                    btn.setVisibility(View.INVISIBLE);
                }else{
                    btn.setVisibility(View.VISIBLE);
                    btn.setText(videos.get(videos.size()-2).getName());
                    editor.putString("SC", videos.get(videos.size()-2).getUrl());
                    editor.putString("SN",videos.get(videos.size()-2).getName() );
                }

                editor.commit();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void updateTimes(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        String key = prefs.getString("KEY", "key");
        DatabaseReference newRef = database.child("users").child(key);
        DatabaseReference statusRef = newRef.child("opened");
        statusRef.setValue(prefs.getInt("OPENED", 0));
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(!isFinishing()){
//            Toast.makeText(this, "Not finishing", Toast.LENGTH_SHORT).show();
//            int c = prefs.getInt("OPENED",0);
//            c--;
//            prefs.edit().putInt("OPENED",c).commit();
//        }
    }

    public void newVideo(View v){
        String n;
        File file;

        n = URLUtil.guessFileName(prefs.getString("FT", "url"), null, null);
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Clavam/" + n);

        if(file.exists()){
            Log.d("FILE", "Exists");
            runVideo(n);
        }
        else{
            if(!isNetworkAvailable(this)){
                Toast.makeText(this, "Internet not available.", Toast.LENGTH_SHORT).show();
                return;
            }
            download(prefs.getString("FT", "url"));
            Toast.makeText(this, "Downloading file.. Please wait..", Toast.LENGTH_SHORT).show();
            Log.d("FILE", "Does not exists");
        }

    }



    public void newClick(View v) {
        String n;
        File file;

        n = URLUtil.guessFileName(prefs.getString("FT", "url"), null, null);
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Clavam/" + n);

        if(file.exists()){
            Log.d("FILE", "Exists");
            runVideo(n);
        }
        else{
            if(!isNetworkAvailable(this)){
                Toast.makeText(this, "Internet not available.", Toast.LENGTH_SHORT).show();
                return;
            }
            download(prefs.getString("FT", "url"));
            Toast.makeText(this, "Downloading file.. Please wait..", Toast.LENGTH_SHORT).show();
            Log.d("FILE", "Does not exists");
        }
    }


    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void click(View v){
        String n;
        File file;

        n = URLUtil.guessFileName(prefs.getString("SC", "url"), null, null);
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Clavam/" + n);

        if(file.exists()){
            Log.d("FILE", "Exists");
            runVideo(n);
        }
        else{
            if(!isNetworkAvailable(this)){
            Toast.makeText(this, "Internet not available.", Toast.LENGTH_SHORT).show();
            return;
            }
            download(prefs.getString("SC", "url"));
            Toast.makeText(this, "Downloading file.. Please wait..", Toast.LENGTH_SHORT).show();
            Log.d("FILE", "Does not exists");
        }
    }


    private void runVideo(String name){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Clavam/ " + name));
        intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Clavam/" + name ), "video/*");
        startActivity(intent);
    }


    private void download(String url){
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            String n = URLUtil.guessFileName(url, null, null);
            request.setDescription(n);
            request.setTitle("Downloading video");
            // in order for this if to run, you must use the android 3.2 to compile your app
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }


            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Clavam/" + n;
            request.setDestinationInExternalPublicDir("/Clavam", n);

            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        }catch(Exception e){
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }

    }

}

package com.codebreaker.chavam;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by abhishek on 3/16/17.
 */

public class CheckDownloadComplete extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        String action = intent.getAction();
        if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor cursor = manager.query(query);
            if (cursor.moveToFirst()) {
                if (cursor.getCount() > 0) {

                    int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    Long download_id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,0);

                    // status contain Download Status
                    // download_id contain current download reference id

                    if (status == DownloadManager.STATUS_SUCCESSFUL)
                    {
                        String file = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                        Log.d("RECEIVER", "Sucessfully downloaded " + file );

                        SharedPreferences prefs = context.getSharedPreferences("MYPREFS", Context.MODE_PRIVATE);
                        String fname = prefs.getString("FIRST_NAME", "sdfa");
                        String sname = prefs.getString("SECOND_NAME", "asdf");

                        if(file.contains(fname)){
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("FIRST_PATH", file);
                        }else{
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("SECOND_PATH", file);
                        }


//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putString("")
                        //file contains downloaded file name

                        // do your stuff here on download success

                    }
                }
            }
            cursor.close();
        }
    }
}
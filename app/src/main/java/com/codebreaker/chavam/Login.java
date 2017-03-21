package com.codebreaker.chavam;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    private EditText me, hq, mob;
    private SharedPreferences prefs;

    String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE};


    private static final int PERMISSION_WRITE_REQUEST_CODE = 4;
    private static final int PERMISSION_LOCATION_REQUEST_CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        prefs = getSharedPreferences("MYPREFS", MODE_PRIVATE);
        if(prefs.getBoolean("FIRST", false)){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
            return;
        }



        me = (EditText) findViewById(R.id.me);
        hq = (EditText) findViewById(R.id.hq);
        mob = (EditText) findViewById(R.id.mob);


        askPermissions();

    }



    private void askPermissions() {
        int PERMISSION_ALL = 1;

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


        switch (requestCode) {
            case PERMISSION_WRITE_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;

            case PERMISSION_LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;

        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public void login(View view){

        if(!hasPermissions(this, PERMISSIONS)){
            Toast.makeText(this, "Grant permissions first", Toast.LENGTH_SHORT).show();
            askPermissions();
            return;
        }


        if(me.getText()==null || me.getText().toString().equals(" ")){
            Toast.makeText(this, "Invalid me", Toast.LENGTH_SHORT).show();
            return;
        }


        if(mob.getText()==null || mob.getText().toString().length() != 10){
            Toast.makeText(this, "Invalid mobile number", Toast.LENGTH_SHORT).show();
            return;
        }

        if(hq.getText()==null || hq.getText().toString().equals(" ")){
            Toast.makeText(this, "Invalid headquarter", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newRef = database.child("users").push();
        newRef.setValue(new User(me.getText().toString(), mob.getText().toString(), hq.getText().toString()));
        Toast.makeText(this, "Successfully registered", Toast.LENGTH_SHORT).show();
        String key = newRef.getKey();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("FIRST", true);
        editor.putString("KEY", key);
        editor.commit();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
        return;
    }

}

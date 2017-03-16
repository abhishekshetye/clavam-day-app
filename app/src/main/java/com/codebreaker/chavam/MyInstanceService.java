package com.codebreaker.chavam;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyInstanceService extends FirebaseInstanceIdService {

    public MyInstanceService() {

    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("NOTIF", "Refreshed token: " + refreshedToken);
    }



}

package com.accengage.privaliatest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ad4screen.sdk.A4S;
import com.ad4screen.sdk.GCMHandler;

public class MyGCMHandler extends GCMHandler {

    /*@Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
    }*/

    @Override
    protected void processPush(final Context context, final Intent intent) {
        android.util.Log.d("MyGCMHandler", "GCM message is received");
        if (A4S.get(context).isAccengagePush(context, intent)) {

            Bundle payload = intent.getExtras();
            final String id = payload.getString("a4sid"); // ID de push message
            final int systemId = Integer.parseInt(payload.getString("a4ssysid")); // System ID de notification to display

            A4S.get(context).getIDFV(new A4S.Callback<String>() {
                @Override
                public void onResult(String s) {
                    String IDFV = s != null ? s : "null"; // Device Profile ID
                    android.util.Log.d("MyGCMHandler", "Push id=" + id + ", systemId=" + systemId + " is received for " + IDFV);
                    // TODO Send your event over Google Analytics or Firebase, etc...
                }
                @Override
                public void onError(int i, String s) {
                    android.util.Log.d("MyGCMHandler", "Push id=" + id + ", systemId=" + systemId + " is received, but IDFV is not detected");
                    // TODO normally should never happen, but send as an event too
                }
            });

            // Process push and display it
            processA4SPush(context, intent);
        } else {
            //This push must be dispatched elsewhere
            onPushReceive(context, intent);
        }
    }
}

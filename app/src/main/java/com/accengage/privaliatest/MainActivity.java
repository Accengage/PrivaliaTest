package com.accengage.privaliatest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.ad4screen.sdk.A4S;
import com.ad4screen.sdk.Constants;
import com.ad4screen.sdk.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SwitchCompat pushEnabled = (SwitchCompat) findViewById(R.id.pushEnableSW);

        A4S.get(this).isPushEnabled(new A4S.Callback<Boolean>() {
            @Override
            public void onResult(Boolean aBoolean) {
                Log.debug("Callback is received, isPushEnabled " + aBoolean);
                pushEnabled.setChecked(aBoolean);

                pushEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {

                        A4S.get(MainActivity.this).getPushToken(new A4S.Callback<String>() {
                            @Override
                            public void onResult(String token) {
                                Log.debug("Callback is received, getPushToken: " + token);
                                if (isChecked) {
                                    if (token != null) {
                                        // The previous token was not erased, try to check again after a small pause
                                        Log.warn("Push token is not cleaned");
                                        return;
                                    }
                                } else {
                                    if (token == null) {
                                        // There is no token, try to check again after a small pause
                                        Log.warn("Push was enabled but there is no valid token");
                                        return;
                                    }

                                }
                                // Subscribe/Unsubscribe for/from GCM messages (obtain/delete token)
                                A4S.get(MainActivity.this).setPushEnabled(isChecked);
                                updateSystemOptinNotif(isChecked);
                            }

                            @Override
                            public void onError(int i, String s) {
                                Log.debug("Callback is received, getPushToken error: " + s);
                                pushEnabled.setChecked(!isChecked); // do not change the value in case of errors
                            }
                        });
                    }
                });
            }
            @Override
            public void onError(int i, String s) {
                Log.error("Callback is received, isPushEnabled error: " + s);
            }
        });

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        A4S.get(this).setIntent(intent);
        Log.debug("acc onNewIntent");

        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Log.debug("acc onNewIntent getExtras");
                Bundle bundleA4S = bundle.getBundle(Constants.EXTRA_GCM_PAYLOAD);
                if (bundleA4S != null) {
                    Log.debug("acc onNewIntent url parameter: " + bundleA4S.getString("url"));
                }
            }
        } else {
            Log.debug("acc onNewIntent no intent");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        A4S.get(this).startActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        A4S.get(this).stopActivity(this);
    }

    private void updateSystemOptinNotif(boolean enabled) {
        Bundle bundle = new Bundle();
        bundle.putString("system_optin_notifs", enabled ? "Y" : "N");
        A4S.get(this).updateDeviceInfo(bundle);
    }

}

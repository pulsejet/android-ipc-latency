package com.lrptest.client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.lrptest.daemon.ILrpBoundService;

public class MainActivity extends AppCompatActivity {

    final String TAG = "LRP_LOG_CLIENT";
    LrpClient lrpClient = new LrpClient();
    ILrpBoundService mBoundService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "Start time (system): " + System.currentTimeMillis() + " ms");

        findViewById(R.id.intent_toast_btn).setOnClickListener(v -> {
            Log.i(TAG, "toast()");
            Intent it = new Intent("com.lrptest.daemon.toast");
            getBaseContext().sendBroadcast(putMeta(it));
        });

        findViewById(R.id.intent_measure_btn).setOnClickListener(v -> {
            Log.i(TAG, "measure()");
            Intent it = new Intent("com.lrptest.daemon.measure");
            getBaseContext().sendBroadcast(putMeta(it));
        });

        findViewById(R.id.udp_hello_btn).setOnClickListener(v -> {
            Log.i(TAG, "udphello()");
            lrpClient.measure();
        });

        findViewById(R.id.aidl_measure_btn).setOnClickListener(v -> {
            if (mBoundService != null) {
                try {
                    mBoundService.measure(System.nanoTime());
                } catch (RemoteException e) {
                    Toast.makeText(getApplicationContext(), "Service Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Service not connected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i(TAG, "Initiating Bind");
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.lrptest.daemon","com.lrptest.daemon.LrpService"));
        bindService(intent, boundServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public Intent putMeta(Intent it) {
        it.putExtra("toast", ((Switch) findViewById(R.id.toast_switch)).isChecked());
        it.putExtra("time", System.currentTimeMillis());
        it.putExtra("nanoTime", System.nanoTime());
        return it;
    }

    private final ServiceConnection boundServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i(TAG, "LRP service connected");
            mBoundService =  ILrpBoundService.Stub.asInterface(service);
            Toast.makeText(getApplicationContext(), "AIDL service connected", Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "LRP service disconnected");
            mBoundService = null;
            Toast.makeText(getApplicationContext(), "AIDL service disconnected", Toast.LENGTH_SHORT).show();
        }
    };
}
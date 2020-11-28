package com.lrptest.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lrptest.daemon.ILrpBoundService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    final String TAG = "LRP_LOG_CLIENT";
    LrpClient lrpClient = new LrpClient();
    ILrpBoundService mBoundService = null;

    ScheduledExecutorService ses = Executors.newScheduledThreadPool(4);

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

        findViewById(R.id.measure_ping).setOnClickListener(v -> {
            ping();
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

    private boolean ping() {
        String ip = ((EditText)findViewById(R.id.ip_address)).getText().toString();
        String port = ((EditText)findViewById(R.id.port)).getText().toString();
        int sleep = Integer.parseInt(((EditText) findViewById(R.id.send_after_millis)).getText().toString());
        String cStr = ip + ":" + port;

        long scheduleStartTime = System.nanoTime();

        Runnable runnable = () -> {
            long scheduleEndTime = System.nanoTime();

            long startTime = System.nanoTime();
            boolean result = isReachable(ip, Integer.parseInt(port), 2000);
            long endTime = System.nanoTime();

            long time = (endTime - startTime) / 1000;
            final String msg = result ? "ping(): " + time + " us" : "ping(): timeout " + cStr;
            Log.i(TAG, msg);

            Log.i(TAG, "ping(): scheduling delay: " +
                    ((scheduleEndTime - scheduleStartTime) / 1000 - (sleep * 1000)) + " us");

            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            });
        };

        ses.schedule(runnable, sleep, TimeUnit.MILLISECONDS);
        return true;
    }

    private static boolean isReachable(String addr, int openPort, int timeOutMillis) {
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
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
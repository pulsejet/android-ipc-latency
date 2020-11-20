package com.lrptest.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    final String tag = "LRP_LOG_CLIENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(tag, "Start time (system): " + System.currentTimeMillis() + " ms");

        findViewById(R.id.intent_toast_btn).setOnClickListener(v -> {
            Log.i(tag, "toast()");
            Intent it = new Intent("com.lrptest.daemon.toast");
            getBaseContext().sendBroadcast(putMeta(it));
        });

        findViewById(R.id.intent_measure_btn).setOnClickListener(v -> {
            Log.i(tag, "measure()");
            Intent it = new Intent("com.lrptest.daemon.measure");
            getBaseContext().sendBroadcast(putMeta(it));
        });
    }

    public Intent putMeta(Intent it) {
        it.putExtra("toast", ((Switch) findViewById(R.id.toast_switch)).isChecked());
        it.putExtra("time", System.currentTimeMillis());
        it.putExtra("nanoTime", System.nanoTime());
        return it;
    }

}
package com.lrptest.daemon;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> results = new ArrayList<String>();

        results.add("Start time (native): " + getNativeTime() + " us");
        results.add("Start time (system): " + getTime() + " us");

        long jniOverhead = getJNIOverhead();
        results.add("JNI overhead: " + jniOverhead + " us per call");

        long clockOffset = getTime() * 1000 - getNativeTime();
        results.add("Clock offset: " + clockOffset + " us");

        results.forEach((s) -> {
            Log.i("LRP_DAEMON", s);
        });

        ((TextView)findViewById(R.id.result_text)).setText(String.join("\n", results));
    }

    public long getTime() {
        return System.currentTimeMillis();
    }

    public int getJNIOverhead() {
        final int iterations = 1000;
        int totalTime = 0;

        for (int i = 0; i < iterations; i++) {
            long time1 = getNativeTime();
            long time2 = getNativeTime();

            totalTime += (time2 - time1);
        }

        return totalTime / iterations / 2;
    }

    public native long getNativeTime();
}
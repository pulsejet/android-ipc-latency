package com.lrptest.daemon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LrpHandler {
    final static String TAG = "LRP_DAEMON";

    public static void handleIntent(Intent intent) {
        handleIntent(intent, null);
    }

    public static void handleIntent(Intent intent, Context context) {
        final Bundle bundle = intent.getExtras();
        final boolean toast = context != null && bundle.getBoolean("toast", false);

        switch (intent.getAction().toLowerCase()) {
            case "com.lrptest.daemon.toast":
                if (toast)
                    Toast.makeText(context, "Toast from daemon", Toast.LENGTH_SHORT).show();
                break;

            case "com.lrptest.daemon.measure":
                final long sendMicroTime = bundle.getLong("nanoTime") / 1000;

                long nTime = doNativeWork();
                long t2 = System.nanoTime() / 1000;

                final String message = "measure(): " + (t2 - nTime - sendMicroTime) + " us";
                Log.i(TAG, message);

                if (toast)
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public static native long getNativeTime();
    public static native long doNativeWork();
}

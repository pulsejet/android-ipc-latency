package com.lrptest.daemon;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LrpClient extends Thread {
    public final String TAG = "LRP_LOG_CLIENT";
    private DatagramPacket packet;
    private boolean active = false;

    LrpClient() {
        start();
    }

    @Override
    public void run() {
        DatagramSocket clientSocket = null;
        try {
            clientSocket = new DatagramSocket();
        } catch (IOException e) {
            Log.e(TAG, "Failed to create DatagramSocket: " + e.getMessage());
            return;
        }

        active = true;

        while (true) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    return;
                }

                if (packet != null) {
                    Log.d(TAG, "Sending LRP packet");
                    try {
                        clientSocket.send(packet);
                        packet = null;
                    } catch (IOException e) {
                        Log.e(TAG, "Sending LRP packet failed");
                    }
                }
            }
        }
    }

    public boolean measure() {
        if (!active) return false;

        byte[] sendData = Long.toString(System.nanoTime()).getBytes();
        InetAddress IPAddress = InetAddress.getLoopbackAddress();
        packet = new DatagramPacket(sendData, sendData.length, IPAddress, 15113);
        packet.setLength(sendData.length);

        synchronized (this) {
            notify();
        }

        return true;
    }
}
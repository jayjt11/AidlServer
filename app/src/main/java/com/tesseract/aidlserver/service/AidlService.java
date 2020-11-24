package com.tesseract.aidlserver.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;

import com.tesseract.aidlinterface.AidlInterface;

public class AidlService extends Service {

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }
    public float pitch = 0.00f;
    public float roll =  0.00f;

    public static final String ACTION = "com.tesseract.aidlserver.TESSERACT";

    @Override
    public void onCreate() {
        super.onCreate();

        setPitch(0.00f);
        setRoll(0.00f);
        registerReceiver(myBroadcastReceiverData, new IntentFilter(ACTION));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    AidlInterface.Stub binder = new AidlInterface.Stub() {

        @Override
        public float[] getOrientationData() {
            float values[] = new float[2];
            values[0] = getPitch();
            values[1] = getRoll();
            return values;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (myBroadcastReceiverData != null) {

            unregisterReceiver(myBroadcastReceiverData);
        }
    }

    private BroadcastReceiver myBroadcastReceiverData = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(ACTION)){

                float pitch = intent.getFloatExtra("pitch",0.00f);
                float roll = intent.getFloatExtra("roll",0.00f);
                setPitch(pitch);
                setRoll(roll);
            }
        }
    };
}

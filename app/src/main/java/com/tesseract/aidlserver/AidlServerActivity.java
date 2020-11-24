package com.tesseract.aidlserver;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AidlServerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mRotationSensor;

    private static final int SENSOR_DELAY = 8 * 1000; // 8ms
    private static final int FROM_RADS_TO_DEGS = -57;

    float pitch;
    float roll;

    public static final String ACTION = "com.tesseract.aidlserver.TESSERACT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        try {
            mSensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
            mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
        } catch (Exception e) {
            Toast.makeText(this, "Hardware compatibility issue", Toast.LENGTH_LONG).show();
        }
    }
    // Sensor Methods

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor == mRotationSensor) {
            if (sensorEvent.values.length > 4) {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(sensorEvent.values, 0, truncatedRotationVector, 0, 4);
                update(truncatedRotationVector);
            } else {
                update(sensorEvent.values);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void update(float[] vectors) {

        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);

        setPitch(orientation[1] * FROM_RADS_TO_DEGS);
        setRoll(orientation[2] * FROM_RADS_TO_DEGS);
        onResponse(getPitch(), getRoll());
    }

    private void onResponse(float pitch, float roll) {
        Intent intent = new Intent();
        intent.setAction(ACTION);
        intent.putExtra("pitch", pitch);
        intent.putExtra("roll", roll);
        sendBroadcast(intent);
    }

    // getter setter

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
}

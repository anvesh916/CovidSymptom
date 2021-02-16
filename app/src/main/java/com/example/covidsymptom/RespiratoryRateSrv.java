package com.example.covidsymptom;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

public class RespiratoryRateSrv extends Service implements SensorEventListener {
    public RespiratoryRateSrv() {
    }

    private SensorManager accelManage;
    private ResultReceiver mResultReceiver;
    public static final int DURATION = 45; //seconds
    public static final int FREQUENCY = 10; // 10 samples per seconds
    public static final int READING_RATE = 1000 * 1000 / FREQUENCY;
    public static final int NO_OF_SAMPLES = DURATION * (FREQUENCY + 2); // Receiving two samples extra

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        accelManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = accelManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManage.registerListener(this, accelerometer, READING_RATE);
        this.setResultReceiver(intent.getParcelableExtra(Intent.EXTRA_RESULT_RECEIVER));
        return START_NOT_STICKY;
    }

    int index = 0;
    public static final float epsilon = 19;
    private float[] z = new float[NO_OF_SAMPLES];
    private final float[] diff = new float[NO_OF_SAMPLES];
    private int peak = 0;
    private float prev;

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (index < NO_OF_SAMPLES) {
            float curr = (event).values[2];
            if (index > 0) {
                diff[index] = (100 * (curr - prev));
                if ((diff[index - 1] < epsilon) && (diff[index] > epsilon)) {
                    peak = peak + 1;
                }
            } else {
                // When index is 0
                diff[index] = 0;
            }
            prev = curr;
            Log.d("Accelerometer Diff", String.valueOf(diff[index]));
            printData(peak);
            index++;
        } else {
            index = 0;
            printData((peak * 60) / (2 * DURATION));
            accelManage.unregisterListener(this);
            mResultReceiver.send(MainActivity.RESULT_CANCELED, null);
        }
    }


    void printData(int data) {
        Bundle bundle = new Bundle();
        bundle.putString("Result", Integer.toString(data));
        mResultReceiver.send(MainActivity.RESULT_OK, bundle);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        mResultReceiver.send(MainActivity.RESULT_CANCELED, null);
        accelManage.unregisterListener(this); //
    }

    public void setResultReceiver(ResultReceiver mResultReceiver) {
        this.mResultReceiver = mResultReceiver;
    }
}
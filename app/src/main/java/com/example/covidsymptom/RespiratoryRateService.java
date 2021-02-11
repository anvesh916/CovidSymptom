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
import android.widget.Toast;

public class RespiratoryRateService extends Service implements SensorEventListener {
    public RespiratoryRateService() {
    }

    private SensorManager accelManage;
    private ResultReceiver mResultReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int READING_RATE = 10 * 1000;

        accelManage = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = accelManage.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelManage.registerListener(this, accelerometer, READING_RATE); // 10ms
        this.setResultReceiver((ResultReceiver) intent.getParcelableExtra(Intent.EXTRA_RESULT_RECEIVER));
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    int data = 0;
    int index = 0;
    float prevData = 0;
    int respiratons = 0;
    @Override
    public void onSensorChanged(SensorEvent event) {
        data = (int) ((event.values[2] - prevData) * 100);
        prevData = (event).values[2];
        index++;
        if (Math.abs(data) > 30) {
            respiratons++;
            Bundle bundle = new Bundle();
            bundle.putString("Z", Integer.toString((data)));
            bundle.putString("R", Integer.toString((respiratons)));
            mResultReceiver.send(MainActivity.RESULT_OK, bundle);
        }
        if (index >= 100 * 60) {
            mResultReceiver.send(MainActivity.RESULT_CANCELED, null);
            accelManage.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onDestroy() {
        data = 0;
        index = 0;
        prevData = 0;
        respiratons = 0;
        mResultReceiver.send(MainActivity.RESULT_CANCELED, null);
        accelManage.unregisterListener(this); //
        Toast.makeText(this, "Calculation Done", Toast.LENGTH_SHORT).show();
    }

    public void setResultReceiver(ResultReceiver mResultReceiver) {
        this.mResultReceiver = mResultReceiver;
    }

}
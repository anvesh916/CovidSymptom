package com.example.covidsymptom;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyMainActivity";
    private static final int VIDEO_CAPTURE = 1;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 1996;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 112;
    public static final String FILE_NAME = "myvideo.mp4";

    private Uri uriForFile;
    private Intent symptomActivity;
    private Intent respirationService;
    private DataBaseHelper dataBaseHelper;
    public int respirationRate;
    public int heartRate;

    HeartRateTask hrt;


    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    break;
                default: {
                    super.onManagerConnected(status);
                    break;
                }
            }

        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        symptomActivity = new Intent(this, SymptomActivity.class);

        // Respiratory rate service
        respirationService = new Intent(getApplicationContext(), RespiratoryRateSrv.class);
        ResultReceiver resultReceiver = new RespirationResultReceiver(null);
        respirationService.putExtra(Intent.EXTRA_RESULT_RECEIVER, resultReceiver);

        dataBaseHelper = new DataBaseHelper(MainActivity.this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "Open CV is installed");
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        } else {
            Log.d(TAG, "Open CV is not installed");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, baseLoaderCallback);
        }
    }

    public void navToSymptomActivity(View view) {
        startActivity(symptomActivity);
    }

    public void measureHeartRate(View view) {
        this.setParametersForHR("Measurement Started", false);
        this.preInvokeCamera();
      //  this.startCalculation();
    }

    private void preInvokeCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            this.startRecording();
        } else {
            String[] permissionReq = {Manifest.permission.CAMERA};
            requestPermissions(permissionReq, CAMERA_PERMISSION_REQUEST_CODE);

            String[] permissionReq2 = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissionReq2, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Ensure that this result is for the camera permission request
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // Check if the request was granted or denied
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                this.startRecording();
            } else {
                // The request was denied -> tell the user and exit the application
                Toast.makeText(this, "Camera permission required.",
                        Toast.LENGTH_LONG).show();
                this.finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void startRecording() {
        File videoPath = getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
        File mediaFile = new File(videoPath, FILE_NAME);
        uriForFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", mediaFile);

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 45);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
        takeVideoIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == VIDEO_CAPTURE && resultCode == RESULT_OK) {
            this.startCalculation();

        } else {
            this.setParametersForHR("Measurement Cancelled", true);
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void startCalculation() {
        this.setParametersForHR("Video Recorded", false);

        // Set video in the media player
        MediaController m = new MediaController(this);
        VideoView videoView = findViewById(R.id.video_preview);
        videoView.setMediaController(m);
        videoView.setVideoURI(uriForFile);

        hrt = new HeartRateTask();
        hrt.execute(uriForFile);

    }

    private class HeartRateTask extends AsyncTask<Uri, Integer, Integer> {

        protected Integer doInBackground(Uri... url) {
            float totalred = 0;
            int peak = 0;
            int totalTimeMilli = 0;
            try {

                File videoPath = getExternalFilesDir(Environment.getStorageDirectory().getAbsolutePath());
                File videoFile = new File(videoPath, FILE_NAME);
                Uri videoFileUri = Uri.parse(videoFile.toString());
                MediaPlayer mp = MediaPlayer.create(getBaseContext(), videoFileUri);
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(videoFile.getAbsolutePath());

                totalTimeMilli = mp.getDuration(); //milli seconds
                int second = 1000000; //
                int imgSize = 100; // Size of the box
                int rate = 4; //number of samples per sec
                int recordingDuration = (int) Math.floor(totalTimeMilli / 1000) * second; //rounding to the nearest second

                int w = 0;
                int h = 0;
                int j = 0;
                float[] diff = new float[imgSize * imgSize];
                float epsilon = 100;
                float prev = 0;
                int no_of_frames = (totalTimeMilli * rate) / 1000;
                for (int i = rate; i <= recordingDuration; i += second / rate) {
                    Bitmap bitmap = retriever.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

                    //get image size only once
                    if (w == 0 || h == 0) {
                        w = bitmap.getWidth();
                        h = bitmap.getHeight();
                    }
                    totalred = 0;
                    // Center box of the image
                    for (int x = (w - imgSize); x < w; x++)
                        for (int y = (h - imgSize); y < h; y++) {
                            totalred += Color.red(bitmap.getPixel(x, y));
                        }

                    if (j > 0) {
                        diff[j] = Math.abs(totalred - prev);
                        if ((diff[j - 1] < epsilon) && (diff[j] > epsilon)) {
                            peak = peak + 1;
                        }
                    } else {
                        // When index is 0
                        diff[j] = 0;
                    }
                    prev = totalred;
                    Log.d("ASYNC", "" + diff[j]);
                    j += 1;
                    onProgressUpdate(j, no_of_frames);
                }
                retriever.release();
            } catch (Exception e) {
                return 0;
            }
            return (peak * 60 * 1000) / totalTimeMilli;
        }

        protected void onProgressUpdate(Integer progress, int frames) {
            setParametersForHR("Processing frames " + Integer.toString(progress) + "/" + frames , false);
        }

        protected void onPostExecute(Integer result) {
            heartRate = Math.round(result);
            setParametersForHR("Heart Rate is " + heartRate, true);
        }
    }


    public void stopHRMeasurement(View view) {
        hrt.cancel(true);
        this.setParametersForHR("Measurement cancelled by user", true);
    }

    void setParametersForHR(String displayText, boolean isDone) {
        Button stopButton = findViewById(R.id.stopHRMeasuring);
        stopButton.setVisibility(isDone ? View.INVISIBLE : View.VISIBLE);

        Button hrMeasure = findViewById(R.id.heartRateButton);
        hrMeasure.setVisibility(isDone ? View.VISIBLE : View.INVISIBLE);
        // Stop from the service
        TextView heartRateText = findViewById(R.id.heartRateText);
        heartRateText.setText(displayText);
    }

    public void measureRespiratoryRate(View view) {
        this.setParametersForRR("Measurement Started", false);
        startService(respirationService);
    }

    public void stopRRMeasurement(View view) {
        this.setParametersForRR("Measurement Cancelled", true);
        stopService(respirationService);
    }

    void setParametersForRR(String displayText, boolean isDone) {
        Button stopButton = findViewById(R.id.stopRRMeasuring);
        stopButton.setVisibility(isDone ? View.INVISIBLE : View.VISIBLE);

        Button rrMeasure = findViewById(R.id.respiratoryRateButton);
        rrMeasure.setVisibility(isDone ? View.VISIBLE : View.INVISIBLE);

        TextView respirationData = findViewById(R.id.respiratoryText);
        respirationData.setText(displayText);
    }

    public void uploadSigns(View view) {
        SymptomModel allSymptoms = dataBaseHelper.getByID(1);
        allSymptoms.setRESP_RATE(respirationRate);
        allSymptoms.setHEART_RATE(heartRate);
        if (dataBaseHelper.addOne(allSymptoms, 1)) {
            Toast.makeText(this, "Saved to DB",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        stopService(respirationService);
        super.onDestroy();
    }


    public class RespirationResultReceiver extends ResultReceiver {

        public RespirationResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == RESULT_OK && resultData != null) {
                respirationRate = Integer.parseInt(resultData.getString("Result"));
                setParametersForRR("Recorded respiration " + respirationRate, false);
            } else if (resultCode == RESULT_CANCELED) {
                stopService(respirationService);
                setParametersForRR("Respiration Rate is " + respirationRate, true);
            }
        }
    }
}
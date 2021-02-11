package com.example.covidsymptom;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyMainActivity";
    private static final int VIDEO_CAPTURE = 1;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 1996;
    public static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 112;

    private Uri uriForFile;
    private Intent symptomActivity;
    private Intent respirationService;
    boolean hasCameraFlash = false;
    HeartRateTask hrt;
//    JavaCameraView cameraView;
//    Mat mRGBA, mRGBAT;

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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        symptomActivity = new Intent(this, SymptomActivity.class);
        // Respiratory rate service
        respirationService = new Intent(getApplicationContext(), RespiratoryRateService.class);
        ResultReceiver resultReceiver = new RespirationResultReceiver(null);
        respirationService.putExtra(Intent.EXTRA_RESULT_RECEIVER, resultReceiver);

        //Heart rate service
//        cameraView = findViewById(R.id.camera_preview);
//        cameraView.setCvCameraViewListener(this);
//        cameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
//        cameraView.setCameraPermissionGranted();
//        cameraView.setMaxFrameSize(1000, 1200);

        hasCameraFlash = getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }


    @Override
    protected void onPause() {
        super.onPause();
//        if (cameraView != null) {
//            cameraView.disableView();
//        }
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
        //cameraView.setVisibility(SurfaceView.VISIBLE);
        // cameraView.enableView();
        // cameraView.setFlashLightVisibility(true);
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
                // The request was granted -> tell the camera view
//                cameraView.setCameraPermissionGranted();
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
        File mediaFile = new File(videoPath, "myvideo.mp4");
        uriForFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", mediaFile);

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 45);
        takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);
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
        this.setParametersForHR("Video Recorded", true);

        // Set video in the media player
        MediaController m = new MediaController(this);
        VideoView videoView = findViewById(R.id.video_preview);
        videoView.setMediaController(m);
        videoView.setVideoURI(uriForFile);

         hrt = new HeartRateTask();
        hrt.execute();

    }

    private class HeartRateTask extends AsyncTask<Uri, Integer, Float> {

        protected Float doInBackground(Uri... url) {

            try {
                for (int i = 0; i < 20; i++) {
                    onProgressUpdate(i*5);
                    Thread.sleep(500);
                }

            } catch (InterruptedException e) {
                return Float.valueOf(0);
            }
            return Float.valueOf(100);

        }

        protected void onProgressUpdate(Integer progress) {
            setParametersForHR("Calculating " + String.valueOf(progress) + "% done", false);
        }

        protected void onPostExecute(Float result) {
            setParametersForHR("Heart Rate is " + result.toString(), true);
        }
    }


    public void stopHRMeasurement(View view) {
        this.setParametersForHR("Measurement Cancelled", true);
        hrt.cancel(true);
        //cameraView.setVisibility(SurfaceView.INVISIBLE);
        // cameraView.disableView();
        //  cameraView.setFlashLightVisibility(false);
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
    }

//    private void setFlashLightVisibility(boolean isVisible) {
//        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        if (!hasCameraFlash) {
//            Toast.makeText(this, "Flash not available.",
//                    Toast.LENGTH_LONG).show();
//            return;
//        }
//        try {
//            String cameraId = cameraManager.getCameraIdList()[0];
//            cameraManager.setTorchMode(cameraId, isVisible);
//        } catch (CameraAccessException e) {
//            System.out.println("Unable to turn on the camera");
//        }
//    }
//    @Override
//    public void onCameraViewStarted(int width, int height) {
//        mRGBA = new Mat(height, width, CvType.CV_8UC4);
//    }
//
//    @Override
//    public void onCameraViewStopped() {
//        mRGBA.release();
//    }
//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        mRGBA = inputFrame.rgba();
//        return mRGBA;
//    }


    @Override
    protected void onDestroy() {
        stopService(respirationService);
        super.onDestroy();
//        if (cameraView != null) {
//            cameraView.disableView();
//        }
    }


    public class RespirationResultReceiver extends ResultReceiver {

        public RespirationResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            TextView respirationData = findViewById(R.id.respiratoryText);
            if (resultCode == RESULT_OK && resultData != null) {
                String dataZ = resultData.getString("Z");
                String respiration = resultData.getString("R");
                respirationData.setText("Z-Axis: " + dataZ + " Respiration Count " + respiration);
            } else if (resultCode == RESULT_CANCELED) {
                respirationData.setText("Reading Done");
            }
        }
    }
}
package com.sdi.castivate;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("deprecation")

public class CastingCustomVideoCamera extends Activity implements SurfaceHolder.Callback,ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener{

    private SurfaceView videoSurfaceView;
    private SurfaceHolder videoSurfaceHolder;
    private Camera camera;
    private ImageButton recordFlipCamera,recordFlash,videoRecord,closeRecord,videoRecordFinish;
    private int cameraId;
    private boolean flashmode = false;
    private int rotation;
    private byte[] mCameraData;
    private ImageView videoImageView;
    private LinearLayout recordReset;
    private TextView recordRetake,saveRecordVideo;

    private MediaRecorder recorder;
    private CamcorderProfile camcorderProfile;
    boolean recording = false;
    private TextView showTimer;

    //Show Timer
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    private  String videoRecordingPath="/sdcard/Video_"+System.currentTimeMillis() +".mp4";
    /*private  String videoRecordingPath="/sdcard/casting_video/";
    private String videoRecordingPath_local ="";*/
    Bitmap rotatedBitmap;
    ImageButton videoPreview;
    Context context;
    private static final String TAG = CastingCustomVideoCamera.class.getSimpleName();
    private static final int REQUEST_CAMERA =2919;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_video_camera);

        context = getApplicationContext();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestCameraPermission();

        } else {
            Log.i(TAG,"CAMERA permission has already been granted. Displaying camera preview.");
            init();
        }
        //init();

    }
    private void requestCameraPermission() {

        Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            ActivityCompat.requestPermissions(CastingCustomVideoCamera.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},
                    REQUEST_CAMERA);

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},
                    REQUEST_CAMERA);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e("Permission", "Granted");

                    Intent ChangeIntent = new Intent(CastingCustomVideoCamera.this, CastingCustomVideoCamera.class);
                    startActivity(ChangeIntent);
                    finish();
                } else {
                    Log.e("Permission", "Denied");
                }
                return;
            }
        }
    }

    private void init()
    {
        cameraId = CameraInfo.CAMERA_FACING_BACK;

        recordFlipCamera = (ImageButton) findViewById(R.id.recordFlipCamera);
        recordFlash = (ImageButton) findViewById(R.id.recordFlash);
        videoRecord = (ImageButton) findViewById(R.id.videoRecord);
        videoRecordFinish = (ImageButton) findViewById(R.id.videoRecordFinish);
        closeRecord = (ImageButton) findViewById(R.id.closeRecord);

        videoImageView = (ImageView) findViewById(R.id.videoImageView);
        videoImageView.setVisibility(View.INVISIBLE);

        recordReset = (LinearLayout) findViewById(R.id.recordReset);
        recordReset.setVisibility(View.INVISIBLE);

        recordRetake=(TextView) findViewById(R.id.recordRetake);
        saveRecordVideo=(TextView) findViewById(R.id.saveRecordVideo);

        showTimer=(TextView) findViewById(R.id.showTimer);
        showTimer.setVisibility(View.GONE);

        videoSurfaceView = (SurfaceView) findViewById(R.id.videoSurfaceView);
        videoSurfaceHolder = videoSurfaceView.getHolder();

        videoPreview=(ImageButton) findViewById(R.id.videoPreview);
        videoPreview.setVisibility(View.GONE);

        videoSurfaceHolder.addCallback(this);
        recordFlipCamera.setOnClickListener(this);
        videoRecord.setOnClickListener(this);
        recordFlash.setOnClickListener(this);
        closeRecord.setOnClickListener(this);
        recordRetake.setOnClickListener(this);
        saveRecordVideo.setOnClickListener(this);
        videoRecordFinish.setOnClickListener(this);
        videoPreview.setOnClickListener(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Camera.getNumberOfCameras() > 1) {
            recordFlipCamera.setVisibility(View.VISIBLE);
        }
        if (!getBaseContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH)) {
            recordFlash.setVisibility(View.GONE);
        }

        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
    }

    private boolean openCamera(int id) {
        boolean result = false;
        cameraId = id;
        releaseCamera();
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (camera != null) {
            try {
                setUpCamera(camera);
                camera.setErrorCallback(new ErrorCallback() {

                    @Override
                    public void onError(int error, Camera camera) {

                    }
                });
                camera.setPreviewDisplay(videoSurfaceHolder);
                camera.startPreview();

                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }
        return result;
    }

    private void setUpCamera(Camera c) {
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;
            default:
                break;
        }

        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing
            rotation = (info.orientation + degree) % 330;
            rotation = (360 - rotation) % 360;
        } else {
            // Back-facing
            rotation = (info.orientation - degree + 360) % 360;
        }
        c.setDisplayOrientation(rotation);
        Parameters params = c.getParameters();

        showFlashButton(params);

        List<String> focusModes = params.getSupportedFlashModes();
        if (focusModes != null) {
            if (focusModes
                    .contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                params.setFlashMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
        }

        params.setRotation(rotation);
    }

    private void showFlashButton(Parameters params) {
        boolean showFlash = (getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH) && params.getFlashMode() != null)
                && params.getSupportedFlashModes() != null
                && params.getSupportedFocusModes().size() > 1;

        recordFlash.setVisibility(showFlash ? View.VISIBLE
                : View.INVISIBLE);

    }

    private void releaseCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (!openCamera(CameraInfo.CAMERA_FACING_BACK)) {
            alertCameraDialog();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recordFlash:
                flashOnButton();
                break;
            case R.id.recordFlipCamera:
                flipCamera();
                break;
            case R.id.videoRecord:
                recordCom();
                takeImage();
                recordVideo();
                break;
            case R.id.closeRecord:
                finish();
                break;
            case R.id.recordRetake:
                setupImageCapture();
                break;
            case R.id.videoRecordFinish:
                recordComVisible();
                recordVideo();
                break;
            case R.id.saveRecordVideo:
                setupVideoImageSave();
                break;
            case R.id.videoPreview:
                videoPreview();
                break;
            default:
                break;
        }
    }

    private void setupVideoImageSave() {

            Intent intent = new Intent();
            intent.putExtra("videoRecordingPath", videoRecordingPath);
            setResult(RESULT_OK, intent);
            finish();
    }

    private void recordVideo()
    {
        if (recording) {
            // stop recording and release camera
            //recorder.stop();

            try{
                recorder.stop();
            }
            catch (IllegalStateException e) {
                e.printStackTrace();
            }
            // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            // Toast.makeText(MainActivity.this, "Video captured!", Toast.LENGTH_LONG).show();

            //video pause
            videoImageView.setVisibility(View.VISIBLE);
            videoImageView.setImageBitmap(rotatedBitmap);
            videoPreview.setVisibility(View.VISIBLE);
            videoSurfaceView.setVisibility(View.GONE);
            videoRecord.setVisibility(View.GONE);
            recordReset.setVisibility(View.VISIBLE);
            recordFlipCamera.setVisibility(View.GONE);
            recordFlash.setVisibility(View.GONE);
            closeRecord.setVisibility(View.GONE);


            recording = false;
        } else {
            if (!prepareRecorder()) {
                Toast.makeText(CastingCustomVideoCamera.this, "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                finish();
            }
            // work on UiThread for better performance
            runOnUiThread(new Runnable() {
                public void run() {
                    // If there are stories, add them to the table

                    try {
                        recorder.start();
                    } catch (final Exception ex) {
                        // Log.i("---","Exception in thread");
                    }
                }
            });

            recording = true;
        }
    }

    private void  videoPreview()
    {
        System.out.println("videoRecordingPath : "+videoRecordingPath);

        Intent intent = new Intent(CastingCustomVideoCamera.this,CastingVideoPreview.class);
        intent.putExtra("videoRecordingPath",videoRecordingPath);
        startActivity(intent);
    }
    private void setupImageCapture() {

        File file = new File(videoRecordingPath);

        boolean deleted = file.delete();

        if(deleted)
            System.out.println("videoRecordingPath Delete :"+file.getPath());
        else
            System.out.println("videoRecordingPath Not Delete :"+file.getPath());

        videoImageView.setVisibility(View.GONE);
        videoPreview.setVisibility(View.GONE);
        videoSurfaceView.setVisibility(View.VISIBLE);
        camera.startPreview();
        recordReset.setVisibility(View.GONE);
        recordFlipCamera.setVisibility(View.VISIBLE);
        videoRecord.setVisibility(View.VISIBLE);
        recordFlash.setVisibility(View.VISIBLE);
        closeRecord.setVisibility(View.VISIBLE);
    }

    private boolean prepareRecorder() {

        //Utility.createDirIfNotExist(videoRecordingPath);
        try {
        recorder = new MediaRecorder();
        recorder.setPreviewDisplay(videoSurfaceHolder.getSurface());
        camera.unlock();
            recorder.setCamera(camera);
        recorder.setOrientationHint(rotation);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setProfile(camcorderProfile);

        if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {

            recorder.setOutputFile(videoRecordingPath);
            System.out.println("videoRecordingPath 1 : "+videoRecordingPath);
        } else {
            recorder.setOutputFile(videoRecordingPath);

            System.out.println("videoRecordingPath 2 : "+videoRecordingPath);
        }
            recorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (recorder != null) {
            try {

                recorder.reset(); // clear recorder configuration
                recorder.release(); // release the recorder object
                recorder = null;
                camera.lock(); // lock camera for later use
            }
            catch (IllegalStateException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void recordCom() {

        showTimer.setVisibility(View.VISIBLE);
        videoRecordFinish.setVisibility(View.VISIBLE);
        videoRecord.setVisibility(View.GONE);
        recordFlash.setVisibility(View.GONE);
        closeRecord.setVisibility(View.GONE);
        recordFlipCamera.setVisibility(View.GONE);
        recordReset.setVisibility(View.GONE);
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }
    private void recordComVisible() {

        showTimer.setVisibility(View.GONE);
        videoRecordFinish.setVisibility(View.GONE);
        videoRecord.setVisibility(View.VISIBLE);
        recordFlash.setVisibility(View.VISIBLE);
        closeRecord.setVisibility(View.VISIBLE);
        recordFlipCamera.setVisibility(View.VISIBLE);
        recordReset.setVisibility(View.GONE);
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
        timeSwapBuff=0L;
        timeInMilliseconds=0L;
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hours = (secs / 60) % 24;;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            String Timer=(String.format("%02d", mins) + ":"+ String.format("%02d", secs));
            //String Timer=(String.format("%02d", hours) + ":"+String.format("%02d", mins) + ":"+ String.format("%02d", secs));
            showTimer.setText(Timer);

            customHandler.postDelayed(this, 0);

            /*if(Timer.equals("01:01:01"))
            {
                recorder.stop(); // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                // Toast.makeText(MainActivity.this, "Video captured!", Toast.LENGTH_LONG).show();

                //video pause
                videoImageView.setVisibility(View.VISIBLE);
                videoImageView.setImageBitmap(rotatedBitmap);
                videoPreview.setVisibility(View.VISIBLE);
                videoSurfaceView.setVisibility(View.GONE);
                videoRecord.setVisibility(View.GONE);
                recordReset.setVisibility(View.VISIBLE);
                recordFlipCamera.setVisibility(View.GONE);
                recordFlash.setVisibility(View.GONE);
                closeRecord.setVisibility(View.GONE);


                recording = false;
            }*/
        }

    };

    private void takeImage() {

        camera.takePicture(null, null, new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {

                    // convert byte array into bitmap
                    Bitmap loadedImage = null;
                    rotatedBitmap = null;
                    loadedImage = BitmapFactory.decodeByteArray(data, 0,data.length);

                    // rotate Image
                    Matrix rotateMatrix = new Matrix();
                    rotateMatrix.postRotate(rotation);
                    rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                            loadedImage.getWidth(), loadedImage.getHeight(),
                            rotateMatrix, false);


                    Bitmap bmp = rotatedBitmap;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    mCameraData = stream.toByteArray();


                } catch (Exception e) {
                    e.printStackTrace();
                }
                camera.startPreview();
            }
        });
    }

    private void flipCamera() {
        int id = (cameraId == CameraInfo.CAMERA_FACING_BACK ? CameraInfo.CAMERA_FACING_FRONT
                : CameraInfo.CAMERA_FACING_BACK);
        if (!openCamera(id)) {
            alertCameraDialog();
        }
    }

    private void alertCameraDialog() {
        Builder dialog = createAlert(CastingCustomVideoCamera.this,
                "Camera info", "error to open camera");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private Builder createAlert(Context context, String title, String message) {

        Builder dialog = new Builder(
                new ContextThemeWrapper(context,android.R.style.Theme_Holo_Light_Dialog));
        dialog.setIcon(R.drawable.ic_launcher);
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.setTitle("Information");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;

    }

    private void flashOnButton() {
        if (camera != null) {
            try {
                Parameters param = camera.getParameters();
                param.setFlashMode(!flashmode ? Parameters.FLASH_MODE_TORCH
                        : Parameters.FLASH_MODE_OFF);
                camera.setParameters(param);
                flashmode = !flashmode;
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }

}

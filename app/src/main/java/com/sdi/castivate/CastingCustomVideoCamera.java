package com.sdi.castivate;

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
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")

public class CastingCustomVideoCamera extends Activity implements SurfaceHolder.Callback, View.OnClickListener {

    private SurfaceView videoSurfaceView;
    private SurfaceHolder videoSurfaceHolder;
    private Camera camera;
    private ImageButton recordFlipCamera,recordFlash,videoRecord,closeRecord,videoRecordFinish;
    private int cameraId;
    private boolean flashmode = false;
    private int rotation;
    private byte[] mCameraData;
    public static final String EXTRA_CAMERA_DATA = "camera_data";
    private ImageView videoImageView;
    private LinearLayout recordReset;
    private TextView recordRetake,saveRecordVideo;


    private MediaRecorder recorder;
    private CamcorderProfile camcorderProfile;
    boolean recording = false;
    boolean usecamera = true;
    boolean previewRunning = false;
    private TextView showTimer;
    SimpleDateFormat simpleDateFormat;
    String timeStamp;

    //Show Timer
    private long startTime = 0L;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;

    long timeSwapBuff = 0L;

    long updatedTime = 0L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_video_camera);

        init();

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

        videoSurfaceHolder.addCallback(this);
        recordFlipCamera.setOnClickListener(this);
        videoRecord.setOnClickListener(this);
        recordFlash.setOnClickListener(this);
        closeRecord.setOnClickListener(this);
        recordRetake.setOnClickListener(this);
        saveRecordVideo.setOnClickListener(this);
        videoRecordFinish.setOnClickListener(this);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Camera.getNumberOfCameras() > 1) {
            recordFlipCamera.setVisibility(View.VISIBLE);
        }
        if (!getBaseContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH)) {
            recordFlash.setVisibility(View.GONE);
        }

        simpleDateFormat = new SimpleDateFormat("ddMMyyyyhhmmss", Locale.ENGLISH);
        timeStamp = simpleDateFormat.format(new Date());
        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        if (!openCamera(CameraInfo.CAMERA_FACING_BACK)) {
            alertCameraDialog();
        }

        System.out.println("onsurfacecreated");

        /*if (usecamera) {
            camera = Camera.open(CameraInfo.CAMERA_FACING_BACK);

            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewRunning = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

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
                    .contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFlashMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
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
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        System.out.println("onsurface changed");

        if (!recording && usecamera) {
            if (previewRunning) {
                camera.stopPreview();
            }

            try {
                Camera.Parameters p = camera.getParameters();

                p.setPreviewSize(camcorderProfile.videoFrameWidth,
                        camcorderProfile.videoFrameHeight);
                p.setPreviewFrameRate(camcorderProfile.videoFrameRate);

                camera.setParameters(p);

                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewRunning = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*prepareRecorder();
            if (!recording) {
                recording = true;
                recorder.start();
            }*/
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        System.out.println("surfaceDestroyed");

        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();
        if (usecamera) {
            previewRunning = false;
            // camera.lock();
            camera.release();
        }
        finish();

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
                prepareRecorder();
                //takeImage();
                //captureImage();
                break;
            case R.id.closeRecord:
                finish();
                break;
            case R.id.recordRetake:
                setupImageCapture();
                break;
            case R.id.videoRecordFinish:
                recordComVisible();
                recordStop();
                break;
            case R.id.saveRecordVideo:
                setupImageSave();
                break;

            default:
                break;
        }
    }


    private void setupImageCapture() {

        videoImageView.setVisibility(View.GONE);
        videoSurfaceView.setVisibility(View.VISIBLE);
        camera.startPreview();
        recordReset.setVisibility(View.GONE);
        recordFlipCamera.setVisibility(View.VISIBLE);
        videoRecord.setVisibility(View.VISIBLE);
        recordFlash.setVisibility(View.VISIBLE);
        closeRecord.setVisibility(View.VISIBLE);
    }

    private void prepareRecorder() {

        recorder = new MediaRecorder();
        recorder.setPreviewDisplay(videoSurfaceHolder.getSurface());
        if (usecamera) {
            //camera.unlock();
            recorder.setCamera(camera);
        }
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        recorder.setProfile(camcorderProfile);

        if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {

            File file = new File(Environment.getExternalStorageDirectory(), "Video_" + System.currentTimeMillis() + ".mp4" );

            recorder.setOutputFile(file.getPath());
            /*recorder.setOutputFile("/sdcard/XYZApp/" + "XYZAppVideo" + ""
                    + new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date())
                    + ".mp4");*/
        } else {
            File file = new File(Environment.getExternalStorageDirectory(), "Video_" + System.currentTimeMillis() + ".mp4" );

            recorder.setOutputFile(file.getPath());

            /*recorder.setOutputFile("/sdcard/XYZApp/" + "XYZAppVideo" + ""
                    + new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date())
                    + ".mp4");*/
        }

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
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

        }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);

            int mins = secs / 60;

            secs = secs % 60;

            int milliseconds = (int) (updatedTime % 1000);

            showTimer.setText("" + mins + ":"+ String.format("%02d", secs) + ":"+ String.format("%03d", milliseconds));

            customHandler.postDelayed(this, 0);

        }

    };

    private void recordStop()
    {
        System.out.println("recordStop :------------------> ");
        if (recording) {
            recorder.stop();
            if (usecamera) {
                try {
                    camera.reconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            recording = false;
        }
    }

    private void takeImage() {

        camera.takePicture(null, null, new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                try {

                    //mCameraData=data;
                    // convert byte array into bitmap
                    Bitmap loadedImage = null;
                    Bitmap rotatedBitmap = null;
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


                    videoImageView.setVisibility(View.VISIBLE);
                    videoImageView.setImageBitmap(rotatedBitmap);

                    camera.stopPreview();

                    videoSurfaceView.setVisibility(View.GONE);
                    videoRecord.setVisibility(View.GONE);
                    recordReset.setVisibility(View.VISIBLE);
                    recordFlipCamera.setVisibility(View.GONE);
                    recordFlash.setVisibility(View.GONE);
                    closeRecord.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupImageSave() {

        System.out.println("received");

        if (mCameraData != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_CAMERA_DATA, mCameraData);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }

        finish();
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
                new ContextThemeWrapper(context,
                        android.R.style.Theme_Holo_Light_Dialog));
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

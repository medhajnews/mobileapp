package in.medhajnews.app.ui;

import android.app.Activity;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.medhajnews.app.R;
import in.medhajnews.app.external.camera.CameraCallbacks;
import in.medhajnews.app.external.camera.CameraPreview;
import in.medhajnews.app.data.ImageHandler;

public class CameraActivity extends Activity {

    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final int FOCUS_AREA_SIZE = 300;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.Parameters mCameraparams;
    private boolean isFlashSupported = false;
    private boolean isAutoFocusSupported = false;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;
    private boolean isCameraFacingFront = false;
    private ImageView captureButton, modeButton;
    private boolean isVideoMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_camera);
        setContentView(R.layout.activity_camera_old);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        captureButton = (ImageView) findViewById(R.id.button_capture);
        modeButton = (ImageView) findViewById(R.id.button_mode);

        /**
         * Camera2 API implementations are buggy. Use the old camera API until support is completely
         * dropped.
         */
//        if (null == savedInstanceState) {
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.container, Camera2BasicFragment.newInstance())
//                    .commit();
//        }

        modeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVideoMode = !isVideoMode;
            }
        });

        //todo WTF
        if ((getPackageManager().hasSystemFeature(CAMERA_SERVICE))) { //dont' run on devices without cameras
            Toast.makeText(CameraActivity.this, "Could not detect camera", Toast.LENGTH_SHORT).show();
            finish();
        }

        //Detect and Access Camera
        mCamera = getCameraInstance();
        if (mCamera == null) finish();
        //get feature list
        mCameraparams = mCamera.getParameters();
        //Get Supported Flash and Focus Modes
        List<String> focusModes = mCameraparams.getSupportedFocusModes();
        List<String> flashModes = mCameraparams.getSupportedFlashModes();
        List<Camera.Size> pictureSizes = mCameraparams.getSupportedPictureSizes();

        if (focusModes != null) {
            isAutoFocusSupported = focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        if (isAutoFocusSupported) {
            //auto focus is supported
            mCameraparams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        if (flashModes != null) {
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                //auto flash supported
                mCameraparams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            }
        }

        Camera.Size bestSize = pictureSizes.get(0);
        for(int i = 1; i < pictureSizes.size(); i++){
            if((pictureSizes.get(i).width * pictureSizes.get(i).height) > (bestSize.width * bestSize.height)){
                bestSize = pictureSizes.get(i);
            }
        }

        mCameraparams.setJpegQuality(50);
        mCameraparams.setPictureSize(bestSize.width, bestSize.height);

        //OnCLickListeners
        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //set parameter on camera
        try {
            mCamera.setParameters(mCameraparams);
        } catch (RuntimeException err) {
            Log.e(TAG, err.getMessage());
        }

        mPreview = new CameraPreview(this, mCamera);
        preview.addView(mPreview);

        //setup listeners for capture
        preview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (mCamera != null) {
                    mCamera.cancelAutoFocus();
                    Rect focusRect = calculateFocusArea(event.getX(), event.getY());

                    Camera.Parameters parameters = mCamera.getParameters();
                    if (parameters.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_AUTO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    }
                    if (parameters.getMaxNumFocusAreas() > 0) {
                        List<Camera.Area> mylist = new ArrayList<Camera.Area>();
                        mylist.add(new Camera.Area(focusRect, 1000));
                        parameters.setFocusAreas(mylist);
                    }

                    try {
                        mCamera.cancelAutoFocus();
                        mCamera.setParameters(parameters);
                        mCamera.startPreview();
                        mCamera.autoFocus(CameraCallbacks.AutoFocusCallback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });

        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isVideoMode) {
                            if (isRecording) {
                                stopRecord();
                            } else {
                                startRecord();
                            }
                        } else {
                            mCamera.takePicture(
                                    CameraCallbacks.ShutterCallback,
                                    null,
                                    CameraCallbacks.PictureCallback);
                        }
                    }
                }
        );


    }

    // remember to check for null return
    public static Camera getCameraInstance() {
        try {
            return Camera.open();
        } catch (Exception err) {
            Log.e(TAG, err.getLocalizedMessage());
            return null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaRecorder();
        releaseCamera();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onBackPressed() {
        if(isRecording) {
            stopRecord();
        } else {
            super.onBackPressed();
        }
    }

    private void stopRecord() {
        // stop recording and release camera
        mMediaRecorder.stop();  // stop the recording
        releaseMediaRecorder(); // release the MediaRecorder object
        mCamera.lock();         // take camera access back from MediaRecorder

        // inform the user that recording has stopped
        isRecording = false;
    }

    private void startRecord() {
        // initialize video camera
        if (prepareVideoRecorder()) {
            // Camera is available and unlocked, MediaRecorder is prepared,
            // now you can start recording
            mMediaRecorder.start();

            // inform the user that recording has started
            isRecording = true;
        } else {
            // prepare didn't work, release the camera
            releaseMediaRecorder();
            // inform user
        }
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    private boolean prepareVideoRecorder() {
        mCamera = getCameraInstance();
        if (mCamera == null) {
            Log.e(TAG, "Camera instance turned out to be null!");
            return false;
        }
        mMediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_CIF));

        File outputfile = ImageHandler.getOutputMediaFile(ImageHandler.MEDIA_TYPE_VIDEO);

        if (outputfile != null) {
            mMediaRecorder.setOutputFile(outputfile.toString());
        } else {
            Log.e(TAG, "No access to external media");
        }

        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        mMediaRecorder.setMaxDuration(45000); //max time limit 45 seconds


        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.e(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.e(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private Rect calculateFocusArea(float x, float y) {
        int left = clamp(Float.valueOf((x / mPreview.getWidth()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);
        int top = clamp(Float.valueOf((y / mPreview.getHeight()) * 2000 - 1000).intValue(), FOCUS_AREA_SIZE);

        return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
        int result;
        if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
            if (touchCoordinateInCameraReper > 0) {
                result = 1000 - focusAreaSize / 2;
            } else {
                result = -1000 + focusAreaSize / 2;
            }
        } else {
            result = touchCoordinateInCameraReper - focusAreaSize / 2;
        }
        return result;
    }


    private int findFrontCameraId() {
        int camId = 0;
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                camId = i;
                break;
            }
        }
        return camId;
    }

    private int findBackCameraId() {
        int camId = 0;
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                camId = i;
                break;
            }
        }
        return camId;
    }

    private void switchCamera() {
        if (mCamera != null) {
            releaseCamera();
        }
        if (!isCameraFacingFront) {
            mCamera = Camera.open(findBackCameraId());
        } else {
            mCamera = Camera.open(findFrontCameraId());
        }
    }




}
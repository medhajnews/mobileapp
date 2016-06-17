/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.medhajnews.app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.medhajnews.app.Camera.CameraPreview;
import in.medhajnews.app.Data.ImageHandler;

public class CameraActivity extends Activity {

    private static final String TAG = CameraActivity.class.getSimpleName();
    private static  final int FOCUS_AREA_SIZE= 300;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.Parameters mCameraparams;
    private boolean isFlashSupported = false;
    private boolean isAutoFocusSupported = false;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_camera);
        setContentView(R.layout.activity_camera_old);
        /**
         * Camera2 API implementations are buggy. Use the old camera API until support is completely
         * dropped.
         */
//        if (null == savedInstanceState) {
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.container, Camera2BasicFragment.newInstance())
//                    .commit();
//        }
        if(!checkCameraHardware(this)) { //dont' run on devices without cameras
            return;
        }
        //Detect and Access Camera
        mCamera = getCameraInstance();

        //get feature list
        mCameraparams = mCamera.getParameters();

        List<String> focusModes = mCameraparams.getSupportedFocusModes();
        if(focusModes!=null) {
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                //auto focus is supported
                mCameraparams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
        }

        List<String> flashModes = mCameraparams.getSupportedFlashModes();
        if(flashModes!=null) {
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                //auto flash supported
                mCameraparams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            }
        }

        //set parameter on camera
        mCamera.setParameters(mCameraparams);

        //instantiate a preview class
        mPreview = new CameraPreview(this, mCamera);

        //Build a preview layout
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
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
                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                if (camera.getParameters().getFocusMode().equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                                    Camera.Parameters parameters = camera.getParameters();
                                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                                    if (parameters.getMaxNumFocusAreas() > 0) {
                                        parameters.setFocusAreas(null);
                                    }
                                    camera.setParameters(parameters);
                                    camera.startPreview();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });

        final Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isRecording) {
                            // stop recording and release camera
                            mMediaRecorder.stop();  // stop the recording
                            releaseMediaRecorder(); // release the MediaRecorder object
                            mCamera.lock();         // take camera access back from MediaRecorder

                            // inform the user that recording has stopped
                            captureButton.setText("Capture");
                            isRecording = false;
                        } else {
                            // initialize video camera
                            if (prepareVideoRecorder()) {
                                // Camera is available and unlocked, MediaRecorder is prepared,
                                // now you can start recording
                                mMediaRecorder.start();

                                // inform the user that recording has started
                                captureButton.setText("Stop");
                                isRecording = true;
                            } else {
                                // prepare didn't work, release the camera
                                releaseMediaRecorder();
                                // inform user
                            }
                        }
                    }
                }
        );



    }

    // remember to check for null return
    public static Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception err) {
            Log.e(TAG, err.getLocalizedMessage());
        }
        return camera;
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();
        releaseCamera();
    }

    private void releaseCamera() {
        if(mCamera!=null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void releaseMediaRecorder() {
        if(mMediaRecorder!=null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private boolean prepareVideoRecorder() {
        mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //todo set audio/video size, bitrate and encoding

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_CIF));

        File outputfile = ImageHandler.getOutputMediaFile(ImageHandler.MEDIA_TYPE_VIDEO);

        if(outputfile!=null) {
            mMediaRecorder.setOutputFile(outputfile.toString());
        } else {
            Log.e(TAG, "No access to external media" );
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
        if (Math.abs(touchCoordinateInCameraReper)+focusAreaSize/2>1000){
            if (touchCoordinateInCameraReper>0){
                result = 1000 - focusAreaSize/2;
            } else {
                result = -1000 + focusAreaSize/2;
            }
        } else{
            result = touchCoordinateInCameraReper - focusAreaSize/2;
        }
        return result;
    }
}
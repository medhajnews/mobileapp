package in.medhajnews.app.Camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import in.medhajnews.app.Data.ImageHandler;

/**
 * Created by bhav on 6/15/16 for the Medhaj News Project.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = CameraPreview.class.getSimpleName();
    private Camera mCamera;
    private SurfaceHolder mHolder;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;

        mHolder = getHolder();
        mHolder.addCallback(this);
        //type will be auto detected. probably.
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }
        catch (IOException err) {
            Log.e(TAG, err.getLocalizedMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //RELEASE CAMERA FROM THE ACTIVITY
    }

    public static Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File picture = ImageHandler.getOutputMediaFile(ImageHandler.MEDIA_TYPE_IMAGE);

            if(picture == null) {
                Log.e(TAG, "Failed to create file ");
                return;
            }

            try {
                FileOutputStream fos =  new FileOutputStream(picture);
                fos.write(data);
                fos.close();
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

        }
    };
}

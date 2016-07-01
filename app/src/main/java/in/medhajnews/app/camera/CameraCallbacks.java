package in.medhajnews.app.camera;

import android.hardware.Camera;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import in.medhajnews.app.data.ImageHandler;

/**
 * Created by bhav on 6/18/16 for the Medhaj News Project.
 */
public class CameraCallbacks {

    private static final String TAG = CameraCallbacks.class.getSimpleName();

    public static Camera.AutoFocusCallback AutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean b, Camera camera) {
//            if (camera.getParameters().getFocusMode().equals(
//                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
//
//                Camera.Parameters parameters = camera.getParameters();
//                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//
//                if (parameters.getMaxNumFocusAreas() > 0) {
//                    parameters.setFocusAreas(null);
//                }
//
//                camera.setParameters(parameters);
//                camera.startPreview();
//            }
        }
    };

    public static Camera.ErrorCallback ErrorCallback = new Camera.ErrorCallback() {
        @Override
        public void onError(int i, Camera camera) {
            //release the camera in case of error ?
            if(camera!=null) {
                camera.release();
                camera = null;
            }
        }
    };

    public static Camera.PictureCallback PictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File picture = ImageHandler.getOutputMediaFile(ImageHandler.MEDIA_TYPE_IMAGE);
            if (picture == null) {
                Log.e(TAG, "Failed to create file ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(picture);
                fos.write(data);
                fos.close();
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
            camera.startPreview();
        }
    };

    public static Camera.PreviewCallback PreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {

        }
    };

    public static Camera.ShutterCallback ShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    public static Camera.AutoFocusMoveCallback AutoFocusMoveCallback = new Camera.AutoFocusMoveCallback(){
        @Override
        public void onAutoFocusMoving(boolean b, Camera camera) {

        }
    };
}

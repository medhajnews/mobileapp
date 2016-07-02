package in.medhajnews.app.external.camera;

/**
 * Created by bhav on 6/18/16 for the Medhaj News Project.
 */
public class CameraParameters {

    private CameraParameters() {

    }

    // FOCUS
    public static final int	AF_MODE_UNSUPPORTED = -1; //Default focus mode value. Often presents on front cameras
    public static final int	AF_MODE_OFF = 0;
    public static final int	AF_MODE_AUTO = 1;
    public static final int	AF_MODE_MACRO = 2;
    public static final int	AF_MODE_CONTINUOUS_VIDEO = 3;
    public static final int AF_MODE_CONTINUOUS_PICTURE = 4;
    public static final int	AF_MODE_EDOF = 5;
    public static final int AF_MODE_INFINITY = 6;
    public static final int	AF_MODE_NORMAL	 = 7;
    public static final int	AF_MODE_FIXED = 8;

    public static final int	AF_MODE_LOCK = 10;
    public static final int	MF_MODE	 = 11;	// Manual focus mode

    // FLASH
    public static final int	FLASH_MODE_OFF	 = 0;
    public static final int	FLASH_MODE_AUTO = 3;
    public static final int	FLASH_MODE_SINGLE = 1;
    public static final int	FLASH_MODE_REDEYE	 = 4;
    public static final int	FLASH_MODE_TORCH = 2;
    public static final int	FLASH_MODE_CAPTURE_TORCH = 5;

}

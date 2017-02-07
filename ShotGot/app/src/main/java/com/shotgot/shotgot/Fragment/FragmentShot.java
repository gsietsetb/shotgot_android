package com.shotgot.shotgot.Fragment;

/**
 * Created by Guillermo on 9/1/17.
 */

import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.RadioButton;

import com.shotgot.shotgot.API.SocketPic;
import com.shotgot.shotgot.R;
import com.shotgot.shotgot.Utils.CameraView;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.shotgot.shotgot.R.id.tags;

public class FragmentShot extends ImmersiveModeFragment {// implements CameraBridgeViewBase.CvCameraViewListener2 {

    private int nTags = 0, nColorsClarifai = 0;
    private boolean isTags = false, isCols = false, previousSearch = false;
    private GridLayout tagsLayout;
    private GridLayout colorsLayout;
    private ArrayAdapter<String> adapter;
    private String[] foundTags;


    /**
     * Native Android Camera
     */
    private Camera mCamera = null;
    private CameraView mCameraView = null;

    /**
     * @// TODO: 2/02/17  ToBe @Deprecated
     */
    private Camera.PictureCallback mPicture;
    /**) {
    @Override public void run() {
    String name = (String) args[0];
    Log.d("SocketClousight", name);
    addRespLayout(name);
    }
    });
     }
     * For NodeJS API api.shotgot.com
     */
    private Socket mSocket;
    private Emitter.Listener onCloudsightResp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
//            Log.d("Socket", "Creating Socket listener...");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String concepts = (String) args[0];
                    Log.d("Socket", concepts);
                    addRespLayout(concepts);
                }
            });
        }
    };
    private Emitter.Listener onClarifaiTagResp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
//            Log.d("Socket", "Creating Socket listener...");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //In order to prevent double params
                    previousSearch = true;
                    String concepts = (String) args[0];
                    Log.d("Socket", concepts);
                    addRespLayout(concepts);
                }
            });
        }
    };
    private Emitter.Listener onGoogleTagResp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
//            Log.d("Socket", "Creating Socket listener...");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //In order to prevent double params
                    previousSearch = true;
                    String concepts = (String) args[0];
                    Log.d("Socket", concepts);
                    addRespLayout(concepts);
                }
            });
        }
    };
    private Emitter.Listener onGoogleLogoResp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
//            Log.d("Socket", "Creating Socket listener...");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //In order to prevent double params
                    previousSearch = true;
                    String concepts = (String) args[0];
                    Log.d("Socket", concepts);
                    addRespLayout(concepts);
                }
            });
        }
    };

    private Emitter.Listener onGoogleLabelResp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
//            Log.d("Socket", "Creating Socket listener...");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //In order to prevent double params
                    previousSearch = true;
                    String concepts = (String) args[0];
                    Log.d("Socket", concepts);
                    addRespLayout(concepts);
                }
            });
        }
    };
    private Emitter.Listener onGoogleTextResp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
//            Log.d("Socket", "Creating Socket listener...");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //In order to prevent double params
                    previousSearch = true;
                    String concepts = (String) args[0];
                    Log.d("Socket", concepts);
                    addRespLayout(concepts);
                }
            });
        }
    };
    private Emitter.Listener onClarifaiColorResp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
//            Log.d("Socket", "Creating Socket listener...");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    previousSearch = true;
                    String col = (String) args[0];
                    Log.d("Socket", col);
                    addRespColorLayout(col);
                }
            });
        }
    };

    /**Safe method for getting a camera instance.
     * @return
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    /**OpenCV Camera
     private CameraBridgeViewBase mOpenCvCameraView;

     private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this.getActivity()) {
    @Override public void onManagerConnected(int status){
    switch (status){
    case LoaderCallbackInterface.SUCCESS:
    {
    Log.i("CameraCV", "OpenCV loaded Successfully");
    mOpenCvCameraView.enableView();
    }
    break;
    default:
    {
    super.onManagerConnected(status);
    }
    break;
    }
    }
    };*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shot, container, false);

        /**OpenCV Camera version
         getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
         //        getActivity().setContentView(R.colorsLayout.fragment_computer_vision);
         mOpenCvCameraView = (CameraBridgeViewBase) view.findViewById(R.id.CV_VIEW);
         mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
         mOpenCvCameraView.setCvCameraViewListener(this);*/

        /**InnerCamera Version*/
        /* Create an instance of Camera*/
        mCamera = getCameraInstance();

        // Socket init
        SocketPic appSocket = new SocketPic();
        mSocket = appSocket.getSocket();
        mSocket.on("CLOUDSIGHT", onCloudsightResp);
        mSocket.on("CLARIFAI_CONCEPTS", onClarifaiTagResp);
        mSocket.on("CLARIFAI_COLOR", onClarifaiColorResp);
        mSocket.on("GOOGLE_LOGOS", onGoogleLogoResp);
        mSocket.on("GOOGLE_LABELS", onGoogleLabelResp);
        mSocket.on("GOOGLE_TEXT", onGoogleTextResp);
        mSocket.connect();

        // Create our Preview view and set it as the content of our activity.
        if (mCamera != null) {
            mCameraView = new CameraView(getActivity().getBaseContext(), mCamera);
            mPicture = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    //Todo get the base 64 string
//                    data = data.Bitmap.CompressFormat.JPEG, 100, data)
                    attemptSocketSendImg(data);
                }
            };

            FrameLayout camera_view = (FrameLayout) view.findViewById(R.id.camera_preview);
            camera_view.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // get an image from the camera
                            getPicResults();
                        }
                    }
            );
            camera_view.addView(mCameraView);//add the SurfaceView to the colorsLayout

            // Trap the capture button.
            Button captureButton = (Button) view.findViewById(R.id.button_capture);
            captureButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // get an image from the camera
                            getPicResults();
                        }
                    }
            );
        }
        return view;
    }

    /*public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CAPTURE) {
                // get the Uri for the captured image
                picUri = data.getData();
                performCrop();
            }
            // user is returning from cropping the image
            else if (requestCode == CROP_PIC) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                /**Compress the cropped image afterwards/
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                thePic.compress(Bitmap.CompressFormat.JPEG, 100, byteStream);
                byte[] bytes = new byte[0];
                byteStream.write(bytes, 0, bytes.length);
                attemptSocketSendImg(bytes);
//                ImageView picView = (ImageView) findViewById(R.id.picture);
//                picView.setImageBitmap(thePic);
            }
        }
    }*/

    /**
     * this function does the crop operation.
     * <p>
     * private void performCrop() {
     * // take care of exceptions
     * try {
     * // call the standard crop action intent (the user device may not
     * // support it)
     * Intent cropIntent = new Intent("com.android.camera.action.CROP");
     * // indicate image type and Uri
     * cropIntent.setDataAndType(picUri, "image/*");
     * // set crop properties
     * cropIntent.putExtra("crop", "true");
     * // indicate aspect of desired crop
     * cropIntent.putExtra("aspectX", 2);
     * cropIntent.putExtra("aspectY", 1);
     * // indicate output X and Y
     * cropIntent.putExtra("outputX", 256);
     * cropIntent.putExtra("outputY", 256);
     * // retrieve data on return
     * cropIntent.putExtra("return-data", true);
     * // start the activity - we handle returning in onActivityResult
     * startActivityForResult(cropIntent, CROP_PIC);
     * }
     * // respond to users whose devices do not support the crop action
     * catch (ActivityNotFoundException anfe) {
     * Toast toast = Toast
     * .makeText(getContext(), "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
     * toast.show();
     * }
     * }
     */

    @Override
    public void onResume() {
        super.onResume();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this.getActivity(), mLoaderCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**@deprecated ToUndo
        if (mOpenCvCameraView != null)
        mOpenCvCameraView.disableView();*/
        mSocket.disconnect();
        mSocket.off("disconnect", onClarifaiColorResp);
        mSocket.off("disconnect", onClarifaiTagResp);
        mSocket.off("disconnect", onCloudsightResp);
        mSocket.off("disconnect", onGoogleLogoResp);
        mSocket.off("disconnect", onGoogleTagResp);
        mSocket.off("disconnect", onGoogleLabelResp);
    }

    @Override
    public void onPause() {
        super.onPause();
        /**@deprecated ToUndo
        if (mOpenCvCameraView != null)
        mOpenCvCameraView.disableView();*/
    }

    private void addRespLayout(String tag) {
        Button butt = new Button(getContext());
        butt.setBackgroundColor(getResources().getColor(R.color.transp_dark_background));
        butt.setTextColor(getResources().getColor(R.color.wallet_holo_blue_light));
        butt.setText(tag);
        tagsLayout.addView(butt, ++nTags);
    }

    private void addRespColorLayout(String col) {
        RadioButton rButt = new RadioButton(getContext());
        rButt.setBackgroundColor(Color.parseColor(col));
        colorsLayout.addView(rButt, ++nColorsClarifai);
    }

    /**
     * Through Socket.io to api.shotgot.com API
     */
    private void attemptSocketSendImg(byte[] data) {
        Log.d("SOCKET", "[0] Going to connect to remote server");
        mSocket.connect();
        String imgString = Base64.encodeToString(data, Base64.NO_WRAP);
        Log.d("SOCKET", "[1] Connected to remote server. Going to send: " + imgString);
        mSocket.emit("PIC_REQ", imgString);
        Log.d("SOCKET", "[2] Already sent data to remote server.");
    }

    /**PostProcess Captured Img, i.e crop it?*/
    private void getPicResults() {
        /**Delete previous matches from the screen*/
        if (!previousSearch) {
//            foundTags = new String[]{"Tags"};
//            adapter = new ArrayAdapter<>(getContext(),
//                    android.R.layout.simple_list_item_1,
//                    foundTags);
//            tagsLayout.setAdapter(adapter);
            tagsLayout = (GridLayout) getActivity().findViewById(tags);
            tagsLayout.setVisibility(View.VISIBLE);
            colorsLayout = (GridLayout) getActivity().findViewById(R.id.colors);
            colorsLayout.setVisibility(View.VISIBLE);
        } else {
            tagsLayout.removeViews(0, nTags);
            colorsLayout.removeViews(0, nColorsClarifai);
        }
        /**Attempt to Crop
         try {
         // use standard intent to capture an image
         Intent captureIntent = new Intent(
         MediaStore.ACTION_IMAGE_CAPTURE);
         // we will handle the returned data in onActivityResult
         startActivityForResult(captureIntent, CAMERA_CAPTURE);
         } catch (ActivityNotFoundException anfe) {
         Toast toast = Toast.makeText(getContext(), "This device doesn't support the crop action!",
         Toast.LENGTH_SHORT);
         toast.show();
         } */
        mCamera.takePicture(null, null, mPicture);

        /**Play camera's Beep sound*/
        MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.cam_beep);
        mp.start();
    }

    /**
     * This method is invoked when camera preview has started. After this method is invoked
     * the frames will start to be delivered to client via the onCameraFrame() callback.
     *
     * @param width  -  the width of the frames that will be delivered
     * @param height - the height of the frames that will be delivered

     @Override public void onCameraViewStarted(int width, int height) {

     } */

    /**
     * This method is invoked when camera preview has been stopped for some reason.
     * No frames will be delivered via onCameraFrame() callback after this method is called.

     @Override public void onCameraViewStopped() {

     }*/

    /**
     * This method is invoked when delivery of the frame needs to be done.
     * The returned values - is a modified frame which needs to be displayed on the screen.
     * TODO: pass the parameters specifying the format of the frame (BPP, YUV or RGB and etc)
     *
     * @param inputFrame
     */
//    @Override
//    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        /**ImgProcessing on RT*/
//        Mat rgba = inputFrame.rgba();
////        Utils.bitmapToMat(inputFrame, rgba);
//        /**EdgeDetection Feature*/
//        Mat edges = new Mat(rgba.size(), CvType.CV_8UC1);
//        Imgproc.cvtColor(rgba, edges, Imgproc.COLOR_RGB2GRAY, 4);
//        Imgproc.Canny(edges, edges, 80, 100);
//        return inputFrame.rgba();
//    }

}

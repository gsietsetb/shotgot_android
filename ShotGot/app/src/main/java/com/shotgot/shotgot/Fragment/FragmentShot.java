package com.shotgot.shotgot.Fragment;

/**
 * Created by Guillermo on 9/1/17.
 */

import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.shotgot.shotgot.API.SocketPic;
import com.shotgot.shotgot.R;
import com.shotgot.shotgot.Utils.CameraView;

import org.json.JSONArray;
import org.json.JSONException;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FragmentShot extends ImmersiveModeFragment {// implements CameraBridgeViewBase.CvCameraViewListener2 {

    /**
     * For NodeJS API api.shotgot.com
     */
    private Socket mSocket;
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("Socket", "Creating Socket listener...");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONArray concepts = (JSONArray) args[0];
                    Log.d("Socket", concepts.toString());
//                    String username;
//                    String message;
//                    try {
////                        username = data.getString("username");
//                        message = data.getString("message");
//                    } catch (JSONException e) {
//                        return;
//                    }

                    // add the message to view
                    try {
                        addRespToFragment(concepts);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    /**
     * Native Android Camera
     */
    private Camera mCamera = null;
    private CameraView mCameraView = null;

    /**
     * @// TODO: 2/02/17  ToBe @Deprecated
     */
    private Camera.PictureCallback mPicture;

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
    public void onResume() {
        super.onResume();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this.getActivity(), mLoaderCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shot, container, false);

        /**OpenCV Camera version
         getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
         //        getActivity().setContentView(R.layout.fragment_computer_vision);
         mOpenCvCameraView = (CameraBridgeViewBase) view.findViewById(R.id.CV_VIEW);
         mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
         mOpenCvCameraView.setCvCameraViewListener(this);*/

        /**InnerCamera Version*/
        /* Create an instance of Camera*/
        mCamera = getCameraInstance();

        // Socket init
        SocketPic appSocket = new SocketPic();
        mSocket = appSocket.getSocket();

        // Create our Preview view and set it as the content of our activity.
        if (mCamera != null) {
            mCameraView = new CameraView(getActivity().getBaseContext(), mCamera);
            mPicture = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    //Todo get the base 64 string
                    //TODO JSONObject dataJSON = new JSONObject();
                    attemptSocketSend(data);
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
            camera_view.addView(mCameraView);//add the SurfaceView to the layout

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**@deprecated ToUndo
        if (mOpenCvCameraView != null)
        mOpenCvCameraView.disableView();*/
        mSocket.disconnect();
        mSocket.off("disconnect", onNewMessage);
    }

    @Override
    public void onPause() {
        super.onPause();
        /**@deprecated ToUndo
        if (mOpenCvCameraView != null)
        mOpenCvCameraView.disableView();*/
    }

    private void addRespToFragment(JSONArray resp) throws JSONException {
        FrameLayout layout = (FrameLayout) getActivity().findViewById(R.id.camera_preview);
        int i;
        for (i = 0; i < resp.length(); i++) {
            String tag = (String) resp.get(i);
            TextView txt = new TextView(getContext());
            txt.setX(40);
            txt.setText(tag);
            layout.addView(txt);
        }
//        Toast.makeText(getActivity().getApplicationContext(),
//                resp, Toast.LENGTH_LONG).show();
    }

    /**
     * Through Socket.io to api.shotgot.com API
     */
    private void attemptSocketSend(byte[] data) {
        String imgString = Base64.encodeToString(data, Base64.NO_WRAP);
        Log.d("SOCKET", "[0] Going to connect to remote server");
        mSocket.connect();
        Log.d("SOCKET", "[1] Connected to remote server. Going to send: " + imgString);
        mSocket.emit("EVENT_MESSAGE", imgString);
        Log.d("SOCKET", "[2] Already sent data to remote server.");
    }

    /**PostProcess Captured Img, i.e crop it?*/
    private void getPicResults() {
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

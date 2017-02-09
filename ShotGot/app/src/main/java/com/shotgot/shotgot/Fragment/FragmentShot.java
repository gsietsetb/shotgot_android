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
import android.widget.GridLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.shotgot.shotgot.API.SocketIO;
import com.shotgot.shotgot.R;
import com.shotgot.shotgot.Utils.CameraView;
import com.shotgot.shotgot.Utils.Meta;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.shotgot.shotgot.R.id.tags;
import static com.shotgot.shotgot.Utils.CameraView.getCameraInstance;

public class FragmentShot extends ImmersiveModeFragment {// implements CameraBridgeViewBase.CvCameraViewListener2 {

    private int nTags = 0, nColorsClarifai = 0;
    private boolean previousSearch = false;
    private GridLayout tagsLayout;
    private GridLayout colorsLayout;

    /**
     * Native Android Camera
     */
    private Camera mCamera = null;
    private CameraView mCameraView = null;

    private Camera.PictureCallback mPicture;

    /**
     * For NodeJS API api.shotgot.com
     */
    private Socket mSocket;

    private Emitter.Listener onMetadataResp = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /**Delete previous matches from the screen*/
                    if (!previousSearch) {
                        tagsLayout = (GridLayout) getActivity().findViewById(tags);
                        tagsLayout.setVisibility(View.VISIBLE);
                        colorsLayout = (GridLayout) getActivity().findViewById(R.id.colors);
                        colorsLayout.setVisibility(View.VISIBLE);
                    } else {
//                        tagsLayout.removeViews(0, nTags);
//                        colorsLayout.removeViews(0, nColorsClarifai);
                    }
                    previousSearch = true;
                    Log.d("API", "This is args: " + args[0]);
                    JSONObject preMeta = (JSONObject) args[0];
                    try {
                        Meta meta = new Meta((preMeta.getJSONObject("respTag")));
                        if (meta != null) {
                            if (meta.data != null) {
//                            Log.d("API", "This is meta: "+meta.toString());
                                if (meta.isCorrect()) {
                                    Log.d("SocketRx", meta.toString());
                                    if (meta.type.equals(Meta.TYPE_COLORS)) {
                                        addRespColorLayout(meta);
                                    } else {
                                        addRespLabels(meta);
                                    }
                                } else Log.d("SocketFAILURE: ", "Check correctness");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void addRespLabels(Meta labels) throws JSONException {
        Log.d("AddView", "Trying to add " + labels.toString());
        if (labels.API.equals(Meta.API_CLOUDSIGHT)
                || labels.type.equals(Meta.TYPE_OCR)
                || labels.type.equals(Meta.TYPE_LOGO)) {
            Toast.makeText(getContext(), labels.data, Toast.LENGTH_LONG).show();
            Log.d("AddView", "Seems to be Cloudsiht" + labels.toString());
            Button butt = new Button(getContext());
            butt.setBackgroundColor(getResources().getColor(R.color.transp_dark_background));
            butt.setTextColor(getResources().getColor(R.color.wallet_holo_blue_light));
            butt.setText(labels.data);
            tagsLayout.addView(butt);
        }
    }

    private void addRespColorLayout(Meta colorWrapper) throws JSONException {
        for (int col : colorWrapper.colorArray) {
            RadioButton rButt = new RadioButton(getContext());
            rButt.setBackgroundColor(col);
            colorsLayout.addView(rButt, ++nColorsClarifai);
        }
    }

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
        SocketIO appSocket = new SocketIO();
        mSocket = appSocket.getSocket();
        mSocket.on("METADATA", onMetadataResp);
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
        mSocket.off("disconnect", onMetadataResp);
    }

    @Override
    public void onPause() {
        super.onPause();
        /**@deprecated ToUndo
        if (mOpenCvCameraView != null)
        mOpenCvCameraView.disableView();*/
    }

    /**
     * Through Socket.io to api.shotgot.com API
     */
    private void attemptSocketSendImg(byte[] data) {
//        Log.d("SOCKET", "[0] Going to connect to remote server");
        mSocket.connect();
        String imgString = Base64.encodeToString(data, Base64.NO_WRAP);
//        Log.d("SOCKET", "[1] Connected to remote server. Going to send: " + imgString);
        mSocket.emit("PIC_REQ", imgString);
//        Log.d("SOCKET", "[2] Already sent data to remote server.");
    }

    /**PostProcess Captured Img, i.e crop it?*/
    private void getPicResults() {
        mCamera.takePicture(null, null, mPicture);

        /**Play camera's Beep sound*/
        MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.cam_beep);
        mp.start();
    }

}

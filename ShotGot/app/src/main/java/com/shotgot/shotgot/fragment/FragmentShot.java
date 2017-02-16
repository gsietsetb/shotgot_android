package com.shotgot.shotgot.fragment;

/**
 * Created by Guillermo on 9/1/17.
 */

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shotgot.shotgot.R;
import com.shotgot.shotgot.api.SocketIO;
import com.shotgot.shotgot.model.MetaModel;
import com.shotgot.shotgot.utils.CameraView;

import java.util.ArrayList;
import java.util.Random;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.shotgot.shotgot.utils.CameraView.getCameraInstance;

public class FragmentShot extends ImmersiveModeFragment {// implements CameraBridgeViewBase.CvCameraViewListener2 {

    ArrayList<MetaModel> metaList = new ArrayList<MetaModel>();
    private boolean previousSearch = false;
    private boolean retryFocus = false;
    private GridLayout tagsLayout;
    private GridLayout colorsLayout;
    private Random rand = new Random();
    /**
     * Native Android Camera
     */
    private Camera mCamera = null;
    private CameraView mCameraView = null;
    private Camera.PictureCallback mPictureCallback;
    private Camera.ShutterCallback mShutterCallback;

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
                        tagsLayout = (GridLayout) getActivity().findViewById(R.id.tagsLayout);
                        tagsLayout.setVisibility(View.VISIBLE);
                        colorsLayout = (GridLayout) getActivity().findViewById(R.id.colors);
                        colorsLayout.setVisibility(View.VISIBLE);
                    } else {
//                        tagsLayout.removeViews(0, nTags);
//                        colorsLayout.removeViews(0, nColorsClarifai);
                    }
                    previousSearch = true;

                    /**Converts JSON into MetaModel class by using GSON*/
                    Log.d("GSON", args[0].toString());
                    final Gson gson = new Gson();
                    MetaModel tagRx = gson.fromJson((args[0]).toString(), MetaModel.class);
                    if (tagRx.isNotEmpty()) {
                        if (tagRx.isColor()) {
//                            addRespColorLayout(tagRx);
                        } else {
                            addRespLabels(tagRx);
                        }
                    }
                    Log.d("API", "This is args: " + args[0]);
                }
            });
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_shot, container, false);

        /**OpenCV Camera version*/
//         getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
//         //        getActivity().setContentView(R.colorsLayout.fragment_computer_vision);
//         mOpenCvCameraView = (CameraBridgeViewBase) view.findViewById(R.id.CV_VIEW);
//         mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
//         mOpenCvCameraView.setCvCameraViewListener(this);

        /**InnerCamera Version*/
        /* Create an instance of Camera*/
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        if (mCamera != null) {
//            mShutterCallback = new Camera.ShutterCallback() {
//                @Override
//                public void onShutter() {
//                    /**Play camera's Beep sound*/
//                    MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.cam_beep);
//                    mp.start();
//                }
//            };
            mPictureCallback = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    /**PostProcess Captured Img, i.e crop it?*/
                    socketSendImg(data);
                    /**Sets default AUTO Flash mode back*/
                    mCameraView.toggleFlash();
                }
            };

            mCameraView = new CameraView(getActivity().getBaseContext(), mCamera);
            FrameLayout camera_view = (FrameLayout) view.findViewById(R.id.camera_preview);
            camera_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPicResults();
                }
            });
            camera_view.addView(mCameraView);//add the SurfaceView to the colorsLayout

            // Trap the capture button.
            view.findViewById(R.id.button_capture).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPicResults();
                }
            });
            final ImageView flashIcon = (ImageView) view.findViewById(R.id.iconFlash);
            flashIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCameraView.toggleFlash();
                    if (mCameraView.flashOn)
                        flashIcon.setAlpha((float) 0.6);
                }
            });

        }

        /** Socket init*/
        SocketIO appSocket = new SocketIO();
        mSocket = appSocket.getSocket();
        mSocket.on("METADATA", onMetadataResp);
        mSocket.connect();
        return view;
    }

    /**
     * Sets the functionality to take a picture and call
     * 'socketSendImg' method afterwards
     */
    private void getPicResults() {
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    camera.takePicture(/*mShutterCallback*/null, null, mPictureCallback);
                }
//                }else{
//                    Log.d("FOCUS", "Error focusing the camera");
//                    if(!retryFocus)
//                        getPicResults();
//                    retryFocus=true;
//                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this.getActivity(), mLoaderCallback);
    }

    @Override
    public void onPause() {
        super.onPause();
        /**@deprecated ToUndo*/
//        if (mOpenCvCameraView != null)
//        mOpenCvCameraView.disableView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**@deprecated ToUndo*/
//        if (mOpenCvCameraView != null)
//        mOpenCvCameraView.disableView();
        mSocket.disconnect();
        mSocket.off("disconnect", onMetadataResp);
    }

    private void addRespLabels(MetaModel tags) {
        Log.d("AddView", "Trying to add " + tags.toString());
        Log.d("AddView", "Seems to be Cloudsiht" + tags.toString());
        TextView tagView = new TextView(getContext());
        tagView.setX(rand.nextInt(400));
        tagView.setY(rand.nextInt(800));
        tagView.setTextColor(getResources().getColor(R.color.wallet_holo_blue_light));
        tagView.setText(tags.getData().toString());
        tagsLayout.addView(tagView);
    }

//    private void addRespColorLayout(MetaModel colorWrapper) {
//        for (int col : colorWrapper.getData()) {
//            RadioButton rButt = new RadioButton(getContext());
//            rButt.setBackgroundColor(col);
//            colorsLayout.addView(rButt, ++nColorsClarifai);
//        }
//    }

    /**
     * Through Socket.io to api.shotgot.com API
     */
    private void socketSendImg(byte[] data) {
        mSocket.connect();
        String imgString = Base64.encodeToString(data, Base64.NO_WRAP);
        mSocket.emit("PIC_REQ", imgString);
    }

}

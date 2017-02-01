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
import android.widget.Toast;

import com.shotgot.shotgot.API.SocketPic;
import com.shotgot.shotgot.R;
import com.shotgot.shotgot.Utils.CameraView;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FragmentShot extends ImmersiveModeFragment {
    private Camera mCamera = null;
    private CameraView mCameraView = null;
    private Camera.PictureCallback mPicture;
    /**
     * For NodeJS API api.shotgot.com
     */
    private Socket mSocket;
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    TODO JSONObject data = (JSONObject) args[0];
//                    try {
//                        String tags = data.getString("tags");
                    addRespToFragment(args[0].toString());
//                    } catch (JSONException e) {
                    return;
//                    }
                }
            });
        }
    };

    /**
     * Safe method for getting a camera instance.
     *
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

    private void addRespToFragment(String resp) {
        Toast.makeText(getActivity().getApplicationContext(),
                resp, Toast.LENGTH_LONG).show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shot, container, false);

        // Create an instance of Camera
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

    private void getPicResults() {
        mCamera.takePicture(null, null, mPicture);

        /**Play camera's Beep sound*/
        MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.cam_beep);
        mp.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }
}

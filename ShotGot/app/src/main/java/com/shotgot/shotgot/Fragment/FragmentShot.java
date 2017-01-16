package com.shotgot.shotgot.Fragment;

/**
 * Created by Guillermo on 9/1/17.
 */

import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.shotgot.shotgot.R;
import com.shotgot.shotgot.Utils.CameraView;

public class FragmentShot extends ImmersiveModeFragment {
    private Camera mCamera = null;
    private CameraView mCameraView = null;

    /**
     * Safe method for getting a camera instance.
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shot, container, false);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        if (mCamera != null) {
            mCameraView = new CameraView(getActivity().getBaseContext(), mCamera);

            FrameLayout camera_view = (FrameLayout) view.findViewById(R.id.camera_preview);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }

        // Trap the capture button.
        Button captureButton = (Button) view.findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
//                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );

        return view;
    }

}

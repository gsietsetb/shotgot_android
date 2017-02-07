package com.shotgot.shotgot.Utils;

/**
 * Created by Guillermo on 9/1/17.
 */

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.height;
import static android.R.attr.width;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private final SurfaceHolder mHolder;
    private final Camera mCamera;
    private Camera.Size mPreviewSize;
    private List<Camera.Size> mSupportedPreviewSizes;

    public CameraView(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setParameters(defineParameters(mCamera.getParameters()));
            Log.d("CAMERA", "PARAMS set:");
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("CAMERA", "Error setting camera preview: " + e.getMessage());
        }
    }

    @NonNull
    private Camera.Parameters defineParameters(Camera.Parameters mParams) {
        /**Sizes*/
        mPreviewSize = mParams.getPreviewSize();
        mSupportedPreviewSizes = mParams.getSupportedPreviewSizes();
        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes,
                    mPreviewSize.width,
                    mPreviewSize.height);
            if (mPreviewSize != null) {
                mParams.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            }
        }

        /**Rotation*/
        mParams.setRotation(0);
        Log.e("CAMERA", "PRE ROTATION - Width: " + width + " Height:" + height);

        /**Focus*/
        List<String> focusModes = mParams.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        Rect centerRect = new Rect(
                (this.getWidth() / 2 - 100),
                (this.getHeight() / 2 - 100),
                (this.getWidth() / 2 + 100),
                (this.getHeight() / 2 + 100));
        final Rect targetFocusRect = new Rect(
                centerRect.left * 2000 / this.getWidth() - 1000,
                centerRect.top * 2000 / this.getHeight() - 1000,
                centerRect.right * 2000 / this.getWidth() - 1000,
                centerRect.bottom * 2000 / this.getHeight() - 1000);
        ArrayList<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
        focusAreas.add(new Camera.Area(targetFocusRect, 1000));
        mParams.setFocusAreas(focusAreas);

        /**Flash*/
        List<String> mSupportedFlashModes = mCamera.getParameters().getSupportedFlashModes();
        if (mSupportedFlashModes != null && mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        }
        return mParams;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            Rect touchRect = new Rect(
                    (int) (x - 100),
                    (int) (y - 100),
                    (int) (x + 100),
                    (int) (y + 100));

            final Rect targetFocusRect = new Rect(
                    touchRect.left * 2000 / this.getWidth() - 1000,
                    touchRect.top * 2000 / this.getHeight() - 1000,
                    touchRect.right * 2000 / this.getWidth() - 1000,
                    touchRect.bottom * 2000 / this.getHeight() - 1000);
            ArrayList<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
            focusAreas.add(new Camera.Area(targetFocusRect, 1000));
            Camera.Parameters mParams = mCamera.getParameters();
            mParams.setFocusAreas(focusAreas);
            mCamera.setParameters(mParams);
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d("CAMERA", "Error starting camera preview: " + e.getMessage());
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
//        final double ASPECT_TOLERANCE = 0.05;
//        double targetRatio = (double) w / h;
        Log.d("CAM", "Entering in getOptimal with w: " + w + " and h:" + h + " for sizes" + sizes);
        final double ASPECT_TOLERANCE = 1;//0.05;
        double targetRatio = (double) w / h;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
    }
}

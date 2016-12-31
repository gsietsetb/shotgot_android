package com.shotgot.shotgot;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.*;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean viewIsAtHome;

    SurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCamera();

        // Create our Preview view and set it as the content of our activity.
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initCamera() {
        Log.d("ACT_MAIN", "initCamera() start");
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.d("ACT_MAIN", "acquired cameraManager: " + cameraManager);

        String[] cameraIdList;
        try {
            cameraIdList = cameraManager.getCameraIdList();
        } catch (CameraAccessException e) {
            Log.e("ACT_MAIN", "couldn't get camera list", e);
            return;
        }
        Log.d("ACT_MAIN", "acquired cameraIdList: length: " + cameraIdList.length);

        if (cameraIdList.length == 0) {
            Log.w("ACT_MAIN", "couldn't detect a camera");
            return;
        }

        String camera0Id = cameraIdList[0];

        Log.d("ACT_MAIN", "chosen camera: " + camera0Id);

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraManager.openCamera(camera0Id, deviceCallback, null);
        } catch (CameraAccessException e) {
            Log.e("ACT_MAIN", "couldn't open camera", e);
        }
        Log.d("ACT_MAIN", "called cameraManager.openCamera()");
    }

    CameraDevice.StateCallback deviceCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            Log.d("ACT_MAIN", "deviceCallback.onOpened() start");

            mSurfaceView.getHolder().setFixedSize(previewSize.getWidth(), previewSize.getHeight());

            Surface surface = mSurfaceView.getHolder().getSurface();
            Log.d("ACT_MAIN", "surface: " + surface);

            List<Surface> surfaceList = Collections.singletonList(surface);

            try {
                camera.createCaptureSession(surfaceList, sessionCallback, null);
            } catch (CameraAccessException e) {
                Log.e("ACT_MAIN", "couldn't create capture session for camera: " + camera.getId(), e);
                return;
            }

        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.d("ACT_MAIN", "deviceCallback.onDisconnected() start");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.d("ACT_MAIN", "deviceCallback.onError() start");
        }

    };

    CameraCaptureSession.StateCallback sessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            Log.i("ACT_MAIN", "capture session configured: " + session);
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            Log.e("ACT_MAIN", "capture session configure failed: " + session);
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_ShotGot) {    //Augmented Reality
//            // Handle the camera action
//        } else if (id == R.id.nav_BotGot) {  //Augmented Reality
//
//        } else if (id == R.id.nav_WishGot) {  //Voice
//
//        } else if (id == R.id.nav_ShotWhat) {
//
//        } else if (id == R.id.nav_TrendGot) {   // Small Market
//
//        } else if (id == R.id.nav_AroundGot) {  //Google Maps
//
//        } else if (id == R.id.nav_TrendGot) {
//
//        } else if (id == R.id.nav_share) {
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        displayView(item.getItemId());
        return true;
    }

    public void displayView(int viewId){
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {

//        if (id == R.id.nav_ShotGot) {    //Augmented Reality
//            // Handle the camera action
//        } else if (id == R.id.nav_BotGot) {  //Augmented Reality
//
//        } else if (id == R.id.nav_WishGot) {  //Voice
//
//        } else if (id == R.id.nav_ShotWhat) {
//
//        } else if (id == R.id.nav_TrendGot) {   // Small Market
//
//        } else if (id == R.id.nav_AroundGot) {  //Google Maps
//
//        } else if (id == R.id.nav_TrendGot) {
//
//        } else if (id == R.id.nav_share) {
//
            case R.id.nav_ShotGot:
                setContentView(R.layout.activity_shot_got_ar);
//                fragment = new shotGotAr();
                title  = getString(R.string.title_activity_shot_got_ar);
                viewIsAtHome = false;
                break;

//            case R.id.nav_BotGot:
//                fragment = new EventsFragment();
//                title = getString(R.string.events_title);
//                viewIsAtHome = false;
//                break;
//
//            case R.id.nav_gallery:
//                fragment = new GalleryFragment();
//                title = getString(R.string.gallery_title);
//                viewIsAtHome = false;
//                break;
        }
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }
}

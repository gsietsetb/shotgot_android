package com.shotgot.shotgot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.Menu;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import android.os.Build;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private boolean viewIsAtHome;
    final View decorView = this.getWindow().getDecorView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorView.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int i) {
                        int height = decorView.getHeight();
                        Log.i("", "Current height: " + height);
                    }
                });
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        initCamera();

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

//    public static void saveToPreferences(Context context, String key, Boolean allowed) {
//        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF,
//                Context.MODE_PRIVATE);
//        SharedPreferences.Editor prefsEditor = myPrefs.edit();
//        prefsEditor.putBoolean(key, allowed);
//        prefsEditor.commit();
//    }
//
//    public static Boolean getFromPref(Context context, String key) {
//        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF,
//                Context.MODE_PRIVATE);
//        return (myPrefs.getBoolean(key, false));
//    }
//
//private void showSettingsAlert() {
//        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
//        alertDialog.setTitle("Alert");
//        alertDialog.setMessage("App needs to access the Camera.");
//
//        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
//        new DialogInterface.OnClickListener() {
//
//public void onClick(DialogInterface dialog, int which) {
//        dialog.dismiss();
//        //finish();
//        }
//        });
//
//        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
//        new DialogInterface.OnClickListener() {
//
//public void onClick(DialogInterface dialog, int which) {new DialogInterface.OnClickListener() {
//public void onClick(DialogInterface dialog, int which) {
//        dialog.dismiss();
//        finish();
//        dialog.dismiss();
//        startInstalledAppDetailsActivity(MainActivity.this);
//        }
//        });
//
//        alertDialog.show();
//        }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_CAMERA: {
//                for (int i = 0, len = permissions.length; i < len; i++) {
//                    String permission = permissions[i];
//
//                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                        boolean
//                                showRationale =
//                                ActivityCompat.shouldShowRequestPermissionRationale(
//                                        this, permission);
//
//                        if (showRationale) {
//                            showAlert();
//                        } else if (!showRationale) {
//                            // user denied flagging NEVER ASK AGAIN
//                            // you can either enable some fall back,
//                            // disable features of your app
//                            // or open another dialog explaining
//                            // again the permission and directing to
//                            // the app setting
//                            saveToPreferences(MainActivity.this, ALLOW_KEY, true);
//                        }
//                    }
//                }
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
////    }
//
//    public static void startInstalledAppDetailsActivity(final Activity context) {
//        if (context == null) {
//            return;
//        }
//
//        final Intent i = new Intent();
//        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        i.addCategory(Intent.CATEGORY_DEFAULT);
//        i.setData(Uri.parse("package:" + context.getPackageName()));
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//        context.startActivity(i);
//    }
@Override
public boolean onOptionsItemSelected(MenuItem item) {

    return true;
}

    /**
     * Detects and toggles immersive mode (also known as "hidey bar" mode).
     */
    public void toggleHideyBar() {

        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions =this.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        boolean isImmersiveModeEnabled =
                ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        if (isImmersiveModeEnabled) {
            Log.i("", "Turning immersive mode mode off. ");
        } else {
            Log.i("", "Turning immersive mode mode on.");
        }

        // Navigation bar hiding:  Backwards compatible to ICS.
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }

        // Status bar hiding: Backwards compatible to Jellybean
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
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

            case R.id.nav_manage:
                toggleHideyBar();
                break;
            case R.id.nav_ShotGot:
                setContentView(R.layout.activity_shot_got_ar);
//                fragment = new shotGotAr();
                title  = getString(R.string.title_activity_shot_got_ar);
                viewIsAtHome = false;
                break;

            case R.id.nav_AroundGot:
                setContentView(R.layout.activity_maps);
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
}

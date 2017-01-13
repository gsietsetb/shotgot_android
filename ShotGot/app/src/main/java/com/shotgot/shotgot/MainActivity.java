package com.shotgot.shotgot;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.shotgot.shotgot.Fragment.FragmentAround;
import com.shotgot.shotgot.Fragment.FragmentShot;

public class MainActivity extends ActionBarActivity implements NavigationView.OnNavigationItemSelectedListener,
        FragmentAround.OnFragmentInteractionListener {

    /**
     * Actions
     */
    public static final int SELECT_PICTURE_ACTION = 0;
    public static final int SELECT_ITEM_ACTION = 1;
    public static final int SELECT_WISH_ACTION = 2;

    /**
     * Fragment Identifiers
     */
    public static final int SHOT_FRAGMENT = 0;
    public static final int BOT_FRAGMENT = 1;
    public static final int WHAT_FRAGMENT = 2;
    public static final int AROUND_FRAGMENT = 3;
    public static final int ACCOUNTS_SETTINGS_FRAGMENT = 4;

    /*
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
    private NavigationDrawerFragment mNavigationDrawerFragment;
    */

    public CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**Set Fullscreen inmersive mode*/
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_main);

        /**Toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toggleHideyBar();

        /**For the Lateral menu */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /**Auto Shortcut to Camera mode*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabShot);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNavigationItemSelected(navigationView.getMenu().getItem(0));
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });



        mTitle = getTitle();

        /**InCase of using custom navDrawerFragment
         mNavigationDrawerFragment = (NavigationDrawerFragment)
         getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

         // Set up the drawer.
         mNavigationDrawerFragment.setUp(
         R.id.navigation_drawer,
         (DrawerLayout) findViewById(R.id.drawer_layout));
         */

        /**Fragment management
        Fragment fragment = new FragmentShot(); // create a fragement object
        FragmentTransaction ft = getFragmentManager().beginTransaction();
         ft.replace(R.id.content_main, fragment);
         ft.commit();*/
    }

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
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment targetFragment = null;
        FragmentManager fragmentManager = getFragmentManager();
        switch (item.getItemId()) {
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
            case R.id.nav_ShotGot:
                targetFragment = new FragmentShot();
                //FragmentShot.newInstance(item.getItemId());
                mTitle = getString(R.string.title_Shot);
                //setContentView(R.layout.activity_shot_got_ar);
                break;
            case R.id.nav_AroundGot:
                targetFragment = new FragmentAround();
                mTitle = getString(R.string.title_Around);
                //setContentView(R.layout.activity_maps);
                break;

            default:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
        }

        /**Todo Refactor
         *

         case R.id.nav_BotGot:
         targetFragment = new FragmentShot.newInst(item.getItemId());
         mTitle = getString(R.string.title_Bot);
         //setContentView(R.layout.activity_maps);
         break;
         case R.id.nav_BotGot:
         targetFragment = new EventsFragment();
         title = getString(R.string.events_title);
         viewIsAtHome = false;
         break;

         case R.id.nav_gallery:
         targetFragment = new GalleryFragment();
         title = getString(R.string.gallery_title);
         viewIsAtHome = false;
         break;
            case R.id.nav_manage:
                toggleHideyBar();
         break;
         // Handle navigation view item clicks here.
         int id = ;

         if (id == R.id.nav_ShotGot) {    //Augmented Reality
         // Handle the camera action
         } else if (id == R.id.nav_BotGot) {  //Augmented Reality

         } else if (id == R.id.nav_WishGot) {  //Voice

         } else if (id == R.id.nav_ShotWhat) {

         } else if (id == R.id.nav_TrendGot) {   // Small Market

         } else if (id == R.id.nav_AroundGot) {  //Google Maps

         } else if (id == R.id.nav_TrendGot) {

         } else if (id == R.id.nav_share) {

         }*/

//Select the targetFragment.
        fragmentManager.beginTransaction()
                .replace(R.id.mainFrame, targetFragment)
                .commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
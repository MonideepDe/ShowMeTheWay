package com.monideepde.showmetheway;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class HomeActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String TAG_HOME_ACTIVITY = HomeActivity.class.getSimpleName();
    public static final String DESTINATION_NAME = "com.monideepde.showmetheway.DESTINATION_NAME";

    int mFragmentSelectedPos=0;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Log.d(TAG_HOME_ACTIVITY, "Position=" + position);
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch(position) {
            case 0:
                mFragmentSelectedPos=0;
                fragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment.newInstance())
                        .commit();
                break;
//            case 1:
//                //Same as case 0:
//                mFragmentSelectedPos=1;
//                fragmentManager.beginTransaction()
//                        .replace(R.id.container, HomeFragment.newInstance())
//                        .commit();
//                break;
            case 1:
                mFragmentSelectedPos=1;
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AboutFragment.newInstance())
                        .commit();
                //getSupportActionBar().setTitle("Hero");

                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
//            case 2:
//                mTitle = getString(R.string.title_section2);
//                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {

        if(mFragmentSelectedPos == 1) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(getString(R.string.title_section3_actionbar));
        } else {

            ActionBar actionBar = getSupportActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    public void goToDestination(View v) {
        Log.d(TAG_HOME_ACTIVITY, "Entering goToDestination");
        //Check the value from Spinner
        Spinner s = (Spinner) findViewById(R.id.destinationList_spinner);
        String dest = (String) s.getSelectedItem();
        if(dest != null) {
            Log.d(TAG_HOME_ACTIVITY, "Spinner value = " + dest);
        } else {
            Log.d(TAG_HOME_ACTIVITY, "Spinner value is null");
        }

        if(dest.equals("Add a new destination")) {
            Log.d(TAG_HOME_ACTIVITY, "Hurray! Add a new destination");

            Intent intent = new Intent(getApplicationContext(), LocationAddActivity.class);
            startActivity(intent);

        } else {
            //Go to Maps
            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
            intent.putExtra(DESTINATION_NAME, dest);
            startActivity(intent);
        }
    }


}

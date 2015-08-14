package me.zujko.locchat.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zujko.locchat.R;
import me.zujko.locchat.fragments.ChatroomFragment;
import me.zujko.locchat.fragments.LocationFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment currentFragment;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.fab) FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.title_chatroom));

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentFragment instanceof ChatroomFragment) {
                    startActivity(new Intent(getApplicationContext(), NewChatroomActivity.class));
                }
            }
        });

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //Checks if GPS is enabled
        if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if(savedInstanceState != null) {
                currentFragment = getSupportFragmentManager().getFragment(savedInstanceState,"fragment");
            } else {
                currentFragment = new ChatroomFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
            }
        } else {
            createNoGpsAlert();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "fragment", currentFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                //Do something
                return true;
            case R.id.action_location_settings:
                currentFragment = new LocationFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).addToBackStack(null).commit();
                getSupportActionBar().setTitle(getString(R.string.fragment_name_Location));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(currentFragment instanceof LocationFragment) {
            getSupportActionBar().setTitle(getString(R.string.title_chatroom));
        }
        super.onBackPressed();
    }

    /**
     * Creates an alert dialog alerting the user that they must enable location services.
     */
    private void createNoGpsAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.error_title_gps_disabled))
                .setMessage(getString(R.string.error_message_gps_disabled))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
        .setCancelable(false);

        AlertDialog mNoGpsAlertDialog = builder.create();
        mNoGpsAlertDialog.show();
    }
}

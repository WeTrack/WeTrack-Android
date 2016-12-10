package com.wetrack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.wetrack.service.LocationServiceManager;
import com.wetrack.utils.Tags;

public class SettingActivity extends AppCompatActivity {
    private ImageButton backButton = null;
    private Switch shareLocSwitch = null;

    private LocationServiceManager mLocationServiceManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        backButton = (ImageButton) findViewById(R.id.setting_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });

        shareLocSwitch = (Switch) findViewById(R.id.setting_share_loc_switch);
        shareLocSwitch.setChecked(true);
        shareLocSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mLocationServiceManager != null) {
                    if (isChecked) {
                        mLocationServiceManager.setShareLocPermission(true);
                        Toast.makeText(SettingActivity.this,
                                "Now you are sharing your locations", Toast.LENGTH_SHORT).show();
                    } else {
                        mLocationServiceManager.setShareLocPermission(false);
                        Toast.makeText(SettingActivity.this,
                                "Now you don't share your locations any more", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mLocationServiceManager = new LocationServiceManager(this) {
            @Override
            public void onReceivedLocation(com.wetrack.model.Location location) {}
        };
        mLocationServiceManager.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mLocationServiceManager.stop();
        mLocationServiceManager = null;
    }
}

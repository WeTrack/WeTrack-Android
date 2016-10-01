package com.wetrack;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.wetrack.map.MapController;
import com.wetrack.map.MapFragment;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMapInView(R.id.map_content);
    }

    public void initMapInView(int viewId) {
        MapController.getInstance(this).addMapToView(getSupportFragmentManager(), viewId);
    }

}

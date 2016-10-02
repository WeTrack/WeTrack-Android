package com.wetrack;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.wetrack.map.MapController;


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

    @Override
    protected void onResume() {
        super.onResume();
        MapController.getInstance(this).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MapController.getInstance(this).stop();
    }



}

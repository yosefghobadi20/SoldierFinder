package com.nikosoft.soldierfinder;

import android.app.Application;

/**
 * Created by Yosef on 20/01/2019.
 */

public class SoldierFinder extends Application {

    String font="iransans_web_light.ttf";
    @Override
    public void onCreate() {
        super.onCreate();

        FontsOverride.setDefaultFont(this, "DEFAULT", font);
        FontsOverride.setDefaultFont(this, "MONOSPACE", font);
        FontsOverride.setDefaultFont(this, "SERIF", font);
        FontsOverride.setDefaultFont(this, "SANS_SERIF", font);


    }
}

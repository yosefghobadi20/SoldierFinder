package com.nikosoft.soldierfinder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.provider.ContactsContract;

import ir.myteam.adsdk.AdCommon;

public class SplashScreen extends Activity {

    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private Utility utility;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        utility = new Utility(this);
        context = this;

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //detect app first run
                G g = new G();
                G.FirstRun = RetrieveFirstRun();
                if (G.FirstRun)
                    SaveFirstRun(false);
//                G.context=SplashScreen.this;
//                Soldier_Info soldier_info = utility.Deserialize_soldier_info(utility.GetSoldierInfo(utility.getUniquePsuedoID()));
//                if (soldier_info.Name == null)
//                {
//                    Intent mainIntent = new Intent(SplashScreen.this, profile.class);
//                    SplashScreen.this.startActivity(mainIntent);
//                }
//                else
//                    {

                G.GetNewSoldiers(context);


                Intent mainIntent = new Intent(SplashScreen.this, Main.class);
                SplashScreen.this.startActivity(mainIntent);
                //}
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    public void SaveFirstRun(boolean Value) {
        SharedPreferences pref = this.getSharedPreferences("firstrun", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("FirstRun", Value);

        editor.apply();
    }

    public boolean RetrieveFirstRun() {
        try {
            SharedPreferences pref = this.getSharedPreferences("firstrun", MODE_PRIVATE);
            boolean value = pref.getBoolean("FirstRun", true);
            return value;
        } catch (Exception ex) {
            return true;
        }

    }

}

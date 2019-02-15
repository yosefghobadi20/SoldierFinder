package com.nikosoft.soldierfinder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class G extends AppCompatActivity {

    public  static ArrayList<Soldier_Info> AllSoldiers = new ArrayList<>();
    public  static ArrayList<Soldier_Info> AppropriateSoldiers = new ArrayList<>();
    public static Context context;
    public static int LastID=0;
    public static Activity CurrentActivity;
    public static boolean FirstRun=true;
    //search fields
    public static String cur_state="";
    public static String cur_city="";
    public static String req_state="";
    public static String req_city="";
    public static int rate=0;
    public static int organ=0;
    public static int sub_organ=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
CurrentActivity=this;

    }
    public static void GetNewSoldiers(Context context)
    {
        Utility utility=new Utility(context);

        if( Utility.isNetworkAvailable(context))
        {
            //get Soldiers from server and show them in list
            String res = utility.call("GetAllSoldiers");
            Log.i("Res :",res);
            if (res.equals("error"))
                Main.ShowMessage ("خطا در خواندن اطلاعات از سرور");
            else if (res.equals("[]"))
                Main.ShowMessage ("") ;
            else {
                AllSoldiers.clear();
                //G.LastID=0;
                String s = utility.Deserialize(res,AllSoldiers);
                if (s.equals("error"))
                    Main.ShowMessage ( "خطا در نوشتن اطلاعات");

            }
        }
        else
            Main.ShowMessage("اتصال اینترنت برقرار نیست");
    }


}

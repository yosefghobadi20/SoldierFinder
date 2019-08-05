package com.nikosoft.soldierfinder;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class frag_nav extends Fragment {

    public DrawerLayout dLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView ver;
    private View view;

    public frag_nav() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.frag_nav,container,false);
        ver=(TextView) view.findViewById(R.id.txt_ver);
        String version= BuildConfig.VERSION_NAME;
        Log.i("version=",version+"");
        ver.setText(" نرم افزار سرباز یاب نسخه "+version+" تهیه شده توسط گروه نرم افزاری نیکوسافت ");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_nav, container, false);

    }

    public void Install(DrawerLayout drawerLayout,Toolbar toolbar) {
        dLayout=drawerLayout;

        actionBarDrawerToggle=new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.open,R.string.close)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        dLayout.setDrawerListener(actionBarDrawerToggle);
        dLayout.post(new Runnable() {
            @Override
            public void run() {

                actionBarDrawerToggle.syncState();
            }
        });
    }


}

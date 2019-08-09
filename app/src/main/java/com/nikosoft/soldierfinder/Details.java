package com.nikosoft.soldierfinder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.core.content.ContextCompat;
//import android.support.v7.app.ActionBarActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.getbase.floatingactionbutton.FloatingActionButton;
//import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;


public class Details extends G {
    public Intent intent;
    private String ID = "";

    private FloatingActionButton fab_call;
    private FloatingActionButton fab_sms;

    private Toolbar toolbar;
    private TextView txt_name;
    private TextView txt_family;
    private TextView txt_cur_state;
    private TextView txt_cur_city;
    private TextView txt_cur_padegan;
    private TextView txt_req_state;
    private TextView txt_req_city;
    private TextView txt_req_padegan;
    private TextView txt_sendout_date;
    private TextView txt_req_date;
    private TextView txt_phone;
    private TextView txt_rate;
    private TextView txt_organ;
    //private TextView txt_description;
    private TextView txt_title;

    private ImageView img_organ;
    private ImageView img_rate;
    //for find that user clicked on fav button
    private boolean favorite = false;
    public Rate_Organ rate_organ = new Rate_Organ();
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        Utility utility=new Utility(G.context);

        toolbar = findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        collapsingToolbarLayout=findViewById(R.id.collaps_detail);
        collapsingToolbarLayout.setExpandedTitleTextColor(ContextCompat.getColorStateList(this,android.R.color.transparent));
        collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColorStateList(this,R.color.textColor_light));
        //collapsingToolbarLayout.setTitle("اطلاعات سرباز");

//
//
//        if(getSupportActionBar()!=null)
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        fab_call = (FloatingActionButton) findViewById(R.id.fab_call);
        fab_sms =(FloatingActionButton)  findViewById(R.id.fab_sms);
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_family = (TextView) findViewById(R.id.txt_lastname);
        txt_cur_state = (TextView) findViewById(R.id.txt_cur_state);
        txt_cur_city = (TextView)  findViewById(R.id.txt_cur_city);
        txt_cur_padegan = (TextView)  findViewById(R.id.txt_cur_padegan);
        txt_req_state =  (TextView) findViewById(R.id.txt_req_state);
        txt_req_city = (TextView)  findViewById(R.id.txt_req_city);
        txt_req_padegan =  (TextView) findViewById(R.id.txt_req_padegan);
        txt_sendout_date = (TextView)  findViewById(R.id.txt_sendout_date);
        txt_req_date =  (TextView) findViewById(R.id.txt_request_date);
        txt_phone =  (TextView) findViewById(R.id.txt_phone);
        txt_rate = (TextView)  findViewById(R.id.txt_rate);
        txt_organ =  (TextView) findViewById(R.id.txt_organ);
        //txt_description = (TextView) findViewById(R.id.txt_description);
        //txt_title = (TextView) findViewById(R.id.txt_title);
        img_organ = (ImageView) findViewById(R.id.img_organ);
        img_rate = (ImageView) findViewById(R.id.img_rate);
        intent = getIntent();

        Log.i("ID=", intent.getIntExtra("ID", -1) + "");
        ID = intent.getIntExtra("ID", -1) + "";
        txt_name.setText(intent.getStringExtra("Name"));
        txt_family.setText(intent.getStringExtra("Family"));
        txt_cur_state.setText(intent.getStringExtra("CurrentState"));
        txt_cur_city.setText(intent.getStringExtra("CurrentCity"));
        txt_cur_padegan.setText(intent.getStringExtra("CurrentPadegan"));
        txt_req_state.setText(intent.getStringExtra("RequestState"));
        txt_req_city.setText(intent.getStringExtra("RequestCity"));
        txt_req_padegan.setText(intent.getStringExtra("RequestPadegan"));
        txt_sendout_date.setText(intent.getStringExtra("Send_OutDate"));
        txt_req_date.setText(intent.getStringExtra("RequestDate"));
        if(utility.RetrievePref(Utility.PAID).equals("ok"))
            txt_phone.setText(intent.getStringExtra("Phone"));
        else
            txt_phone.setText("برنامه را ارتقا دهید");
        //txt_description.setText(intent.getStringExtra("Description"));

        int rate = intent.getIntExtra("Rate", -1);
        switch (rate) {
            case -1:
                txt_rate.setText("");

                break;
            case 1:
                txt_rate.setText("سرباز سوم");
                img_rate.setImageResource(R.drawable.s3);
                break;
            case 2:
                txt_rate.setText("سرباز دوم");
                img_rate.setImageResource(R.drawable.s2);
                break;
            case 3:
                txt_rate.setText("سرباز یکم");
                img_rate.setImageResource(R.drawable.s1);
                break;
            case 4:
                txt_rate.setText("سرجوخه");
                img_rate.setImageResource(R.drawable.sj);
                break;
            case 5:
                txt_rate.setText("گروهبان سوم");
                img_rate.setImageResource(R.drawable.g3);
                break;
            case 6:
                txt_rate.setText("گروهبان دوم");
                img_rate.setImageResource(R.drawable.g2);
                break;
            case 7:
                txt_rate.setText("گروهبان یکم");
                img_rate.setImageResource(R.drawable.g1);
                break;
            case 8:
                txt_rate.setText("استوار دوم");
                img_rate.setImageResource(R.drawable.os2);
                break;
            case 9:
                txt_rate.setText("استوار یکم");
                img_rate.setImageResource(R.drawable.os1);
                break;
            case 10:
                txt_rate.setText("ستوان سوم");
                img_rate.setImageResource(R.drawable.so3);
                break;
            case 11:
                txt_rate.setText("ستوان دوم");
                img_rate.setImageResource(R.drawable.so2);
                break;
            case 12:
                txt_rate.setText("ستوان یکم");
                img_rate.setImageResource(R.drawable.so1);
                break;
        }

        int organ = intent.getIntExtra("Organ", -1);
        String organs = "";
        switch (organ) {
            case -1:

                img_organ.setImageResource(R.drawable.nikosoft);
                break;
            case 1:
                organs = "ارتش جمهوری اسلامی ایران";
                img_organ.setImageResource(R.drawable.artesh);
                break;
            case 2:
                organs = "سپاه پاسداران انقلاب اسلامی";
                img_organ.setImageResource(R.drawable.sepah);
                break;
            case 3:
                organs = "نیروی انتظامی جمهوری اسلامی ایران";
                img_organ.setImageResource(R.drawable.naja);
                break;
        }

        int sub_organ = intent.getIntExtra("Sub_Organ", -1);
        String sub_organs = rate_organ.GetSubOrgan(organ, sub_organ);

        txt_organ.setText(organs + " - " + sub_organs);
//        txt_title.setText(txt_organ.getText());


        fab_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + txt_phone.getText()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }
        });

        fab_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
                        + txt_phone.getText())));
            }
        });


    }


    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home)
            //NavUtils.navigateUpFromSameTask(this);
            onBackPressed();
//        if (id == R.id.action_favorite)
//            try {
//
//
//                if (favorite) {
//                    favorite = false;
//                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.fav_off));
//                    FavoriteDelete(ID);
//                } else {
//                    favorite = true;
//                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.fav_on));
//                    FavoriteAdd(ID);
//                }
//            }
//            catch (Exception e)
//            {
//
//            }
        return super.onOptionsItemSelected(item);
    }


    private void FavoriteAdd(String id) {
        Utility utility = new Utility(G.context);
        String s = utility.readFromFile() + id + ",";
        utility.writeToFile(s);
    }

    private void FavoriteDelete(String id) {
        Utility utility = new Utility(G.context);
        String all = utility.readFromFile();

        String[] allID = all.split(",");
        String str = "";
        for (int i = 0; i < allID.length; i++) {
            if (allID[i].equals(id)) {
                allID[i] = "";
            }
            if (allID[i].equals("") == false) {
                str += allID[i] + ",";
            }

        }
        utility.writeToFile(str);
    }

    private boolean isFavorite(String id) {
        Utility utility = new Utility(G.context);
        String all = utility.readFromFile();
        String[] allID = all.split(",");
        boolean exist = false;
        for (int i = 0; i < allID.length; i++) {
            String s = allID[i];
            exist = s.equals(id);

            if (exist)
                return true;

        }
        return false;
    }
}
















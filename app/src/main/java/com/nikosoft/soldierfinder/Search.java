package com.nikosoft.soldierfinder;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import de.mrapp.android.dialog.MaterialDialog;
import de.mrapp.android.dialog.ProgressDialog;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;
import smtchahal.materialspinner.MaterialSpinner;

public class Search extends G {

    private MaterialSpinner sp_cur_state;
    private MaterialSpinner sp_cur_city;
    private MaterialSpinner sp_req_state;
    private MaterialSpinner sp_req_city;
    private MaterialSpinner sp_rate;
    private MaterialSpinner sp_organ;
    private MaterialSpinner sp_suborgan;

    private AppCompatButton btn_search;
    private States_Cities states_cities = new States_Cities();

    private ArrayAdapter<String> curCitiesAdapter;
    private ArrayAdapter<String> curIranStateAdpter;
    private ArrayAdapter<String> reqCitiesAdapter;
    private ArrayAdapter<String> reqIranStateAdpter;
    private ArrayAdapter<String> rateAdapter;
    private ArrayAdapter<String> organAdapter;
    private ArrayAdapter<String> suborganAdapter;

    private PersianDatePickerDialog persianDatePickerDialog;
    private PersianCalendar initDate;


//    private MaterialEditText txt_name;
//    private MaterialEditText txt_lastname;
//    private MaterialEditText txt_cur_padegan;
//    private MaterialEditText txt_req_padegan;

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
//Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp_cur_state = (MaterialSpinner) findViewById(R.id.sp_search_cur_state);
        sp_cur_city = (MaterialSpinner) findViewById(R.id.sp_search_cur_city);
        sp_req_state = (MaterialSpinner) findViewById(R.id.sp_search_req_state);
        sp_req_city = (MaterialSpinner) findViewById(R.id.sp_search_req_city);

        sp_rate = (MaterialSpinner) findViewById(R.id.sp_search_rate);
        sp_organ = (MaterialSpinner) findViewById(R.id.sp_search_organ);
        sp_suborgan = (MaterialSpinner) findViewById(R.id.sp_search_suborgan);

        btn_search = (AppCompatButton) findViewById(R.id.btn_search);
//        txt_name = (MaterialEditText) findViewById(R.id.txt_search_name);
//        txt_lastname = (MaterialEditText) findViewById(R.id.txt_search_lastname);
//        txt_cur_padegan = (MaterialEditText) findViewById(R.id.txt_search_cur_padegan);
//        txt_req_padegan = (MaterialEditText) findViewById(R.id.txt_search_req_padegan);

        //Iran States Spinner
        curIranStateAdpter = new ArrayAdapter<String>(
                G.context, android.R.layout.simple_spinner_dropdown_item, states_cities.IranStates);
        curIranStateAdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_cur_state.setAdapter(curIranStateAdpter);

        reqIranStateAdpter = new ArrayAdapter<String>(
                G.context, android.R.layout.simple_spinner_dropdown_item, states_cities.IranStates);
        reqIranStateAdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_req_state.setAdapter(reqIranStateAdpter);
        //Cities of selected state

        curCitiesAdapter = new ArrayAdapter<String>(
                G.context, android.R.layout.simple_spinner_dropdown_item);

        curCitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_cur_city.setAdapter(curCitiesAdapter);

        reqCitiesAdapter = new ArrayAdapter<String>(
                G.context, android.R.layout.simple_spinner_dropdown_item);

        reqCitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_req_city.setAdapter(reqCitiesAdapter);

        //Current Satet spinner item selection
        sp_cur_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                curCitiesAdapter.clear();
                States_Cities states_cities = new States_Cities();
                curCitiesAdapter.addAll(states_cities.GetStatesCities(position));

                //sp_cur_city.setSelection(0);
                curCitiesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //Request State Spinner item selection
        sp_req_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                reqCitiesAdapter.clear();
                States_Cities states_cities = new States_Cities();
                reqCitiesAdapter.addAll(states_cities.GetStatesCities(position));


                //sp_req_city.setSelection(0);
                reqCitiesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //fill rate spinner
        final Rate_Organ rate_organ = new Rate_Organ();
        rateAdapter = new ArrayAdapter<String>(
                G.context, android.R.layout.simple_spinner_dropdown_item, rate_organ.Rate);

        sp_rate.setAdapter(rateAdapter);
        //fill organ spinner
        organAdapter = new ArrayAdapter<String>(
                G.context, android.R.layout.simple_spinner_dropdown_item, rate_organ.Organ);

        sp_organ.setAdapter(organAdapter);

        //fill sub organ adapter
        suborganAdapter = new ArrayAdapter<String>(
                G.context, android.R.layout.simple_spinner_dropdown_item);

        sp_suborgan.setAdapter(suborganAdapter);

        sp_organ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                suborganAdapter.clear();
                suborganAdapter.addAll(rate_organ.GetSubOrgan(position+1));
                suborganAdapter.notifyDataSetChanged();
                //sp_suborgan.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //do search
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utility.isNetworkAvailable(Search.context))
                {
                    new DoSearch().execute();
                }
                else
                    Toast.makeText(Search.this, "اتصال اینترنت برقرار نیست!", Toast.LENGTH_SHORT).show();

            }
        });

        int pos = states_cities.GetStatePosition(G.cur_state, states_cities.IranStates);
        sp_cur_state.setSelection(pos+1 );

        curCitiesAdapter.addAll(states_cities.GetStatesCities(pos));;
        //get city position for selecting in cur city
        pos = states_cities.GetCityPosition(G.cur_city, G.cur_state);
        sp_cur_city.setSelection(pos+1);

        pos = states_cities.GetStatePosition(G.req_state, states_cities.IranStates);
        sp_req_state.setSelection(pos+1);

        reqCitiesAdapter.addAll(states_cities.GetStatesCities(pos));
        //get city position for selecting in req city
        pos = states_cities.GetCityPosition(G.req_city, G.req_state);
        sp_req_city.setSelection(pos+1);

        sp_rate.setSelection(G.rate);
        sp_organ.setSelection(G.organ);
        //get sub organ position for selecting in sub organ
        suborganAdapter.addAll(rate_organ.GetSubOrgan(G.organ));
        sp_suborgan.setSelection(G.sub_organ);
    }

    // sending searched soldier information to server in background

    class DoSearch extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        ProgressDialog.Builder dialogbuilder;
        Utility utility=new Utility(G.context);
        String cur_state;
        String cur_city;
        String req_state;
        String req_city;
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //show dialog for show connect progress
            dialogbuilder = new ProgressDialog.Builder(Search.this);
            dialogbuilder.setTitle("در حال جستجو");
            dialogbuilder.setMessage("لطفا صبر کنید...");
            dialogbuilder.setNegativeButton("انصراف", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Toast.makeText(profile.this, "cancel clicked!", Toast.LENGTH_SHORT).show();
                }
            });
            dialogbuilder.setProgressBarPosition(ProgressDialog.ProgressBarPosition.LEFT);
            dialogbuilder.setProgressBarColor(getResources().getColor(R.color.colorPrimary));
            dialogbuilder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
            dialogbuilder.setBackgroundColor(getResources().getColor(R.color.backColor));
            dialogbuilder.setTitleColor(getResources().getColor(android.R.color.white));
            dialogbuilder.setMessageColor(getResources().getColor(R.color.textColor));
            dialog = dialogbuilder.create();
            dialog.show();


        }

        @Override
        protected String doInBackground(Void... voids) {
            String res;
            try {

                if(sp_cur_state.getSelectedItemId()==0)
                    cur_state="";
                else
                    cur_state=sp_cur_state.getSelectedItem().toString();
                if(sp_cur_city.getSelectedItemId()==0)
                    cur_city="";
                else
                    cur_city=sp_cur_city.getSelectedItem().toString();
                if(sp_req_state.getSelectedItemId()==0)
                    req_state="";
                else
                    req_state=sp_req_state.getSelectedItem().toString();
                if(sp_req_city.getSelectedItemId()==0)
                    req_city="";
                else
                    req_city=sp_req_city.getSelectedItem().toString();
                Utility utility = new Utility(G.context);
                res = utility.Search(
                        cur_state,
                        cur_city,
                        req_state,
                        req_city,

                        (int)sp_rate.getSelectedItemId(),
                        (int)sp_organ.getSelectedItemId(),
                        (int)sp_suborgan.getSelectedItemId());
//                Log.i("cur_state =",cur_state);
//                Log.i("cur_city =",cur_city);
//                Log.i("req_state =",req_state);
//                Log.i("req_city =",req_city);
//                Log.i("rate =",sp_rate.getSelectedItemId()+"");
//                Log.i("organ =",sp_organ.getSelectedItemId()+"");
//                Log.i("suborgan =",sp_suborgan.getSelectedItemId()+"");
                if (res.equals("error"))
                    return "خطا در دریافت اطلاعات !";
            } catch (Exception e) {
                e.printStackTrace();
                return "خطا در برقراری ارتباط ! ";
            }
            return res;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            Log.i("Res= ",s);
            dialog.dismiss();
            G.AllSoldiers.clear();
            utility.Deserialize(s,G.AllSoldiers);
            Intent intent= new Intent(Search.context, Main.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //finish();
            Main.RefreshSoldiersList();
            Log.i("cur_state",G.cur_state);
            Log.i("cur_city",G.cur_city);
            Log.i("req_state",G.req_state);
            Log.i("req_city",G.req_city);
            Log.i("rate",G.rate+"");
            Log.i("organ",G.organ+"");
            Log.i("sub_organ",G.sub_organ+"");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_search).setVisible(false);
//        menu.findItem(R.id.action_menu).setVisible(false);
        //menu.findItem(R.id.action_favorite).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_back)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


}

package com.nikosoft.soldierfinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import de.mrapp.android.dialog.MaterialDialog;
import de.mrapp.android.dialog.ProgressDialog;
import de.mrapp.android.dialog.model.Dialog;
import ir.hamsaa.persiandatepicker.Listener;
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog;
import ir.hamsaa.persiandatepicker.util.PersianCalendar;
//import me.zhanghai.android.materialedittext.MaterialEditText;
import me.toptas.fancyshowcase.FancyShowCaseView;
import smtchahal.materialspinner.MaterialSpinner;


public class profile extends G {

    private Toolbar toolbar;

    private MaterialSpinner sp_cur_state;
    private MaterialSpinner sp_cur_city;
    private MaterialSpinner sp_req_state;
    private MaterialSpinner sp_req_city;
    private MaterialSpinner sp_rate;
    private MaterialSpinner sp_organ;
    private MaterialSpinner sp_suborgan;

    private AppCompatButton btn_sendout_date;

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


    private MaterialEditText txt_name;
    private MaterialEditText txt_lastname;
    private MaterialEditText txt_cur_padegan;
    private MaterialEditText txt_req_padegan;
    private MaterialEditText txt_phone;
    //private MaterialEditText txt_description;

    private SwitchCompat sw_cur_status;
    private SwitchCompat sw_movment_status;


    private AppCompatButton btn_submit;
    private boolean Move = false;
    private boolean Tarkhis = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (G.FirstRun) {
            Move = true;
            Tarkhis = true;
        }

        sp_cur_state = (MaterialSpinner) findViewById(R.id.sp_cur_state);
        sp_cur_city = (MaterialSpinner) findViewById(R.id.sp_cur_city);
        sp_req_state = (MaterialSpinner) findViewById(R.id.sp_req_state);
        sp_req_city = (MaterialSpinner) findViewById(R.id.sp_req_city);

        sp_rate = (MaterialSpinner) findViewById(R.id.sp_rate);
        sp_organ = (MaterialSpinner) findViewById(R.id.sp_organ);
        sp_suborgan = (MaterialSpinner) findViewById(R.id.sp_suborgan);

        btn_sendout_date = (AppCompatButton) findViewById(R.id.btn_sendout_date);

        txt_name = (MaterialEditText) findViewById(R.id.txt_name);
        txt_lastname = (MaterialEditText) findViewById(R.id.txt_lastname);
        txt_cur_padegan = (MaterialEditText) findViewById(R.id.txt_cur_padegan);
        txt_req_padegan = (MaterialEditText) findViewById(R.id.txt_req_padegan);
        //txt_description = (MaterialEditText) findViewById(R.id.txt_description);
        txt_phone = (MaterialEditText) findViewById(R.id.txt_phone);

        sw_cur_status = (SwitchCompat) findViewById(R.id.sw_cur_status);
        sw_movment_status = (SwitchCompat) findViewById(R.id.sw_movment_status);

        btn_submit = (AppCompatButton) findViewById(R.id.btn_submit);
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
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

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
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
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

        //fill sub organ spiiner
        suborganAdapter = new ArrayAdapter<String>(
                G.context, android.R.layout.simple_spinner_dropdown_item);

        //curCitiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_suborgan.setAdapter(suborganAdapter);
        sp_organ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                suborganAdapter.clear();
                Log.i("pos= ", position + "");
                suborganAdapter.addAll(rate_organ.GetSubOrgan(position + 1));
                suborganAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //recieve data from intent
        Intent intent = getIntent();
        if (intent != null) {
            States_Cities states_cities = new States_Cities();

            //intent.getStringExtra("ID");
            txt_name.setText(intent.getStringExtra("Name"));
            txt_lastname.setText(intent.getStringExtra("Family"));

            int pos = states_cities.GetStatePosition(intent.getStringExtra("CurrentState"), states_cities.IranStates);
            sp_cur_state.setSelection(pos + 1);

            curCitiesAdapter.addAll(states_cities.GetStatesCities(pos));

            //get city position for selecting in cur city
            pos = states_cities.GetCityPosition(intent.getStringExtra("CurrentCity"), intent.getStringExtra("CurrentState"));
            sp_cur_city.setSelection(pos + 1);

            txt_cur_padegan.setText(intent.getStringExtra("CurrentPadegan"));

            pos = states_cities.GetStatePosition(intent.getStringExtra("RequestState"), states_cities.IranStates);
            sp_req_state.setSelection(pos + 1);

            reqCitiesAdapter.addAll(states_cities.GetStatesCities(pos));
            //get city position for selecting in req city
            pos = states_cities.GetCityPosition(intent.getStringExtra("RequestCity"), intent.getStringExtra("RequestState"));
            sp_req_city.setSelection(pos + 1);

            txt_req_padegan.setText(intent.getStringExtra("RequestPadegan"));
            btn_sendout_date.setText(intent.getStringExtra("Send_OutDate"));
            txt_phone.setText(intent.getStringExtra("Phone"));
            sw_cur_status.setChecked(intent.getBooleanExtra("CurrentStatus", false));
            sw_movment_status.setChecked(intent.getBooleanExtra("MovmentStatus", false));
            sp_rate.setSelection(intent.getIntExtra("Rate", -1));
            sp_organ.setSelection(intent.getIntExtra("Organ", -1));
            //get sub organ position for selecting in sub organ
            suborganAdapter.addAll(rate_organ.GetSubOrgan(intent.getIntExtra("Organ", -1)));
            sp_suborgan.setSelection(intent.getIntExtra("Sub_Organ", -1));
            //txt_description.setText(intent.getStringExtra("Description"));
        }


        //persian date picker dialog for choose send out date
        initDate = new PersianCalendar();
        initDate.setPersianDate(initDate.getPersianYear(), initDate.getPersianMonth(), initDate.getPersianDay());
        persianDatePickerDialog = new PersianDatePickerDialog(G.context);
        persianDatePickerDialog.setPositiveButtonString("ثبت")
                .setNegativeButton("انصراف")
                .setTodayButton("امروز")
                .setTodayButtonVisible(true)
                .setMinYear(1390)
                .setMaxYear(initDate.getPersianYear())
                .setInitDate(initDate)
                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                .setListener(new Listener() {
                    @Override
                    public void onDateSelected(PersianCalendar persianCalendar) {
                        btn_sendout_date.setText(persianCalendar.getPersianYear()
                                + "/" + persianCalendar.getPersianMonth()
                                + "/" + persianCalendar.getPersianDay());
                        btn_sendout_date.setError(null);
                    }

                    @Override
                    public void onDisimised() {
                    }
                });

        //set on click for send out date button
        btn_sendout_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                persianDatePickerDialog.show();
            }
        });


        //btn send soldier information to server
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validate edit texts and spinners for true information
                //Toast.makeText(profile.this, sw_cur_status.isChecked() + "", Toast.LENGTH_SHORT).show();
                if (ValidateEditText(txt_name)) {
                    if (ValidateEditText(txt_lastname)) {
                        if (ValidateSpinner(sp_cur_state)) {
                            if (ValidateSpinner(sp_cur_city)) {
                                if (ValidateEditText(txt_cur_padegan)) {
                                    if (ValidateSpinner(sp_req_state)) {
                                        if (ValidateSpinner(sp_req_city)) {
                                            if (ValidateEditText(txt_req_padegan)) {
                                                if (!(btn_sendout_date.getText().toString().equals(""))) {
                                                    if (ValidateEditText(txt_phone)) {
                                                        if (ValidateSpinner(sp_rate)) {
                                                            if (ValidateSpinner(sp_organ)) {
                                                                if (ValidateSpinner(sp_suborgan)) {
                                                                    //if (ValidateEditText(txt_description)) {
                                                                        if (Utility.isNetworkAvailable(profile.context))
                                                                            new SendInformation().execute();
                                                                        else
                                                                            Toast.makeText(profile.this, "اتصال اینترنت برقرار نیست!", Toast.LENGTH_SHORT).show();
                                                                    //}
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    btn_sendout_date.setError("تاریخ اعزامتان را وارد کنید");
                                                    btn_sendout_date.requestFocus();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        });
        sw_cur_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Tarkhis){
                new FancyShowCaseView.Builder(profile.this)
                        .focusOn(sw_cur_status)
                        .title("اگر در حال خدمت هستید مقدار این گزینه را تغییر ندهید!")
                        .titleStyle(R.style.MyTitleStyle1, Dialog.Gravity.CENTER_VERTICAL)
                        .build()
                        .show();
                Tarkhis = false;
                    sw_cur_status.setChecked(false);
                }
            }
        });

        sw_movment_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Move){
                    new FancyShowCaseView.Builder(profile.this)
                            .focusOn(sw_movment_status)
                            .title("اگر مایل به جابجایی هستید مقدار این گزینه را تغییر ندهید!")
                            .titleStyle(R.style.MyTitleStyle1, Dialog.Gravity.CENTER_VERTICAL)
                            .build()
                            .show();
                    Move = false;
                    sw_movment_status.setChecked(false);
                }
            }
        });

    }
    // sending soldier information to server in background

    class SendInformation extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        ProgressDialog.Builder dialogbuilder;
        Utility utility = new Utility(G.context);

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //show dialog for show connect progress
            dialogbuilder = new ProgressDialog.Builder(profile.this);
            dialogbuilder.setTitle("در حال ارسال اطلاعات");
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
                Utility utility = new Utility(G.context);
                res = utility.SendInformation(txt_name.getText().toString(),
                        txt_lastname.getText().toString(),
                        sp_cur_state.getSelectedItem().toString(),
                        sp_cur_city.getSelectedItem().toString(),
                        txt_cur_padegan.getText().toString(),
                        sp_req_state.getSelectedItem().toString(),
                        sp_req_city.getSelectedItem().toString(),
                        txt_req_padegan.getText().toString(),
                        btn_sendout_date.getText().toString(),
                        txt_phone.getText().toString(),
                        sw_cur_status.isChecked(),
                        sw_movment_status.isChecked(),
                        (int) sp_rate.getSelectedItemId(),
                        (int) sp_organ.getSelectedItemId(),
                        (int) sp_suborgan.getSelectedItemId());
                        //txt_description.getText().toString()
                //save phone for retrieve soldier info from server
                utility.SavePref("phone", txt_phone.getText().toString());
                if (res.equals("error"))
                    return "خطا در دریافت اطلاعات !";
                else if (res.equals("ok")) {

                    return "اطلاعات با موفقیت ثبت شد.";
                } else if (res.equals("sms sent"))
                    return "اطلاعات با موفقیت ثبت شد.";
            } catch (Exception e) {
                e.printStackTrace();
                return "خطا در برقراری ارتباط ! ";
            }
            return res;
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            MaterialDialog.Builder mbuilder = new MaterialDialog.Builder(G.context);
            mbuilder.setTitle(s);
            mbuilder.setPositiveButton("باشه", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (s.equals("اطلاعات با موفقیت ثبت شد.")) {
                        Intent intent = new Intent(profile.this, Main.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                }
            });
            mbuilder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
            mbuilder.setBackgroundColor(getResources().getColor(R.color.backColor));
            mbuilder.setTitleColor(getResources().getColor(android.R.color.white));
            mbuilder.setMessageColor(getResources().getColor(R.color.textColor));
            MaterialDialog dialog1 = mbuilder.create();
            dialog1.show();


        }
    }

    //validate edittext that is not null and out of range
    private boolean ValidateEditText(MaterialEditText materialEditText_name) {
        //this if for deferent message for description field is created
//        if (materialEditText_name.getId() == R.id.txt_description && materialEditText_name.getText().toString().trim().matches("")) {
//            materialEditText_name.setError("مثال:من در نیروی زمینی ارتش و با رسته پیاده و در قسمت پاسدارخانه خدمت میکنم");
//            materialEditText_name.requestFocus();
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(materialEditText_name, InputMethodManager.SHOW_IMPLICIT);
//            return false;
//        }
        //this if for checking phone field 11 digit
         if (materialEditText_name.getId() == R.id.txt_phone & materialEditText_name.getText().length() != 11) {
            materialEditText_name.setError("شماره تلفن باید 11 رقم باشد");
            materialEditText_name.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(materialEditText_name, InputMethodManager.SHOW_IMPLICIT);
            return false;
        } else {
            if (materialEditText_name.getText().toString().trim().matches("")) {
                materialEditText_name.setError(materialEditText_name.getFloatingLabelText() + " نمیتواند خالی باشد");
                materialEditText_name.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(materialEditText_name, InputMethodManager.SHOW_IMPLICIT);
                return false;
            } else if (materialEditText_name.getText().length() > materialEditText_name.getMaxCharacters()) {
                materialEditText_name.setError(materialEditText_name.getFloatingLabelText() + "تعداد حروف بیش از حد مجاز!");
                materialEditText_name.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(materialEditText_name, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
        }
        return true;
    }

    //Validate spinners that one item be selected
    private boolean ValidateSpinner(MaterialSpinner materialSpinner_name) {
        if (materialSpinner_name.getSelectedItem() == null) {
            materialSpinner_name.setError(materialSpinner_name.getFloatingLabelText() + " را انتخاب کنید");
            materialSpinner_name.requestFocus();
            return false;
        }
        return true;
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
        int id = item.getItemId();
        if (id == R.id.action_back)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}

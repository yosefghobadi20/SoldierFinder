package com.nikosoft.soldierfinder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import de.mrapp.android.dialog.MaterialDialog;
import de.mrapp.android.dialog.ProgressDialog;

public class Activation extends G {

    private MaterialEditText txt_active_code;
    private AppCompatTextView tv_second;
    private AppCompatTextView tv_resend;
    private AppCompatButton btn_activation;
    private Utility utility = new Utility(G.context);
    private Intent intent;
    //for that user cant enter code more than 3 time in 30 second
    public static int Active_time = 0;


    String METHOD_NAME = "";
    // prival String METHOD_NAME = "HelloWorld";
    final String NAMESPACE = "http://tempuri.org/";
    // prival String NAMESPACE = "http://tempuri.org/";
    final String URL = "http://192.168.1.104:8080/Service_SoldierFinder.svc";
    // private static final String URL = "http://192.168.0.2:8080/webservice1/Service1.asmx";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activation);

        txt_active_code = (MaterialEditText) findViewById(R.id.txt_active_code);
        tv_second = (AppCompatTextView) findViewById(R.id.tv_second);
        tv_resend = (AppCompatTextView) findViewById(R.id.tv_resend);
        btn_activation = (AppCompatButton) findViewById(R.id.btn_activation);

        intent = getIntent();

        StartTimer(tv_resend,60000);
        // activation button
        btn_activation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_active_code.getText().length() > 5 | txt_active_code.getText().length() < 5) {
                    txt_active_code.setError("کد فعالسازی باید 5 رقم باشد!");
                    txt_active_code.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(txt_active_code, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    if (Active_time >= 3) {
                        StartTimer(tv_second,30000);
                    } else {
                        Active_time += 1;
                        new SendInformation().execute();
                    }
                }
            }
        });

        //resend sms to user
        tv_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Resend_sms().execute();
            }
        });

    }

    private void StartTimer(final AppCompatTextView appCompatTextView,long milis) {

        tv_second.setVisibility(View.VISIBLE);
        new CountDownTimer(milis, 1000) {
            @Override
            public void onTick(long l) {
                if(appCompatTextView.getId()==R.id.tv_second) {
                    appCompatTextView.setText("لطفا پس از " + l / 1000 + " ثانیه دوباره امتحان کنید");
                    btn_activation.setEnabled(false);
                }
                else {
                    appCompatTextView.setText("ارسال دوباره کد فعالسازی پس از " + l / 1000 + " ثانیه");
                    appCompatTextView.setEnabled(false);
                }
            }

            @Override
            public void onFinish() {
                if(appCompatTextView.getId()==R.id.tv_second) {
                    btn_activation.setEnabled(true);
                    tv_second.setVisibility(View.INVISIBLE);
                    Active_time = 0;
                }
                else
                {
                    tv_resend.setEnabled(true);
                    tv_resend.setText("ارسال دوباره کد فعالسازی");
                }
            }
        }.start();

    }

    //active software in background
    class SendInformation extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        ProgressDialog.Builder dialogbuilder;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //show dialog for show connect progress
            dialogbuilder = new ProgressDialog.Builder(Activation.this);
            dialogbuilder.setTitle("در حال فعالسازی");
            dialogbuilder.setMessage("لطفا صبر کنید...");

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
                res = Active(intent.getStringExtra("phone"), txt_active_code.getText().toString());
                if (res.equals("error"))
                    return "خطا در دریافت اطلاعات !";
                else if (res.equals("ok"))
                    return "فعالسازی با موفقیت انجام شد.";

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
                    if (s.equals("فعالسازی با موفقیت انجام شد.")) {
                        Intent intent = new Intent(Activation.this, Main.class);
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

    //resend sms contains activation code
    class Resend_sms extends AsyncTask<Void,Void,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            StartTimer(tv_resend,60000);
        }

        @Override
        protected String doInBackground(Void... voids) {

            return ResendSMS(utility.RetrievePref("phone"));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("error")) {
                MaterialDialog.Builder mbuilder = new MaterialDialog.Builder(G.context);
                mbuilder.setTitle("خطا در هنگام ارسال دوباره کد فعالسازی!");
                mbuilder.setPositiveButton("باشه", null);
                mbuilder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
                mbuilder.setBackgroundColor(getResources().getColor(R.color.backColor));
                mbuilder.setTitleColor(getResources().getColor(android.R.color.white));
                mbuilder.setMessageColor(getResources().getColor(R.color.textColor));
                MaterialDialog dialog1 = mbuilder.create();
                dialog1.show();
            }
        }
    }
    public String Active(String phone, String active_code) {
        try {
            StringBuilder sb = new StringBuilder();
            METHOD_NAME = "Activation";
            final String SOAP_ACTION = "http://tempuri.org/IService_SoldierFinder/" + METHOD_NAME;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("phone", phone);
            request.addProperty("active_code", active_code);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            androidHttpTransport.call(SOAP_ACTION, envelope);

            SoapPrimitive result = null;
            result = (SoapPrimitive) envelope.getResponse();
            String resultData = result.toString();

            sb.append(resultData);
            return sb.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            return "خطا در ارسال اطلاعات";
        }
    }

    public String ResendSMS(String phone) {
        try {
            StringBuilder sb = new StringBuilder();
            METHOD_NAME = "Activation";
            final String SOAP_ACTION = "http://tempuri.org/IService_SoldierFinder/" + METHOD_NAME;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("phone", phone);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            androidHttpTransport.call(SOAP_ACTION, envelope);

            SoapPrimitive result = null;
            result = (SoapPrimitive) envelope.getResponse();
            String resultData = result.toString();

            sb.append(resultData);
            return sb.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            return "خطا در ارسال اطلاعات";
        }
    }
}

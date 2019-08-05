package com.nikosoft.soldierfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import org.ksoap2.transport.HttpTransportSE;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Yosef on 28/03/2017.
 */
public class Utility {
    static Context _context;


    String METHOD_NAME = "";
    // prival String METHOD_NAME = "HelloWorld";
    final String NAMESPACE = "http://tempuri.org/";
    // prival String NAMESPACE = "http://tempuri.org/";
    final String URL = "http://service.nikosoft.ir/Service_SoldierFinder.svc";
    //final String URL = "http://192.168.1.101:8080/Service_SoldierFinder.svc";


    public Utility(Context context) {
        _context = context;
    }

    public String call(String MethodeName, String PropertyName, int PropertyValue) {
        try {
            StringBuilder sb = new StringBuilder();
            METHOD_NAME = MethodeName;
            final String SOAP_ACTION = "http://tempuri.org/IService_SoldierFinder/" + METHOD_NAME;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty(PropertyName, PropertyValue);
            request.addProperty("cur_state", G.cur_state);
            request.addProperty("cur_city", G.cur_city);
            request.addProperty("req_state", G.req_state);
            request.addProperty("req_city", G.req_city);
            request.addProperty("rate", G.rate);
            request.addProperty("organ", G.organ);
            request.addProperty("sub_organ", G.sub_organ);

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
            return "error";
        }


    }

    public String call(String MethodeName) {
        try {
            METHOD_NAME = MethodeName;
            final String SOAP_ACTION = "http://tempuri.org/IService_SoldierFinder/" + METHOD_NAME;
            StringBuilder sb = new StringBuilder();

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("cur_state", G.cur_state);
            request.addProperty("cur_city", G.cur_city);
            request.addProperty("req_state", G.req_state);
            request.addProperty("req_city", G.req_city);
            request.addProperty("rate", G.rate);
            request.addProperty("organ", G.organ);
            request.addProperty("sub_organ", G.sub_organ);
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
            return "error";
        }


    }

    public String Deserialize(String Res, ArrayList<Soldier_Info> arrayList) {

        JSONArray jsonArray = null;
        JSONObject jsonObject = null;
        Soldier_Info info = null;

        try {
            jsonArray = new JSONArray(Res);
            for (int i = 0; i < jsonArray.length(); i++) {
                info = new Soldier_Info();
                jsonObject = jsonArray.getJSONObject(i);
                info.ID = jsonObject.getInt("ID");
                info.Name = jsonObject.getString("Name");
                info.Family = jsonObject.getString("Family");
                info.CurrentState = jsonObject.getString("CurrentState");
                info.CurrentCity = jsonObject.getString("CurrentCity");
                info.CurrentPadegan = jsonObject.getString("CurrentPadegan");
                info.RequestState = jsonObject.getString("RequestState");
                info.RequestCity = jsonObject.getString("RequestCity");
                info.RequestPadegan = jsonObject.getString("RequestPadegan");
                info.Send_OutDate = jsonObject.getString("Send_OutDate");
                info.RequestDate = jsonObject.getString("RequestDate");
                info.Phone = jsonObject.getString("Phone");
                info.CurrentStatus = jsonObject.getBoolean("CurrentStatus");
                info.Sub_Organ = jsonObject.getInt("Sub_Organ");
                info.MovmentStatus = jsonObject.getBoolean("MovmentStatus");
                info.Rate = jsonObject.getInt("Rate");
                info.Organ = jsonObject.getInt("Organ");
                //info.Description = jsonObject.getString("Description");
                G.LastID = info.ID;
                //G.AllSoldiers.add(info);
                arrayList.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "error";
        }
        return "";
    }

    public Soldier_Info Deserialize_soldier_info(String Res) {

        JSONObject jsonObject = null;
        Soldier_Info info = null;
        if(Res.equals("null"))
        {
            Soldier_Info si=new Soldier_Info();
            return si;
        }
        else if (Res != "خطا در ارسال اطلاعات" & Res != "") {
            try {

                info = new Soldier_Info();
                jsonObject = new JSONObject(Res);
                info.ID = jsonObject.getInt("ID");
                info.Name = jsonObject.getString("Name");
                info.Family = jsonObject.getString("Family");
                info.CurrentState = jsonObject.getString("CurrentState");
                info.CurrentCity = jsonObject.getString("CurrentCity");
                info.CurrentPadegan = jsonObject.getString("CurrentPadegan");
                info.RequestState = jsonObject.getString("RequestState");
                info.RequestCity = jsonObject.getString("RequestCity");
                info.RequestPadegan = jsonObject.getString("RequestPadegan");
                info.Send_OutDate = jsonObject.getString("Send_OutDate");
                info.RequestDate = jsonObject.getString("RequestDate");
                info.Phone = jsonObject.getString("Phone");
                info.CurrentStatus = jsonObject.getBoolean("CurrentStatus");
                info.Sub_Organ = jsonObject.getInt("Sub_Organ");
                info.MovmentStatus = jsonObject.getBoolean("MovmentStatus");
                info.Rate = jsonObject.getInt("Rate");
                info.Organ = jsonObject.getInt("Organ");
                //info.Description = jsonObject.getString("Description");
                return info;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }

            }
            return false;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    public void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(G.context.openFileOutput("config.txt", MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = G.context.openFileInput("config.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public String SendInformation(String Name, String Lastname, String Cur_state, String Cur_city, String Cur_padegan, String Req_state
            , String Req_city, String Req_padegan, String Sendout_date, String Phone, boolean Cur_status
            , boolean Move_status, int Rate, int Organ, int Suborgan) {
        try {
            StringBuilder sb = new StringBuilder();
            METHOD_NAME = "UpdateSoldierInfo";
            final String SOAP_ACTION = "http://tempuri.org/IService_SoldierFinder/" + METHOD_NAME;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("name", Name);
            request.addProperty("family", Lastname);
            request.addProperty("current_state", Cur_state);
            request.addProperty("current_city", Cur_city);
            request.addProperty("current_padegan", Cur_padegan);
            request.addProperty("request_state", Req_state);
            request.addProperty("request_city", Req_city);
            request.addProperty("request_padegan", Req_padegan);
            request.addProperty("send_out", Sendout_date);
            request.addProperty("phone", Phone);
            request.addProperty("current_status", Cur_status);
            request.addProperty("sub_organ", Suborgan);
            request.addProperty("movment_status", Move_status);
            request.addProperty("rate", Rate);
            request.addProperty("organ", Organ);
//            request.addProperty("description", Description);
            request.addProperty("serial", getUniquePsuedoID());

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
            return "error";
        }
    }

    public String Search(String Cur_state, String Cur_city, String Req_state, String Req_city, int Rate, int Organ, int Suborgan) {
        try {
            StringBuilder sb = new StringBuilder();
            METHOD_NAME = "GetAllSoldiers";
            final String SOAP_ACTION = "http://tempuri.org/IService_SoldierFinder/" + METHOD_NAME;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            G.cur_state = Cur_state;
            G.cur_city = Cur_city;
            G.req_state = Req_state;
            G.req_city = Req_city;
            G.rate = Rate;
            G.organ = Organ;
            G.sub_organ = Suborgan;

            request.addProperty("cur_state", Cur_state);
            request.addProperty("cur_city", Cur_city);
            request.addProperty("req_state", Req_state);
            request.addProperty("req_city", Req_city);
            request.addProperty("rate", Rate);
            request.addProperty("organ", Organ);
            request.addProperty("sub_organ", Suborgan);
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
            return "error";
        }
    }

    public String CheckPayment() {
        try {
            METHOD_NAME="CheckPayment";
            final String SOAP_ACTION = "http://tempuri.org/IService_SoldierFinder/"+METHOD_NAME;
            StringBuilder sb = new StringBuilder();

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            String s=getUniquePsuedoID();
            request.addProperty(NAMESPACE,"Serial", s);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);


            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            androidHttpTransport.call(SOAP_ACTION, envelope);

            Object result = null;
            result = (Object) envelope.getResponse();
            String resultData = result.toString();

            sb.append(resultData);
            return sb.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
            return "err";
        }
    }

    public int CheckNewVer() {
        try {
            METHOD_NAME="GetLastVersion";
            final String SOAP_ACTION = "http://tempuri.org/IService_SoldierFinder/"+METHOD_NAME;
            StringBuilder sb = new StringBuilder();

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);


            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            androidHttpTransport.call(SOAP_ACTION, envelope);

            Object result = 0;
            result = (Object) envelope.getResponse();

            return Integer.parseInt(result.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public String GetSoldierInfo(String serial,String method_name) {
        try {
            StringBuilder sb = new StringBuilder();
            METHOD_NAME = method_name;
            final String SOAP_ACTION = "http://tempuri.org/IService_SoldierFinder/" + METHOD_NAME;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("serial", serial);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            androidHttpTransport.call(SOAP_ACTION, envelope);

            SoapPrimitive result = null;
            result = (SoapPrimitive) envelope.getResponse();
            String resultData = result.toString();

            sb.append(resultData);
            Log.i("soldier info", sb.toString());
            return sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "خطا در ارسال اطلاعات";
        }
    }

    public String SendInstallInfo() {
        try {
            StringBuilder sb = new StringBuilder();
            METHOD_NAME = "SendInstallinfo";
            final String SOAP_ACTION = "http://tempuri.org/IService_SoldierFinder/" + METHOD_NAME;
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("Serial", getUniquePsuedoID());
            request.addProperty("Version", BuildConfig.VERSION_NAME);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            androidHttpTransport.call(SOAP_ACTION, envelope);

            SoapPrimitive result = null;
            result = (SoapPrimitive) envelope.getResponse();
            String resultData = result.toString();

            sb.append(resultData);
            Log.i("soldier info", sb.toString());
            return sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "خطا در ارسال اطلاعات";
        }
    }

    public void SavePref(String Name, String Value) {
        SharedPreferences pref = G.context.getSharedPreferences("soldier_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Name, Value);
        editor.apply();
    }

    public String RetrievePref(String Name) {
        try {
            SharedPreferences pref = G.context.getSharedPreferences("soldier_pref", MODE_PRIVATE);
            String value = pref.getString(Name, "");
            return value;
        }
        catch (Exception ex)
        {
            return  "";
        }
    }

    public static String farsinumber(String text){
        String out = text.replaceAll("1","۱")
                .replaceAll("2","۲")
                .replaceAll("3","۳")
                .replaceAll("4","۴")
                .replaceAll("5","۵")
                .replaceAll("6","۶")
                .replaceAll("7","۷")
                .replaceAll("8","۸")
                .replaceAll("9","۹")
                .replaceAll("0","۰");
        return out;

    }

    public  String getUniquePsuedoID() {
        String s=RetrievePref("serial");
        if(s!=null & s!="")

            return s;
        else
        {
            // If all else fails, if the user does have lower than API 9 (lower
            // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
            // returns 'null', then simply the ID returned will be solely based
            // off their Android device information. This is where the collisions
            // can happen.
            // Thanks http://www.pocketmagic.net/?p=1662!
            // Try not to use DISPLAY, HOST or ID - these items could change.
            // If there are collisions, there will be overlapping data
            String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

            // Thanks to @Roman SL!
            // https://stackoverflow.com/a/4789483/950427
            // Only devices with API >= 9 have android.os.Build.SERIAL
            // http://developer.android.com/reference/android/os/Build.html#SERIAL
            // If a user upgrades software or roots their device, there will be a duplicate entry
            String serial = null;
            String uuid=null;
            try {
                serial = android.os.Build.class.getField("SERIAL").get(null).toString();
                uuid=new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
                SavePref("serial",uuid);
                // Go ahead and return the serial for api => 9
                return uuid;
            } catch (Exception exception) {
                // String needs to be initialized
                serial = ""; // some value
            }

            // Thanks @Joe!
            // https://stackoverflow.com/a/2853253/950427
            // Finally, combine the values we have found by using the UUID class to create a unique identifier
            uuid=new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
            SavePref("serial",uuid);
            // Go ahead and return the serial for api => 9
            return uuid;
        }

    }
    public void closeApp() {
        //close app
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}

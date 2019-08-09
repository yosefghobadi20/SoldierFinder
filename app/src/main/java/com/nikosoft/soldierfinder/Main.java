package com.nikosoft.soldierfinder;


import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.farsitel.bazaar.IUpdateCheckService;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;


import com.baoyz.widget.PullRefreshLayout;
import com.nikosoft.soldierfinder.util.IabHelper;
import com.nikosoft.soldierfinder.util.IabResult;
import com.nikosoft.soldierfinder.util.Inventory;
import com.nikosoft.soldierfinder.util.Purchase;

import java.util.ArrayList;

import de.mrapp.android.dialog.MaterialDialog;
import de.mrapp.android.dialog.ProgressDialog;
import de.mrapp.android.dialog.model.Dialog;

import io.fabric.sdk.android.services.common.Crash;
import ir.tapsell.sdk.*;
import ir.tapsell.sdk.TapsellAd;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;

import okhttp3.internal.Util;

import static ir.tapsell.sdk.TapsellAdRequestOptions.CACHE_TYPE_CACHED;

public class Main extends G implements NavigationView.OnNavigationItemSelectedListener {

    private static AlphaInAnimationAdapter alphaInAnimationAdapter;
    private static Soldier_adapter Soldier_adapter;
    private static RecyclerView recyclerView;
    private Toolbar toolbar;
    public PullRefreshLayout refreshLayout;
    public ProgressBar progressBar;
    public static View view;
    public int lastITEM;
    public static boolean loading = true;
    private FloatingActionButton fab_profile;
    private DrawerLayout dl;
    private NavigationView navigationView;

    //for show information for first run

    public Utility utility;

    //bazzar fields-----------------------
    static final String TAG = "Bazar";

    // SKUs for our products: the premium upgrade (non-consumable)
    static final String SKU_PREMIUM = "upgrade";

    // Does the user have the premium upgrade?
    boolean mIsPremium = false;

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 100;

    // The helper object
    IabHelper mHelper;

    IUpdateCheckService service;
    UpdateServiceConnection connection;
    //----------------------------------------

    android.app.ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.main);
        utility = new Utility(G.context);


        //send install and version info
        if (!(utility.RetrievePref("Installinfo").equals(BuildConfig.VERSION_CODE + "")))
            new SendInstallInfo().execute();

        Soldier_adapter = new Soldier_adapter(G.context, G.AllSoldiers);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar_main);
        view = findViewById(R.id.lay_rootview);
        recyclerView = (RecyclerView) findViewById(R.id.recy);
        refreshLayout = (PullRefreshLayout) findViewById(R.id.lay_refresh);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab_profile = (FloatingActionButton) findViewById(R.id.fab_profile);
        fab_profile.attachToRecyclerView(recyclerView);
        dl = (DrawerLayout) findViewById(R.id.lay_drawer);
        navigationView = (NavigationView) findViewById(R.id.nv);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, dl, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.addDrawerListener(toggle);
        toggle.syncState();

        //refresh Soldiers list on start....................................................
        RefreshSoldiersList();

        recyclerView.setAdapter(Soldier_adapter);

        //load more soldiers from server when list pull down......................................
        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    GetDataOnPullDown();

                } catch (Exception e) {
                    Log.i("OnRefresh: ", e.getStackTrace() + "");
                }
            }
        });

        fab_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utility.isNetworkAvailable(G.context))
                    new GetSoldierInfo().execute();
                else
                    Toast.makeText(Main.this, "اتصال اینترنت برقرار نیست!", Toast.LENGTH_SHORT).show();

            }
        });

        //Lazy loading.............................................................................
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(G.context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            int totalItemCount = 0;
            int postVisiblesItems = 0;
            int visibleItemCount = 0;


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) //check for scroll down
                {

                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    postVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                    lastITEM = totalItemCount - 1;

                    if ((postVisiblesItems + visibleItemCount) >= totalItemCount - 5) {
                        if (loading) {

                            loading = false;
                            GetInfo();

                        }
                        loading = true;
                    }
                }
            }
        });


        //SHow case view
        if (G.FirstRun) {


            utility.startShowCaseView(fab_profile, this, "پروفایل", "برای اینکه بتونیم سرباز متناسب شما رو پیدا کنیم، باید اطلاعات خودتو وارد کنی!"
                    , new TapTargetView.Listener() {
                        @Override
                        public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                            utility.startShowToolbarCaseView(Main.this,toolbar,R.id.action_search, "جستجوی هوشمند", " برای استفاده از این گزینه باید اطلاعات خود را وارد کنید", new TapTargetView.Listener() {
                                @Override
                                public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                                    utility.startShowToolbarCaseView( Main.this,toolbar,R.id.action_search_manual, "جستجوی دستی", "برای جستجوی دستی سربازان از این گزینه استفاده کنید"
                                            , new TapTargetView.Listener() {
                                                @Override
                                                public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                                                    super.onTargetDismissed(view, userInitiated);
                                                }
                                            });
                                }
                            });
                        }
                    });

        }

        Log.i("PAYMENT", utility.RetrievePref(Utility.PAID));

        if (Utility.isNetworkAvailable(Main.this)) {
            new CheckPay().execute();
            new CheckNewVer().execute();

        }
    }

    @Override
    protected void onResume() {
        utility.loadAd();
        super.onResume();
    }

    private void prepareBazarWithHelper() {
        //bazar buy helper
        if (utility.isConnectedInternet(this)) {
            try {


                //اتصال به بازار با mHelper
                mHelper = new IabHelper(this, utility.getRSAKey(this));
                Log.d(TAG, "Starting setup.");


                mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                    @Override
                    public void onIabSetupFinished(IabResult result) {


                        if (!result.isSuccess()) {
                            Toast.makeText(Main.this, result.getMessage() + "خطا در بر قراری ارتباط \nوارد حساب خود در کافه بازار شوید", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        } else {
                            Log.d(TAG, "Query inventory start.");

                            //لیست محصولات قابل خریداری
                            ArrayList skuList = new ArrayList();
                            skuList.add(SKU_PREMIUM);

                            //ارسال درخواست برای دریافت محصولات
                            mHelper.queryInventoryAsync(true, skuList, new IabHelper.QueryInventoryFinishedListener() {
                                @Override
                                public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                                    Log.d(TAG, "Query inventory finished.");
                                    if (result.isFailure()) {
                                        Toast.makeText(Main.this, "لطفا وارد حساب بازار خود شوید", Toast.LENGTH_SHORT).show();

                                        //صفحه ورود به بازار
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("bazaar://login"));
                                        intent.setPackage("com.farsitel.bazaar");
                                        startActivity(intent);


                                        Log.d(TAG, "Failed to query inventory: " + result);
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        return;
                                    } else {
                                        Log.d(TAG, "Query inventory was successful.");

                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();

                                        //-----------------------------------------دریافت اشتراک خریده شده توسط کاربر و اعمال آن
                                        if (inventory.hasPurchase(SKU_PREMIUM)) {
                                            Log.d(TAG, "اشتراک ماهانه" + inventory.getPurchase(SKU_PREMIUM).getOriginalJson());
                                            //show phone number to user
                                            utility.SavePref(Utility.PAID, "ok");
                                            Toast.makeText(Main.this, "برنامه قبلا ارتقا پیدا کرده است", Toast.LENGTH_SHORT).show();


                                        } else {
                                            utility.SavePref(Utility.PAID, "buy");

                                            mHelper.launchPurchaseFlow(Main.this, SKU_PREMIUM, 1, new IabHelper.OnIabPurchaseFinishedListener() {
                                                @Override
                                                public void onIabPurchaseFinished(IabResult result, Purchase info) {
                                                    if (result.isFailure()) {
                                                        Toast.makeText(Main.this, "خطا در پرداخت", Toast.LENGTH_LONG).show();
                                                        return;
                                                    }
                                                    if (info.getSku().equals(SKU_PREMIUM) & info.getDeveloperPayload().equals("soldierfinder")) {
                                                        Toast.makeText(Main.this, "پرداخت با موفقیت انجام شد", Toast.LENGTH_LONG).show();
                                                        utility.SavePref(Utility.PAID, "ok");

                                                    } else
                                                        Toast.makeText(Main.this, "پرداخت با خطا مواجه شد", Toast.LENGTH_LONG).show();
                                                }
                                            }, "soldierfinder");
                                        }
                                    }

                                    Log.d(TAG, "Initial inventory query finished; enabling main UI.");
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                }

                            });
                        }
                    }
                });


            } catch (Exception ex) {
                Toast.makeText(this, "برنامه بازار روی تلفن شما نصب نیست\nاگر این برنامه را از کافه بازار دریافت نکرده اید ،برنامه را از بازار دریافت کنید", Toast.LENGTH_LONG).show();
                ex.printStackTrace();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        } else {
            Toast.makeText(this, "عدم اتصال به اینترنت", Toast.LENGTH_SHORT).show();
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }


    //refresh Soldiers list
    public static void RefreshSoldiersList() {
        try {
            alphaInAnimationAdapter = new AlphaInAnimationAdapter(Soldier_adapter);
            alphaInAnimationAdapter.setFirstOnly(false);
            recyclerView.setHasFixedSize(true);
            Soldier_adapter.notifyDataSetChanged();

        } catch (Exception ex) {

        }

    }

    // get soldier from server in background and show in list
    public void GetInfo() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {


                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getdata();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressBar.setVisibility(View.GONE);
                RefreshSoldiersList();
            }
        }.execute();
    }

    //get info from server
    public boolean getdata() {
        Utility utility = new Utility(G.context);
        if (Utility.isNetworkAvailable(G.context)) {
            String res = utility.call("GetOldSoldiers", "SoldierID", G.LastID);
            if (res == "[]") {
                ShowMessage("");
                return false;
            } else if (res.equals("error")) {
                ShowMessage("خطا در خواندن اطلاعات از سرور)");
                return false;
            } else {
                String s = utility.Deserialize(res, G.AllSoldiers);
                if (s.equals("error"))
                    ShowMessage("خطا در نوشتن اطلاعات");
                return true;
            }
        } else
            ShowMessage("اتصال اینترنت برقرار نیست");
        return false;
    }


    public static void ShowMessage(String message) {
        try {
            if (message != null && message != "") {
                //Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                Toast.makeText(G.context, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //menu.findItem(R.id.action_favorite).setVisible(false);
        menu.findItem(R.id.action_back).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {

            if (!utility.RetrievePref(Utility.FILL_PROFILE).equals("true")) {
                fab_profile.performClick();
            } else {
                if (Utility.isNetworkAvailable(G.context))
                    new GetAppropriateSoldiers().execute();
                else
                    Toast.makeText(Main.this, "اتصال اینترنت برقرار نیست!", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.action_search_manual) {

            startActivity(new Intent(Main.this, Search.class));

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_update) {
            {
                String s = utility.RetrievePref(Utility.PAID);
                if (s.equals("buy")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://nikosoft.ir/home/buysoldierfinder?serial=" + utility.getUniquePsuedoID()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else if (s.equals("ok")) {
                    Toast.makeText(Main.this, "نرم افزار قبلا ارتقا یافته است!", Toast.LENGTH_LONG).show();
                } else if (s.equals("error")) {
                    Toast.makeText(Main.this, "خطا در هنگام دریافت اطلاعات از سرور!", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(Main.this, "خطا در خواندن اطلاعات دریافتی!", Toast.LENGTH_LONG).show();
            }

        } else if (id == R.id.menu_search) {

            if (!utility.RetrievePref(Utility.FILL_PROFILE).equals("true")) {
                fab_profile.performClick();
            } else {
                if (Utility.isNetworkAvailable(G.context))
                    new GetAppropriateSoldiers().execute();
                else
                    Toast.makeText(Main.this, "اتصال اینترنت برقرار نیست!", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.action_search_manual) {

            startActivity(new Intent(Main.this, Search.class));

        } else if (id == R.id.menu_edit) {
            startActivity(new Intent(Main.this, profile.class));
            fab_profile.performClick();
        } else if (id == R.id.menu_delete_info) {
            MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(Main.this);
            dialogBuilder.setTitle("حذف اطلاعات");
            dialogBuilder.setMessage("آیا مطمئن هستید؟");
            dialogBuilder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (Utility.isNetworkAvailable(Main.this))
                        new DeleteUserInfo().execute();
                }
            });
            dialogBuilder.setNegativeButton("بی خیال", null);
            dialogBuilder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
            dialogBuilder.setBackgroundColor(getResources().getColor(R.color.backColor));
            dialogBuilder.setTitleColor(getResources().getColor(android.R.color.white));
            dialogBuilder.setMessageColor(getResources().getColor(R.color.textColor));
            MaterialDialog dialog = dialogBuilder.create();
            dialog.show();
        } else if (id == R.id.menu_about) {
            startActivity(new Intent(Main.this, About_app.class));
        } else if (id == R.id.menu_share) {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "جابجایی سرباز");
                String sAux = "\nبه آسانی در شهر دلخواهتان خدمت کنید:\n";
                sAux = sAux + "\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, "انتخاب کنید"));
            } catch (Exception e) {
                //e.toString();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.lay_drawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //get soldier info from server to show in profile activity
    class GetSoldierInfo extends AsyncTask<Void, Void, Soldier_Info> {
        ProgressDialog dialog;
        ProgressDialog.Builder dialogbuilder;
        Utility utility = new Utility(G.context);
        MaterialDialog dialog1;
        private volatile boolean running = true;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //show dialog for show connect progress
            dialogbuilder = new ProgressDialog.Builder(Main.this);
            dialogbuilder.setTitle("در حال دریافت اطلاعات");
            dialogbuilder.setMessage("لطفا صبر کنید...");
            dialogbuilder.setNegativeButton("انصراف", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    cancel(true);
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
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected Soldier_Info doInBackground(Void... voids) {
            if (running)
                return utility.Deserialize_soldier_info(utility.GetSoldierInfo(utility.getUniquePsuedoID(), "GetSoldierInfo"));
            else
                return null;
        }

        @Override
        protected void onPostExecute(Soldier_Info s) {
            super.onPostExecute(s);
            if (running) {
                Intent intent = new Intent(Main.this, profile.class);
                if (s != null) {
                    if (s.Name != null)
                        utility.SavePref(Utility.FILL_PROFILE, "true");
                    else
                        utility.SavePref(Utility.FILL_PROFILE, "false");
                    try {
                        intent.putExtra("ID", s.ID);
                        intent.putExtra("Name", s.Name);
                        intent.putExtra("Family", s.Family);
                        intent.putExtra("CurrentState", s.CurrentState);
                        intent.putExtra("CurrentCity", s.CurrentCity);
                        intent.putExtra("CurrentPadegan", s.CurrentPadegan);
                        intent.putExtra("RequestState", s.RequestState);
                        intent.putExtra("RequestCity", s.RequestCity);
                        intent.putExtra("RequestPadegan", s.RequestPadegan);
                        intent.putExtra("RequestDate", s.RequestDate);
                        intent.putExtra("Send_OutDate", s.Send_OutDate);
                        intent.putExtra("Phone", s.Phone);
                        intent.putExtra("CurrentStatus", s.CurrentStatus);
                        intent.putExtra("MovmentStatus", s.MovmentStatus);
                        intent.putExtra("Rate", s.Rate);
                        intent.putExtra("Sub_Organ", s.Sub_Organ);
                        intent.putExtra("Organ", s.Organ);
                        //intent.putExtra("Description", s.Description);
                    } catch (Exception ex) {

                    }
                    startActivity(intent);

                } else {
                    MaterialDialog.Builder mbuilder = new MaterialDialog.Builder(G.context);
                    mbuilder.setTitle("خطا در اتصال به سرور");
                    mbuilder.setMessage("لطفا بعدا تلاش کنید");
                    mbuilder.setPositiveButton("باشه", null);
                    mbuilder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
                    mbuilder.setBackgroundColor(getResources().getColor(R.color.backColor));
                    mbuilder.setTitleColor(getResources().getColor(android.R.color.white));
                    mbuilder.setMessageColor(getResources().getColor(R.color.textColor));
                    dialog1 = mbuilder.create();
                    dialog1.show();
                }
                dialog.dismiss();
            }


        }

    }


    ///get data on pull down screen
    private void GetDataOnPullDown() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                G.cur_state = "";
                G.cur_city = "";
                G.req_state = "";
                G.req_city = "";
                G.rate = 0;
                G.organ = 0;
                G.sub_organ = 0;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                G.GetNewSoldiers(G.context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                RefreshSoldiersList();
                loading = true;
                refreshLayout.setRefreshing(false);
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {

        if (dl.isDrawerOpen(Gravity.RIGHT))
            dl.closeDrawer(Gravity.RIGHT);
        else {
            MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(Main.this);
            dialogBuilder.setTitle("خروج");
            dialogBuilder.setMessage("آیا مایل به خروج از برنامه هستید؟");
            dialogBuilder.setPositiveButton("بله", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            });
            dialogBuilder.setNegativeButton("بی خیال", null);
            dialogBuilder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
            dialogBuilder.setBackgroundColor(getResources().getColor(R.color.backColor));
            dialogBuilder.setTitleColor(getResources().getColor(android.R.color.white));
            dialogBuilder.setMessageColor(getResources().getColor(R.color.textColor));
            MaterialDialog dialog = dialogBuilder.create();
            dialog.show();

        }
    }

    @Override
    protected void onDestroy() {
        if (mHelper != null)
            mHelper.dispose();

        if (service != null)
            service = null;

        if (connection != null)
            connection = null;
        super.onDestroy();
    }

    //check if user buy full version or not
    class CheckPay extends AsyncTask<Void, Void, String> {
        //ProgressDialog dialog;
        //ProgressDialog.Builder dialogbuilder;
        Utility utility = new Utility(G.context);
        private volatile boolean running = true;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //show dialog for show connect progress
//            dialogbuilder = new ProgressDialog.Builder(Main.this);
//            dialogbuilder.setTitle("در حال ارتباط");
//            dialogbuilder.setMessage("لطفا صبر کنید...");
//            dialogbuilder.setNegativeButton("انصراف", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    cancel(true);
//                }
//            });
//            dialogbuilder.setProgressBarPosition(ProgressDialog.ProgressBarPosition.LEFT);
//            dialogbuilder.setProgressBarColor(getResources().getColor(R.color.colorPrimary));
//            dialogbuilder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
//            dialogbuilder.setBackgroundColor(getResources().getColor(R.color.backColor));
//            dialogbuilder.setTitleColor(getResources().getColor(android.R.color.white));
//            dialogbuilder.setMessageColor(getResources().getColor(R.color.textColor));
//            dialog = dialogbuilder.create();
//            dialog.show();
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected String doInBackground(Void... voids) {
           /* if (running) {
                Soldier_Info soldier_info = utility.Deserialize_soldier_info(utility.GetSoldierInfo(utility.getUniquePsuedoID(),"GetSoldierInfo"));
                if (soldier_info.Name == null) {
                    fillProfile = true;
                } else
                    fillProfile = false;
                return utility.CheckPayment();
            } else
                return null;*/
            //if(!(utility.RetrievePref(Utility.PAID).equals("buy")|utility.RetrievePref(Utility.PAID).equals("ok")))
            return utility.CheckPayment();
//            else
//                return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {

                utility.SavePref(Utility.PAID, s);

            }
        }

    }

    class CheckNewVer extends AsyncTask<Void, Void, Integer> {

        Utility utility = new Utility(G.context);
        private volatile boolean running = true;


        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            if (running)
                return utility.CheckNewVer();
            else
                return null;
        }

        @Override
        protected void onPostExecute(Integer newver) {
            super.onPostExecute(newver);
            if (running) {
                int oldver = BuildConfig.VERSION_CODE;
                if (newver > oldver) {
                    final MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(G.context);
                    dialogBuilder.setTitle("بروز رسانی");
                    dialogBuilder.setMessage("نسخه جدید اپلیکیشن سربازیاب موجود است");
                    dialogBuilder.setPositiveButton("دانلود", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://nikosoft.ir/home/downloadAPK")));
                        }
                    });
                    dialogBuilder.setNegativeButton("خروج", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            utility.closeApp();
                        }
                    });
                     dialogBuilder.create().show();

                }


            }

        }

    }

    private void initService() {
        Log.i(TAG, "initService()");
        connection = new UpdateServiceConnection();

        Intent i = new Intent(
                "com.farsitel.bazaar.service.UpdateCheckService.BIND");
        i.setPackage("com.farsitel.bazaar");
        boolean ret = bindService(i, connection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "initService() bound value: " + ret);
    }

    class UpdateServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IUpdateCheckService.Stub
                    .asInterface((IBinder) boundService);
            try {
                long vCode = service.getVersionCode(getPackageName());
                Log.i("VERSION", "new version code: " + vCode);
                utility.SavePref(utility.NEW_VERSION, String.valueOf(vCode));
                Log.i("VERSION", "app version code: " + utility.getAppVersionNumber());
                if (vCode > utility.getAppVersionNumber())
                    showUpdateDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "onServiceConnected(): Connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            Log.d(TAG, "onServiceDisconnected(): Disconnected");
        }
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("بروز رسانی");
        builder.setMessage("لطفا برنامه رو بروز رسانی کنید");
        builder.setCancelable(false);
        builder.setPositiveButton("بروز رسانی", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("bazaar://details?id=" + getPackageName()));
                    intent.setPackage("com.farsitel.bazaar");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Main.this, "برنامه بازار نصب نیست", Toast.LENGTH_SHORT).show();
                    utility.closeApp();
                }
            }
        });
        builder.setNegativeButton("خروج", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                utility.closeApp();
            }
        });

        builder.show();
    }


    //get appropriate soldiers
    class GetAppropriateSoldiers extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        ProgressDialog.Builder dialogbuilder;
        Utility utility = new Utility(G.context);
        MaterialDialog dialog1;
        private volatile boolean running = true;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();


            //show dialog for show connect progress
            dialogbuilder = new ProgressDialog.Builder(Main.this);
            dialogbuilder.setTitle("در حال دریافت اطلاعات");
            dialogbuilder.setMessage(" ممکن است کمی طول بکشد لطفا صبور باشید...");
            dialogbuilder.setNegativeButton("انصراف", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    cancel(true);
                }
            });
            dialogbuilder.setProgressBarPosition(ProgressDialog.ProgressBarPosition.LEFT);
            dialogbuilder.setProgressBarColor(getResources().getColor(R.color.colorPrimary));
            dialogbuilder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
            dialogbuilder.setBackgroundColor(getResources().getColor(R.color.backColor));
            dialogbuilder.setTitleColor(getResources().getColor(android.R.color.white));
            dialogbuilder.setMessageColor(getResources().getColor(android.R.color.white));
            dialog = dialogbuilder.create();
            dialog.show();


        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (running) {
                AppropriateSoldiers.clear();
                return utility.Deserialize(utility.GetSoldierInfo(utility.getUniquePsuedoID(), "GetAppropriateSoldiers"), G.AppropriateSoldiers);
            } else
                return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (running) {

                if (s.equals("error"))
                    Main.ShowMessage("خطا در خواندن اطلاعات از سرور");
                else
                    startActivity(new Intent(Main.this, com.nikosoft.soldierfinder.AppropriateSoldiers.class));


            } else {
                MaterialDialog.Builder mbuilder = new MaterialDialog.Builder(G.context);
                mbuilder.setTitle("خطا در اتصال به سرور");
                mbuilder.setMessage("لطفا بعدا تلاش کنید");
                mbuilder.setPositiveButton("باشه", null);
                mbuilder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
                mbuilder.setBackgroundColor(getResources().getColor(R.color.backColor));
                mbuilder.setTitleColor(getResources().getColor(android.R.color.white));
                mbuilder.setMessageColor(getResources().getColor(R.color.textColor));
                dialog1 = mbuilder.create();
                dialog1.show();
            }
            dialog.dismiss();
        }


    }


    //delete user information
    class DeleteUserInfo extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        ProgressDialog.Builder dialogbuilder;
        Utility utility = new Utility(G.context);
        MaterialDialog dialog1;
        private volatile boolean running = true;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //show dialog for show connect progress
            dialogbuilder = new ProgressDialog.Builder(Main.this);
            dialogbuilder.setTitle("حذف اطلاعات");
            dialogbuilder.setMessage("در حال حذف...");
//            dialogbuilder.setNegativeButton("انصراف", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    cancel(true);
//                }
//            });
            dialogbuilder.setProgressBarPosition(ProgressDialog.ProgressBarPosition.LEFT);
            dialogbuilder.setProgressBarColor(getResources().getColor(R.color.colorPrimary));
            dialogbuilder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
            dialogbuilder.setBackgroundColor(getResources().getColor(R.color.backColor));
            dialogbuilder.setTitleColor(getResources().getColor(android.R.color.white));
            dialogbuilder.setMessageColor(getResources().getColor(android.R.color.white));
            dialog = dialogbuilder.create();
            dialog.show();
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (running) {
                String res = utility.GetSoldierInfo(utility.getUniquePsuedoID(), "DeleteSoldierInfo");
                if (res.equals("true")) {
                    return res;
                }

            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (running) {

                if (s.equals("null"))
                    Main.ShowMessage("اطلاعات شما ثبت نشده است");
                else if (s.equals("true")) {
                    utility.SavePref(Utility.FILL_PROFILE, "false");
                    Main.ShowMessage("اطلاعات شما با موفقیت حذف شد");
                } else
                    Main.ShowMessage("خطا در انجام عملیات");

            } else {
                MaterialDialog.Builder mbuilder = new MaterialDialog.Builder(G.context);
                mbuilder.setTitle("خطا در اتصال به سرور");
                mbuilder.setMessage("لطفا بعدا تلاش کنید");
                mbuilder.setPositiveButton("باشه", null);
                mbuilder.setButtonTextColor(getResources().getColor(R.color.colorPrimary));
                mbuilder.setBackgroundColor(getResources().getColor(R.color.backColor));
                mbuilder.setTitleColor(getResources().getColor(android.R.color.white));
                mbuilder.setMessageColor(getResources().getColor(R.color.textColor));
                dialog1 = mbuilder.create();
                dialog1.show();
            }
            dialog.dismiss();
        }


    }


    //Send Install and version info
    class SendInstallInfo extends AsyncTask<Void, Void, String> {

        Utility utility = new Utility(G.context);
        private volatile boolean running = true;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected String doInBackground(Void... voids) {
            if (running)
                return utility.SendInstallInfo();
            else
                return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            if (running) {
                if (res.equals("true"))
                    utility.SavePref("Installinfo", BuildConfig.VERSION_CODE + "");
                else
                    utility.SavePref("Installinfo", "false");
            }

        }

    }

}




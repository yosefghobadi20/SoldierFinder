package com.nikosoft.soldierfinder;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

import ir.tapsell.sdk.Tapsell;
import ir.tapsell.sdk.TapsellAd;
import ir.tapsell.sdk.TapsellAdRequestListener;
import ir.tapsell.sdk.TapsellAdRequestOptions;
import ir.tapsell.sdk.TapsellAdShowListener;
import ir.tapsell.sdk.TapsellShowOptions;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;

import static ir.tapsell.sdk.TapsellAdRequestOptions.CACHE_TYPE_CACHED;

public class AppropriateSoldiers extends AppCompatActivity {


    private static AlphaInAnimationAdapter alphaInAnimationAdapter;
    private static Soldier_adapter Soldier_adapter;
    private static RecyclerView recyclerView;
    private Toolbar toolbar;
    public PullRefreshLayout refreshLayout;
    public int lastITEM;
    public static boolean loading = true;
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appropriate_soldiers);


        Soldier_adapter = new Soldier_adapter(G.context, G.AppropriateSoldiers);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_main);
        recyclerView = (RecyclerView) findViewById(R.id.recy);
        refreshLayout = (PullRefreshLayout) findViewById(R.id.lay_refresh);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        //refresh Soldiers list on start....................................................
        RefreshSoldiersList();

        recyclerView.setAdapter(Soldier_adapter);

        //load more soldiers from server when list pull down......................................
        refreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener()

        {
            @Override
            public void onRefresh() {
                try {
                    GetDataOnPullDown();

                } catch (Exception e) {
                    Log.i("OnRefresh: ", e.getStackTrace() + "");
                }
            }
        });




        //Lazy loading.............................................................................
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(G.context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()

        {

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
        //show adver
        showAd();
    }

    private void showAd() {
        Tapsell.requestAd(G.context, "5b5599b96c1dec000183928b"	, new TapsellAdRequestOptions(CACHE_TYPE_CACHED), new TapsellAdRequestListener() {
            @Override
            public void onError (String error)
            {
                Log.i("Tapsell",error);
            }

            @Override
            public void onAdAvailable (TapsellAd ad)
            {
                Log.i("Tapsell","Ad is available");
                TapsellShowOptions options=new TapsellShowOptions();
                options.setBackDisabled(true);
                ad.show(G.context, options, new TapsellAdShowListener() {
                    @Override
                    public void onOpened(TapsellAd tapsellAd) {

                    }

                    @Override
                    public void onClosed(TapsellAd tapsellAd) {

                    }
                });
            }

            @Override
            public void onNoAdAvailable ()
            {
                Log.i("Tapsell","NoAdAvailable");
            }

            @Override
            public void onNoNetwork ()
            {
                Log.i("Tapsell","NoNetwork");
            }

            @Override
            public void onExpiring (TapsellAd ad)
            {
                Log.i("Tapsell","Expiring");
            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_back)
            onBackPressed();
        return super.onOptionsItemSelected(item);
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
                String s = utility.Deserialize(res,G.AppropriateSoldiers);
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

}

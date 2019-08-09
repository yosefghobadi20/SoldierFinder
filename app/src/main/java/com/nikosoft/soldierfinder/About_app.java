package com.nikosoft.soldierfinder;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

public class About_app extends AppCompatActivity {

    TextView title;
    TextView text;
    AppCompatButton btn;
    MaterialRippleLayout rippleLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_app);
        title = (TextView) findViewById(R.id.txt_title);
        text = (TextView) findViewById(R.id.txt_text);
        rippleLayout= (MaterialRippleLayout) findViewById(R.id.lay_ripple);
        toolbar = (Toolbar) findViewById(R.id.about_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        String s = BuildConfig.VERSION_NAME.toString();
        title.setText(getString(R.string.about_text_p1) + " " + s);
        btn = (AppCompatButton) findViewById(R.id.btn_submit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
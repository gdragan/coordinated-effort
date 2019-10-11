package com.example.android.coordinatedeffort;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;


public class SimpleBehaviorActivity extends AppCompatActivity implements
        View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        SimpleAdapter adapter = new SimpleAdapter(list);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.fab).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Snackbar.make(v, "That Was Easy…", Snackbar.LENGTH_SHORT).show();
    }

}

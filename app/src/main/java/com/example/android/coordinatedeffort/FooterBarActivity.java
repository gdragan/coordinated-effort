package com.example.android.coordinatedeffort;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

public class FooterBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_footer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        SimpleAdapter adapter = new SimpleAdapter(list);
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(this));
    }
}

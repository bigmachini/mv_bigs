package net.bigmachini.mv_bigs.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.adapters.UserAdapter;
import net.bigmachini.mv_bigs.models.UserModel;

import java.util.ArrayList;
import java.util.List;

import me.aflak.bluetooth.Bluetooth;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<UserModel> videoModelList = new ArrayList<>();
    private Context mContext;
    private Bluetooth bluetooth;
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.createUser(mContext);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = HomeActivity.this;

        bluetooth = new Bluetooth(mContext);
        btnDelete = findViewById(R.id.btn_delete);

        mRecyclerView =  findViewById(R.id.rv_users);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(mLayoutManager);

        List<UserModel> users = UserModel.getUsers(mContext);
        // specify an adapter (see also next example)
        mAdapter = new UserAdapter(mContext, users, btnDelete);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetooth.onStart();

        if (!bluetooth.isEnabled())
            bluetooth.enable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bluetooth.onStop();
    }
}

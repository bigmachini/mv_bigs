package net.bigmachini.mv_bigs.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.macroyau.blue2serial.BluetoothDeviceListDialog;
import com.macroyau.blue2serial.BluetoothSerialListener;

import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.adapters.UserAdapter;
import net.bigmachini.mv_bigs.models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class Home3Activity extends BaseActivity
        implements BluetoothSerialListener, BluetoothDeviceListDialog.OnDeviceSelectedListener {
    private static final String TAG = Home3Activity.class.getSimpleName();
    public RecyclerView mRecyclerView;
    public UserAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext;
    private Button btnDelete;
    public List<UserModel> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkBluetooth();
                if (!bluetoothSerial.isConnected()) {
                    Toast.makeText(mContext, "Please connect to device", Toast.LENGTH_LONG).show();
                } else {
                    progressDialog.setMessage("Creating User");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    Utils.createUser(mContext);
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = Home3Activity.this;
        btnDelete = findViewById(R.id.btn_delete);

        mRecyclerView = findViewById(R.id.rv_users);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(mLayoutManager);
        users = UserModel.getUsers(mContext);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<UserModel> dUsers = new ArrayList<>(mAdapter.getUsers());
                StringBuilder sb = new StringBuilder();
                for (int i = dUsers.size() - 1; i >= 0; i--) {
                    if (dUsers.get(i).isSelected()) {
                        UserModel user = dUsers.get(i);
                        if (user.ids.size() > 0) {
                            sb.append(user.name + "\n");
                        } else {
                            dUsers.remove(i);
                        }
                    }
                }
                if (sb.toString().isEmpty() || sb.toString().length() != 0) {
                    Utils.toastText(mContext, "Please delete all ids for user(s):\n" + sb.toString());
                }

                mAdapter.clear();
                mAdapter.updateList(dUsers);
            }
        });

        // specify an adapter (see also next example)
        progressDialog = new ProgressDialog(this);
        mAdapter = new UserAdapter(mContext, users, btnDelete, progressDialog);
        mRecyclerView.setAdapter(mAdapter);
        // Create a new instance of BluetoothSerial
        initializa(mContext);

    }

}

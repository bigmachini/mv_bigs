package net.bigmachini.mv_bigs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.bigmachini.mv_bigs.Constants;
import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.adapters.DeviceAdapter;
import net.bigmachini.mv_bigs.db.controllers.DeviceController;
import net.bigmachini.mv_bigs.services.APIListResponse;
import net.bigmachini.mv_bigs.services.APIService;
import net.bigmachini.mv_bigs.services.MyAPI;
import net.bigmachini.mv_bigs.structures.DeviceStructure;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceActivity extends AppCompatActivity {


    RecyclerView rvDevices;
    DeviceController mDeviceController;
    Context mContext;
    private LinearLayoutManager mLayoutManager;
    private DeviceAdapter mAdapter;
    private MaterialDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DeviceActivity.this, HomeActivity.class));
                Constants.gSelectedDevice = null;
            }
        });
        fab.setVisibility(View.GONE);

        mContext = DeviceActivity.this;
        mDeviceController = new DeviceController(mContext);
        rvDevices = findViewById(R.id.rv_device_list);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvDevices.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

        rvDevices.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new DeviceAdapter(mContext);
        rvDevices.setAdapter(mAdapter);
        mDialog = new MaterialDialog.Builder(mContext)
                .title(R.string.login)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();
        getDevices(mContext);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter = new DeviceAdapter(mContext);
            rvDevices.setAdapter(mAdapter);
        }
    }


    public void getDevices(Context context) {
        if (Utils.CheckConnection(context)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("id", Constants.gLoginStructure.id);
            MyAPI myAPI = APIService.createService(MyAPI.class, 60);
            Call<APIListResponse<DeviceStructure>> call = myAPI.getDevices(params);
            call.enqueue(new Callback<APIListResponse<DeviceStructure>>() {
                @Override
                public void onResponse(Call<APIListResponse<DeviceStructure>> call, Response<APIListResponse<DeviceStructure>> response) {
                    mDialog.dismiss();
                    try {
                        if (response.code() >= 200 && response.code() < 300) {
                            if (response.body().nStatus < 10) {
                                List<DeviceStructure> devices = response.body().data;
                                mAdapter = new DeviceAdapter(mContext);
                                rvDevices.setAdapter(mAdapter);
                            } else {
                                Toast.makeText(mContext, getString(R.string.no_device_found), Toast.LENGTH_LONG).show();

                            }
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(mContext, response.body().strMessage.toString(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        mDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<APIListResponse<DeviceStructure>> call, Throwable t) {
                    mDialog.dismiss();
                }
            });
        } else {
            mDialog.dismiss();
            if (mAdapter != null) {
                mAdapter = new DeviceAdapter(mContext);
                rvDevices.setAdapter(mAdapter);
            }
        }
    }
}

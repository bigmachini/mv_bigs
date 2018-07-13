package net.bigmachini.mv_bigs.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.bigmachini.mv_bigs.Constants;
import net.bigmachini.mv_bigs.Global;
import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.adapters.DeviceAdapter;
import net.bigmachini.mv_bigs.db.controllers.DeviceController;
import net.bigmachini.mv_bigs.db.entities.DeviceEntity;
import net.bigmachini.mv_bigs.services.APIListResponse;
import net.bigmachini.mv_bigs.services.APIService;
import net.bigmachini.mv_bigs.services.MyAPI;
import net.bigmachini.mv_bigs.structures.DeviceStructure;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = DeviceActivity.this;
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.CheckConnection(mContext)) {
                    new MaterialDialog.Builder(mContext)
                            .title(R.string.attach_device)
                            .content(R.string.enter_mac_address)
                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS)
                            .input(R.string.device_mac_address, R.string.empty, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {

                                    if (validate(input.toString())) {
                                        mDialog = new MaterialDialog.Builder(mContext)
                                                .title(R.string.assing_mac_address)
                                                .content(R.string.please_wait)
                                                .progress(true, 0)
                                                .show();
                                        assignDevice(mContext, input.toString());
                                    } else {
                                        Toast.makeText(mContext, "Invalid Mac Address : " + input.toString(), Toast.LENGTH_LONG).show();

                                    }
                                }
                            }).show();

                } else {
                    Utils.toastText(mContext, getString(R.string.no_internet));
                }
            }
        });

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
                .title(R.string.fetching_devices)
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

    private static boolean validate(String macAddress) {
        Pattern pattern = Pattern.compile(Constants.PATTERN);
        Matcher matcher = pattern.matcher(macAddress);
        return matcher.matches();

    }

    public void getDevices(Context context) {
        if (Utils.CheckConnection(context)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("id", Global.gLoginStructure.id);
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
                                updateDatabase(devices);
                                mAdapter = new DeviceAdapter(mContext);
                                rvDevices.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();

                                if (devices.size() == 0) {
                                    Toast.makeText(mContext, getString(R.string.no_device_found), Toast.LENGTH_LONG).show();
                                }
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

    private void updateDatabase(List<DeviceStructure> data) {
        for (DeviceStructure deviceStructure : data) {
            DeviceEntity deviceEntity = new DeviceEntity();
            deviceEntity.setId(deviceStructure.id);
            deviceEntity.setMacAddress(deviceStructure.macAddress);
            deviceEntity.setStatus(deviceStructure.status);
            mDeviceController.createDevice(deviceEntity);
        }
    }

    public void assignDevice(Context context, String macAddress) {
        if (Utils.CheckConnection(context)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("mac_address", macAddress);
            params.put("id", Global.gLoginStructure.id);
            MyAPI myAPI = APIService.createService(MyAPI.class, 60);
            Call<APIListResponse<DeviceStructure>> call = myAPI.assignDevice(params);
            call.enqueue(new Callback<APIListResponse<DeviceStructure>>() {
                @Override
                public void onResponse(Call<APIListResponse<DeviceStructure>> call, Response<APIListResponse<DeviceStructure>> response) {
                    mDialog.dismiss();
                    try {
                        if (response.code() >= 200 && response.code() < 300) {
                            if (response.body().nStatus < 10) {
                                List<DeviceStructure> devices = response.body().data;
                                updateDatabase(devices);
                                mAdapter = new DeviceAdapter(mContext);
                                rvDevices.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();

                                if (devices.size() == 0) {
                                    Toast.makeText(mContext, getString(R.string.no_device_found), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(mContext, getString(R.string.no_device_found), Toast.LENGTH_LONG).show();

                            }
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(mContext, response.body().strMessage.toString(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        mDialog.dismiss();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<APIListResponse<DeviceStructure>> call, Throwable t) {
                    mDialog.dismiss();
                    t.printStackTrace();
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

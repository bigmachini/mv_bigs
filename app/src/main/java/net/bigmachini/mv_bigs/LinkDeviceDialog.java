package net.bigmachini.mv_bigs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.bigmachini.mv_bigs.activities.DeviceActivity;
import net.bigmachini.mv_bigs.services.APIListResponse;
import net.bigmachini.mv_bigs.services.APIService;
import net.bigmachini.mv_bigs.services.MyAPI;
import net.bigmachini.mv_bigs.structures.DeviceStructure;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LinkDeviceDialog extends DialogFragment {
    EditText edtMacAddress;
    EditText edtSerial;
    MaterialDialog mDialog;
    Context mContext;

    public LinkDeviceDialog() {
    }


    public static LinkDeviceDialog newInstance() {
        LinkDeviceDialog f = new LinkDeviceDialog();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_dialog, container, false);
        getDialog().setTitle("Link Device: ");
        Button btnLink = view.findViewById(R.id.btn_link_device);
        edtMacAddress = view.findViewById(R.id.edt_mac_address);
        edtSerial = view.findViewById(R.id.edt_serial);
        mContext = getContext();
        btnLink.setOnClickListener(doneAction);
        return view;
    }

    View.OnClickListener doneAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String macAddress = edtMacAddress.getText().toString().trim();
            if (macAddress.isEmpty() || macAddress == null || macAddress.length() == 0) {
                edtMacAddress.setError(getString(R.string.mac_address_invalid));
                return;
            }

            String serialNo = edtMacAddress.getText().toString().trim();
            if (serialNo.isEmpty() || serialNo == null || serialNo.length() == 0) {
                edtMacAddress.setError(getString(R.string.invalid_serial_no));
                return;
            }

            assignDevice(mContext, macAddress, serialNo);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            dialog.setTitle("Link Device: ");
        }
    }


    public void assignDevice(Context context, final String macAddress, String SerialNo) {
        if (Utils.CheckConnection(context)) {
            mDialog = new MaterialDialog.Builder(getContext())
                    .title(R.string.assing_mac_address)
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .show();
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

                                if (mContext instanceof DeviceActivity) {
                                    ((DeviceActivity) mContext).updateDatabase(devices);
                                    ((DeviceActivity) mContext).mAdapter.notifyDataSetChanged();
                                }

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
        }
    /*    else {
            mDialog.dismiss();
            if (mAdapter != null) {
                mAdapter = new DeviceAdapter(mContext);
                rvDevices.setAdapter(mAdapter);
            }
        }*/
    }

}
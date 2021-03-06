package net.bigmachini.mv_bigs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
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
//    EditText edtMacAddress;
//    EditText edtSerial;
    MaterialDialog mDialog;
    Context mContext;
    TextInputLayout macAddressInputLayout, serialNumberInputLayout;
    TextInputEditText macAddressEditText, serialNumberEditText;
    AppCompatButton linkDeviceButton;

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
//        Button btnLink = view.findViewById(R.id.btn_link_device);
//        edtMacAddress = view.findViewById(R.id.edt_mac_address);
//        edtSerial = view.findViewById(R.id.edt_serial);
        mContext = getContext();
        macAddressInputLayout = view.findViewById(R.id.mac_address_input_layout);
        serialNumberInputLayout = view.findViewById(R.id.serial_text_input_layout);
        macAddressEditText = view.findViewById(R.id.mac_address_edit_text);
        serialNumberEditText = view.findViewById(R.id.serial_edit_text);
        linkDeviceButton = view.findViewById(R.id.btn_link_device);

        linkDeviceButton.setOnClickListener(doneAction);

        macAddressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                macAddressInputLayout.setError(null);
                serialNumberInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        serialNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                macAddressInputLayout.setError(null);
                serialNumberInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }



    View.OnClickListener doneAction = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String macAddress = macAddressEditText.getText().toString().trim();
            if (macAddress.isEmpty() || macAddress == null || macAddress.length() == 0) {
                macAddressInputLayout.setError(getString(R.string.mac_address_invalid));
                return;
            }

            String serialNo = serialNumberEditText.getText().toString().trim();
            if (serialNo.isEmpty() || serialNo == null || serialNo.length() == 0) {
                serialNumberInputLayout.setError(getString(R.string.invalid_serial_no));
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
            params.put("serial_no", SerialNo);
            params.put("id", Global.gLoginStructure.id);
            MyAPI myAPI = APIService.createService(MyAPI.class, 60);
            Call<APIListResponse<DeviceStructure>> call = myAPI.assignDevice(params);
            call.enqueue(new Callback<APIListResponse<DeviceStructure>>() {
                @Override
                public void onResponse(Call<APIListResponse<DeviceStructure>> call, Response<APIListResponse<DeviceStructure>> response) {
                    mDialog.dismiss();
                    dismiss();
                    try {
                        if (response.code() >= 200 && response.code() < 300) {
                            if (response.body().nStatus < 10) {
                                List<DeviceStructure> devices = response.body().data;

                                if (mContext instanceof DeviceActivity) {
                                    ((DeviceActivity) mContext).updateDevices(devices);
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
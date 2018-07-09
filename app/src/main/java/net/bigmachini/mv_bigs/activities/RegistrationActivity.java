package net.bigmachini.mv_bigs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import net.bigmachini.mv_bigs.Constants;
import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.models.RegistrationModel;
import net.bigmachini.mv_bigs.services.APIResponse;
import net.bigmachini.mv_bigs.services.APIService;
import net.bigmachini.mv_bigs.services.MyAPI;
import net.bigmachini.mv_bigs.structures.BaseStructure;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {

    Button btnRegister;
    EditText etPhoneNumber, etPin, etConfirmPin;
    Context mContext;
    private MaterialDialog mDialog;
    private BaseStructure baseStructure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab.setVisibility(View.GONE);

        btnRegister = findViewById(R.id.btn_register);
        etConfirmPin = findViewById(R.id.et_confirm_pin);
        etPin = findViewById(R.id.et_pin);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        mContext = RegistrationActivity.this;

        etPhoneNumber.setText(Utils.getStringSetting(mContext, Constants.PHONE_NUMBER, ""));
        etPhoneNumber.setEnabled(false);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                boolean proceed = true;

                if (phoneNumber.isEmpty() || phoneNumber.length() == 0 || phoneNumber == null) {
                    etPhoneNumber.setError("Invalid Phone Number");
                    proceed = false;

                }

                String pin = etPin.getText().toString().trim();

                if (pin.isEmpty() || pin.length() == 0 || pin == null) {
                    etPin.setError("Invalid Pin format");
                    proceed = false;
                }

                String confirmPin = etConfirmPin.getText().toString().trim();

                if (confirmPin.isEmpty() || confirmPin.length() == 0 || confirmPin == null) {
                    etConfirmPin.setError("Invalid pin format");
                    proceed = false;
                }

                if (proceed == false)
                    return;

                if (!new String(pin).equals(new String(confirmPin))) {
                    Utils.toastText(mContext, "The pin did not match try again");
                    etConfirmPin.setText("");
                    return;
                }
                mDialog = new MaterialDialog.Builder(mContext)
                        .title(R.string.registering_user)
                        .content(R.string.please_wait)
                        .progress(true, 0)
                        .show();
                RegistrationModel registrationModel = new RegistrationModel();
                registrationModel.phoneNumber = phoneNumber;
                registrationModel.pin = pin;
                registerUser(mContext, registrationModel);

            }
        });
    }

    public void registerUser(Context context, final RegistrationModel registrationModel) {
        if (Utils.CheckConnection(context)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("phone_number", registrationModel.phoneNumber);
            params.put("pin", registrationModel.pin);
            MyAPI myAPI = APIService.createService(MyAPI.class, 60);
            Call<APIResponse<BaseStructure>> call = myAPI.createUser(params);
            call.enqueue(new Callback<APIResponse<BaseStructure>>() {
                @Override
                public void onResponse(Call<APIResponse<BaseStructure>> call, Response<APIResponse<BaseStructure>> response) {
                    mDialog.dismiss();
                    try {
                        if (response.code() >= 200 && response.code() < 300) {
                            if (response.body().nStatus < 10) {
                                baseStructure = response.body().data;
                                Utils.setStringSetting(mContext, Constants.REGISTRATION_MODEL, new Gson().toJson(registrationModel).toString());
                                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(mContext, getString(R.string.invalid_credentials), Toast.LENGTH_LONG).show();

                            }
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(mContext, getString(R.string.invalid_credentials), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        mDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<APIResponse<BaseStructure>> call, Throwable t) {
                    mDialog.dismiss();
                }
            });
        } else {

            Toast.makeText(context, "Please connect your data first", Toast.LENGTH_SHORT).show();
        }
    }
}

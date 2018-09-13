package net.bigmachini.mv_bigs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

//    Button btnRegister;
//    EditText etPhoneNumber, etPin, etConfirmPin;
    Context mContext;
    private MaterialDialog mDialog;
    private BaseStructure baseStructure;

    private TextInputLayout phoneNumberWrapper, pinNumberWrapper, confirmPinNumberWrapper;
    private TextInputEditText phoneNumberEditText, pinNumberEditText, confirmPinNumberEditText;
    private AppCompatButton registerButton;
    private TextView backToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = RegistrationActivity.this;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        });
        fab.setVisibility(View.GONE);

        phoneNumberWrapper = findViewById(R.id.et_phone_number_wrapper);
        pinNumberWrapper = findViewById(R.id.et_pin_input_layout);
        confirmPinNumberWrapper = findViewById(R.id.et_confirm_pin_input_Layout);

        phoneNumberEditText = findViewById(R.id.et_phone_number);
        pinNumberEditText = findViewById(R.id.et_pin);
        confirmPinNumberEditText = findViewById(R.id.et_confirm_pin);

        registerButton = findViewById(R.id.btn_register);
        backToLogin = findViewById(R.id.back_to_login);

        phoneNumberEditText.setText(Utils.getStringSetting(mContext, Constants.PHONE_NUMBER, ""));
        Utils.setStringSetting(mContext, Constants.PHONE_NUMBER, "");
        phoneNumberEditText.setEnabled(false);

        registerButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            String pin = pinNumberEditText.getText().toString().trim();
            String confirmPin = confirmPinNumberEditText.getText().toString().trim();

            if (phoneNumber.isEmpty() || phoneNumber.length() == 0 || phoneNumber == null) {
                phoneNumberWrapper.setError("Invalid Phone Number");
                return;
            }

            if (pin.isEmpty() || pin.length() == 0 || pin == null) {
                pinNumberWrapper.setError("Invalid Pin Number");
                return;
            }

            if (confirmPin.isEmpty() || confirmPin.length() == 0 || confirmPin == null) {
                confirmPinNumberWrapper.setError("Invalid Pin Number");
                return;
            }

            if (!confirmPin.equals(pin)) {
                confirmPinNumberWrapper.setError(("The pin do not match, try again."));
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
        });

        backToLogin.setOnClickListener(v -> {
            Intent loginIntent = new Intent(mContext, LoginActivity.class);
            startActivity(loginIntent);
        });

        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneNumberWrapper.setError(null);
                confirmPinNumberWrapper.setError(null);
                pinNumberWrapper.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pinNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneNumberWrapper.setError(null);
                confirmPinNumberWrapper.setError(null);
                pinNumberWrapper.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPinNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneNumberWrapper.setError(null);
                confirmPinNumberWrapper.setError(null);
                pinNumberWrapper.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

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

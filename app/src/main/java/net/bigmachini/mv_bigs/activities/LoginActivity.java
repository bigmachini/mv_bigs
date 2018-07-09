package net.bigmachini.mv_bigs.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import net.bigmachini.mv_bigs.Constants;
import net.bigmachini.mv_bigs.R;
import net.bigmachini.mv_bigs.Utils;
import net.bigmachini.mv_bigs.models.RegistrationModel;
import net.bigmachini.mv_bigs.services.APIResponse;
import net.bigmachini.mv_bigs.services.APIService;
import net.bigmachini.mv_bigs.services.MyAPI;
import net.bigmachini.mv_bigs.structures.LoginStructure;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    EditText etPin;
    TextView tvForgotPassword;
    private MaterialDialog mDialog;
    private LoginStructure loginStructure;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = LoginActivity.this;
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab.setVisibility(View.GONE);
        btnLogin = findViewById(R.id.btn_login);
        tvForgotPassword = findViewById(R.id.tv_forgot_pin);
        final RegistrationModel registrationModel = new Gson().fromJson(Utils.getStringSetting(mContext, Constants.REGISTRATION_MODEL, ""), RegistrationModel.class);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(mContext)
                        .title(R.string.reset_pin)
                        .content(R.string.are_you_sure)
                        .progress(true, 0)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                resetPin(mContext, registrationModel);
                            }
                        })
                        .show();

            }
        });


        etPin = findViewById(R.id.et_pin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = etPin.getText().toString().trim();
                if (pin.isEmpty() || pin.length() == 0) {
                    Utils.toastText(mContext, "Invalid pin");
                    return;
                }
                mDialog = new MaterialDialog.Builder(mContext)
                        .title(R.string.login)
                        .content(R.string.please_wait)
                        .progress(true, 0)
                        .show();
                performLogin(mContext, registrationModel, pin);
            }
        });
    }


    public void performLogin(Context context, RegistrationModel registrationModel, String pin) {
        if (Utils.CheckConnection(context) && !Utils.getStringSetting(mContext, Constants.PHONE_NUMBER,"").isEmpty()) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("phone_number", registrationModel.phoneNumber);
            params.put("pin", pin);
            MyAPI myAPI = APIService.createService(MyAPI.class, 60);
            Call<APIResponse<LoginStructure>> call = myAPI.loginUser(params);
            call.enqueue(new Callback<APIResponse<LoginStructure>>() {
                @Override
                public void onResponse(Call<APIResponse<LoginStructure>> call, Response<APIResponse<LoginStructure>> response) {
                    mDialog.dismiss();
                    try {
                        if (response.code() >= 200 && response.code() < 300) {
                            if (response.body().nStatus < 10) {
                                Constants.gLoginStructure = response.body().data;
                                startActivity(new Intent(LoginActivity.this, DeviceActivity.class));
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
                public void onFailure(Call<APIResponse<LoginStructure>> call, Throwable t) {
                    mDialog.dismiss();
                }
            });
        } else {
            if (registrationModel.verifyPin(pin)) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {

                Utils.toastText(context, "Invalid login");
            }
        }
    }

    public void resetPin(Context context, RegistrationModel registrationModel) {
        if (Utils.CheckConnection(context)) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("phone_number", registrationModel.phoneNumber);
            MyAPI myAPI = APIService.createService(MyAPI.class, 60);
            Call<APIResponse<Boolean>> call = myAPI.forgotPin(params);
            call.enqueue(new Callback<APIResponse<Boolean>>() {
                @Override
                public void onResponse(Call<APIResponse<Boolean>> call, Response<APIResponse<Boolean>> response) {
                    mDialog.dismiss();
                    try {
                        if (response.code() >= 200 && response.code() < 300) {
                            if (response.body().nStatus < 10) {

                            }
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(mContext, getString(R.string.some_went_wrong), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        mDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<APIResponse<Boolean>> call, Throwable t) {
                    mDialog.dismiss();
                }
            });
        } else {

            Toast.makeText(context, "Please connect your data first", Toast.LENGTH_SHORT).show();
        }
    }
}

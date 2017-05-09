package com.mottc.chat.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.chat.Activity.MainActivity;
import com.mottc.chat.R;

import shem.com.materiallogin.DefaultLoginView;
import shem.com.materiallogin.DefaultRegisterView;
import shem.com.materiallogin.MaterialLoginView;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    private boolean progressShow;
    private ProgressDialog mProgressDialog;


    private String loginUserName;
    private String loginPassword;
    private String RegisterUserName;
    private String RegisterPassword;
    private String RegisterPasswordRep;

    private LoginContract.Presenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginPresenter = new LoginPresenter(this);
        mLoginPresenter.autoLogin();

        setContentView(R.layout.activity_login);
        MaterialLoginView login = (MaterialLoginView) findViewById(R.id.login);

        //登陆

        ((DefaultLoginView) login.getLoginView()).setListener(new DefaultLoginView.DefaultLoginViewListener() {
            @Override
            public void onLogin(TextInputLayout loginUser, TextInputLayout loginPass) {

                loginUserName = loginUser.getEditText().getText().toString();
                if (loginUserName.isEmpty()) {
                    loginUser.setError("User name can't be empty");
                    return;
                }
                loginUser.setError("");

                loginPassword = loginPass.getEditText().getText().toString();
                if (loginPassword.isEmpty()) {
                    loginPass.setError("Password can't be empty");
                    return;
                }
                loginPass.setError("");

                mLoginPresenter.login(loginUserName, loginPassword);

            }
        });

        //注册


        ((DefaultRegisterView) login.getRegisterView()).setListener(new DefaultRegisterView.DefaultRegisterViewListener() {
            @Override
            public void onRegister(TextInputLayout registerUser, TextInputLayout registerPass, TextInputLayout registerPassRep) {
                //Handle register
                RegisterUserName = registerUser.getEditText().getText().toString();
                if (RegisterUserName.isEmpty()) {
                    registerUser.setError("User name can't be empty");
                    return;
                }
                registerUser.setError("");

                RegisterPassword = registerPass.getEditText().getText().toString();
                if (RegisterPassword.isEmpty()) {
                    registerPass.setError("Password can't be empty");
                    return;
                }
                registerPass.setError("");

                RegisterPasswordRep = registerPassRep.getEditText().getText().toString();
                if (!RegisterPassword.equals(RegisterPasswordRep)) {
                    registerPassRep.setError("Passwords are different");
                    return;
                }
                registerPassRep.setError("");

                if (!TextUtils.isEmpty(RegisterUserName) && !TextUtils.isEmpty(RegisterPassword)) {
                    mLoginPresenter.register(RegisterUserName, RegisterPassword);
                }
            }
        });


        //使界面可以接受点击事件，以便相应软键盘消失。点击非编辑框区域，软键盘消失
        findViewById(R.id.login_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

    }


    @Override
    protected void onDestroy() {
        mLoginPresenter.onDestroy();
        super.onDestroy();
    }

    public void back(View view) {
        finish();
    }


    @Override
    public void showNoNet() {
        Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoginProgressDialog() {
        progressShow = true;
        mProgressDialog = new ProgressDialog(LoginActivity.this);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                progressShow = false;
            }
        });
        mProgressDialog.setMessage(getString(R.string.Is_landing));
        mProgressDialog.show();
    }

    @Override
    public void cancelLoginProgressDialog() {
        mProgressDialog.dismiss();
    }

    @Override
    public void showRegisterProgressDialog() {
        mProgressDialog = new ProgressDialog(LoginActivity.this);
        mProgressDialog.setMessage(getResources().getString(R.string.Is_the_registered));
        mProgressDialog.show();
    }

    @Override
    public void cancelRegisterProgressDialog() {
        mProgressDialog.dismiss();
    }



    @Override
    public void showCanNotLogin(String message) {
        if (!progressShow) {
            return;
        }
        mProgressDialog.dismiss();
        Toast.makeText(this, getString(R.string.Login_failed) + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showCanNotRegister(HyphenateException e) {
        int errorCode = e.getErrorCode();
        if (errorCode == EMError.NETWORK_ERROR) {
            Toast.makeText(this, R.string.network_anomalies, Toast.LENGTH_SHORT).show();
        } else if (errorCode == EMError.USER_ALREADY_EXIST) {
            Toast.makeText(this,R.string.User_already_exists, Toast.LENGTH_SHORT).show();
        } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
            Toast.makeText(this, R.string.registration_failed_without_permission, Toast.LENGTH_SHORT).show();
        } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
            Toast.makeText(this, R.string.illegal_user_name, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.Registration_failed + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showRegisterSuccessfully() {

        Toast.makeText(this, getResources().getString(R.string.Registered_successfully), Toast.LENGTH_LONG).show();
    }

    @Override
    public void go2LoginActivity() {

        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void go2MainActivity() {
        // 进入主页面
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

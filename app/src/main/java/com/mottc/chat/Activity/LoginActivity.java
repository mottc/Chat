package com.mottc.chat.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.chat.MyApplication;
import com.mottc.chat.R;
import com.mottc.chat.db.DBManager;
import com.mottc.chat.db.EaseUser;
import com.mottc.chat.utils.EaseCommonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shem.com.materiallogin.DefaultLoginView;
import shem.com.materiallogin.DefaultRegisterView;
import shem.com.materiallogin.MaterialLoginView;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static final int REQUEST_CODE_SETNICK = 1;


    private boolean progressShow;
    private boolean autoLogin = false;

    private String loginUserName;
    private String loginPassword;
    private String RegisterUserName;
    private String RegisterPassword;
    private String RegisterPasswordRep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //如果登录成功过，直接进入主页面
        if (EMClient.getInstance().isLoggedInBefore()) {
            autoLogin = true;
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            return;
        }

        setContentView(R.layout.activity_login);

        //登陆

        final MaterialLoginView login = (MaterialLoginView) findViewById(R.id.login);
        ((DefaultLoginView) login.getLoginView()).setListener(new DefaultLoginView.DefaultLoginViewListener() {
            @Override
            public void onLogin(TextInputLayout loginUser, TextInputLayout loginPass) {

                if (!EaseCommonUtils.isNetWorkConnected(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
                    return;
                }
                //Handle login
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

                progressShow = true;
                final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setCanceledOnTouchOutside(false);
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.d(TAG, "EMClient.getInstance().onCancel");
                        progressShow = false;
                    }
                });
                pd.setMessage(getString(R.string.Is_landing));
                pd.show();
                // close it before login to make sure DemoDB not overlap
                DBManager.getInstance().closeDB();
                // reset current loginUserName name before login
                MyApplication.getInstance().setCurrentUserName(loginUserName);
                // 调用sdk登陆方法登陆聊天服务器
                Log.d(TAG, "EMClient.getInstance().login");
                EMClient.getInstance().login(loginUserName, loginPassword, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "login: onSuccess");

                        if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                            pd.dismiss();
                        }

                        // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                        // ** manually load all local groups and
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        getFriends();

                        // 进入主页面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish();
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                        Log.d(TAG, "login: onProgress");
                    }

                    @Override
                    public void onError(final int code, final String message) {
                        Log.d(TAG, "login: onError: " + code);
                        if (!progressShow) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

//                Toast.makeText(LoginActivity.this,"Login success!",Toast.LENGTH_SHORT).show();
//
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//
//
//                //Snackbar.make(login, "Login success!", Snackbar.LENGTH_LONG).show();
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
                    final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                    pd.setMessage(getResources().getString(R.string.Is_the_registered));
                    pd.show();

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                // 调用sdk注册方法
                                EMClient.getInstance().createAccount(RegisterUserName, RegisterPassword);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (!LoginActivity.this.isFinishing())
                                            pd.dismiss();
                                        // 保存用户名
                                        MyApplication.getInstance().setCurrentUserName(RegisterUserName);
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                                    }
                                });
                            } catch (final HyphenateException e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (!LoginActivity.this.isFinishing())
                                            pd.dismiss();
                                        int errorCode = e.getErrorCode();
                                        if (errorCode == EMError.NETWORK_ERROR) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                                        } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                                        } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                                        } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }).start();


                }

//                Toast.makeText(LoginActivity.this,"Register success!",Toast.LENGTH_SHORT).show();
//
//                startActivity(new Intent(LoginActivity.this, MainActivity.class));


                //Snackbar.make(login, "Register success!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void getFriends() {
        try {
            List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
            Map<String, EaseUser> users = new HashMap<String, EaseUser>();
            for (String username : usernames) {
                EaseUser user = new EaseUser(username);
                users.put(username, user);
            }

            MyApplication.getInstance().setContactList(users);

        } catch (HyphenateException e) {
            e.printStackTrace();
        }

    }

    public void back(View view) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoLogin) {
            return;
        }
    }
}

package com.mottc.chat.addcontact;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mottc.chat.R;

public class AddContactActivity extends AppCompatActivity implements AddContactContract.View{


    private ProgressDialog progressDialog;
    private AddContactContract.Presenter mPresenter;
    private EditText et_username;
    private EditText et_reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        mPresenter = new AddContactPresenter(this);
        et_username = (EditText) this.findViewById(R.id.et_username);
        et_reason = (EditText) this.findViewById(R.id.et_reason);
        Button btn_add = (Button) this.findViewById(R.id.btn_add);
        ImageButton btn_add_back = (ImageButton) this.findViewById(R.id.back);

        String mName = this.getIntent().getStringExtra("username");
        if(!(TextUtils.isEmpty(mName))){
            et_username.setText(mName);
            et_username.setSelection(mName.length());
        }

        btn_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = et_username.getText().toString().trim();
                String reason = et_reason.getText().toString().trim();
                mPresenter.addContact(username, reason);
            }

        });

        btn_add_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void showUsernameIsEmpty() {
        Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccess() {

        String s1 = getResources().getString(R.string.send_successful);
        Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFailure(String error) {

        String s2 = getResources().getString(R.string.Request_add_buddy_failure);
        Toast.makeText(getApplicationContext(), s2 + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDialog() {
        progressDialog = new ProgressDialog(this);
        String str = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(str);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    public void dialogDismiss() {
        progressDialog.dismiss();
    }
}

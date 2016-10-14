package com.mottc.chat.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.mottc.chat.R;

public class AddContactActivity extends AppCompatActivity {


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        final EditText et_username = (EditText) this.findViewById(R.id.et_username);
        final EditText et_reason = (EditText) this.findViewById(R.id.et_reason);

        Button btn_add = (Button) this.findViewById(R.id.btn_add);
        ImageButton btn_add_back = (ImageButton) this.findViewById(R.id.back);
        btn_add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = et_username.getText().toString().trim();
                String reason = et_reason.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {

                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;

                }
                addContact(username,reason);
            }

        });

        btn_add_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * 添加contact
     *
     *
     */
    public void addContact(final String username,final String reason) {
        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {

                    EMClient.getInstance().contactManager().addContact(username, reason);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    public void back(View v) {
        finish();
    }
}

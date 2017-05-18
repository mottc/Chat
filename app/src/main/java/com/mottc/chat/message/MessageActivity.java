package com.mottc.chat.message;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/10/10
 * Time: 9:44
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mottc.chat.R;
import com.mottc.chat.data.bean.ChatInviteMessage;

import java.util.List;

/**
 * 申请与通知
 */
public class MessageActivity extends Activity implements MessageContract.View {

    private RecyclerView mRecyclerView;
    private ImageButton btn_newfriends_back;
    private MessageAdapter adapter;
    private MessageContract.Presenter mPresenter;
    private ProgressDialog pd;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_msg);
        mPresenter = new MessagePresenter(this);
        btn_newfriends_back = (ImageButton) findViewById(R.id.back);
        btn_newfriends_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.new_friends_list);
        adapter = new MessageAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        mPresenter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void addAllMessages(List<ChatInviteMessage> messages) {
        adapter.addAllMessages(messages);
    }

    @Override
    public void agree() {
        Toast.makeText(this, "已同意", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void tryAgain() {
        Toast.makeText(this, "请重试", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void acceptInvitation(Button button, ChatInviteMessage chatInviteMessage) {
        mPresenter.acceptInvitation(button, chatInviteMessage);
    }

    @Override
    public void showDialog() {
        pd = new ProgressDialog(this);
        String str1 = getResources().getString(R.string.Are_agree_with);
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    @Override
    public void dialogDismiss() {
        pd.dismiss();
    }
}
package com.mottc.chat.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.mottc.chat.groupdetail.GroupDetailActivity;
import com.mottc.chat.userdetail.UserDetailActivity;
import com.mottc.chat.R;
import com.mottc.chat.main.MainActivity;
import com.mottc.chat.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/10/12
 * Time: 12:14
 */
public class ChatActivity extends AppCompatActivity implements View.OnLayoutChangeListener, ChatContract.View {

    private ListView listView;
    private Button btn_send;
    private ImageButton btn_back;
    private EditText et_content;
    private ImageButton detail;
    private MessageAdapter adapter;
    private NotificationManager manager;//通知栏控制类
    protected PowerManager.WakeLock wakeLock;
    private List<View> hideInputExcludeViews = new ArrayList<>();
    private ChatContract.Presenter mPresenter;
    private TextView tv_toUsername;
    private MessageListener mMessageListener;
    private String toChatUsername;
    private int chatType;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

//      软键盘弹起,整个页面不上移。
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_chat);
        toChatUsername = this.getIntent().getStringExtra("username");
        chatType = this.getIntent().getIntExtra("type", 1);

        mPresenter = new ChatPresenter(this);
        mMessageListener = new MessageListener(toChatUsername, this);
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel("", 0);

        tv_toUsername = (TextView) this.findViewById(R.id.tv_toUsername);
        listView = (ListView) this.findViewById(R.id.listView);
        btn_send = (Button) this.findViewById(R.id.btn_send);
        et_content = (EditText) this.findViewById(R.id.et_content);
        btn_back = (ImageButton) this.findViewById(R.id.back);
        detail = (ImageButton) findViewById(R.id.detail);

        wakeLock = ((PowerManager) this.getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");


        mPresenter.setInfo(toChatUsername, chatType);

        mPresenter.start();

        setChatToUsername(chatType, toChatUsername);

        adapter = new MessageAdapter(this);
        listView.setAdapter(adapter);
        listView.setSelection(listView.getCount() - 1);
        hideInputExcludeViews.add(btn_send);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = et_content.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                mPresenter.sendMessage(content);
            }

        });
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.gotoDetailActivity();
            }
        });


        mPresenter.loadAllMessages();
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
        listView.addOnLayoutChangeListener(this);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        /*点击非键盘区，键盘落下*/
        DisplayUtils.hideInputWhenTouchOtherView(this, event, hideInputExcludeViews);
        return super.dispatchTouchEvent(event);
    }


    @Override
    public void setChatToUsername(int chatType, String username) {
        if (chatType == 1) {
            tv_toUsername.setText(username);
        } else {
            tv_toUsername.setText(EMClient.getInstance().groupManager().getGroup(username).getGroupName());
        }
    }

    @Override
    public void sendNotification(String username, String info) {

        Intent intent = new Intent(ChatActivity.this, ChatActivity.class).putExtra("username", username);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);//设置图标
        builder.setWhen(System.currentTimeMillis());//设置时间
        builder.setContentTitle(username);//设置标题
        builder.setContentText(info);//设置通知内容
        builder.setContentIntent(pendingIntent);//点击后的意图
        builder.setDefaults(Notification.DEFAULT_ALL);//设置震动、响铃、呼吸灯。
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setPriority(Notification.PRIORITY_HIGH);

//        builder.setAutoCancel(true);
        Notification notification = builder.build();//4.1以上
        notification.flags = Notification.FLAG_AUTO_CANCEL;//通知栏消息，点击后消失。
//        manager.notify((int) System.currentTimeMillis(), notification);
        manager.notify(username, 0, notification);
    }

    @Override
    public void clearContent() {

        et_content.setText("");
        et_content.clearFocus();
    }

    @Override
    public void addMessage(EMMessage message) {
        adapter.addMessage(message);
    }



    @Override
    public void gotoListBottom() {
        listView.setSelection(listView.getCount() - 1);
    }

    @Override
    public void gotoUserDetailActivity(String toChatUsername) {
        startActivity(new Intent(ChatActivity.this, UserDetailActivity.class).putExtra("username", toChatUsername));

    }

    @Override
    public void gotoGroupDetailActivity(String toChatUsername) {

        startActivity(new Intent(ChatActivity.this, GroupDetailActivity.class).putExtra("groupId", toChatUsername));

    }

    @Override
    public void addMessages(List<EMMessage> messages) {
        adapter.addMessages(messages);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.markAllMessagesAsRead();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
    }


    /*弹起软键盘时，对话列表上移，使其不受键盘遮挡*/
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {

        if (bottom < oldBottom) {
            listView.smoothScrollToPosition(listView.getCount() - 1);
        }

    }

}

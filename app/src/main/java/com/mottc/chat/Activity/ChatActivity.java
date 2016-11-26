package com.mottc.chat.Activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.mottc.chat.Constant;
import com.mottc.chat.R;
import com.mottc.chat.utils.PersonAvatarUtils;
import com.mottc.chat.utils.DisplayUtils;
import com.mottc.chat.utils.EaseCommonUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/10/12
 * Time: 12:14
 */
public class ChatActivity extends AppCompatActivity implements View.OnLayoutChangeListener {

    private ListView listView;
    private int chatType;
    private String toChatUsername;
    private Button btn_send;
    private ImageButton btn_back;
    private EditText et_content;
    private List<EMMessage> msgList;
    private ImageButton detail;
    MessageAdapter adapter;
    private EMConversation conversation;
    protected int pagesize = 20;
    NotificationManager manager;//通知栏控制类


    List<View> excludeViews = new ArrayList<View>();

    @Override

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

//      软键盘弹起,整个页面不上移。
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_chat);

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        toChatUsername = this.getIntent().getStringExtra("username");
        chatType = this.getIntent().getIntExtra("type", 1);
        TextView tv_toUsername = (TextView) this.findViewById(R.id.tv_toUsername);
        if (chatType == 1) {
            tv_toUsername.setText(toChatUsername);
        } else {
            tv_toUsername.setText(EMClient.getInstance().groupManager().getGroup(toChatUsername).getGroupName());
        }

        manager.cancel(toChatUsername, 0);
        listView = (ListView) this.findViewById(R.id.listView);
        btn_send = (Button) this.findViewById(R.id.btn_send);
        et_content = (EditText) this.findViewById(R.id.et_content);
        btn_back = (ImageButton) this.findViewById(R.id.back);
        detail = (ImageButton) findViewById(R.id.detail);

        getAllMessage();
        msgList = conversation.getAllMessages();
        adapter = new MessageAdapter(msgList, toChatUsername, ChatActivity.this);
        listView.setAdapter(adapter);
        listView.setSelection(listView.getCount() - 1);

        excludeViews.add(btn_send);
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
                setMesaage(content);
            }

        });
        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatType == 1) {
                    startActivity(new Intent(ChatActivity.this, UserDetailActivity.class).putExtra("username", toChatUsername));
                } else {
                    startActivity(new Intent(ChatActivity.this, GroupDetailActivity.class).putExtra("groupId", toChatUsername));
                }
            }
        });

        EMClient.getInstance().chatManager().addMessageListener(msgListener);
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
        DisplayUtils.hideInputWhenTouchOtherView(this, event, excludeViews);
        return super.dispatchTouchEvent(event);
    }

    protected void getAllMessage() {
        // 获取当前conversation对象

        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername,
                EaseCommonUtils.getConversationType(chatType), true);
        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }

    }

    private void setMesaage(String content) {

        // 创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        // 如果是群聊，设置chattype，默认是单聊
        if (chatType == Constant.CHATTYPE_GROUP)
            message.setChatType(EMMessage.ChatType.GroupChat);
        // 发送消息
        Log.i("ChatActivity", "setMesaage: " + message.getChatType().toString());
        EMClient.getInstance().chatManager().sendMessage(message);
        msgList.add(message);

        adapter.notifyDataSetChanged();
        if (msgList.size() > 0) {
            listView.setSelection(listView.getCount() - 1);
        }
        et_content.setText("");
        et_content.clearFocus();
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            for (EMMessage message : messages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                    username = message.getTo();
                    Log.i("ChatActivity", "onMessageReceived: " + "g" + username);
                } else {
                    // 单聊消息
                    username = message.getFrom();
                    Log.i("ChatActivity", "onMessageReceived: " + "dan" + username);

                }
                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(toChatUsername)) {
                    msgList.addAll(messages);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            if (msgList.size() > 0) {
                                listView.setSelection(listView.getCount() - 1);
                            }
                        }
                    });
                } else {
                    //                  获取发来的消息
                    String info = message.toString();
                    int start = info.indexOf("txt:\"");
                    int end = info.lastIndexOf("\"");
                    info = info.substring((start + 5), end);
                    sendNotification(username, info);
                }
            }
            // 收到消息
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            // 收到透传消息
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            // 收到已读回执
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            // 收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            // 消息状态变动
        }
    };

    //  收到新消息，在通知栏显示通知
    private void sendNotification(String username, String info) {

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
    protected void onPause() {
        super.onPause();
        conversation.markAllMessagesAsRead();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }


    /*弹起软键盘时，对话列表上移，使其不受键盘遮挡*/
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {

        if (bottom < oldBottom) {
            listView.smoothScrollToPosition(listView.getCount() - 1);
        }

    }


    @SuppressLint("InflateParams")
    class MessageAdapter extends BaseAdapter {
        private List<EMMessage> msgs;
        private Context context;
        private LayoutInflater inflater;
        private String tousername;

        public MessageAdapter(List<EMMessage> msgs, String toUserName, Context context_) {
            this.msgs = msgs;
            this.context = context_;
            inflater = LayoutInflater.from(context);
            this.tousername = toUserName;
        }

        public void setMsgs(List<EMMessage> msgs) {
            this.msgs = msgs;
        }

        @Override
        public int getCount() {
            return msgs.size();
        }

        @Override
        public EMMessage getItem(int position) {
            return msgs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            EMMessage message = getItem(position);
            return message.direct() == EMMessage.Direct.RECEIVE ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final EMMessage message = getItem(position);
            final int viewType = getItemViewType(position);
            if (convertView == null) {
                if (viewType == 0) {
                    convertView = inflater.inflate(R.layout.item_message_received, parent, false);
                } else {
                    convertView = inflater.inflate(R.layout.item_message_sent, parent, false);
                }
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
                holder.toUsername = (TextView) convertView.findViewById(R.id.tv_userid);
                holder.mImageView = (ImageView) convertView.findViewById(R.id.iv_userhead);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            holder.tv.setText(txtBody.getMessage());
            if (chatType == 1) {
                holder.toUsername.setText(tousername);
                PersonAvatarUtils.setAvatar(context, message.getFrom(), holder.mImageView);
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewType == 0) {
                            startActivity(new Intent(ChatActivity.this, UserDetailActivity.class).putExtra("username", tousername));
                        } else {
                            startActivity(new Intent(ChatActivity.this,UserDetailActivity.class).putExtra("username",EMClient.getInstance().getCurrentUser()));
                        }

                    }
                });

            } else {
                holder.toUsername.setText(message.getFrom());
                PersonAvatarUtils.setAvatar(context, message.getFrom(), holder.mImageView);
                holder.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewType == 0) {
                            startActivity(new Intent(ChatActivity.this, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        } else {
                            startActivity(new Intent(ChatActivity.this, UserDetailActivity.class).putExtra("username", EMClient.getInstance().getCurrentUser()));

                        }
                    }
                });

            }


            return convertView;
        }


        public class ViewHolder {

            TextView tv;
            TextView toUsername;
            ImageView mImageView;

        }
    }


}

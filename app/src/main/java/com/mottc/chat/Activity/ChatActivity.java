package com.mottc.chat.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.mottc.chat.Constant;
import com.mottc.chat.R;
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
public class ChatActivity extends AppCompatActivity {

    private ListView listView;
    private int chatType = 1;
    private String toChatUsername;
    private Button btn_send;
    private ImageButton btn_back;
    private EditText et_content;
    private List<EMMessage> msgList;
    MessageAdapter adapter;
    private EMConversation conversation;
    protected int pagesize = 20;

    List<View> excludeViews = new ArrayList<View>();

    @Override

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);



//      软键盘弹起不会遮挡文字
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_chat);


        toChatUsername = this.getIntent().getStringExtra("username");
        TextView tv_toUsername = (TextView) this.findViewById(R.id.tv_toUsername);
        tv_toUsername.setText(toChatUsername);




        listView = (ListView) this.findViewById(R.id.listView);
        btn_send = (Button) this.findViewById(R.id.btn_send);
        et_content = (EditText) this.findViewById(R.id.et_content);
        btn_back = (ImageButton) this.findViewById(R.id.back);
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

        EMClient.getInstance().chatManager().addMessageListener(msgListener);

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
                if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();
                }
                // 如果是当前会话的消息，刷新聊天页面
                if (username.equals(toChatUsername)) {
                    msgList.addAll(messages);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    if (msgList.size() > 0) {
                        listView.setSelection(listView.getCount() - 1);
                    }
                }
            });

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
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

            EMMessage message = getItem(position);
            int viewType = getItemViewType(position);
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
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
            holder.tv.setText(txtBody.getMessage());
            holder.toUsername.setText(tousername);
            return convertView;
        }


        public class ViewHolder {

            TextView tv;
            TextView toUsername;

        }
    }


}

package com.mottc.chat.Activity.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.mottc.chat.Activity.UserDetailActivity;
import com.mottc.chat.R;
import com.mottc.chat.utils.PersonAvatarUtils;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/30
 * Time: 12:12
 */

public class MessageAdapter extends BaseAdapter {
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
//            new AvatarURLDownloadUtils().downLoad(message.getFrom(), context, holder.mImageView, false);
        PersonAvatarUtils.setAvatar(context, message.getFrom(), holder.mImageView);
        holder.toUsername.setText(message.getFrom());
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
            }
        });

        return convertView;
    }

    public class ViewHolder {

        TextView tv;
        TextView toUsername;
        ImageView mImageView;

    }
}
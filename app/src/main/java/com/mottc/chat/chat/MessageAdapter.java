package com.mottc.chat.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.mottc.chat.R;
import com.mottc.chat.utils.PersonAvatarUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/30
 * Time: 12:12
 */

public class MessageAdapter extends BaseAdapter {
    private List<EMMessage> mMessages;
    private ChatContract.View mView;
    private LayoutInflater inflater;


    public MessageAdapter(ChatContract.View view) {
        mView = view;
        inflater = LayoutInflater.from((Context) view);
        mMessages = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public EMMessage getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final EMMessage message = mMessages.get(position);
        int viewType = getItemViewType(position);

        ViewHolderTxtReceive viewHolderTxtReceive = null;
        ViewHolderTxtSent viewHolderTxtSent = null;


        if (convertView == null) {
            switch (viewType) {
                case 0:
                    viewHolderTxtReceive = new ViewHolderTxtReceive();
                    convertView = inflater.inflate(R.layout.item_message_received, parent, false);
                    viewHolderTxtReceive.toUsername = (TextView) convertView.findViewById(R.id.tv_userid);
                    viewHolderTxtReceive.mImageView = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    viewHolderTxtReceive.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);


                    PersonAvatarUtils.setAvatar((Context) mView, message.getFrom(), viewHolderTxtReceive.mImageView);
                    viewHolderTxtReceive.toUsername.setText(message.getFrom());
                    viewHolderTxtReceive.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mView.gotoUserDetailActivity(message.getFrom());
                        }
                    });

                    EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                    viewHolderTxtReceive.tv.setText(txtBody.getMessage());
                    convertView.setTag(viewHolderTxtReceive);
                    break;

                case 1:

                    viewHolderTxtSent = new ViewHolderTxtSent();
                    convertView = inflater.inflate(R.layout.item_message_sent, parent, false);
//                    viewHolderTxtSent.toUsername = (TextView) convertView.findViewById(R.id.tv_userid);
                    viewHolderTxtSent.mImageView = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    viewHolderTxtSent.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);


                    PersonAvatarUtils.setAvatar((Context) mView, message.getFrom(), viewHolderTxtSent.mImageView);
//                    viewHolderTxtSent.toUsername.setText(message.getFrom());
                    viewHolderTxtSent.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mView.gotoUserDetailActivity(message.getFrom());
                        }
                    });


                    EMTextMessageBody txtBodySent = (EMTextMessageBody) message.getBody();
                    viewHolderTxtSent.tv.setText(txtBodySent.getMessage());

                    convertView.setTag(viewHolderTxtSent);
                    break;


                default:
                    break;
            }
        } else {
            switch (viewType) {
                case 0:
                    viewHolderTxtReceive = (ViewHolderTxtReceive) convertView.getTag();
                    PersonAvatarUtils.setAvatar((Context) mView, message.getFrom(), viewHolderTxtReceive.mImageView);
                    viewHolderTxtReceive.toUsername.setText(message.getFrom());
                    viewHolderTxtReceive.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mView.gotoUserDetailActivity(message.getFrom());
                        }
                    });

                    EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                    viewHolderTxtReceive.tv.setText(txtBody.getMessage());
                    break;

                case 1:
                    viewHolderTxtSent = (ViewHolderTxtSent) convertView.getTag();
                    PersonAvatarUtils.setAvatar((Context) mView, message.getFrom(), viewHolderTxtSent.mImageView);
//                    viewHolderTxtSent.toUsername.setText(message.getFrom());
                    viewHolderTxtSent.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mView.gotoUserDetailActivity(message.getFrom());

                        }
                    });


                    EMTextMessageBody txtBodySent = (EMTextMessageBody) message.getBody();
                    viewHolderTxtSent.tv.setText(txtBodySent.getMessage());
                    break;
                default:
                    break;
            }
        }

        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            return 0;
        } else {
            return 1;
        }

    }

    public void addMessage(EMMessage message) {
        mMessages.add(message);
        notifyDataSetChanged();
        if (mMessages.size() > 0) {
            mView.gotoListBottom();
        }
    }

    public void addMessages(List<EMMessage> messages) {
        mMessages.addAll(messages);
        notifyDataSetChanged();
        if (mMessages.size() > 0) {
            mView.gotoListBottom();
        }
    }





    public class ViewHolderTxtReceive {
        TextView tv;
        TextView toUsername;
        ImageView mImageView;
    }

    public class ViewHolderTxtSent {
        TextView tv;
        ImageView mImageView;
    }

}
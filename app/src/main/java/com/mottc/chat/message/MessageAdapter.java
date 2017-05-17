package com.mottc.chat.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mottc.chat.Constant;
import com.mottc.chat.R;
import com.mottc.chat.data.bean.ChatInviteMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/10/12
 * Time: 8:16
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private MessageContract.View mView;

    private List<ChatInviteMessage> mChatInviteMessages;

    String str1 = "已同意";
    String str2 = "同意";
    String str3 = "已拒绝";

    public MessageAdapter(MessageContract.View view) {
        this.mView = view;
        mChatInviteMessages = new ArrayList<>();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invite_msg, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        int orderNum = mChatInviteMessages.size() - position - 1;
        final ChatInviteMessage msg = mChatInviteMessages.get(orderNum);
        holder.mName.setText(mChatInviteMessages.get(orderNum).getFrom());
        holder.mReason.setText(mChatInviteMessages.get(orderNum).getReason());
        if (mChatInviteMessages.get(orderNum).getStatus() == Constant.AGREE) {
            holder.mReason.setText(str1);
        } else if (mChatInviteMessages.get(orderNum).getStatus() == Constant.UNHANDLE) {
            holder.mAgree.setVisibility(View.VISIBLE);
            holder.mAgree.setEnabled(true);
            holder.mAgree.setText(str2);
            // 设置点击事件
            holder.mAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 同意别人发的好友请求
                    mView.acceptInvitation(holder.mAgree, msg);

                }
            });
        } else if (mChatInviteMessages.get(orderNum).getStatus() == Constant.REFUSE) {
            holder.mReason.setText(str3);
        }

    }


    @Override
    public int getItemCount() {
        return mChatInviteMessages.size();
    }


    public void addAllMessages(List<ChatInviteMessage> messages) {
        mChatInviteMessages.addAll(messages);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mName;
        public final TextView mReason;
        public final Button mAgree;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.tv_name);
            mReason = (TextView) view.findViewById(R.id.tv_reason);
            mAgree = (Button) view.findViewById(R.id.btn_agree);

        }

    }

}

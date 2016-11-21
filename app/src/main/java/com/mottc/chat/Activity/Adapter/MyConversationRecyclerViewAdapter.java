package com.mottc.chat.Activity.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.util.DateUtils;
import com.mottc.chat.Activity.ConversationFragment;
import com.mottc.chat.R;

import java.util.Date;
import java.util.List;


public class MyConversationRecyclerViewAdapter extends RecyclerView.Adapter<MyConversationRecyclerViewAdapter.ViewHolder> {

    private final List<EMConversation> mValues;
    private final ConversationFragment.OnConversationFragmentInteractionListener mListener;

    public MyConversationRecyclerViewAdapter(List<EMConversation> items, ConversationFragment.OnConversationFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if (mValues.get(position).isGroup()){
            holder.mNameView.setText(EMClient.getInstance().groupManager().getGroup(mValues.get(position).getUserName()).getGroupName());
        }else{
            holder.mNameView.setText(mValues.get(position).getUserName());
        }

        int unread = mValues.get(position).getUnreadMsgCount();

        if (unread == 0){
            holder.mUnreadView.setVisibility(View.INVISIBLE);
        }else {
            holder.mUnreadView.setVisibility(View.VISIBLE);
            holder.mUnreadView.setText(String.valueOf(unread));
        }
        holder.mTime.setText(DateUtils.getTimestampString(new Date(mValues.get(position).getLastMessage().getMsgTime())));
        String mes = mValues.get(position).getLastMessage().getBody().toString();
        int start = mes.indexOf("txt:\"");
        int end = mes.lastIndexOf("\"");
        mes = mes.substring((start + 5), end);

        holder.mContent.setText(mes);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onConversationFragmentInteraction(holder.mItem);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mUnreadView;
        public final TextView mTime;
        public final TextView mContent;
        public EMConversation mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mUnreadView = (TextView) view.findViewById(R.id.unread_msg_number);
            mTime = (TextView) view.findViewById(R.id.time);
            mContent = (TextView) view.findViewById(R.id.message);
        }


    }
}

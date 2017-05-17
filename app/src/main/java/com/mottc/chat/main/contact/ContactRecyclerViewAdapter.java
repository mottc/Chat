package com.mottc.chat.main.contact;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mottc.chat.data.bean.ChatUser;
import com.mottc.chat.main.contact.ContactFragment.OnListFragmentInteractionListener;
import com.mottc.chat.R;
import com.mottc.chat.utils.AvatarUtils;

import java.util.ArrayList;
import java.util.List;

class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ViewHolder> {

    private  List<ChatUser> mValues;
    private  OnListFragmentInteractionListener mListener;
    private  Context context;

    ContactRecyclerViewAdapter(Context context, OnListFragmentInteractionListener listener) {
        mValues = new ArrayList<>();
        mListener = listener;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mName.setText(mValues.get(position).getUserName());
        AvatarUtils.setAvatar(context,mValues.get(position).getUserName(),holder.mAvatar);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void loadAllContract(List<ChatUser> chatUsers) {
        mValues.addAll(chatUsers);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        TextView mName;
        ImageView mAvatar;
        ChatUser mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.tv_name);
            mAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        }

    }
}

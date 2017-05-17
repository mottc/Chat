package com.mottc.chat.groupdetail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.mottc.chat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/22
 * Time: 11:55
 */
class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.ViewHolder> {


    private List<String> members;
    private String owner;
    private OnGroupMembersListClickListener mOnGroupMembersListClickListener;

    GroupMembersAdapter(String owner) {
        this.owner = owner;
        members = new ArrayList<>();
    }


    interface OnGroupMembersListClickListener {
        void OnGroupMembersListClick(String item);
    }

    void setOnGroupMembersListClickListener(OnGroupMembersListClickListener mOnGroupMembersListClickListener) {
        this.mOnGroupMembersListClickListener = mOnGroupMembersListClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_members_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = members.get(position);
        holder.mName.setText(members.get(position));
        if (owner.equals(members.get(position))) {
            holder.mOwner.setVisibility(View.VISIBLE);
        }
        if ((EMClient.getInstance().getCurrentUser()).equals(members.get(position))){
            holder.mSelf.setVisibility(View.VISIBLE);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnGroupMembersListClickListener.OnGroupMembersListClick(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    void addAllMembers(List<String> groupMembers) {
        members.addAll(groupMembers);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        final TextView mName;
        final TextView mOwner;
        final TextView mSelf;
        String mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.membersName);
            mOwner = (TextView) view.findViewById(R.id.isOwner);
            mSelf = (TextView) view.findViewById(R.id.self);

        }

    }
}

package com.mottc.chat.Activity.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mottc.chat.R;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/22
 * Time: 11:55
 */
public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.ViewHolder> {


    private final List<String> members;
    private final String owner;

    public GroupMembersAdapter(List<String> members, String owner) {
        this.members = members;
        this.owner = owner;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_members_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = members.get(position);
        holder.mName.setText(members.get(position));
        if (owner.equals(members.get(position))) {
            holder.mOwner.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mName;
        public final TextView mOwner;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.membersName);
            mOwner = (TextView) view.findViewById(R.id.isOwner);

        }

    }
}

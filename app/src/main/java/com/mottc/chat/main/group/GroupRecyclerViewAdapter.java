package com.mottc.chat.main.group;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;
import com.mottc.chat.R;
import com.mottc.chat.utils.GroupAvatarUtils;

import java.util.ArrayList;
import java.util.List;


public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder> {

    private  List<EMGroup> mGroups;
    private  Context mContext;
    private  GroupFragment.OnGroupFragmentInteractionListener mListener;

    public GroupRecyclerViewAdapter(Context context, GroupFragment.OnGroupFragmentInteractionListener listener) {
        mListener = listener;
        mContext = context;
        mGroups = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mGroups.get(position);
        holder.mName.setText(mGroups.get(position).getGroupName());
        GroupAvatarUtils.setAvatar(mContext, mGroups.get(position).getGroupId(), holder.mImageView);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onGroupFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    public void loadAllGroups(List<EMGroup> groupList) {
        mGroups.addAll(groupList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mName;
        public final ImageView mImageView;
        public EMGroup mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.tv_name);
            mImageView = (ImageView) view.findViewById(R.id.iv_avatar);


        }

    }
}

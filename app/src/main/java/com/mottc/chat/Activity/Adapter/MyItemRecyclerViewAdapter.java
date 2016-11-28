package com.mottc.chat.Activity.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mottc.chat.Activity.ContactFragment.OnListFragmentInteractionListener;
import com.mottc.chat.R;
import com.mottc.chat.db.EaseUser;
import com.mottc.chat.utils.PersonAvatarUtils;

import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<EaseUser> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final Context context;

    public MyItemRecyclerViewAdapter(Context context,List<EaseUser> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mName.setText(mValues.get(position).getUsername());
        PersonAvatarUtils.setAvatar(context,mValues.get(position).getUsername(),holder.mAvatar);
//        new AvatarURLDownloadUtils().downLoad(mValues.get(position).getUsername(), context, holder.mAvatar, false);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.


                    mListener.onListFragmentInteraction(holder.mItem);
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
        public final TextView mName;
        public final ImageView mAvatar;
        public EaseUser mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.tv_name);
            mAvatar = (ImageView) view.findViewById(R.id.iv_avatar);

        }

    }
}

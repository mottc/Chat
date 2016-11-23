package com.mottc.chat.Activity.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.mottc.chat.R;
import com.mottc.chat.db.InviteMessage;
import com.mottc.chat.db.InviteMessage.InviteMessageStatus;
import com.mottc.chat.db.InviteMessageDao;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/10/12
 * Time: 8:16
 */
public class NewFriendsMsgAdapter extends RecyclerView.Adapter<NewFriendsMsgAdapter.ViewHolder> {

    private Context context;
    private InviteMessageDao messgeDao;
    List<InviteMessage> objects;

    String str1 = "已同意";
    String str2 = "同意";
    String str3 = "请求加你为好友";

    public NewFriendsMsgAdapter(Context context, List<InviteMessage> objects) {
        this.context = context;
        messgeDao = new InviteMessageDao(context);
        this.objects = objects;

    }


    /**
     * 同意好友请求或者群申请
     */
    private void acceptInvitation(final Button buttonAgree, final InviteMessage msg) {
        final ProgressDialog pd = new ProgressDialog(context);
        String str1 = context.getResources().getString(R.string.Are_agree_with);
        final String str2 = context.getResources().getString(R.string.Has_agreed_to);
        final String str3 = context.getResources().getString(R.string.Agree_with_failure);
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                // 调用sdk的同意方法
                try {
                    if (msg.getStatus() == InviteMessageStatus.BEINVITEED) {//同意好友请求
                        EMClient.getInstance().contactManager().acceptInvitation(msg.getFrom());
                    } else if (msg.getStatus() == InviteMessageStatus.BEAPPLYED) { //同意加群申请
                        EMClient.getInstance().groupManager().acceptApplication(msg.getFrom(), msg.getGroupId());
                    } else if (msg.getStatus() == InviteMessageStatus.GROUPINVITATION) {
                        EMClient.getInstance().groupManager().acceptInvitation(msg.getGroupId(), msg.getGroupInviter());
                    }
                    msg.setStatus(InviteMessageStatus.AGREED);
                    // 更新db
                    ContentValues values = new ContentValues();
                    values.put(InviteMessageDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                    messgeDao.updateMessage(msg.getId(), values);
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @SuppressWarnings("deprecation")
                        @Override
                        public void run() {
                            pd.dismiss();
                            buttonAgree.setText(str2);
                            buttonAgree.setBackgroundDrawable(null);
                            buttonAgree.setEnabled(false);
                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @SuppressLint("ShowToast")
                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invite_msg, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        int orderNum = objects.size()-position-1;
        final InviteMessage msg = objects.get(orderNum);
        holder.mName.setText(objects.get(orderNum).getFrom());
        holder.mReason.setText(objects.get(orderNum).getReason());
        holder.mAgree.setVisibility(View.INVISIBLE);
        if (objects.get(orderNum).getStatus() == InviteMessage.InviteMessageStatus.BEAGREED) {
            holder.mReason.setText(str1);
        } else if (objects.get(orderNum).getStatus() == InviteMessage.InviteMessageStatus.BEINVITEED ||
                objects.get(orderNum).getStatus() == InviteMessage.InviteMessageStatus.BEAPPLYED ||
                objects.get(orderNum).getStatus() == InviteMessage.InviteMessageStatus.GROUPINVITATION) {
            holder.mAgree.setVisibility(View.VISIBLE);
            holder.mAgree.setEnabled(true);
            holder.mAgree.setText(str2);
            if (objects.get(orderNum).getReason() == null) {
                // 如果没写理由
                holder.mReason.setText(str3);
            }
            // 设置点击事件
            holder.mAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 同意别人发的好友请求
                    acceptInvitation(holder.mAgree, msg);
                }
            });
        } else if(objects.get(orderNum).getStatus() == InviteMessage.InviteMessageStatus.AGREED){
            holder.mAgree.setVisibility(View.VISIBLE);
            holder.mAgree.setText("已同意");
            holder.mAgree.setBackgroundDrawable(null);
            holder.mAgree.setEnabled(false);
        }

    }


    @Override
    public int getItemCount() {
        return objects.size();
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

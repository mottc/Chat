package com.mottc.chat.Activity.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.util.DateUtils;
import com.mottc.chat.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.baidu.mapapi.BMapManager.getContext;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/10/26
 * Time: 15:35
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {


    private static final String TAG = "ChatAllHistoryAdapter";
    private List<EMConversation> conversationList;
    private List<EMConversation> copyConversationList;
    private boolean notiyfyByFilter;

    protected int primaryColor;
    protected int secondaryColor;
    protected int timeColor;
    protected int primarySize;
    protected int secondarySize;
    protected float timeSize;

    public ConversationAdapter(List<EMConversation> objects) {
        conversationList = objects;
        copyConversationList = new ArrayList<EMConversation>();
        copyConversationList.addAll(objects);
    }

    public EMConversation getItem(int arg0) {
        if (arg0 < conversationList.size()) {
            return conversationList.get(arg0);
        }
        return null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(getContext())
                .inflate(com.hyphenate.easeui.R.layout.ease_row_chat_history, parent, false);
        return new ViewHolder(convertView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get conversation
        EMConversation conversation = getItem(position);
        // get username or group id
        String username = conversation.getUserName();

        holder.name.setText(getItem(position).getUserName());
        holder.unreadLabel.setText(getItem(position).getUnreadMsgCount());
        holder.message.setText(getItem(position).getLastMessage().toString());
        holder.time.setText(DateUtils.getTimestampString(new Date(getItem(position).getLastMessage().getMsgTime())));
        holder.avatar.setImageResource(R.mipmap.avatar);







//        if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
//            String groupId = conversation.getUserName();
//            if (EaseAtMessageHelper.get().hasAtMeMsg(groupId)) {
//                holder.motioned.setVisibility(View.VISIBLE);
//            } else {
//                holder.motioned.setVisibility(View.GONE);
//            }
//            // group message, show group avatar
//            holder.avatar.setImageResource(com.hyphenate.easeui.R.drawable.ease_group_icon);
//            EMGroup group = EMClient.getInstance().groupManager().getGroup(username);
//            holder.name.setText(group != null ? group.getGroupName() : username);
//        } else if (conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
//            holder.avatar.setImageResource(com.hyphenate.easeui.R.drawable.ease_group_icon);
//            EMChatRoom room = EMClient.getInstance().chatroomManager().getChatRoom(username);
//            holder.name.setText(room != null && !TextUtils.isEmpty(room.getName()) ? room.getName() : username);
//            holder.motioned.setVisibility(View.GONE);
//        } else {
//            EaseUserUtils.setUserAvatar(getContext(), username, holder.avatar);
//            EaseUserUtils.setUserNick(username, holder.name);
//            holder.motioned.setVisibility(View.GONE);
//        }
//
//        if (conversation.getUnreadMsgCount() > 0) {
//            // show unread message count
//            holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
//            holder.unreadLabel.setVisibility(View.VISIBLE);
//        } else {
//            holder.unreadLabel.setVisibility(View.INVISIBLE);
//        }
//
//        if (conversation.getAllMsgCount() != 0) {
//            // show the content of latest message
//            EMMessage lastMessage = conversation.getLastMessage();
//            String content = null;
//            if (cvsListHelper != null) {
//                content = cvsListHelper.onSetItemSecondaryText(lastMessage);
//            }
//            holder.message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, (getContext()))),
//                    TextView.BufferType.SPANNABLE);
//            if (content != null) {
//                holder.message.setText(content);
//            }
//            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
//            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
//                holder.msgState.setVisibility(View.VISIBLE);
//            } else {
//                holder.msgState.setVisibility(View.GONE);
//            }
//        }

        //set property
        holder.name.setTextColor(primaryColor);
        holder.message.setTextColor(secondaryColor);
        holder.time.setTextColor(timeColor);
        if (primarySize != 0)
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if (secondarySize != 0)
            holder.message.setTextSize(TypedValue.COMPLEX_UNIT_PX, secondarySize);
        if (timeSize != 0)
            holder.time.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeSize);


    }

    @Override
    public int getItemCount() {
        return 0;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * who you chat with
         */
        final TextView name;
        /**
         * unread message count
         */
        final TextView unreadLabel;
        /**
         * content of last message
         */
        final TextView message;
        /**
         * time of last message
         */
        final TextView time;
        /**
         * avatar
         */
        final ImageView avatar;
        /**
         * status of last message
         */
        final View msgState;
        /**
         * layout
         */
        final RelativeLayout list_itease_layout;
        final TextView motioned;
        public final View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            name = (TextView) view.findViewById(com.hyphenate.easeui.R.id.name);
            unreadLabel = (TextView) view.findViewById(com.hyphenate.easeui.R.id.unread_msg_number);
            message = (TextView) view.findViewById(com.hyphenate.easeui.R.id.message);
            time = (TextView) view.findViewById(com.hyphenate.easeui.R.id.time);
            avatar = (ImageView) view.findViewById(com.hyphenate.easeui.R.id.avatar);
            msgState = view.findViewById(com.hyphenate.easeui.R.id.msg_state);
            list_itease_layout = (RelativeLayout) view.findViewById(com.hyphenate.easeui.R.id.list_itease_layout);
            motioned = (TextView) view.findViewById(com.hyphenate.easeui.R.id.mentioned);

        }

    }
}

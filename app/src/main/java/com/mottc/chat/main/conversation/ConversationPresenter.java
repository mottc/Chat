package com.mottc.chat.main.conversation;

import android.util.Pair;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.mottc.chat.main.conversation.ConversationContract.Presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/15
 * Time: 21:28
 */
public class ConversationPresenter implements Presenter {
    private ConversationContract.View mView;
    public ConversationPresenter(ConversationContract.View view) {
        mView = view;
    }

    @Override
    public void start() {

        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<>();

        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> conversationList = new ArrayList<>();
        conversationList.clear();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            conversationList.add(sortItem.second);
        }

        mView.loadAllConversation(conversationList);

    }

    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    public void onDestroy() {
        mView = null;
    }
}

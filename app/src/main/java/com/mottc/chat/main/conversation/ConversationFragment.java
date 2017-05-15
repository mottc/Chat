package com.mottc.chat.main.conversation;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.mottc.chat.R;

import java.util.List;

public class ConversationFragment extends Fragment implements ConversationContract.View {

    private OnConversationFragmentInteractionListener mConversationListener;
    private ConversationRecyclerViewAdapter mConversationRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private ConversationContract.Presenter mPresenter;
    private ChatMessageListener mChatMessageListener;


    public ConversationFragment() {
        mPresenter = new ConversationPresenter(this);
        mChatMessageListener = new ChatMessageListener(this);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ConversationFragment newInstance() {
        ConversationFragment fragment = new ConversationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.start();
        EMClient.getInstance().chatManager().addMessageListener(mChatMessageListener);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            mConversationRecyclerViewAdapter = new ConversationRecyclerViewAdapter(getActivity(), mConversationListener);
            recyclerView.setAdapter(mConversationRecyclerViewAdapter);
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(mChatMessageListener);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnConversationFragmentInteractionListener) {
            mConversationListener = (OnConversationFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mConversationListener = null;
    }



    @Override
    public void update() {
        mPresenter.start();
    }

    @Override
    public void loadAllConversation(List<EMConversation> conversationList) {
        mConversationRecyclerViewAdapter.loadAllConversation(conversationList);
    }


    public interface OnConversationFragmentInteractionListener {
        // TODO: Update argument type and name
        void onConversationFragmentInteraction(EMConversation item);
    }
}

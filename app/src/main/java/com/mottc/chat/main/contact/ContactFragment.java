package com.mottc.chat.main.contact;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mottc.chat.R;
import com.mottc.chat.data.bean.ChatUser;

import java.util.List;

public class ContactFragment extends Fragment implements ContactContract.View{


    private OnListFragmentInteractionListener mListener;
    private ContactRecyclerViewAdapter mContactRecyclerViewAdapter;
    private ContactPresenter mContactPresenter;

    public ContactFragment() {
        mContactPresenter = new ContactPresenter(this);
    }

    @SuppressWarnings("unused")
    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContactRecyclerViewAdapter = new ContactRecyclerViewAdapter(getActivity(), mListener);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(mContactRecyclerViewAdapter);
            mContactPresenter.start();
        }

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mContactPresenter.onDestroy();
    }

    @Override
    public void loadContact(List<ChatUser> chatUsers) {
        mContactRecyclerViewAdapter.loadAllContract(chatUsers);
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ChatUser item);
    }


}

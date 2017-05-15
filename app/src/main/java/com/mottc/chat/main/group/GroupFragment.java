package com.mottc.chat.main.group;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hyphenate.chat.EMGroup;
import com.mottc.chat.R;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnGroupFragmentInteractionListener}
 * interface.
 */
public class GroupFragment extends Fragment implements GroupContract.View{

    private OnGroupFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private GroupRecyclerViewAdapter mGroupRecyclerViewAdapter;
    private GroupContract.Presenter mPresenter;


    public GroupFragment() {
        mPresenter = new GroupPresenter(this);
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static GroupFragment newInstance() {
        GroupFragment fragment = new GroupFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.start();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mGroupRecyclerViewAdapter = new GroupRecyclerViewAdapter(getActivity(), mListener);
            recyclerView.setAdapter(mGroupRecyclerViewAdapter);
            
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGroupFragmentInteractionListener) {
            mListener = (OnGroupFragmentInteractionListener) context;
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
        mPresenter.onDestroy();
    }

    @Override
    public void loadGroups(List<EMGroup> groupList) {
        mGroupRecyclerViewAdapter.loadAllGroups(groupList);
    }

    @Override
    public void loadGroupsError(String errorMsg) {
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    public interface OnGroupFragmentInteractionListener {
        // TODO: Update argument type and name
        void onGroupFragmentInteraction(EMGroup item);
    }
}

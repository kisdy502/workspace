package com.sdt.nepush.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sdt.nepush.App;
import com.sdt.nepush.R;
import com.sdt.nepush.activity.ChatActivity;
import com.sdt.nepush.adapter.ConversationAdapter;
import com.sdt.nepush.db.Conversation2Model;
import com.sdt.nepush.db.Conversation2Model_Table;
import com.sdt.nepush.db.User2Model;
import com.sdt.nepush.widget.recycler.OnItemClickListener;
import com.sdt.nepush.widget.recycler.XRecyclerView;

import java.util.List;

/**
 * Created by sdt13411 on 2019/8/10.
 */

public class ConversationFragment extends Fragment implements OnItemClickListener {

    private XRecyclerView rvConversationList;
    private List<Conversation2Model> mConversationList;
    private ConversationAdapter mConversationAdapter;

    private User2Model mCurrentUser;

    public static ConversationFragment newInstance() {
        ConversationFragment conversationFragment = new ConversationFragment();
        return conversationFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvConversationList = view.findViewById(R.id.rv_conversatiion_list);
        initUser();
        if (mCurrentUser == null) {
            return;
        }
        initUI();
        initConversationList();

        mConversationAdapter = new ConversationAdapter(App.getInstance(), mConversationList);
        rvConversationList.setAdapter(mConversationAdapter);
    }

    private void initUI() {

        rvConversationList.setLayoutManager(new LinearLayoutManager(App.getInstance()));
        rvConversationList.setOnItemClickListener(this);
    }

    private void initUser() {
        mCurrentUser = User2Model.getLoginUser();
    }

    private void initConversationList() {
        List<Conversation2Model> conversation2ModelList = SQLite.select().from(Conversation2Model.class)
                .where(Conversation2Model_Table.createUser.eq(mCurrentUser.getUserName())).queryList();
        mConversationList = conversation2ModelList;
    }


    @Override
    public void onItemClick(RecyclerView parent, View itemView, int position) {
        Conversation2Model conversation2Model = mConversationList.get(position);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("toObject", conversation2Model.getToObjectId());
        intent.putExtra("conversationType", conversation2Model.getConversationType());
        startActivity(intent);
    }
}

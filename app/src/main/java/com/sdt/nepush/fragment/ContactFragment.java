package com.sdt.nepush.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sdt.nepush.R;
import com.sdt.nepush.activity.ChatActivity;
import com.sdt.nepush.db.Conversation2Model;
import com.sdt.nepush.db.Friend2Model;
import com.sdt.nepush.db.Friend2Model_Table;
import com.sdt.nepush.db.User2Model;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.event.I_CEventListener;
import com.sdt.nepush.widget.recycler.OnItemClickListener;
import com.sdt.nepush.widget.recycler.XRecyclerView;

import java.util.List;

/**
 * Created by sdt13411 on 2019/8/10.
 */

public class ContactFragment extends Fragment implements OnItemClickListener, I_CEventListener {
    private XRecyclerView rvContactList;
    private List<Friend2Model> friendList;
    private ContactAdapter mAdapter;

    private static final String[] EVENTS = {
            Events.AGREE_ADD_FRIEND_MESSAGE,
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvContactList = (XRecyclerView) view.findViewById(R.id.rv_contact_list);
        rvContactList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvContactList.setOnItemClickListener(this);
        initData();
        CEventCenter.registerEventListener(this, EVENTS);

    }

    private void initData() {
        User2Model currentUser = User2Model.getLoginUser();
        friendList = SQLite.select().from(Friend2Model.class)
                .where(Friend2Model_Table.myId.eq(currentUser.getUserId()))
                .queryList();

        mAdapter = new ContactAdapter(getContext(), friendList);
        rvContactList.setAdapter(mAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        CEventCenter.unregisterEventListener(this, EVENTS);
    }

    @Override
    public void onItemClick(RecyclerView parent, View itemView, int position) {
        Long toUserId = friendList.get(position).getFriendId();
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("toObject", toUserId);
        intent.putExtra("conversationType", Conversation2Model.Conversation_Type_Single);
        startActivity(intent);
    }

    @Override
    public void onCEvent(String topic, int msgCode, int resultCode, Object obj) {
        friendList.add((Friend2Model) obj);
        mAdapter.notifyDataSetChanged();
    }


    private static class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.VH> {
        private Context mContext;
        private List<Friend2Model> friendList;

        public ContactAdapter(Context context, List<Friend2Model> friendList) {
            this.mContext = context;
            this.friendList = friendList;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_friend, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            holder.tvFriend.setText(friendList.get(position).getUserName());
        }

        @Override
        public int getItemCount() {
            return friendList != null ? friendList.size() : 0;
        }

        static class VH extends RecyclerView.ViewHolder {
            private TextView tvFriend;

            public VH(View itemView) {
                super(itemView);
                tvFriend = (TextView) itemView.findViewById(R.id.tvFriendName);
            }
        }
    }
}

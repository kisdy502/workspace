package com.sdt.nepush.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.MessageType;
import com.sdt.nepush.R;
import com.sdt.nepush.adapter.ChatMessageAdapter;
import com.sdt.nepush.db.Message2Model;
import com.sdt.nepush.db.Message2Model_Table;
import com.sdt.nepush.db.User2Model;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.event.I_CEventListener;
import com.sdt.nepush.msg.SingleMessage;
import com.sdt.nepush.processor.MessageProcessor;
import com.sdt.nepush.util.CThreadPoolExecutor;
import com.sdt.nepush.widget.recycler.XRecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener, I_CEventListener {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private String toUserId;
    private User2Model currentUser;
    private XRecyclerView rvChatList;
    private Button btnSend;
    private EditText edtMessage;
    private List<Message2Model> chatMessageList;
    private ChatMessageAdapter mAdapter;

    private static final String[] EVENTS = {
            Events.CHAT_SINGLE_MESSAGE,
            Events.REPORT_CHAT_MESSAGE_STATUS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toUserId = getIntent().getStringExtra("toUserId");
        currentUser = User2Model.getLoginUser();
        pwDesplay();
        CEventCenter.registerEventListener(this, EVENTS);
        initUI();
        initChatMessageList();
        initRecyclerList();
        initToolBar();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_chat_toolbar);
        toolbar.setPopupTheme(R.style.popup_theme);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(toUserId);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.apply_jurassic));

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void pwDesplay() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        logger.d("w" + dm.widthPixels);
        logger.d("h" + dm.heightPixels);
        logger.d("dpi" + dm.densityDpi);
        logger.d("density" + dm.density);
        logger.d("xdpi" + dm.xdpi);
        logger.d("ydpi" + dm.ydpi);
    }

    private void initUI() {
        rvChatList = (XRecyclerView) findViewById(R.id.rv_message_list);
        edtMessage = (EditText) findViewById(R.id.edt_message);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
    }

    //加载聊天记录
    private void initChatMessageList() {
        OperatorGroup op = OperatorGroup.clause(OperatorGroup.clause()
                .and(Message2Model_Table.fromId.eq(currentUser.getUserName()))
                .and(Message2Model_Table.toId.eq(toUserId)))
                .or(OperatorGroup.clause()
                        .and(Message2Model_Table.fromId.eq(toUserId))
                        .and(Message2Model_Table.toId.eq(currentUser.getUserName())));


        chatMessageList = SQLite.select().from(Message2Model.class)
                .where(op)
                .orderBy(OrderBy.fromNameAlias(NameAlias.of("id")).descending())
                .limit(50)
                .queryList();  //查询最后50条聊天记录
        //需要翻转，让数据按id从小往大排序
        Collections.reverse(chatMessageList);
    }

    private void initRecyclerList() {
        rvChatList.setLayoutManager(new LinearLayoutManager(getApplication()));
        mAdapter = new ChatMessageAdapter(this, chatMessageList, currentUser.getUserName(), toUserId);
        rvChatList.setAdapter(mAdapter);
    }

    private void sendMessage() {
        SingleMessage message = new SingleMessage();
        message.setMsgId(UUID.randomUUID().toString());
        message.setMsgType(MessageType.SINGLE_CHAT.getMsgType());
        message.setMsgContentType(MessageType.MessageContentType.TEXT.getMsgContentType());
        message.setFromId(currentUser.getUserName());
        message.setToId(toUserId);
        message.setTimestamp(System.currentTimeMillis());
        message.setContent(edtMessage.getText().toString());
        MessageProcessor.getInstance().sendMsg(message);

        Message2Model message2Model = saveMessage2Db(message);

        chatMessageList.add(message2Model);
        mAdapter.notifyDataSetChanged();
        rvChatList.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private Message2Model saveMessage2Db(SingleMessage message) {
        Message2Model message2Model = new Message2Model();
        message2Model.setMsgId(message.getMsgId());
        message2Model.setMsgType(message.getMsgType());
        message2Model.setMsgContentType(message.getMsgContentType());
        message2Model.setFromId(message.getFromId());
        message2Model.setToId(message.getToId());
        message2Model.setTimestamp(message.getTimestamp());
        message2Model.setContent(message.getContent());
        message2Model.setStatusReport(message.getStatusReport());
        message2Model.setExtend(message.getExtend());
        message2Model.save();
        return message2Model;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CEventCenter.unregisterEventListener(this, EVENTS);
    }

    @Override
    public void onClick(View view) {
        if (TextUtils.isEmpty(edtMessage.getText().toString().trim())) {
            return;
        }
        sendMessage();
        edtMessage.setText("");
    }

    @Override
    public void onCEvent(String topic, int msgCode, int resultCode, Object obj) {
        switch (topic) {
            case Events.CHAT_SINGLE_MESSAGE: {
                final Message2Model message2Model = (Message2Model) obj;
                chatMessageList.add(message2Model);

                CThreadPoolExecutor.runOnMainThread(new Runnable() {

                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        rvChatList.scrollToPosition(mAdapter.getItemCount() - 1);
                    }
                });
                break;
            }
            case Events.REPORT_CHAT_MESSAGE_STATUS: {
                final Message2Model message2Model = (Message2Model) obj;
                Message2Model message2Model1 = findMessage2Model(chatMessageList, message2Model);
                if (message2Model1 == null) {
                    return;
                }
                message2Model1.setStatusReport(1);
                int pos = chatMessageList.indexOf(message2Model1);
                CThreadPoolExecutor.runOnMainThread(new Runnable() {

                    @Override
                    public void run() {
                        mAdapter.notifyItemChanged(pos);
                    }
                });
                break;
            }
            default:
                break;
        }
    }

    private Message2Model findMessage2Model(List<Message2Model> chatMessageList, Message2Model message2Model) {
        for (Message2Model message : chatMessageList) {
            if (message.getMsgId().equalsIgnoreCase(message2Model.getMsgId())) {
                return message;
            }
        }
        return null;
    }
}

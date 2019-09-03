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
import com.sdt.nepush.db.Conversation2Model;
import com.sdt.nepush.ims.MessageType;
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

    private String toObject;
    private int mConversionType;
    private User2Model currentUser;
    private Conversation2Model mConversation2Model;
    private XRecyclerView rvChatList;
    private Button btnSend;
    private EditText edtMessage;
    private List<Message2Model> chatMessageList;
    private ChatMessageAdapter mAdapter;

    private static final String[] EVENTS = {
            Events.CHAT_SINGLE_MESSAGE,
            Events.REPORT_CHAT_MESSAGE_STATUS,
            Events.REPORT_CHAT_MESSAGE_FAILED_STATUS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        pwDesplay();
        initAndValidExtra();
        CEventCenter.registerEventListener(this, EVENTS);
        initUI();
        initToolBar();
        initChatMessageList();
        initRecyclerList();
        loadConversation();
    }


    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_chat_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(toObject);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setOverflowIcon(getResources().getDrawable(R.drawable.apply_jurassic));

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
    }

    private void pwDesplay() {                 //just for test
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
                .and(Message2Model_Table.toId.eq(toObject)))
                .or(OperatorGroup.clause()
                        .and(Message2Model_Table.fromId.eq(toObject))
                        .and(Message2Model_Table.toId.eq(currentUser.getUserName())));


        chatMessageList = SQLite.select().from(Message2Model.class)
                .where(op)
                .orderBy(OrderBy.fromNameAlias(NameAlias.of("id")).descending())
                .limit(50)
                .queryList();  //查询最后50条聊天记录
        //需要翻转，让数据按id从小往大排序
        Collections.reverse(chatMessageList);
    }

    private void loadConversation() {
        mConversation2Model = Conversation2Model.queryConversion(currentUser.getUserName(), toObject, mConversionType);
    }

    private void initAndValidExtra() {
        currentUser = User2Model.getLoginUser();
        toObject = getIntent().getStringExtra("toObject");
        mConversionType = getIntent().getIntExtra("conversationType", Conversation2Model.Conversation_Type_Single);
        if (currentUser == null || TextUtils.isEmpty(toObject)) {
            finish();
        }
    }

    private void initRecyclerList() {
        rvChatList.setLayoutManager(new LinearLayoutManager(getApplication()));
        mAdapter = new ChatMessageAdapter(this, chatMessageList, currentUser.getUserName(), toObject);
        rvChatList.setAdapter(mAdapter);
    }

    private void sendMessage() {
        SingleMessage message = createAppMessage();
        //消息存db
        Message2Model message2Model = createMessage2Model(message);
        message2Model.save();
        //发送到服务器
        MessageProcessor.getInstance().sendMsg(message);
        //刷新消息列表
        chatMessageList.add(message2Model);
        mAdapter.notifyDataSetChanged();
        //滚动到消息最后一位
        rvChatList.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private SingleMessage createAppMessage() {
        SingleMessage message = new SingleMessage();
        message.setMsgId(UUID.randomUUID().toString());
        message.setMsgType(MessageType.SINGLE_CHAT.getMsgType());
        message.setMsgContentType(MessageType.MessageContentType.TEXT.getMsgContentType());
        message.setFromId(currentUser.getUserName());
        message.setToId(toObject);
        message.setSendTime(System.currentTimeMillis());
        message.setContent(edtMessage.getText().toString());
        return message;
    }

    private Message2Model createMessage2Model(SingleMessage message) {
        Message2Model message2Model = new Message2Model();
        message2Model.setMessageId(message.getMsgId());
        message2Model.setMessageType(message.getMsgType());
        message2Model.setMsgContentType(message.getMsgContentType());
        message2Model.setFromId(message.getFromId());
        message2Model.setToId(message.getToId());
        message2Model.setSendTime(message.getSendTime());
        message2Model.setContent(message.getContent());
        message2Model.setStatusReport(message.getStatusReport());
        message2Model.setExtend(message.getExtend());
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
        if (mConversation2Model == null) {
            mConversation2Model = new Conversation2Model();
            mConversation2Model.setToObject(toObject);
            mConversation2Model.setCreateUser(currentUser.getUserName());
            mConversation2Model.setConversationType(mConversionType);
            mConversation2Model.save();
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
            case Events.REPORT_CHAT_MESSAGE_FAILED_STATUS: {
                final Message2Model message2Model = (Message2Model) obj;
                Message2Model message2Model1 = findMessage2Model(chatMessageList, message2Model);
                if (message2Model1 == null) {
                    return;
                }
                int pos = chatMessageList.indexOf(message2Model1);
                message2Model1.setStatusReport(-1);     //失败
                CThreadPoolExecutor.runOnMainThread(new Runnable() {

                    @Override
                    public void run() {
                        logger.d("refresh pos:" + pos + ",msg id:" + message2Model1.getMessageId());
                        mAdapter.notifyItemChanged(pos);
                    }
                });
            }
            default:
                break;
        }
    }

    private Message2Model findMessage2Model(List<Message2Model> chatMessageList, Message2Model message2Model) {
        for (Message2Model message : chatMessageList) {
            if (message.getMessageId().equalsIgnoreCase(message2Model.getMessageId())) {
                return message;
            }
        }
        return null;
    }
}

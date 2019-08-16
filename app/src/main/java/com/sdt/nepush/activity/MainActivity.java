package com.sdt.nepush.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sdt.libchat.core.ImsClient;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.ImsManager;
import com.sdt.nepush.MessageType;
import com.sdt.nepush.R;
import com.sdt.nepush.db.Message2Model;
import com.sdt.nepush.db.User2Model;
import com.sdt.nepush.db.User2Model_Table;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.event.I_CEventListener;
import com.sdt.nepush.msg.SingleMessage;
import com.sdt.nepush.processor.MessageProcessor;
import com.sdt.nepush.util.CThreadPoolExecutor;

import java.lang.reflect.Method;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, I_CEventListener {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    private EditText edtSendText;
    private Button btnHandShark;
    private Button btnSend;
    private Button btnJump;

    private TextView tvReceiveMsg;

    private String userId;
    private String toUserId;
    private String token;

    private ProgressDialog dialog;

    String hostJson = "[{\"host\":\"192.168.66.72\", \"port\":8866}]";


    private static final String[] EVENTS = {
            Events.SYS_PUSH_MESSAGE,
            Events.CHAT_SINGLE_MESSAGE,
            Events.LIST_FRIEND_MESSAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtSendText = (EditText) findViewById(R.id.txt_send_text);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);

        btnHandShark = (Button) findViewById(R.id.btn_handshark);
        btnHandShark.setOnClickListener(this);

        btnJump = (Button) findViewById(R.id.btn_jump);
        btnJump.setOnClickListener(this);

        tvReceiveMsg = (TextView) findViewById(R.id.tv_receive_msg);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        logger.d("w" + dm.widthPixels);
        logger.d("h" + dm.heightPixels);
        logger.d("dpi" + dm.densityDpi);
        logger.d("density" + dm.density);
        logger.d("xdpi" + dm.xdpi);
        logger.d("ydpi" + dm.ydpi);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        toolbar.setTitle("畅叙");
        toolbar.setPopupTheme(R.style.popup_theme);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });


        CEventCenter.registerEventListener(this, EVENTS);


        initUser();
    }


    private void initUser() {
        User2Model user2Model = SQLite.select().from(User2Model.class)
                .where(User2Model_Table.timeStamp.lessThanOrEq(System.currentTimeMillis()))
                .orderBy(OrderBy.fromNameAlias(NameAlias.of("id")))
                .groupBy(NameAlias.of("id"))
                .querySingle();

        if (user2Model == null) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        logger.e("id=" + user2Model.getId() + ",name=" + user2Model.getUserName() + ",pwd=" + user2Model.getPassword());
        userId = user2Model.getUserName();
        token = user2Model.getToken();

        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(token)) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (userId == null || token == null) {
            initUser();
        }
        if (id == R.id.btn_handshark) {
            dialog = ProgressDialog.show(this, "提示", "正在登陆中");
            dialog.setCanceledOnTouchOutside(true);
            ImsManager.getInstance().init(userId, token, hostJson, ImsClient.APP_FORGROUND);
        } else if (id == R.id.btn_send) {
            sendMessage();
        } else if (id == R.id.btn_jump) {
            startActivity(new Intent(this, HomeActivity.class));
        }
    }

    private void sendMessage() {
        SingleMessage message = new SingleMessage();
        message.setMsgId(UUID.randomUUID().toString());
        message.setMsgType(MessageType.SINGLE_CHAT.getMsgType());
        message.setMsgContentType(MessageType.MessageContentType.TEXT.getMsgContentType());
        message.setFromId(userId);
        message.setToId(toUserId);
        message.setTimestamp(System.currentTimeMillis());
        message.setContent(edtSendText.getText().toString());

        MessageProcessor.getInstance().sendMsg(message);
    }

    @Override
    public void onCEvent(String topic, int msgCode, int resultCode, Object obj) {
        switch (topic) {
            case Events.CHAT_SINGLE_MESSAGE: {
                final Message2Model message = (Message2Model) obj;
                CThreadPoolExecutor.runOnMainThread(new Runnable() {

                    @Override
                    public void run() {
                        tvReceiveMsg.append("from " + message.getFromId() + " " + message.getContent() + "\n");
                    }
                });
                break;
            }
            case Events.SYS_PUSH_MESSAGE: {
                final SingleMessage message = (SingleMessage) obj;
                CThreadPoolExecutor.runOnMainThread(new Runnable() {

                    @Override
                    public void run() {
                        tvReceiveMsg.append("from pushServer:" + message.getContent() + "\n");
                    }
                });
                break;
            }
            case Events.LIST_FRIEND_MESSAGE: {

                CThreadPoolExecutor.runOnMainThread(new Runnable() {

                    @Override
                    public void run() {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CEventCenter.unregisterEventListener(this, EVENTS);
    }
}

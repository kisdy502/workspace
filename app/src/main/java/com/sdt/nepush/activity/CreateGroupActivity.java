package com.sdt.nepush.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.R;
import com.sdt.nepush.db.User2Model;
import com.sdt.nepush.ims.MessageType;
import com.sdt.nepush.msg.AppMessage;
import com.sdt.nepush.processor.MessageBuilder;
import com.sdt.nepush.processor.MessageProcessor;

import java.util.UUID;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    private Toolbar mToolbar;
    private View mContentLayout;
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initToolbar();

        mContentLayout = findViewById(R.id.create_content_layout);
        btnCreate = findViewById(R.id.btn_create_group_chat);
        btnCreate.setOnClickListener(this);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.add_friend_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("创建群组");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
    }


    @Override
    public void onClick(View view) {
        sendCreateGroupMessage();
    }

    private void sendCreateGroupMessage() {
        AppMessage appMessage = MessageBuilder.buildAppMessage(UUID.randomUUID().toString(),
                MessageType.MESSAGE_REQUEST_CREATE_GROUP.getMsgType(),
                0, User2Model.getLoginUser().getUserId(),
                User2Model.getLoginUser().getUserId(), "", "");
        MessageProcessor.getInstance().sendMsg(appMessage);
    }
}

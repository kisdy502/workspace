package com.sdt.nepush.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.R;
import com.sdt.nepush.db.User2Model;
import com.sdt.nepush.ims.MessageType;
import com.sdt.nepush.msg.AppMessage;
import com.sdt.nepush.processor.MessageBuilder;
import com.sdt.nepush.processor.MessageProcessor;

import java.util.UUID;

public class HandleAddFriendActivity extends AppCompatActivity implements View.OnClickListener {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    private Toolbar mToolbar;
    private User2Model mCurrentUser;
    private Long requestUserId;
    private String name;
    private String mobile;
    private String tipMessage;

    private TextView tvTitle;
    private TextView tvRequestPhone;
    private TextView tvTipMessage;
    private Button btnArgee;
    private Button btnRefuse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_add_friend);
        initToolbar();
        requestUserId = getIntent().getLongExtra("id", 0L);
        name = getIntent().getStringExtra("name");
        mobile = getIntent().getStringExtra("mobile");
        tipMessage = getIntent().getStringExtra("tip");

        tvTitle = findViewById(R.id.tv_add_request);
        tvTipMessage = findViewById(R.id.tv_add_tip);
        btnArgee = findViewById(R.id.btn_agree);
        btnRefuse = findViewById(R.id.btn_refuse);
        tvRequestPhone = findViewById(R.id.tv_request_user_info);

        btnArgee.setOnClickListener(this);
        btnRefuse.setOnClickListener(this);
        mCurrentUser = User2Model.getLoginUser();
        tvTitle.setText(name + "请求添加你为好友");
        tvRequestPhone.setText("手机:" + mobile);
        tvTipMessage.setText(tipMessage);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.add_friend_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("处理添加好友");
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
        //通过或者拒绝验证
        if (mCurrentUser == null) {
            return;
        }
        boolean result = false;
        if (view.getId() == R.id.btn_agree) {
            result = true;
        } else if (view.getId() == R.id.btn_refuse) {
            result = false;
        }
        sendHandleAddFreindMessage(mCurrentUser, result);
    }

    //content true 同意 false 不同意
    private void sendHandleAddFreindMessage(User2Model currentUser, boolean result) {
        AppMessage appMessage =
                MessageBuilder.buildAppMessage(
                        UUID.randomUUID().toString(),
                        MessageType.MESSAGE_AGREE_OR_REFUSE_ADD_FRIEND.getMsgType(),
                        0,
                        currentUser.getUserId(),
                        requestUserId,
                        "",
                        result ? "true" : "false");
        MessageProcessor.getInstance().sendMsg(appMessage);
    }
}

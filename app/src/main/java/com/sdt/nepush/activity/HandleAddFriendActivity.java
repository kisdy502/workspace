package com.sdt.nepush.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.R;
import com.sdt.nepush.bean.UserBean;
import com.sdt.nepush.db.User2Model;
import com.sdt.nepush.ims.ImsManager;
import com.sdt.nepush.ims.MessageType;
import com.sdt.nepush.msg.AppMessage;
import com.sdt.nepush.processor.MessageBuilder;

import org.w3c.dom.Text;

import java.util.UUID;

public class HandleAddFriendActivity extends AppCompatActivity implements View.OnClickListener {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    private Toolbar mToolbar;
    private User2Model mCurrentUser;
    private String requestUserId;
    private String tipMessage;

    private TextView tvTitle;
    private TextView tvTipMessage;
    private Button btnArgee;
    private Button btnRefuse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_add_friend);
        initToolbar();
        requestUserId = getIntent().getStringExtra("fromId");
        tipMessage = getIntent().getStringExtra("tipMessage");

        tvTitle = findViewById(R.id.tv_add_request);
        tvTipMessage = findViewById(R.id.tv_add_tip);
        btnArgee = findViewById(R.id.btn_agree);
        btnRefuse = findViewById(R.id.btn_refuse);

        btnArgee.setOnClickListener(this);
        btnRefuse.setOnClickListener(this);
        mCurrentUser = User2Model.getLoginUser();
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
                        User2Model.getLoginUser().getUserName(),
                        requestUserId,
                        currentUser.getUserName(),
                        result ? "true" : "false");
        TransMessageProtobuf.TransMessage transMessage =
                MessageBuilder.getProtoBufMessageBuilderByAppMessage(appMessage).build();
        ImsManager.getInstance().sendMessage(transMessage, true);
    }
}

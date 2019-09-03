package com.sdt.nepush.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.sdt.libchat.core.ImsClient;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.App;
import com.sdt.nepush.ims.ImsManager;
import com.sdt.nepush.R;
import com.sdt.nepush.event.CEventCenter;
import com.sdt.nepush.event.Events;
import com.sdt.nepush.event.I_CEventListener;
import com.sdt.nepush.fragment.ContactFragment;
import com.sdt.nepush.fragment.ConversationFragment;
import com.sdt.nepush.fragment.MineFragment;
import com.sdt.nepush.msg.SingleMessage;
import com.sdt.nepush.net.ApiConstant;
import com.sdt.nepush.util.CThreadPoolExecutor;
import com.sdt.nepush.util.NotificationsUtils;
import com.sdt.nepush.widget.ImageTextView;

import java.lang.reflect.Method;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, I_CEventListener {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    private ImageTextView mBtnConversation;
    private ImageTextView mBtnContact;
    private ImageTextView mMineBtn;

    private ContactFragment mContactFragment;
    private ConversationFragment mConversationFragment;
    private MineFragment mMineFragment;
    private Fragment mCurrentFragment;
    private FragmentManager mFragmentManager;
    private Toolbar mToolbar;
    private ProgressDialog dialog;
    private String userId, token;

    private static final String[] EVENTS = {
            Events.HANDSHAKE_MESSAGE,
            Events.SYS_PUSH_MESSAGE,
            Events.CHAT_SINGLE_MESSAGE,
            Events.FORCE_LOGOUT_MESSAGE,
            Events.LIST_FRIEND_MESSAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_home);
        initUI();
        initToolBar();
        CEventCenter.registerEventListener(this, EVENTS);
        initExtra();
        handshake();

        NotificationsUtils.checkNotificationOpend(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        logger.d("重新开始");
        initExtra();
        handshake();
    }

    private void initExtra() {
        userId = getIntent().getStringExtra("userId");
        token = getIntent().getStringExtra("token");
    }

    private void handshake() {
        if (ImsManager.getInstance().isInited()) {
            return;
        }
        dialog = ProgressDialog.show(this, "提示", "连接中...");
        dialog.setCanceledOnTouchOutside(true);
        ImsManager.getInstance().init(userId, token, ApiConstant.HOSTJSON, ImsClient.APP_FORGROUND);
    }

    private void initUI() {
        mBtnConversation = (ImageTextView) findViewById(R.id.btn_conversation);
        mBtnContact = (ImageTextView) findViewById(R.id.btn_contact);
        mMineBtn = (ImageTextView) findViewById(R.id.mine_btn);

        mBtnConversation.setSelected(true);
        mBtnConversation.setOnClickListener(this);
        mBtnContact.setOnClickListener(this);
        mMineBtn.setOnClickListener(this);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        mConversationFragment = ConversationFragment.newInstance();
        transaction.add(R.id.content_layout, mConversationFragment);
        transaction.commit();

        mCurrentFragment = mContactFragment;
    }


    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mToolbar.setOverflowIcon(getResources().getDrawable(R.drawable.apply_jurassic));

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_item1) {

                } else if (item.getItemId() == R.id.action_item2) {
                    startActivity(new Intent(HomeActivity.this, AddFriendActivity.class));
                }
                return true;
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        CEventCenter.unregisterEventListener(this, EVENTS);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_conversation:
            case R.id.mine_btn:
            case R.id.btn_contact:
                onBottomMenuClick(id);
                break;
            default:
                break;
        }
    }

    private void onBottomMenuClick(int id) {
        clearMenuState();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        hiddenFragment(transaction);
        switch (id) {
            case R.id.btn_conversation:
                mBtnConversation.setSelected(true);
                mToolbar.setTitle("Conversation");
                if (mConversationFragment == null) {
                    mConversationFragment = ConversationFragment.newInstance();
                    transaction.add(R.id.content_layout, mConversationFragment);
                } else {
                    transaction.show(mConversationFragment);
                }
                mCurrentFragment = mConversationFragment;
                break;
            case R.id.mine_btn:
                mMineBtn.setSelected(true);
                mToolbar.setTitle("Mine");
                if (mMineFragment == null) {
                    mMineFragment = new MineFragment();
                    transaction.add(R.id.content_layout, mMineFragment);
                } else {
                    transaction.show(mMineFragment);
                }
                mCurrentFragment = mMineFragment;
                break;
            case R.id.btn_contact:
                mToolbar.setTitle("Contact");
                mBtnContact.setSelected(true);
                if (mContactFragment == null) {
                    mContactFragment = new ContactFragment();
                    transaction.add(R.id.content_layout, mContactFragment);
                } else {
                    transaction.show(mContactFragment);
                }
                mCurrentFragment = mContactFragment;
                break;
        }
        transaction.commit();
    }

    private void clearMenuState() {
        mBtnConversation.setSelected(false);
        mBtnContact.setSelected(false);
        mMineBtn.setSelected(false);
    }

    private void hiddenFragment(FragmentTransaction transaction) {
        if (mConversationFragment != null) {
            transaction.hide(mConversationFragment);
        }
        if (mContactFragment != null) {
            transaction.hide(mContactFragment);
        }
        if (mMineFragment != null) {
            transaction.hide(mMineFragment);
        }
    }

    @Override
    public void onCEvent(String topic, int msgCode, int resultCode, Object obj) {
        switch (topic) {
            case Events.HANDSHAKE_MESSAGE: {
                break;
            }
            case Events.SYS_PUSH_MESSAGE: {
                final SingleMessage message = (SingleMessage) obj;
                CThreadPoolExecutor.runOnMainThread(new Runnable() {

                    @Override
                    public void run() {
                        logger.d("from pushServer:" + message.getContent() + "\n");
                    }
                });
                break;
            }
            case Events.LIST_FRIEND_MESSAGE: {

                CThreadPoolExecutor.runOnMainThread(new Runnable() {

                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
                break;
            }
            case Events.FORCE_LOGOUT_MESSAGE: {
                CThreadPoolExecutor.runOnMainThread(new Runnable() {

                    @Override
                    public void run() {
                        new AlertDialog.Builder(HomeActivity.this).setIcon(android.R.drawable.btn_star)
                                .setTitle("通知").setMessage("账号在其它地方登录")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(HomeActivity.this, "确定了", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                        HomeActivity.this.finish();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(HomeActivity.this, "重新登录", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                        HomeActivity.this.finish();
                                    }
                                }).show();
                    }
                });
                break;
            }
        }
    }
}

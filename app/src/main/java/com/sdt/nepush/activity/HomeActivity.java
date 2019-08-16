package com.sdt.nepush.activity;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.sdt.nepush.R;
import com.sdt.nepush.fragment.ContactFragment;
import com.sdt.nepush.fragment.ConversationFragment;
import com.sdt.nepush.fragment.MineFragment;
import com.sdt.nepush.widget.ImageTextView;

import java.lang.reflect.Method;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageTextView mBtnConversation;
    private ImageTextView mBtnContact;
    private ImageTextView mMineBtn;

    private ContactFragment mContactFragment;
    private ConversationFragment mConversationFragment;
    private MineFragment mMineFragment;
    private Fragment mCurrentFragment;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_home);
        initUI();
        initToolBar();
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setPopupTheme(R.style.popup_theme);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                if (mMineFragment == null) {
                    mMineFragment = new MineFragment();
                    transaction.add(R.id.content_layout, mMineFragment);
                } else {
                    transaction.show(mMineFragment);
                }
                mCurrentFragment = mMineFragment;
                break;
            case R.id.btn_contact:
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
}

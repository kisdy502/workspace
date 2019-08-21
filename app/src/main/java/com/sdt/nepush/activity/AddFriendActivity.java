package com.sdt.nepush.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sdt.im.protobuf.TransMessageProtobuf;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.App;
import com.sdt.nepush.ImsManager;
import com.sdt.nepush.MessageType;
import com.sdt.nepush.R;
import com.sdt.nepush.bean.SearchRestResp;
import com.sdt.nepush.bean.UserBean;
import com.sdt.nepush.db.User2Model;
import com.sdt.nepush.msg.AppMessage;
import com.sdt.nepush.net.MedicalRetrofit;
import com.sdt.nepush.processor.MessageBuilder;

import java.util.UUID;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddFriendActivity extends AppCompatActivity implements View.OnClickListener {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    private Toolbar mToolbar;
    private View mSearchLayout;
    private TextView tvSearchedUser;
    private UserBean userBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initToolbar();

        mSearchLayout = findViewById(R.id.search_friend_result_layout);
        tvSearchedUser = (TextView) findViewById(R.id.tv_searched_user);
        mSearchLayout.setOnClickListener(this);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.add_friend_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("添加新朋友");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        mToolbar.setOverflowIcon(getResources().getDrawable(R.drawable.apply_jurassic));

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_addfriend_search, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.ab_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                logger.i(query);
                Toast.makeText(App.getInstance(), query, Toast.LENGTH_SHORT).show();
                searchUser(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            logger.i("back");
            Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void searchUser(String keyword) {
        String token = User2Model.getLoginUser().getToken();
        MedicalRetrofit.getInstance().getMedicalService().search(keyword, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchRestResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(SearchRestResp searchRestResp) {
                        logger.d("SearchRestResp:" + searchRestResp.toString());
                        userBean = searchRestResp.getUserBean();
                        if (userBean != null) {
                            tvSearchedUser.setText(userBean.getName());
                        } else {
                            tvSearchedUser.setText("用户不存在");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.d("e:" + e.getMessage());
                        Toast.makeText(App.getInstance(), "搜索出错" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onClick(View view) {
        //发送朋友验证
        if (userBean == null) {
            return;
        }
        sendAddFreindMessage(userBean);
    }

    private void sendAddFreindMessage(UserBean userBean) {
        AppMessage appMessage =
                MessageBuilder.buildAppMessage(
                        UUID.randomUUID().toString(),
                        MessageType.MESSAGE_REQUEST_ADD_FRIEND.getMsgType(),
                        0,
                        User2Model.getLoginUser().getUserName(),
                        "",
                        userBean.getName(),
                        ""
                );
        TransMessageProtobuf.TransMessage transMessage =
                MessageBuilder.getProtoBufMessageBuilderByAppMessage(appMessage).build();
        ImsManager.getInstance().sendMessage(transMessage, true);
    }
}

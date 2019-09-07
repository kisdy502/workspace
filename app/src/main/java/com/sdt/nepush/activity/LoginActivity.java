package com.sdt.nepush.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sdt.libchat.ssl.SignleSSlClient;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.App;
import com.sdt.nepush.R;
import com.sdt.nepush.bean.LoginRestResp;
import com.sdt.nepush.bean.UserBean;
import com.sdt.nepush.db.User2Model;
import com.sdt.nepush.db.User2Model_Table;
import com.sdt.nepush.net.MedicalRetrofit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ILogger logger = ILoggerFactory.getLogger(getClass());

    private Button btnLogin;
    private Button btnToRegister;
    private EditText edtUserName;
    private EditText edtPassword;
    private User2Model user2Model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnToRegister = (Button) findViewById(R.id.btnToRegister);
        edtUserName = (EditText) findViewById(R.id.edt_name);
        edtPassword = (EditText) findViewById(R.id.edt_pwd);
        btnLogin.setOnClickListener(this);
        btnToRegister.setOnClickListener(this);
        setTitle("Login");
        initData();

        SignleSSlClient signleSSlClient = new SignleSSlClient(App.getInstance());
        signleSSlClient.start();
    }


    private void initData() {
        User2Model user2Model = User2Model.getLoginUser();
        if (user2Model == null) {
            return;
        }
        edtUserName.setText(user2Model.getUserName());
        edtPassword.setText(user2Model.getPassword());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                String userName = edtUserName.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
                    return;
                }
                login(userName, password);
                break;
            case R.id.btnToRegister:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
                break;
        }

    }

    public void login(String userName, String password) {
        MedicalRetrofit.getInstance().getMedicalService().login(userName, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<LoginRestResp, LoginRestResp>() {
                    @Override
                    public LoginRestResp apply(LoginRestResp loginRestResp) throws Exception {
                        if (!loginRestResp.isSuccess()) {
                            return loginRestResp;
                        }
                        UserBean userBean = loginRestResp.getLoginUser();
                        User2Model currentUser = User2Model.getLoginUser();
                        if (currentUser != null) {
                            currentUser.setCurrentUserTag(User2Model.NOT_CURRENT_TAG);
                            currentUser.update();
                        }
                        user2Model = SQLite.select().from(User2Model.class)
                                .where(User2Model_Table.userId.eq(userBean.getId()))
                                .orderBy(OrderBy.fromNameAlias(NameAlias.of("id")))
                                .groupBy(NameAlias.of("id"))
                                .querySingle();
                        if (user2Model == null) {
                            //db被清掉了情况
                            user2Model = new User2Model();
                            user2Model.setUserId(userBean.getId());
                            user2Model.setUserName(userName);
                            user2Model.setPassword(password);
                            user2Model.setToken(userBean.getToken());
                            user2Model.setCurrentUserTag(User2Model.CURRENT_TAG);
                            user2Model.setTimeStamp(System.currentTimeMillis());
                            user2Model.save();
                        } else {
                            user2Model.setToken(userBean.getToken());
                            user2Model.setCurrentUserTag(User2Model.CURRENT_TAG);
                            user2Model.setTimeStamp(System.currentTimeMillis());
                            user2Model.update();
                        }
                        return loginRestResp;
                    }
                })
                .subscribe(new Observer<LoginRestResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LoginRestResp loginRestResp) {
                        logger.d("LoginRestResp:" + loginRestResp.toString());
                        if (loginRestResp.isSuccess()) {
                            UserBean userBean = loginRestResp.getLoginUser();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("userId", user2Model.getUserId());
                            intent.putExtra("token", userBean.getToken());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(App.getInstance(), loginRestResp.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.d("e:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}

package com.sdt.nepush.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.App;
import com.sdt.nepush.R;
import com.sdt.nepush.bean.RegisterResp;
import com.sdt.nepush.bean.UserBean;
import com.sdt.nepush.db.User2Model;
import com.sdt.nepush.net.MedicalRetrofit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    Button btnRegister;
    Button btnToLogin;
    EditText edtUserName;
    EditText edtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnToLogin = (Button) findViewById(R.id.btnToLogin);
        edtUserName = (EditText) findViewById(R.id.edt_name);
        edtPassword = (EditText) findViewById(R.id.edt_pwd);
        btnRegister.setOnClickListener(this);
        btnToLogin.setOnClickListener(this);
        setTitle("Register");
    }

    public void register(String userName, String password) {
        UserBean userBean = new UserBean();
        userBean.setName(userName);
        userBean.setPassword(password);
        userBean.setMobile("13055556666");
        MedicalRetrofit.getInstance().getMedicalService().register(userBean)
                .map(new Function<RegisterResp, RegisterResp>() {
                    @Override
                    public RegisterResp apply(RegisterResp registerResp) throws Exception {
                        User2Model user2Model = new User2Model();
                        user2Model.setUserId(registerResp.getData().getId());
                        user2Model.setUserName(userName);
                        user2Model.setPassword(password);
                        user2Model.setTimeStamp(System.currentTimeMillis());
                        user2Model.save();
                        return registerResp;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RegisterResp>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(RegisterResp registerResp) {
                        logger.d("registerResp:" + registerResp.toString());
                        if (registerResp.isSuccess()) {
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(App.getInstance(), registerResp.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.d("e:" + e.getMessage());
                        Toast.makeText(App.getInstance(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                String userName = edtUserName.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
                    return;
                }
                register(userName, password);
                break;
            case R.id.btnToLogin:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }

    }
}

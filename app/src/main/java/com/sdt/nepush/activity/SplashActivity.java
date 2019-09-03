package com.sdt.nepush.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;
import com.sdt.nepush.R;
import com.sdt.nepush.db.User2Model;
import com.sdt.nepush.db.User2Model_Table;

public class SplashActivity extends AppCompatActivity {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
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
            finish();
            return;
        }
        logger.e("id=" + user2Model.getId() + ",name=" + user2Model.getUserName() + ",pwd=" + user2Model.getPassword());
        String userId = user2Model.getUserName();
        String token = user2Model.getToken();

        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(token)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("token", token);
            startActivity(intent);
        }
        finish();
    }
}

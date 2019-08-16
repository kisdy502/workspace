package com.sdt.nepush.db;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;
import com.sdt.libcommon.esc.ILogger;
import com.sdt.libcommon.esc.ILoggerFactory;

import java.util.List;

/**
 * Created by sdt13411 on 2019/7/22.
 */

public class DbTest {

    ILogger logger = ILoggerFactory.getLogger(getClass());

    //增删改
    public void testInsertUser() {
        User2Model userModel = new User2Model();
        userModel.setUserName("TestDb");
        userModel.setPassword("test1234");
        userModel.setToken("token_TestDb");
        userModel.setTimeStamp(System.currentTimeMillis());
        userModel.save();
        userModel.update();
        userModel.delete();
    }

    // 查询
    public void queryUserList() {
        List<User2Model> user2Models = SQLite.select().from(User2Model.class)
                .where(User2Model_Table.token.eq("TestDb"),
                        User2Model_Table.userName.eq("test1234")
                        , User2Model_Table.timeStamp.lessThanOrEq(System.currentTimeMillis()))
                .orderBy(OrderBy.fromNameAlias(NameAlias.of("id")))
                .groupBy(NameAlias.of("id"))
                .queryList();
        if (user2Models.size() != 0) {
            for (User2Model user2Model : user2Models) {
                logger.e("id=" + user2Model.getId() + ",name=" + user2Model.getUserName() + ",pwd=" + user2Model.getPassword());
            }
        } else {
            logger.e("no data queryed!");
        }

    }


    //事务
    public void runTransaction() {
        FlowManager.getDatabase(NetPush.class).beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                for (int i = 0; i < 100; i++) {
                    User2Model userModel = new User2Model();
                    userModel.setUserName("UserModel" + i);
                    userModel.setPassword("Test123" + i);
                    userModel.save(databaseWrapper);
                }
            }
        }).success(new Transaction.Success() {
            @Override
            public void onSuccess(@NonNull Transaction transaction) {
                logger.e("onSuccess()");
            }
        }).error(new Transaction.Error() {
            @Override
            public void onError(@NonNull Transaction transaction, @NonNull Throwable error) {
                logger.e("onError()");
            }
        }).build().execute();

    }
}

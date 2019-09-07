package com.sdt.nepush.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by sdt13411 on 2019/7/22.
 */


@Table(database = NetPush.class)
public class User2Model extends BaseModel {

    public final static int CURRENT_TAG = 1;
    public final static int NOT_CURRENT_TAG = 0;

    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private Long userId;
    @Column
    private String userName;
    @Column
    private String password;
    @Column
    private String token;
    @Column
    private long timeStamp;
    @Column
    private int currentUserTag = NOT_CURRENT_TAG; //

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getCurrentUserTag() {
        return currentUserTag;
    }

    public void setCurrentUserTag(int currentUserTag) {
        this.currentUserTag = currentUserTag;
    }

    public static User2Model getLoginUser() {
        return SQLite.select().from(User2Model.class)
                .where(User2Model_Table.timeStamp.lessThanOrEq(System.currentTimeMillis()),
                        User2Model_Table.currentUserTag.eq(CURRENT_TAG))
                .orderBy(OrderBy.fromNameAlias(NameAlias.of("id")))
                .groupBy(NameAlias.of("id"))
                .querySingle();
    }
}

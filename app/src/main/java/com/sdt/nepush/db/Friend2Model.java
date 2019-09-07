package com.sdt.nepush.db;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by sdt13411 on 2019/7/22.
 */


@Table(database = NetPush.class)
public class Friend2Model extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private Long myId;          //我的id 考虑客户端多用户登录场景
    @Column
    private Long friendId;        //好友的id
    @Column
    private String userName;
    @Column
    private long timeStamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getMyId() {
        return myId;
    }

    public void setMyId(Long myId) {
        this.myId = myId;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public static Friend2Model findFriendByFriendId(Long friendId) {
        return SQLite.select().from(Friend2Model.class)
                .where(Friend2Model_Table.friendId.eq(friendId)).querySingle();
    }

    public static Friend2Model findFriendByMyIdAndFriendId(Long myId, Long friendId) {
        return SQLite.select().from(Friend2Model.class)
                .where(Friend2Model_Table.myId.eq(myId),
                        Friend2Model_Table.friendId.eq(friendId)).querySingle();
    }
}

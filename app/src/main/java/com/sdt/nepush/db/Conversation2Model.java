package com.sdt.nepush.db;

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by sdt13411 on 2019/7/22.
 */
@Table(database = NetPush.class)
public class Conversation2Model extends BaseModel {

    public final static int Conversation_Type_Single = 0;
    public final static int Conversation_Type_Group = 1;
    public final static int Conversation_Type_Push = 2;

    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private int conversationType;
    @NotNull
    @Column
    private String createUser;
    @Column
    private String toObject;        //单聊，群聊，推送消息 会话

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConversationType() {
        return conversationType;
    }

    public void setConversationType(int conversationType) {
        this.conversationType = conversationType;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getToObject() {
        return toObject;
    }

    public void setToObject(String toObject) {
        this.toObject = toObject;
    }

    @Nullable
    public static Conversation2Model queryConversion(String createUser, String toObject, int conversationType) {
        return SQLite.select().from(Conversation2Model.class).where(
                Conversation2Model_Table.createUser.eq(createUser),
                Conversation2Model_Table.toObject.eq(toObject),
                Conversation2Model_Table.conversationType.eq(conversationType)
        ).querySingle();
    }
}

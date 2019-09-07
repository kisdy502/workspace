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
    private Long toObjectId;            //单聊，群聊，推送消息 会话
    @Column
    private String lastMessageContent;
    @Column
    private String unreadMessageCount;

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

    public Long getToObjectId() {
        return toObjectId;
    }

    public void setToObjectId(Long toObject) {
        this.toObjectId = toObject;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

    public String getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(String unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    @Nullable
    public static Conversation2Model queryConversion(String createUser, Long toObjectId, int conversationType) {
        return SQLite.select().from(Conversation2Model.class).where(
                Conversation2Model_Table.createUser.eq(createUser),
                Conversation2Model_Table.toObjectId.eq(toObjectId),
                Conversation2Model_Table.conversationType.eq(conversationType)
        ).querySingle();
    }
}

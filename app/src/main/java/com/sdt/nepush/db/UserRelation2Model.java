/**
 * Copyright (C), 2015-2019, 锋芒科技有限公司
 * FileName: UserRelation2Model
 * Author: sdt13411
 * Date: 2019/8/13 15:01
 * Description: 好友表
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.sdt.nepush.db;
/**
 * @ClassName: UserRelation2Model
 * @Description: class is do 
 * @Author: sdt13411
 * @Date: 2019/8/13 15:01
 */

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * <p>@author:          ${Author}</p>
 * <p>@date:            ${Date}</p>
 * <p>@email:           ${Email}</p>
 * <b>
 * <p>@Description:     ${Description}</p>
 * </b>
 */
@Table(database = NetPush.class)
public class UserRelation2Model extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private int relationId;
    @Column
    private String myName;
    @Column
    private String friendName;
    @Column
    private long timeStamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
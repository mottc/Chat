package com.mottc.chat.data.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/5/15
 * Time: 17:25
 */
@Entity
public class ChatInviteMessage {

    //不能用int
    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String from;
    //时间
    private long time;
    //添加理由
    private String reason;

    //未验证，已同意等状态
    private int status;
    //群id
    private String groupId;
    //群名称
    private String groupName;
    @Generated(hash = 52343673)
    public ChatInviteMessage(Long id, String from, long time, String reason,
            int status, String groupId, String groupName) {
        this.id = id;
        this.from = from;
        this.time = time;
        this.reason = reason;
        this.status = status;
        this.groupId = groupId;
        this.groupName = groupName;
    }
    @Generated(hash = 662545613)
    public ChatInviteMessage() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFrom() {
        return this.from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public long getTime() {
        return this.time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public String getReason() {
        return this.reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getGroupName() {
        return this.groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}

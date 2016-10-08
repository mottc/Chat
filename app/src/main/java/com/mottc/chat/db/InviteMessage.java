package com.mottc.chat.db;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/10/8
 * Time: 14:34
 */
public class InviteMessage {

    private String from;
    //时间
    private long time;
    //添加理由
    private String reason;

    //未验证，已同意等状态
    private InviteMessageStatus status;
    //群id
    private String groupId;
    //群名称
    private String groupName;
    //群邀请者
    private String groupInviter;


    private int id;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public InviteMessageStatus getStatus() {
        return status;
    }

    public void setStatus(InviteMessageStatus status) {
        this.status = status;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupInviter(String inviter) {
        groupInviter = inviter;
    }

    public String getGroupInviter() {
        return groupInviter;
    }



    public enum InviteMessageStatus{

        //==好友
        /**被邀请*/
        BEINVITEED,
        /**被拒绝*/
        BEREFUSED,
        /**对方同意*/
        BEAGREED,

        //==群组
        /**对方申请进入群*/
        BEAPPLYED,
        /**我同意了对方的请求*/
        AGREED,
        /**我拒绝了对方的请求*/
        REFUSED,

        //==群邀请
        /**收到对方的群邀请**/
        GROUPINVITATION,
        /**收到对方同意群邀请的通知**/
        GROUPINVITATION_ACCEPTED,
        /**收到对方拒绝群邀请的通知**/
        GROUPINVITATION_DECLINED
    }
}

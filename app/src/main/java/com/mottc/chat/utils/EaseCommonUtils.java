package com.mottc.chat.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.EMLog;
import com.mottc.chat.Constant;
import com.mottc.chat.R;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/10/8
 * Time: 14:53
 */
public class EaseCommonUtils {

    private static final String TAG = "CommonUtils";

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }

        return false;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }

    public static EMMessage createExpressionMessage(String toChatUsername, String expressioName, String identityCode) {
        EMMessage message = EMMessage.createTxtSendMessage("[" + expressioName + "]", toChatUsername);
        if (identityCode != null) {
            message.setAttribute(Constant.MESSAGE_ATTR_EXPRESSION_ID, identityCode);
        }
        message.setAttribute(Constant.MESSAGE_ATTR_IS_BIG_EXPRESSION, true);
        return message;
    }

    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION: // 位置消息
                if (message.direct() == EMMessage.Direct.RECEIVE) {
                    digest = getString(context, R.string.location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;
                } else {
                    digest = getString(context, R.string.location_prefix);
                }
                break;
            case IMAGE: // 图片消息
                digest = getString(context, R.string.picture);
                break;
            case VOICE:// 语音消息
                digest = getString(context, R.string.voice_prefix);
                break;
            case VIDEO: // 视频消息
                digest = getString(context, R.string.video);
                break;
            case TXT: // 文本消息
                EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    digest = getString(context, R.string.voice_call) + txtBody.getMessage();
                } else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    digest = getString(context, R.string.video_call) + txtBody.getMessage();
                } else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    if (!TextUtils.isEmpty(txtBody.getMessage())) {
                        digest = txtBody.getMessage();
                    } else {
                        digest = getString(context, R.string.dynamic_expression);
                    }
                } else {
                    digest = txtBody.getMessage();
                }
                break;
            case FILE: // 普通文件消息
                digest = getString(context, R.string.file);
                break;
            default:
                EMLog.e(TAG, "error, unknow type");
                return "";
        }

        return digest;
    }

    static String getString(Context context, int resId) {
        return context.getResources().getString(resId);
    }



    /**
     * 将应用的会话类型转化为SDK的会话类型
     *
     * @param chatType
     * @return
     */
    public static EMConversation.EMConversationType getConversationType(int chatType) {
        if (chatType == Constant.CHATTYPE_SINGLE) {
            return EMConversation.EMConversationType.Chat;
        } else if (chatType == Constant.CHATTYPE_GROUP) {
            return EMConversation.EMConversationType.GroupChat;
        } else {
            return EMConversation.EMConversationType.ChatRoom;
        }
    }
}

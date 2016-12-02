package com.mottc.chat.Activity.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.util.EMLog;
import com.mottc.chat.R;

import java.io.File;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/30
 * Time: 17:15
 */
public class ChatRowVoicePlayClickListener implements View.OnClickListener {
    private static final String TAG = "VoicePlayClickListener";
    EMMessage message;
    EMVoiceMessageBody voiceBody;
    ImageView voiceIconView;

    private AnimationDrawable voiceAnimation = null;
    MediaPlayer mediaPlayer = null;
    ImageView iv_read_status;
    Activity activity;
    private EMMessage.ChatType chatType;

    public static boolean isPlaying = false;
    public static ChatRowVoicePlayClickListener currentPlayListener = null;
    public static String playMsgId;

    public ChatRowVoicePlayClickListener(EMMessage message, ImageView v, ImageView iv_read_status,  Activity context) {
        this.message = message;
        voiceBody = (EMVoiceMessageBody) message.getBody();
        this.iv_read_status = iv_read_status;
        voiceIconView = v;
        this.activity = context;
        this.chatType = message.getChatType();
    }

    public void stopPlayVoice() {
        voiceAnimation.stop();
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            voiceIconView.setImageResource(R.drawable.chatfrom_voice_playing);
        } else {
            voiceIconView.setImageResource(R.drawable.chatto_voice_playing);
        }
        // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;
        playMsgId = null;

    }

    public void playVoice(String filePath) {
        if (!(new File(filePath).exists())) {
            return;
        }
        playMsgId = message.getMsgId();
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

        mediaPlayer = new MediaPlayer();
//        if (EaseUI.getInstance().getSettingsProvider().isSpeakerOpened()) {
//            audioManager.setMode(AudioManager.MODE_NORMAL);
//            audioManager.setSpeakerphoneOn(true);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
//        } else {
            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
//        }
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mediaPlayer.release();
                    mediaPlayer = null;
                    stopPlayVoice(); // stop animation
                }

            });
            isPlaying = true;
            currentPlayListener = this;
            mediaPlayer.start();
            showAnimation();

            // 如果是接收的消息
            if (message.direct() == EMMessage.Direct.RECEIVE) {
                if (!message.isAcked() && chatType == EMMessage.ChatType.Chat) {
                    // 告知对方已读这条消息
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                }
                if (!message.isListened() && iv_read_status != null && iv_read_status.getVisibility() == View.VISIBLE) {
                    // 隐藏自己未播放这条语音消息的标志
                    iv_read_status.setVisibility(View.INVISIBLE);
                    message.setListened(true);
                    EMClient.getInstance().chatManager().setMessageListened(message);
                }

            }

        } catch (Exception e) {
            System.out.println();
        }
    }

    // show the voice playing animation
    private void showAnimation() {
        // play voice, and start animation
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            voiceIconView.setImageResource(R.drawable.voice_from_icon);
        } else {
            voiceIconView.setImageResource(R.drawable.voice_to_icon);
        }
        voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
        voiceAnimation.start();
    }

    @Override
    public void onClick(View v) {
        String st = activity.getResources().getString(R.string.Is_download_voice_click_later);
        if (isPlaying) {
            if (playMsgId != null && playMsgId.equals(message.getMsgId())) {
                currentPlayListener.stopPlayVoice();
                return;
            }
            currentPlayListener.stopPlayVoice();
        }

        if (message.direct() == EMMessage.Direct.SEND) {
            // for sent msg, we will try to play the voice file directly
            playVoice(voiceBody.getLocalUrl());
        } else {
            if (message.status() == EMMessage.Status.SUCCESS) {
                File file = new File(voiceBody.getLocalUrl());
                if (file.exists() && file.isFile())
                    playVoice(voiceBody.getLocalUrl());
                else
                    EMLog.e(TAG, "file not exist");

            } else if (message.status() == EMMessage.Status.INPROGRESS) {
                Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
            } else if (message.status() == EMMessage.Status.FAIL) {
                Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        EMClient.getInstance().chatManager().downloadAttachment(message);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);

                    }

                }.execute();

            }

        }
    }
}

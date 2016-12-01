package com.mottc.chat.Activity.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.mottc.chat.Activity.UserDetailActivity;
import com.mottc.chat.Model.ImageCache;
import com.mottc.chat.R;
import com.mottc.chat.utils.EaseCommonUtils;
import com.mottc.chat.utils.ImageUtils;
import com.mottc.chat.utils.PersonAvatarUtils;

import java.io.File;
import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/11/30
 * Time: 12:12
 */

public class MessageAdapter extends BaseAdapter {
    private List<EMMessage> msgs;
    private Context context;
    private LayoutInflater inflater;
    private String tousername;
    protected EMCallBack messageReceiveCallback;

    public MessageAdapter(List<EMMessage> msgs, String toUserName, Context context_) {
        this.msgs = msgs;
        this.context = context_;
        inflater = LayoutInflater.from(context);
        this.tousername = toUserName;
    }

    public void setMsgs(List<EMMessage> msgs) {
        this.msgs = msgs;
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public EMMessage getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
//        message.direct() == EMMessage.Direct.RECEIVE ? 0 : 1;

        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (message.getType().equals(EMMessage.Type.TXT)) {
                return 1;
            } else if (message.getType().equals(EMMessage.Type.IMAGE)) {
                return 2;
            } else {
                return 3;
            }

        } else {
            if (message.getType().equals(EMMessage.Type.TXT)) {
                return 4;
            } else if (message.getType().equals(EMMessage.Type.IMAGE)) {
                return 5;
            } else {
                return 6;
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolderTxtReceive viewHolderTxtReceive = null;
        ViewHolderTxtSent viewHolderTxtSent = null;
        ViewHolderImageReceive viewHolderImageReceive = null;
        ViewHolderImageSent viewHolderImageSent = null;
        ViewHolderVoiceReceive viewHolderVoiceReceive = null;
        ViewHolderVoiceSent viewHolderVoiceSent = null;


        final EMMessage message = getItem(position);
        final int viewType = getItemViewType(position);
        if (convertView == null) {
            switch (viewType) {
                case 1:
                    viewHolderTxtReceive = new ViewHolderTxtReceive();
                    convertView = inflater.inflate(R.layout.item_message_received, parent, false);
                    viewHolderTxtReceive.toUsername = (TextView) convertView.findViewById(R.id.tv_userid);
                    viewHolderTxtReceive.mImageView = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    viewHolderTxtReceive.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);

                    PersonAvatarUtils.setAvatar(context, message.getFrom(), viewHolderTxtReceive.mImageView);
                    viewHolderTxtReceive.toUsername.setText(message.getFrom());
                    viewHolderTxtReceive.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        }
                    });


                    EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                    viewHolderTxtReceive.tv.setText(txtBody.getMessage());

                    convertView.setTag(viewHolderTxtReceive);
                    break;
                case 2:
                    viewHolderImageReceive = new ViewHolderImageReceive();
                    convertView = inflater.inflate(R.layout.received_picture, parent, false);
                    viewHolderImageReceive.toUsername = (TextView) convertView.findViewById(R.id.tv_userid);
                    viewHolderImageReceive.mImageView = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    viewHolderImageReceive.mPic = (ImageView) convertView.findViewById(R.id.imagePic);
                    viewHolderImageReceive.mPicPercenttage = (TextView) convertView.findViewById(R.id.percentage);
                    viewHolderImageReceive.mPicProgressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);


                    PersonAvatarUtils.setAvatar(context, message.getFrom(), viewHolderImageReceive.mImageView);
                    viewHolderImageReceive.toUsername.setText(message.getFrom());
                    viewHolderImageReceive.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        }
                    });

                    EMImageMessageBody imgBody = (EMImageMessageBody) message.getBody();
                    // received messages
                    if (message.direct() == EMMessage.Direct.RECEIVE) {
                        if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                                imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                            viewHolderImageReceive.mPic.setImageResource(R.drawable.default_image);
                            setMessageReceiveCallback(message);
                        } else {
                            viewHolderImageReceive.mPicProgressBar.setVisibility(View.GONE);
                            viewHolderImageReceive.mPicPercenttage.setVisibility(View.GONE);
                            viewHolderImageReceive.mPic.setImageResource(R.drawable.default_image);
                            String thumbPath = imgBody.thumbnailLocalPath();
                            if (!new File(thumbPath).exists()) {
                                // to make it compatible with thumbnail received in previous version
                                thumbPath = ImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                            }
                            showImageView(thumbPath, viewHolderImageReceive.mPic, imgBody.getLocalUrl(), message);
                        }
                    }

//                    String filePath = imgBody.getLocalUrl();
//                    String thumbPath = ImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
//                    showImageView(thumbPath, viewHolderImageReceive.mPic, filePath, message);

                    convertView.setTag(viewHolderImageReceive);
                    break;
                case 3:
                    viewHolderVoiceReceive = new ViewHolderVoiceReceive();
                    convertView = inflater.inflate(R.layout.received_voice, parent, false);
                    viewHolderVoiceReceive.toUsername = (TextView) convertView.findViewById(R.id.tv_userid);
                    viewHolderVoiceReceive.mImageView = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    viewHolderVoiceReceive.mVoiceImage = (ImageView) convertView.findViewById(R.id.iv_voice);
                    viewHolderVoiceReceive.mVoiceprogressbar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
                    viewHolderVoiceReceive.mVoiceLength = (TextView) convertView.findViewById(R.id.tv_length);
                    viewHolderVoiceReceive.mUnread_voice = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
                    convertView.setTag(viewHolderVoiceReceive);
                    break;
                case 4:
                    viewHolderTxtSent = new ViewHolderTxtSent();
                    convertView = inflater.inflate(R.layout.item_message_sent, parent, false);
//                    viewHolderTxtSent.toUsername = (TextView) convertView.findViewById(R.id.tv_userid);
                    viewHolderTxtSent.mImageView = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    viewHolderTxtSent.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);

                    PersonAvatarUtils.setAvatar(context, message.getFrom(), viewHolderTxtSent.mImageView);
//                    viewHolderTxtSent.toUsername.setText(message.getFrom());
                    viewHolderTxtSent.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        }
                    });


                    EMTextMessageBody txtBodySent = (EMTextMessageBody) message.getBody();
                    viewHolderTxtSent.tv.setText(txtBodySent.getMessage());


                    convertView.setTag(viewHolderTxtSent);
                    break;
                case 5:
                    viewHolderImageSent = new ViewHolderImageSent();
                    convertView = inflater.inflate(R.layout.sent_picture, parent, false);
                    viewHolderImageSent.mImageView = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    viewHolderImageSent.mPic = (ImageView) convertView.findViewById(R.id.imagePic);
                    viewHolderImageSent.mPicPercenttage = (TextView) convertView.findViewById(R.id.percentage);
                    viewHolderImageSent.mPicProgressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);


                    EMImageMessageBody imgBodySent = (EMImageMessageBody) message.getBody();
                    PersonAvatarUtils.setAvatar(context, message.getFrom(), viewHolderImageSent.mImageView);
                    viewHolderImageSent.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        }
                    });

                    String filePath = imgBodySent.getLocalUrl();
                    String thumbPath = ImageUtils.getThumbnailImagePath(imgBodySent.getLocalUrl());
                    showImageView(thumbPath, viewHolderImageSent.mPic, filePath, message);

                    convertView.setTag(viewHolderImageSent);
                    break;
                case 6:
                    viewHolderVoiceSent = new ViewHolderVoiceSent();
                    convertView = inflater.inflate(R.layout.sent_voice, parent, false);
//                    viewHolderVoiceSent.toUsername = (TextView) convertView.findViewById(R.id.tv_userid);
                    viewHolderVoiceSent.mImageView = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    viewHolderVoiceSent.mVoiceImage = (ImageView) convertView.findViewById(R.id.iv_voice);
                    viewHolderVoiceSent.mVoiceprogressbar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
                    viewHolderVoiceSent.mVoiceLength = (TextView) convertView.findViewById(R.id.tv_length);
                    viewHolderVoiceSent.mUnread_voice = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
                    convertView.setTag(viewHolderVoiceSent);
                    break;
            }
        } else {

            switch (viewType) {
                case 1:
                    viewHolderTxtReceive = (ViewHolderTxtReceive) convertView.getTag();
                    PersonAvatarUtils.setAvatar(context, message.getFrom(), viewHolderTxtReceive.mImageView);
                    viewHolderTxtReceive.toUsername.setText(message.getFrom());
                    viewHolderTxtReceive.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        }
                    });
                    EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                    viewHolderTxtReceive.tv.setText(txtBody.getMessage());
                    break;
                case 2:
                    viewHolderImageReceive = (ViewHolderImageReceive) convertView.getTag();

                    PersonAvatarUtils.setAvatar(context, message.getFrom(), viewHolderImageReceive.mImageView);
                    viewHolderImageReceive.toUsername.setText(message.getFrom());
                    viewHolderImageReceive.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        }
                    });

                    EMImageMessageBody imgBody = (EMImageMessageBody) message.getBody();
                    // received messages
                    if (message.direct() == EMMessage.Direct.RECEIVE) {
                        if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                                imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                            viewHolderImageReceive.mPic.setImageResource(R.drawable.default_image);
                            setMessageReceiveCallback(message);
                        } else {
                            viewHolderImageReceive.mPicProgressBar.setVisibility(View.GONE);
                            viewHolderImageReceive.mPicPercenttage.setVisibility(View.GONE);
                            viewHolderImageReceive.mPic.setImageResource(R.drawable.default_image);
                            String thumbPath = imgBody.thumbnailLocalPath();
                            if (!new File(thumbPath).exists()) {
                                // to make it compatible with thumbnail received in previous version
                                thumbPath = ImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                            }
                            showImageView(thumbPath, viewHolderImageReceive.mPic, imgBody.getLocalUrl(), message);
                        }
                    }
                    break;
                case 3:
                    break;
                case 4:

                    PersonAvatarUtils.setAvatar(context, message.getFrom(), viewHolderTxtSent.mImageView);
//                    viewHolderTxtSent.toUsername.setText(message.getFrom());
                    viewHolderTxtSent.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        }
                    });

                    EMTextMessageBody txtBodySent = (EMTextMessageBody) message.getBody();
                    viewHolderTxtSent.tv.setText(txtBodySent.getMessage());
                    break;
                case 5:
                    PersonAvatarUtils.setAvatar(context, message.getFrom(), viewHolderImageSent.mImageView);
                    viewHolderImageSent.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        }
                    });

                    EMImageMessageBody imgBodySent = (EMImageMessageBody) message.getBody();
                    String filePath = imgBodySent.getLocalUrl();
                    String thumbPath = ImageUtils.getThumbnailImagePath(imgBodySent.getLocalUrl());
                    showImageView(thumbPath, viewHolderImageSent.mPic, filePath, message);
                    break;
                case 6:


                    break;

            }
        }



//        ViewHolder holder = (ViewHolder) convertView.getTag();
//        if (holder == null) {
//            holder = new ViewHolder();
//            holder.toUsername = (TextView) convertView.findViewById(R.id.tv_userid);
//            holder.mImageView = (ImageView) convertView.findViewById(R.id.iv_userhead);
//
//            if (message.getType().equals(EMMessage.Type.TXT)) {
//                holder.tv = (TextView) convertView.findViewById(R.id.tv_chatcontent);
//            } else if (message.getType().equals(EMMessage.Type.IMAGE)) {
//                holder.mPic = (ImageView) convertView.findViewById(R.id.imagePic);
//                holder.mPicPercenttage = (TextView) convertView.findViewById(R.id.percentage);
//                holder.mPicProgressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
//            } else {
//                holder.mVoiceImage = (ImageView) convertView.findViewById(R.id.iv_voice);
//                holder.mVoiceprogressbar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
//                holder.mVoiceLength = (TextView) convertView.findViewById(R.id.tv_length);
//                holder.mUnread_voice = (ImageView) convertView.findViewById(R.id.iv_unread_voice);
//            }
//            convertView.setTag(holder);
//            Log.i("MessageAdapter", "getView: " + "holder == null");
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//            Log.i("MessageAdapter", "getView: " + "holder != null");
//
//        }
//
//
////            new AvatarURLDownloadUtils().downLoad(message.getFrom(), context, holder.mImageView, false);
//        PersonAvatarUtils.setAvatar(context, message.getFrom(), holder.mImageView);
//        holder.toUsername.setText(message.getFrom());
//        holder.mImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
//            }
//        });
//
//        if (message.getType().equals(EMMessage.Type.TXT)) {
//            EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
//            holder.tv.setText(txtBody.getMessage());
//        } else if (message.getType().equals(EMMessage.Type.IMAGE)) {
//
//            imgBody = (EMImageMessageBody) message.getBody();
//            // received messages
//            if (message.direct() == EMMessage.Direct.RECEIVE) {
//                if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
//                        imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
//                    holder.mPic.setImageResource(R.drawable.default_image);
//                    setMessageReceiveCallback(message);
//
//                } else {
//                    holder.mPicProgressBar.setVisibility(View.GONE);
//                    holder.mPicPercenttage.setVisibility(View.GONE);
//                    holder.mPic.setImageResource(R.drawable.default_image);
//                    String thumbPath = imgBody.thumbnailLocalPath();
//                    if (!new File(thumbPath).exists()) {
//                        // to make it compatible with thumbnail received in previous version
//                        thumbPath = ImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
//                    }
//                    showImageView(thumbPath, holder.mPic, imgBody.getLocalUrl(), message);
//                }
//            }
//
//            String filePath = imgBody.getLocalUrl();
//            String thumbPath = ImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
//            showImageView(thumbPath, holder.mPic, filePath, message);
//
////            handleSendMessage(message, holder.mPicProgressBar, holder.mPicPercenttage);
//
//        } else {
//
//            EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();
//            int len = voiceBody.getLength();
//            if (len > 0) {
//                holder.mVoiceLength.setText(voiceBody.getLength() + "\"");
//                holder.mVoiceLength.setVisibility(View.VISIBLE);
//            } else {
//                holder.mVoiceLength.setVisibility(View.INVISIBLE);
//            }
//
//
//            if (ChatRowVoicePlayClickListener.playMsgId != null
//                    && ChatRowVoicePlayClickListener.playMsgId.equals(message.getMsgId()) && ChatRowVoicePlayClickListener.isPlaying) {
//                AnimationDrawable voiceAnimation;
//                if (message.direct() == EMMessage.Direct.RECEIVE) {
//                    holder.mVoiceImage.setImageResource(R.drawable.voice_from_icon);
//                } else {
//                    holder.mVoiceImage.setImageResource(R.drawable.voice_to_icon);
//                }
//                voiceAnimation = (AnimationDrawable) holder.mVoiceImage.getDrawable();
//                voiceAnimation.start();
//            } else {
//                if (message.direct() == EMMessage.Direct.RECEIVE) {
//                    holder.mVoiceImage.setImageResource(R.drawable.chatfrom_voice_playing);
//                } else {
//                    holder.mVoiceImage.setImageResource(R.drawable.chatto_voice_playing);
//                }
//            }
//
//            if (message.direct() == EMMessage.Direct.RECEIVE) {
////                if (message.isListened()) {
////                    // hide the unread icon
////                    readStatusView.setVisibility(View.INVISIBLE);
////                } else {
////                    readStatusView.setVisibility(View.VISIBLE);
////                }
//
//                if (voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
//                        voiceBody.downloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
//                    holder.mVoiceprogressbar.setVisibility(View.VISIBLE);
//
//                } else {
//                    holder.mVoiceprogressbar.setVisibility(View.INVISIBLE);
//
//                }
//
//            }
//
//            // until here, handle sending voice message
//            //handleSendMessage();
//
//        }
//

        return convertView;
    }


    protected void setMessageReceiveCallback(EMMessage message) {
        if (messageReceiveCallback == null) {
            messageReceiveCallback = new EMCallBack() {

                @Override
                public void onSuccess() {
//                    updateView();
                    Log.i("MessageAdapter", "onSuccess: " + "");
                }

                @Override
                public void onProgress(final int progress, String status) {
//                    context.runOnUiThread(new Runnable() {
//                        public void run() {
//                            if(percentageView != null){
//                                percentageView.setText(progress + "%");
//                            }
//                        }
//                    });
                    Log.i("MessageAdapter", "onProgress: " + "");
                }

                @Override
                public void onError(int code, String error) {
//                    updateView();
                    Log.i("MessageAdapter", "onError: " + "");
                }
            };
        }
        message.setMessageStatusCallback(messageReceiveCallback);
    }


    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, final EMMessage message) {
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            return true;
        } else {
            new AsyncTask<Object, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Object... args) {
                    File file = new File(thumbernailPath);
                    if (file.exists()) {
                        return ImageUtils.decodeScaleImage(thumbernailPath, 160, 160);
                    } else if (new File(imgBody.thumbnailLocalPath()).exists()) {
                        return ImageUtils.decodeScaleImage(imgBody.thumbnailLocalPath(), 160, 160);
                    } else {
                        if (message.direct() == EMMessage.Direct.SEND) {
                            if (localFullSizePath != null && new File(localFullSizePath).exists()) {
                                return ImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                }

                protected void onPostExecute(Bitmap image) {
                    if (image != null) {
                        iv.setImageBitmap(image);
                        ImageCache.getInstance().put(thumbernailPath, image);
                    } else {
                        if (message.status() == EMMessage.Status.FAIL) {
                            if (EaseCommonUtils.isNetWorkConnected(context)) {
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        EMClient.getInstance().chatManager().downloadThumbnail(message);
                                    }
                                }).start();
                            }
                        }

                    }
                }
            }.execute();

            return true;
        }
    }


    /**
     * handle sending message
     */
    protected void handleSendMessage(EMMessage message, ProgressBar progressBar, TextView percentageView) {

        switch (message.status()) {
            case SUCCESS:
                progressBar.setVisibility(View.INVISIBLE);
                if (percentageView != null)
                    percentageView.setVisibility(View.INVISIBLE);

                break;
            case FAIL:
                progressBar.setVisibility(View.INVISIBLE);
                if (percentageView != null)
                    percentageView.setVisibility(View.INVISIBLE);

                break;
            case INPROGRESS:
                progressBar.setVisibility(View.VISIBLE);
                if (percentageView != null) {
                    percentageView.setVisibility(View.VISIBLE);
                    percentageView.setText(message.progress() + "%");
                }

                break;
            default:
                progressBar.setVisibility(View.INVISIBLE);
                if (percentageView != null)
                    percentageView.setVisibility(View.INVISIBLE);

                break;
        }
    }

    public class ViewHolderTxtReceive {
        TextView tv;
        TextView toUsername;
        ImageView mImageView;
    }

    public class ViewHolderTxtSent {
        TextView tv;
//        TextView toUsername;
        ImageView mImageView;
    }

    public class ViewHolderImageReceive {
        TextView toUsername;
        ImageView mImageView;
        ImageView mPic;
        ProgressBar mPicProgressBar;
        TextView mPicPercenttage;
    }

    public class ViewHolderImageSent {
//        TextView toUsername;
        ImageView mImageView;
        ImageView mPic;
        ProgressBar mPicProgressBar;
        TextView mPicPercenttage;
    }

    public class ViewHolderVoiceReceive {
        TextView toUsername;
        ImageView mImageView;
        TextView mVoiceLength;
        ImageView mUnread_voice;
        ProgressBar mVoiceprogressbar;
        ImageView mVoiceImage;
    }

    public class ViewHolderVoiceSent {
        TextView toUsername;
        ImageView mImageView;
        TextView mVoiceLength;
        ImageView mUnread_voice;
        ProgressBar mVoiceprogressbar;
        ImageView mVoiceImage;
    }


}
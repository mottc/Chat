package com.mottc.chat.Activity.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.mottc.chat.Activity.ShowImageActivity;
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
    public int getViewTypeCount() {
        return 6;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final EMMessage message = msgs.get(position);
        int viewType = getItemViewType(position);

        ViewHolderTxtReceive viewHolderTxtReceive = null;
        ViewHolderTxtSent viewHolderTxtSent = null;
        ViewHolderImageReceive viewHolderImageReceive = null;
        ViewHolderImageSent viewHolderImageSent = null;
        ViewHolderVoiceReceive viewHolderVoiceReceive = null;
        ViewHolderVoiceSent viewHolderVoiceSent = null;


        if (convertView == null) {
            switch (viewType) {
                case 0:

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
                case 1:

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

                    String thumbPath = null;
                    EMImageMessageBody imgBody = (EMImageMessageBody) message.getBody();
                    // received messages
                    if (message.direct() == EMMessage.Direct.RECEIVE) {
                        if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                                imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                            viewHolderImageReceive.mPic.setImageResource(R.drawable.default_image);
                            viewHolderImageReceive.mPicProgressBar.setVisibility(View.VISIBLE);

//                        setMessageReceiveCallback(message, viewHolderImageReceive.mPicProgressBar, viewHolderImageReceive.mPicPercenttage);

                            while (true) {
                                if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.SUCCESSED) {
                                    viewHolderImageReceive.mPicProgressBar.setVisibility(View.GONE);
                                    viewHolderImageReceive.mPicPercenttage.setVisibility(View.GONE);
                                    viewHolderImageReceive.mPic.setImageResource(R.drawable.default_image);
                                    thumbPath = imgBody.thumbnailLocalPath();
                                    if (!new File(thumbPath).exists()) {
                                        // to make it compatible with thumbnail received in previous version
                                        thumbPath = ImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                                    }
                                    showImageView(thumbPath, viewHolderImageReceive.mPic, imgBody.getLocalUrl(), message);
                                    break;
                                }

                            }
                        } else {
//                        Log.i("MessageAdapter", "getView: " + "已接受");
                            viewHolderImageReceive.mPicProgressBar.setVisibility(View.GONE);
                            viewHolderImageReceive.mPicPercenttage.setVisibility(View.GONE);
                            viewHolderImageReceive.mPic.setImageResource(R.drawable.default_image);
                            thumbPath = imgBody.thumbnailLocalPath();
                            if (!new File(thumbPath).exists()) {
                                // to make it compatible with thumbnail received in previous version
                                thumbPath = ImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                            }
                            showImageView(thumbPath, viewHolderImageReceive.mPic, imgBody.getLocalUrl(), message);
                        }
                    }

                    final String finalThumbPath = thumbPath;
                    viewHolderImageReceive.mPic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            context.startActivity(new Intent(context, ShowImageActivity.class).putExtra("thumbPath", finalThumbPath));

                        }
                    });

                    convertView.setTag(viewHolderImageReceive);

                    break;
                case 2:

                    viewHolderVoiceReceive = new ViewHolderVoiceReceive();
                    convertView = inflater.inflate(R.layout.received_voice, parent, false);
                    viewHolderVoiceReceive.toUsername = (TextView) convertView.findViewById(R.id.tv_userid);
                    viewHolderVoiceReceive.mImageView = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    viewHolderVoiceReceive.mVoiceImage = (ImageView) convertView.findViewById(R.id.iv_voice);
                    viewHolderVoiceReceive.mVoiceLength = (TextView) convertView.findViewById(R.id.tv_length);
                    viewHolderVoiceReceive.mUnread_voice = (ImageView) convertView.findViewById(R.id.unread_dot);


                    PersonAvatarUtils.setAvatar(context, message.getFrom(), viewHolderVoiceReceive.mImageView);
                    viewHolderVoiceReceive.toUsername.setText(message.getFrom());
                    EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();
                    viewHolderVoiceReceive.mVoiceLength.setText(voiceBody.getLength() + "\"");
                    viewHolderVoiceReceive.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        }
                    });
                    if (message.isUnread()) {
                        viewHolderVoiceReceive.mUnread_voice.setVisibility(View.VISIBLE);
                    }

                    final ViewHolderVoiceReceive finalViewHolderVoiceReceive = viewHolderVoiceReceive;

                    viewHolderVoiceReceive.mVoiceImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            finalViewHolderVoiceReceive.mUnread_voice.setVisibility(View.GONE);
                            message.setUnread(false);

                            EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();

                            if (message.status() == EMMessage.Status.SUCCESS) {
                                File file = new File(voiceBody.getLocalUrl());
                                if (file.exists() && file.isFile())
                                    playVoice(message);
                                else {
                                }

                            } else if (message.status() == EMMessage.Status.INPROGRESS) {
                                //Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
                            } else if (message.status() == EMMessage.Status.FAIL) {
                                // Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
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


                            playVoice(message);

                        }
                    });

                    convertView.setTag(viewHolderVoiceReceive);
                    break;
                case 3:

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
                case 4:

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
                    final String thumbPathSent = ImageUtils.getThumbnailImagePath(imgBodySent.getLocalUrl());
                    showImageView(thumbPathSent, viewHolderImageSent.mPic, filePath, message);

                    viewHolderImageSent.mPic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, ShowImageActivity.class).putExtra("thumbPath", thumbPathSent));

                        }
                    });
                    convertView.setTag(viewHolderImageSent);
                    break;

                case 5:

                    viewHolderVoiceSent = new ViewHolderVoiceSent();
                    convertView = inflater.inflate(R.layout.sent_voice, parent, false);
//                    viewHolderVoiceSent.toUsername = (TextView) convertView.findViewById(R.id.tv_userid);
                    viewHolderVoiceSent.mImageView = (ImageView) convertView.findViewById(R.id.iv_userhead);
                    viewHolderVoiceSent.mVoiceImage = (ImageView) convertView.findViewById(R.id.iv_voice);
                    viewHolderVoiceSent.mVoiceprogressbar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
                    viewHolderVoiceSent.mVoiceLength = (TextView) convertView.findViewById(R.id.tv_length);

                    PersonAvatarUtils.setAvatar(context, message.getFrom(), viewHolderVoiceSent.mImageView);
                    EMVoiceMessageBody voiceBodySent = (EMVoiceMessageBody) message.getBody();
                    viewHolderVoiceSent.mVoiceLength.setText(voiceBodySent.getLength() + "\"");
                    viewHolderVoiceSent.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        }
                    });

                    viewHolderVoiceSent.mVoiceImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            playVoice(message);

                        }
                    });
                    convertView.setTag(viewHolderVoiceSent);
                    break;

                default:
                    break;
            }
        } else {
            switch (viewType) {
                case 0:
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
                case 1:
                    viewHolderImageReceive = (ViewHolderImageReceive) convertView.getTag();
                    PersonAvatarUtils.setAvatar(context, message.getFrom(), viewHolderImageReceive.mImageView);
                    viewHolderImageReceive.toUsername.setText(message.getFrom());
                    viewHolderImageReceive.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        }
                    });

                    String thumbPath = null;
                    EMImageMessageBody imgBody = (EMImageMessageBody) message.getBody();
                    // received messages
                    if (message.direct() == EMMessage.Direct.RECEIVE) {
                        if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                                imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                            viewHolderImageReceive.mPic.setImageResource(R.drawable.default_image);
                            viewHolderImageReceive.mPicProgressBar.setVisibility(View.VISIBLE);

//                        setMessageReceiveCallback(message, viewHolderImageReceive.mPicProgressBar, viewHolderImageReceive.mPicPercenttage);

                            while (true) {
                                if (imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.SUCCESSED) {
                                    viewHolderImageReceive.mPicProgressBar.setVisibility(View.GONE);
                                    viewHolderImageReceive.mPicPercenttage.setVisibility(View.GONE);
                                    viewHolderImageReceive.mPic.setImageResource(R.drawable.default_image);
                                    thumbPath = imgBody.thumbnailLocalPath();
                                    if (!new File(thumbPath).exists()) {
                                        // to make it compatible with thumbnail received in previous version
                                        thumbPath = ImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                                    }
                                    showImageView(thumbPath, viewHolderImageReceive.mPic, imgBody.getLocalUrl(), message);
                                    break;
                                }

                            }
                        } else {
//                        Log.i("MessageAdapter", "getView: " + "已接受");
                            viewHolderImageReceive.mPicProgressBar.setVisibility(View.GONE);
                            viewHolderImageReceive.mPicPercenttage.setVisibility(View.GONE);
                            viewHolderImageReceive.mPic.setImageResource(R.drawable.default_image);
                            thumbPath = imgBody.thumbnailLocalPath();
                            if (!new File(thumbPath).exists()) {
                                // to make it compatible with thumbnail received in previous version
                                thumbPath = ImageUtils.getThumbnailImagePath(imgBody.getLocalUrl());
                            }
                            showImageView(thumbPath, viewHolderImageReceive.mPic, imgBody.getLocalUrl(), message);
                        }
                    }

                    final String finalThumbPath = thumbPath;
                    viewHolderImageReceive.mPic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            context.startActivity(new Intent(context, ShowImageActivity.class).putExtra("thumbPath", finalThumbPath));

                        }
                    });
                    break;
                case 2:
                    viewHolderVoiceReceive = (ViewHolderVoiceReceive) convertView.getTag();
                    PersonAvatarUtils.setAvatar(context, message.getFrom(), viewHolderVoiceReceive.mImageView);
                    viewHolderVoiceReceive.toUsername.setText(message.getFrom());
                    EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();
                    viewHolderVoiceReceive.mVoiceLength.setText(voiceBody.getLength() + "\"");
                    viewHolderVoiceReceive.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        }
                    });

                    if (message.isUnread()) {
                        viewHolderVoiceReceive.mUnread_voice.setVisibility(View.VISIBLE);
                    }

                    final ViewHolderVoiceReceive finalViewHolderVoiceReceive1 = viewHolderVoiceReceive;
                    viewHolderVoiceReceive.mVoiceImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            finalViewHolderVoiceReceive1.mUnread_voice.setVisibility(View.GONE);
                            message.setUnread(false);
                            EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();

                            if (message.status() == EMMessage.Status.SUCCESS) {
                                File file = new File(voiceBody.getLocalUrl());
                                if (file.exists() && file.isFile())
                                    playVoice(message);
                                else {
                                }

                            } else if (message.status() == EMMessage.Status.INPROGRESS) {
                                //Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
                            } else if (message.status() == EMMessage.Status.FAIL) {
                                // Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
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
                            playVoice(message);

                        }
                    });

                    break;
                case 3:
                    viewHolderTxtSent = (ViewHolderTxtSent) convertView.getTag();
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
                case 4:
                    viewHolderImageSent = (ViewHolderImageSent) convertView.getTag();
                    EMImageMessageBody imgBodySent = (EMImageMessageBody) message.getBody();
                    PersonAvatarUtils.setAvatar(context, message.getFrom(), viewHolderImageSent.mImageView);
                    viewHolderImageSent.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        }
                    });

                    String filePath = imgBodySent.getLocalUrl();
                    final String thumbPathSent = ImageUtils.getThumbnailImagePath(imgBodySent.getLocalUrl());
                    showImageView(thumbPathSent, viewHolderImageSent.mPic, filePath, message);

                    viewHolderImageSent.mPic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, ShowImageActivity.class).putExtra("thumbPath", thumbPathSent));

                        }
                    });
                    break;
                case 5:
                    viewHolderVoiceSent = (ViewHolderVoiceSent) convertView.getTag();
                    PersonAvatarUtils.setAvatar(context, message.getFrom(), viewHolderVoiceSent.mImageView);
                    EMVoiceMessageBody voiceBodySent = (EMVoiceMessageBody) message.getBody();
                    viewHolderVoiceSent.mVoiceLength.setText(voiceBodySent.getLength() + "\"");
                    viewHolderVoiceSent.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            context.startActivity(new Intent(context, UserDetailActivity.class).putExtra("username", message.getFrom()));
                        }
                    });

                    viewHolderVoiceSent.mVoiceImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            playVoice(message);

                        }
                    });
                    break;

                default:
                    break;
            }
        }

        return convertView;
    }


    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
//        message.direct() == EMMessage.Direct.RECEIVE ? 0 : 1;

        if (message.direct() == EMMessage.Direct.RECEIVE) {
            if (message.getType().equals(EMMessage.Type.TXT)) {
                return 0;
            } else if (message.getType().equals(EMMessage.Type.IMAGE)) {
                return 1;
            } else {
                return 2;
            }

        } else {
            if (message.getType().equals(EMMessage.Type.TXT)) {
                return 3;
            } else if (message.getType().equals(EMMessage.Type.IMAGE)) {
                return 4;
            } else {
                return 5;
            }
        }
    }


    public void playVoice(EMMessage message) {

        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();
        String filePath = voiceBody.getLocalUrl();
        if (!(new File(filePath).exists())) {
            return;
        }
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        final MediaPlayer[] mediaPlayer = {new MediaPlayer()};
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
        mediaPlayer[0].setAudioStreamType(AudioManager.STREAM_RING);
//
        try {
            mediaPlayer[0].setDataSource(filePath);
            mediaPlayer[0].prepare();
            mediaPlayer[0].setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mediaPlayer[0].release();
                    mediaPlayer[0] = null;

//                    if (mediaPlayer != null) {
//                        mediaPlayer[0].stop();
//                        mediaPlayer[0].release();
//                    }
                }

            });


            mediaPlayer[0].start();

        } catch (Exception e) {
            System.out.println();
        }
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
                    } else if (new File(((EMImageMessageBody) (message.getBody())).thumbnailLocalPath()).exists()) {
                        return ImageUtils.decodeScaleImage(((EMImageMessageBody) (message.getBody())).thumbnailLocalPath(), 160, 160);
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


    public class ViewHolderTxtReceive {
        TextView tv;
        TextView toUsername;
        ImageView mImageView;
    }

    public class ViewHolderTxtSent {
        TextView tv;
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
        ImageView mVoiceImage;
    }

    public class ViewHolderVoiceSent {

        ImageView mImageView;
        TextView mVoiceLength;
        ProgressBar mVoiceprogressbar;
        ImageView mVoiceImage;
    }
}
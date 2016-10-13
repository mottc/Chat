package com.mottc.chat.Activity;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/10/10
 * Time: 9:44
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.mottc.chat.Activity.Adapter.NewFriendsMsgAdapter;
import com.mottc.chat.R;
import com.mottc.chat.db.InviteMessage;
import com.mottc.chat.db.InviteMessageDao;

import java.util.List;

/**
 * 申请与通知
 *
 */
public class NewFriendsMsgActivity extends Activity{
    private ListView listView;

    //记录手指按下时的横坐标。
    private float xDown;
    //记录手指移动时的横坐标。
    private float xMove;
    View decorView;
    float screenWidth, screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_msg);


        // 获得手机屏幕的宽度和高度，单位像素
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        listView = (ListView) findViewById(R.id.new_friends_list);
        InviteMessageDao dao = new InviteMessageDao(this);
        List<InviteMessage> msgs = dao.getMessagesList();
        NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
        listView.setAdapter(adapter);
        dao.saveUnreadMessageCount(0);

    }
    /**
     * 从当前位置一直往右滑动到消失。
     * 这里使用了属性动画。
     */
    private void continueMove(float moveDistanceX) {
        // 从当前位置移动到右侧。
        ValueAnimator anim = ValueAnimator.ofFloat(moveDistanceX, screenWidth);
        anim.setDuration(300); // 一秒的时间结束, 为了简单这里固定为1秒
        anim.start();

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 位移
                float x = (float) (animation.getAnimatedValue());
                decorView.setX(x);
            }
        });

        // 动画结束时结束当前Activity
        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animaton) {

                startActivity(new Intent(NewFriendsMsgActivity.this, MainActivity.class));
                finish();
            }

        });
    }

    /**
     * Activity被滑动到中途时，滑回去~
     */
    private void rebackToLeft(float moveDistanceX) {
        ObjectAnimator.ofFloat(decorView, "X", moveDistanceX, 0).setDuration(300).start();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {


//        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = event.getRawX();

                break;
            case MotionEvent.ACTION_MOVE:
                xMove = event.getRawX();

                //滑动的距离
                int distanceX = (int) (xMove - xDown);


                if (distanceX > 0) {
                    decorView.setX(distanceX);
                }

                break;
            case MotionEvent.ACTION_UP:
//                recycleVelocityTracker();

                float moveDistanceX = event.getX() - xDown;
                if (moveDistanceX > screenWidth / 4) {
                    // 如果滑动的距离超过了手机屏幕的四分之一, 滑动处屏幕后再结束当前Activity
                    continueMove(moveDistanceX);
                } else {
                    // 如果滑动距离没有超过一半, 往回滑动
                    rebackToLeft(moveDistanceX);
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

//    public void back(View view) {
//        finish();
//    }


}
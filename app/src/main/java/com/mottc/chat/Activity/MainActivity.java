package com.mottc.chat.Activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;
import com.mottc.chat.Activity.Adapter.MyViewPagerAdapter;
import com.mottc.chat.MyApplication;
import com.mottc.chat.R;
import com.mottc.chat.db.EaseUser;
import com.mottc.chat.db.InviteMessage;
import com.mottc.chat.db.InviteMessage.InviteMessageStatus;
import com.mottc.chat.db.InviteMessageDao;
import com.mottc.chat.db.UserDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ItemFragment.OnListFragmentInteractionListener {

    private InviteMessageDao inviteMessgeDao;
    private UserDao userDao;
    private DrawerLayout drawer;
    MyViewPagerAdapter viewPagerAdapter;
    ViewPager viewpager;
    NotificationManager manager;//通知栏控制类
    int notification_ID;
    int notification_ID2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        inviteMessgeDao = new InviteMessageDao(MainActivity.this);
        userDao = new UserDao(MainActivity.this);
        //注册联系人变动监听
        EMClient.getInstance().contactManager().setContactListener(new MyContactListener());


//      Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(ItemFragment.newInstance(1), "消息");//添加Fragment
        viewPagerAdapter.addFragment(ItemFragment.newInstance(1), "通讯录");
        viewpager.setAdapter(viewPagerAdapter);//设置适配器


        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText("消息"));//给TabLayout添加Tab
        mTabLayout.addTab(mTabLayout.newTab().setText("通讯录"));
        mTabLayout.setupWithViewPager(viewpager);//给TabLayout设置关联ViewPager，如果设置了ViewPager，那么ViewPagerAdapter中的getPageTitle()方法返回的就是Tab上的标题


        FloatingActionButtonPlus mActionButtonPlus = (FloatingActionButtonPlus) findViewById(R.id.FabPlus);
        mActionButtonPlus.setOnItemClickListener(new FloatingActionButtonPlus.OnItemClickListener() {
            @Override
            public void onItemClick(FabTagLayout tagView, int position) {
                if (position == 0) {
//                  好友请求界面
                    startActivity(new Intent(MainActivity.this, NewFriendsMsgActivity.class));
                } else if (position == 1) {
//                  添加好友界面
                    startActivity(new Intent(MainActivity.this, AddContactActivity.class));
                }
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//      设置右滑界面中的用户名
        View headerView = navigationView.getHeaderView(0);
        TextView textView = (TextView) headerView.findViewById(R.id.tvusername);
        textView.setText(getCurrentUser());

        //注册消息变动监听
        EMClient.getInstance().chatManager().addMessageListener(msgListener);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {

            // 收到消息
            for (EMMessage message : messages) {
                String username = null;
                String info = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // 单聊消息
                    username = message.getFrom();//获取发来消息的用户名

//                  获取发来的消息
                    info = message.toString();
                    int start = info.indexOf("txt:\"");
                    int end = info.lastIndexOf("\"");
                    info = info.substring((start + 5), end);

                }
                sendNotification(username, info);
            }
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            // 收到透传消息
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            // 收到已读回执
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            // 收到已送达回执
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            // 消息状态变动
        }
    };

    //  收到新消息，在通知栏显示通知
    private void sendNotification(String username, String info) {

        Intent intent = new Intent(MainActivity.this, ChatActivity.class).putExtra("username", username);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);//设置图标
        builder.setWhen(System.currentTimeMillis());//设置时间
        builder.setContentTitle(username);//设置标题
        builder.setContentText(info);//设置通知内容
        builder.setContentIntent(pendingIntent);//点击后的意图
        builder.setDefaults(Notification.DEFAULT_ALL);//设置震动、响铃、呼吸灯。
//        Notification notification = builder.build();//4.1以上
        Notification notification = builder.getNotification();
        notification.flags = Notification.FLAG_AUTO_CANCEL;//通知栏消息，点击后消失。
        manager.notify(notification_ID, notification);
    }

    //  获取当前用户名
    public String getCurrentUser() {
        return EMClient.getInstance().getCurrentUser();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();

        if (id == R.id.nav_share) {
            Toast.makeText(MainActivity.this, "我还没实现分享", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_send) {
            final ProgressDialog pd = new ProgressDialog(MainActivity.this);
            String st = getResources().getString(R.string.Are_logged_out);
            pd.setMessage(st);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
            MyApplication.getInstance().logout(false, new EMCallBack() {
                @Override
                public void onSuccess() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            // 重新显示登陆页面
                            finish();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    });
                }

                @Override
                public void onProgress(int progress, String status) {
                }

                @Override
                public void onError(int code, String message) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            pd.dismiss();
                            Toast.makeText(MainActivity.this, "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } else if (id == R.id.nav_manage) {

            showVersionDialog();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showVersionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("版本号");//设置标题
//        builder.setIcon(R.drawable.version);//设置图标
        builder.setMessage("0.1.4.161014_alpha_cl");//设置内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();//获取dialog
        dialog.show();//显示对话框
    }


    /**
     * 在主界面点击返回键时，不会回到登陆界面。而是弹出提示是否要推出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                return false;
            } else {
                showTips();
                return false;
            }

        } else {
            return super.onKeyDown(keyCode, event);
        }

    }


    private void showTips() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("提醒")
                .setMessage("是否退出程序")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }

                }).setNegativeButton("取消",

                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).create(); // 创建对话框
        alertDialog.show(); // 显示对话框
    }


    @Override
    public void onListFragmentInteraction(EaseUser item) {
        startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra("username", item.getUsername()));

    }

    /***
     * 好友变化listener
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(final String username) {
            // 保存增加的联系人
            Map<String, EaseUser> localUsers = MyApplication.getInstance().getContactList();
            Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
            EaseUser user = new EaseUser(username);
            // 添加好友时可能会回调added方法两次
            if (!localUsers.containsKey(username)) {
                userDao.saveContact(user);
            }
            toAddUsers.put(username, user);
            localUsers.putAll(toAddUsers);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    /*增加好友后，更新列表*/
//                    TODO:
                    Toast.makeText(getApplicationContext(), "增加联系人：+" + username, Toast.LENGTH_SHORT).show();
                }


            });


        }

        @Override
        public void onContactDeleted(final String username) {
            // 被删除
            Map<String, EaseUser> localUsers = MyApplication.getInstance().getContactList();
            localUsers.remove(username);
            userDao.deleteContact(username);
            inviteMessgeDao.deleteMessage(username);

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    Toast.makeText(getApplicationContext(), "删除联系人：+" + username, Toast.LENGTH_SHORT).show();
                }


            });

        }

        @Override
        public void onContactInvited(final String username, String reason) {
            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);

            // 设置相应status
            msg.setStatus(InviteMessage.InviteMessageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "收到好友申请：+" + username, Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public void onContactAgreed(final String username) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());

            msg.setStatus(InviteMessageStatus.BEAGREED);
            notifyNewIviteMessage(msg);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "好友申请同意：+" + username, Toast.LENGTH_SHORT).show();

                }
            });

        }

        @Override
        public void onContactRefused(String username) {
            // 参考同意，被邀请实现此功能,demo未实现
            Log.d(username, username + "拒绝了你的好友请求");
        }
    }

    /**
     * 保存并提示消息的邀请消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        if (inviteMessgeDao == null) {
            inviteMessgeDao = new InviteMessageDao(MainActivity.this);
        }
        inviteMessgeDao.saveMessage(msg);
        //保存未读数，这里没有精确计算
        inviteMessgeDao.saveUnreadMessageCount(1);
        // 提示有新消息
        //响铃或其他操作

        String username = msg.getFrom();
        String reason = msg.getReason();

        sendNewFriendsNotification(username, reason);
    }

    //  添加通知栏通知
    private void sendNewFriendsNotification(String username, String reason) {

        Intent intent = new Intent(MainActivity.this, NewFriendsMsgActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);//设置图标
        builder.setWhen(System.currentTimeMillis());//设置时间
        builder.setContentTitle(username + ",请求加你为好友：");//设置标题
        builder.setContentText(reason);//设置通知内容
        builder.setContentIntent(pendingIntent);//点击后的意图
        builder.setDefaults(Notification.DEFAULT_ALL);//设置震动、响铃、呼吸灯。
//        Notification notification = builder.build();//4.1以上
        Notification notification = builder.getNotification();
        notification.flags = Notification.FLAG_AUTO_CANCEL;//通知栏消息，点击后消失。
        manager.notify(notification_ID2, notification);
    }
}

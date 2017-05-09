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
import android.support.design.widget.Snackbar;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.NetUtils;
import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;
import com.mottc.chat.Activity.Adapter.MyViewPagerAdapter;
import com.mottc.chat.login.LoginActivity;
import com.mottc.chat.MyApplication;
import com.mottc.chat.R;
import com.mottc.chat.db.EaseUser;
import com.mottc.chat.db.InviteMessage;
import com.mottc.chat.db.InviteMessage.InviteMessageStatus;
import com.mottc.chat.db.InviteMessageDao;
import com.mottc.chat.db.UserDao;
import com.mottc.chat.utils.PersonAvatarUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                   ContactFragment.OnListFragmentInteractionListener,
                   ConversationFragment.OnConversationFragmentInteractionListener,
                   GroupFragment.OnGroupFragmentInteractionListener {

    private InviteMessageDao inviteMessgeDao;
    private UserDao userDao;
    private DrawerLayout drawer;
    MyViewPagerAdapter viewPagerAdapter;
    ViewPager viewpager;
    NotificationManager manager;//通知栏控制类
    View mLayout;
    String currentUserName;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        inviteMessgeDao = new InviteMessageDao(MainActivity.this);
        userDao = new UserDao(MainActivity.this);
        mLayout = findViewById(R.id.coordinator_layout);

//      Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(ConversationFragment.newInstance(1), "消息");//添加Fragment
        viewPagerAdapter.addFragment(ContactFragment.newInstance(1), "通讯录");
        viewPagerAdapter.addFragment(GroupFragment.newInstance(1), "群组");
        viewpager.setAdapter(viewPagerAdapter);//设置适配器

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText("消息"));//给TabLayout添加Tab
        mTabLayout.addTab(mTabLayout.newTab().setText("通讯录"));
        mTabLayout.addTab(mTabLayout.newTab().setText("群组"));
        //给TabLayout设置关联ViewPager，如果设置了ViewPager，那么ViewPagerAdapter中的getPageTitle()方法返回的就是Tab上的标题
        mTabLayout.setupWithViewPager(viewpager);


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
                } else if (position == 2) {
//                    新建群组
                    startActivity(new Intent(MainActivity.this, CreateGroupActivity.class));
                } else if (position == 3) {
//                    加入群组
                    startActivity(new Intent(MainActivity.this, AddGroupActivity.class));
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
        imageView = (ImageView) headerView.findViewById(R.id.imageView);
        currentUserName = EMClient.getInstance().getCurrentUser();
        textView.setText(currentUserName);//  获取当前用户名
//        new AvatarURLDownloadUtils().downLoad(currentUserName, this, imageView, false);

        //注册联系人变动监听
        EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new MyConnectionListener());
        //注册群组变动监听
        EMClient.getInstance().groupManager().addGroupChangeListener(new MyGroupChangeListener());

//        UserDetailActivity userDetailActivity = new UserDetailActivity();
//        userDetailActivity.setOnAvatarChangeListener(new UserDetailActivity.OnAvatarChangeListener() {
//            @Override
//            public void setAvatar() {
//                PersonAvatarUtils.setAvatar(MainActivity.this, currentUserName, imageView);
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        PersonAvatarUtils.setAvatar(this, currentUserName, imageView);
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu2) {
//        // Inflate the menu2; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu2.main, menu2);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    //  侧边栏中，点击自己的信息
    public void detailInfo(View view) {
        startActivity(new Intent(MainActivity.this, UserDetailActivity.class).putExtra("username", EMClient.getInstance().getCurrentUser()));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();

        if (id == R.id.add_friends) {
            startActivity(new Intent(MainActivity.this, AddContactActivity.class));
        } else if (id == R.id.notifition) {
            startActivity(new Intent(MainActivity.this, NewFriendsMsgActivity.class));
        } else if (id == R.id.add_group) {
            startActivity(new Intent(MainActivity.this, AddGroupActivity.class));
        } else if (id == R.id.new_group) {
            startActivity(new Intent(MainActivity.this, CreateGroupActivity.class));
        } else if (id == R.id.change_user) {
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

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * 在主界面点击返回键时，弹出提示是否要推出程序
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

        AlertDialog alertDialog = new AlertDialog
                .Builder(this)
                .setTitle("提醒")
                .setMessage("是否退出程序")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }
                })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                .create(); // 创建对话框

        alertDialog.show(); // 显示对话框
    }


    //对话类型：1为单聊，2为群聊。
    @Override
    public void onListFragmentInteraction(EaseUser item) {
        startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra("username", item.getUsername()).putExtra("type", 1));
    }

    //对话类型：1为单聊，2为群聊。
    @Override
    public void onConversationFragmentInteraction(EMConversation item) {
        if (item.isGroup()) {
            startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra("username", item.getLastMessage().getTo()).putExtra("type", 2));
        } else {
            startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra("username", item.getUserName()).putExtra("type", 1));
        }
    }

    //对话类型：1为单聊，2为群聊。
    @Override
    public void onGroupFragmentInteraction(EMGroup item) {
        startActivity(new Intent(MainActivity.this, ChatActivity.class).putExtra("username", item.getGroupId()).putExtra("type", 2));
    }


    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        Toast.makeText(MainActivity.this, "帐号已经被移除", Toast.LENGTH_SHORT).show();
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        Toast.makeText(MainActivity.this, "帐号在其他设备登录", Toast.LENGTH_SHORT).show();
                    } else {
                        if (NetUtils.hasNetwork(MainActivity.this)) {
                            //连接不到聊天服务器
                            Toast.makeText(MainActivity.this, "连接不到聊天服务器", Toast.LENGTH_SHORT).show();
                        } else {
                            //当前网络不可用，请检查网络设置
                            Toast.makeText(MainActivity.this, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    /**
     * 群组状态监听
     */
    public class MyGroupChangeListener implements EMGroupChangeListener {

        @Override
        public void onUserRemoved(String groupId, String groupName) {
            //当前用户被管理员移除出群组
            try {
                EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            Snackbar.make(mLayout, "您已被移除出群组" + groupName, Snackbar.LENGTH_LONG)
                    .show();
        }

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            //收到加入群组的邀请
            Snackbar.make(mLayout, inviter + "邀请您加入群组" + groupName, Snackbar.LENGTH_LONG)
                    .show();
            Toast.makeText(MainActivity.this, "111111111", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
            //群组邀请被拒绝
            Snackbar.make(mLayout, invitee + "拒绝加入群组" + EMClient.getInstance().groupManager().getGroup(groupId).getGroupName(), Snackbar.LENGTH_LONG)
                    .show();
        }

        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {
            //群组邀请被接受
            Snackbar.make(mLayout, inviter + "已接受加入群组" + EMClient.getInstance().groupManager().getGroup(groupId).getGroupName(), Snackbar.LENGTH_LONG)
                    .show();
            final String id = groupId;
            final String name = inviter;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().addUsersToGroup(id, new String[]{name});//需异步处理
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {
            //群组被创建者解散
            Snackbar.make(mLayout, "群组" + groupName + "已被解散", Snackbar.LENGTH_LONG)
                    .show();
        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {

        }

        @Override
        public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
            //收到加群申请
            Snackbar.make(mLayout, applyer + "申请加入" + groupName + "：" + reason, Snackbar.LENGTH_LONG)
                    .show();

            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(applyer)) {
                    inviteMessgeDao.deleteMessage(applyer);
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applyer);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);

            // 设置相应status
            msg.setStatus(InviteMessage.InviteMessageStatus.BEAPPLYED);
            notifyNewIviteMessage(msg);
            sendNewFriendsAddGroupNotification(applyer, reason, groupName);

        }

        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {
            //加群申请被同意
            try {
                EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            Snackbar.make(mLayout, "加入" + groupName + "群组的请求已被同意", Snackbar.LENGTH_LONG)
                    .show();
        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
            //加群申请被拒绝
            Snackbar.make(mLayout, "加入" + groupName + "群组的请求已被拒绝", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private void sendNewFriendsAddGroupNotification(String applyer, String reason, String groupName) {
        Intent intent = new Intent(MainActivity.this, NewFriendsMsgActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);//设置图标
        builder.setWhen(System.currentTimeMillis());//设置时间
        builder.setContentTitle(applyer + "请求加入" + groupName);//设置标题
        builder.setContentText(reason);//设置通知内容
        builder.setContentIntent(pendingIntent);//点击后的意图
        builder.setDefaults(Notification.DEFAULT_ALL);//设置震动、响铃、呼吸灯。
//        Notification notification = builder.build();//4.1以上
        Notification notification = builder.getNotification();
        notification.flags = Notification.FLAG_AUTO_CANCEL;//通知栏消息，点击后消失。
        manager.notify((int) System.currentTimeMillis(), notification);
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
            sendNewFriendsNotification(username, reason);
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

    }

    //  添加好友变化通知栏通知
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
        manager.notify((int) System.currentTimeMillis(), notification);
    }
}

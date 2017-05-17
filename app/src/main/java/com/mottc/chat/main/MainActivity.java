package com.mottc.chat.main;

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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.util.NetUtils;
import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;
import com.mottc.chat.addcontact.AddContactActivity;
import com.mottc.chat.addgroup.AddGroupActivity;
import com.mottc.chat.creategroup.CreateGroupActivity;
import com.mottc.chat.Activity.NewFriendsMsgActivity;
import com.mottc.chat.userdetail.UserDetailActivity;
import com.mottc.chat.ChatApplication;
import com.mottc.chat.R;
import com.mottc.chat.chat.ChatActivity;
import com.mottc.chat.data.bean.ChatUser;
import com.mottc.chat.login.LoginActivity;
import com.mottc.chat.main.contact.ContactFragment;
import com.mottc.chat.main.conversation.ConversationFragment;
import com.mottc.chat.main.group.GroupFragment;
import com.mottc.chat.utils.AvatarUtils;

public class MainActivity extends AppCompatActivity
        implements MainContract.View,
        NavigationView.OnNavigationItemSelectedListener,
        ContactFragment.OnListFragmentInteractionListener,
        ConversationFragment.OnConversationFragmentInteractionListener,
        GroupFragment.OnGroupFragmentInteractionListener {

    private DrawerLayout drawer;
    private NotificationManager mNotificationManager;//通知栏控制类
    private String currentUserName;
    private ImageView imageView;
    private View layout;
    private MainContract.Presenter mPresenter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUserName = ChatApplication.getInstance().getCurrentUserName();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        layout = findViewById(R.id.coordinator_layout);
        mPresenter = new MainPresenter(this);

//      Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(ConversationFragment.newInstance(), "消息");//添加Fragment
        viewPagerAdapter.addFragment(ContactFragment.newInstance(), "通讯录");
        viewPagerAdapter.addFragment(GroupFragment.newInstance(), "群组");
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                (this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//      设置右滑界面中的用户名
        View headerView = navigationView.getHeaderView(0);
        TextView textView = (TextView) headerView.findViewById(R.id.tvusername);
        imageView = (ImageView) headerView.findViewById(R.id.imageView);
        textView.setText(currentUserName);

        //注册联系人变动监听
        EMClient.getInstance().contactManager().setContactListener(new ChatContactListener(this));
        //注册一个监听连接状态的listener
        EMClient.getInstance().addConnectionListener(new ChatConnectionListener(this));
        //注册群组变动监听
        EMClient.getInstance().groupManager().addGroupChangeListener(new ChatGroupChangeListener(this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        AvatarUtils.setAvatar(this, currentUserName, imageView);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    //  侧边栏中，点击自己的信息
    public void detailInfo(View view) {
        startActivity(new Intent(MainActivity.this, UserDetailActivity.class)
                .putExtra("username", currentUserName));
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
            logout();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        ChatApplication.getInstance().logout(false, new EMCallBack() {
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
                            }
                        })
                .create(); // 创建对话框

        alertDialog.show(); // 显示对话框
    }


    //对话类型：1为单聊，2为群聊。
    @Override
    public void onListFragmentInteraction(ChatUser item) {
        startActivity(new Intent(MainActivity.this, ChatActivity.class)
                .putExtra("username", item.getUserName())
                .putExtra("type", 1));
    }

    //对话类型：1为单聊，2为群聊。
    @Override
    public void onConversationFragmentInteraction(EMConversation item) {
        if (item.isGroup()) {
            startActivity(new Intent(MainActivity.this, ChatActivity.class)
                    .putExtra("username", item.getLastMessage().getTo())
                    .putExtra("type", 2));
        } else {
            startActivity(new Intent(MainActivity.this, ChatActivity.class)
                    .putExtra("username", item.conversationId())
                    .putExtra("type", 1));
        }
    }

    //对话类型：1为单聊，2为群聊。
    @Override
    public void onGroupFragmentInteraction(EMGroup item) {
        startActivity(new Intent(MainActivity.this, ChatActivity.class)
                .putExtra("username", item.getGroupId())
                .putExtra("type", 2));
    }


    @Override
    public void sendNewFriendsAddGroupNotification(String applyer, String reason, String groupName) {
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
        mNotificationManager.notify((int) System.currentTimeMillis(), notification);
    }

    //  添加好友变化通知栏通知
    @Override
    public void sendNewFriendsNotification(String username, String reason) {

        Intent intent = new Intent(MainActivity.this, NewFriendsMsgActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);//设置图标
        builder.setWhen(System.currentTimeMillis());//设置时间
        builder.setContentTitle(username + "请求加你为好友");//设置标题
        builder.setContentText(reason);//设置通知内容
        builder.setContentIntent(pendingIntent);//点击后的意图
        builder.setDefaults(Notification.DEFAULT_ALL);//设置震动、响铃、呼吸灯。
//        Notification notification = builder.build();//4.1以上
        Notification notification = builder.getNotification();
        notification.flags = Notification.FLAG_AUTO_CANCEL;//通知栏消息，点击后消失。
        mNotificationManager.notify((int) System.currentTimeMillis(), notification);
    }

    @Override
    public void showInvitationReceived(String inviter, String groupName) {
        //收到加入群组的邀请
        Snackbar.make(layout, inviter + "邀请您加入群组" + groupName, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showRequestToJoinReceived(String applicant, String groupName, String reason) {

        //收到加群申请
        Snackbar.make(layout, applicant + "申请加入" + groupName + "：" + reason, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showRequestToJoinAccepted(String groupName) {

        Snackbar.make(layout, "加入" + groupName + "群组的请求已被同意", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showRequestToJoinDeclined(String groupName) {
        //加群申请被拒绝
        Snackbar.make(layout, "加入" + groupName + "群组的请求已被拒绝", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showInvitationAccepted(String invitee, String groupId) {

        String groupName = EMClient.getInstance().groupManager().getGroup(groupId).getGroupName();
        //群组邀请被接受
        Snackbar.make(layout, invitee + "已接受加入群组" + groupName, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showInvitationDeclined(String invitee, String groupId) {
        String groupName = EMClient.getInstance().groupManager().getGroup(groupId).getGroupName();
        //群组邀请被拒绝
        Snackbar.make(layout, invitee + "拒绝加入群组" + groupName, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showUserRemoved(String groupName) {
        Snackbar.make(layout, "您已被移除出群组" + groupName, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showGroupDestroyed(String groupName) {

        //群组被创建者解散
        Snackbar.make(layout, "群组" + groupName + "已被解散", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showContactAdded(String username) {
        Toast.makeText(this, "增加联系人：+" + username, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showContactDeleted(String username) {
        Toast.makeText(this, "删除联系人：+" + username, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showFriendRequestDeclined(String username) {
        Toast.makeText(this, username + "拒绝了你的好友请求", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showContactInvited(String username) {
        Toast.makeText(this, username + "发来好友请求", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFriendRequestAccepted(String username) {
        Toast.makeText(this, username + "同意了你的好友申请", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDisconnectedInfo(int error) {
        if (error == EMError.USER_REMOVED) {
            // 显示帐号已经被移除
            Toast.makeText(this, "帐号已经被移除", Toast.LENGTH_SHORT).show();
        } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
            // 显示帐号在其他设备登录
            Toast.makeText(this, "帐号在其他设备登录", Toast.LENGTH_SHORT).show();
        } else {
            if (NetUtils.hasNetwork(this)) {
                //连接不到聊天服务器
                Toast.makeText(this, "连接不到聊天服务器", Toast.LENGTH_SHORT).show();
            } else {
                //当前网络不可用，请检查网络设置
                Toast.makeText(this, "当前网络不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

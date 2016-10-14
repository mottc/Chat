package com.mottc.chat.Activity;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/10/10
 * Time: 9:44
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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
    private ImageButton btn_newfriends_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_msg);

        listView = (ListView) findViewById(R.id.new_friends_list);
        btn_newfriends_back = (ImageButton) findViewById(R.id.back);
        btn_newfriends_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        InviteMessageDao dao = new InviteMessageDao(this);
        List<InviteMessage> msgs = dao.getMessagesList();
        NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
        listView.setAdapter(adapter);
        dao.saveUnreadMessageCount(0);

    }



}
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friends_msg);

        listView = (ListView) findViewById(R.id.new_friends_list);
        InviteMessageDao dao = new InviteMessageDao(this);
        List<InviteMessage> msgs = dao.getMessagesList();
        NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
        listView.setAdapter(adapter);
        dao.saveUnreadMessageCount(0);

    }

    public void back(View view) {
        finish();
    }


}
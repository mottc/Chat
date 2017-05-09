package com.mottc.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mottc.chat.ChatApplication;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2016/10/8
 * Time: 14:32
 */


/**
 * 创建数据库和其中的表
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static DbOpenHelper instance;

    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_NAME_AVATAR + " TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

    private static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + InviteMessageDao.TABLE_NAME + " ("
            + InviteMessageDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InviteMessageDao.COLUMN_NAME_FROM + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_GROUP_ID + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_GROUP_Name + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_REASON + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_STATUS + " INTEGER, "
            + InviteMessageDao.COLUMN_NAME_ISINVITEFROMME + " INTEGER, "
            + InviteMessageDao.COLUMN_NAME_UNREAD_MSG_COUNT + " INTEGER, "
            + InviteMessageDao.COLUMN_NAME_TIME + " TEXT, "
            + InviteMessageDao.COLUMN_NAME_GROUPINVITER + " TEXT); ";

    private static final String ROBOT_TABLE_CREATE = "CREATE TABLE "
            + UserDao.ROBOT_TABLE_NAME + " ("
            + UserDao.ROBOT_COLUMN_NAME_ID + " TEXT PRIMARY KEY, "
            + UserDao.ROBOT_COLUMN_NAME_NICK + " TEXT, "
            + UserDao.ROBOT_COLUMN_NAME_AVATAR + " TEXT);";

    private static final String CREATE_PREF_TABLE = "CREATE TABLE "
            + UserDao.PREF_TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_DISABLED_GROUPS + " TEXT, "
            + UserDao.COLUMN_NAME_DISABLED_IDS + " TEXT);";

    private static final String CREATE_AVATARINFO_TABLE = "CREATE TABLE "
            + AvatarInfoDao.AVATARINFO_TABLE_NAME + " ("
            + AvatarInfoDao.COLUMN_NAME_USERNAME + " TEXT PRIMARY KEY, "
            + AvatarInfoDao.COLUMN_NAME_TIME + " TEXT);";

    private DbOpenHelper(Context context) {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
    }

    public static DbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static String getUserDatabaseName() {
        return ChatApplication.getInstance().getCurrentUserName() + "_demo.db";
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERNAME_TABLE_CREATE);
        db.execSQL(INIVTE_MESSAGE_TABLE_CREATE);
        db.execSQL(CREATE_PREF_TABLE);
        db.execSQL(ROBOT_TABLE_CREATE);
        db.execSQL(CREATE_AVATARINFO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + UserDao.TABLE_NAME + " ADD COLUMN " +
                    UserDao.COLUMN_NAME_AVATAR + " TEXT ;");
        }

        if (oldVersion < 3) {
            db.execSQL(CREATE_PREF_TABLE);
        }
        if (oldVersion < 4) {
            db.execSQL(ROBOT_TABLE_CREATE);
        }
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE " + InviteMessageDao.TABLE_NAME + " ADD COLUMN " +
                    InviteMessageDao.COLUMN_NAME_UNREAD_MSG_COUNT + " INTEGER ;");
        }
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE " + InviteMessageDao.TABLE_NAME + " ADD COLUMN " +
                    InviteMessageDao.COLUMN_NAME_GROUPINVITER + " TEXT;");
        }
    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }
}

package tty.community.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MainDBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(CREATE_TABLE_USER)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) { }

    fun login(context: Context, values: ContentValues) {
        // status = 0 > need to re-login
        // status = 1 > normal
        // status = 2 > cannot do anything
        val db = readableDatabase
        val invalidAll = "update user set status = 0 where status > 0"
        db.execSQL(invalidAll)
        db.insertWithOnConflict("user", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }



    companion object {
        const val TAG = "MainDBHelper"
        const val DB_NAME = "data.db"
        const val DB_VERSION = 1
        const val CREATE_TABLE_USER = "create table user(_id integer primary key autoincrement, id varchar(32) not null unique, nickname varchar(32) not null unique, token text not null, email text not null, status integer not null, personal_signature text, exp integer, user_group text);"

    }
}
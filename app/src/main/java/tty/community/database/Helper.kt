package tty.community.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import tty.community.model.User

class Helper private constructor(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(CREATE_TABLE_USER)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}


    fun findUser(): User? {
        val db = writableDatabase
        val find = "select * from user where status > 0 limit 1"
        val cursor = db.rawQuery(find, arrayOf())
        var user: User? = null

        if (cursor.moveToFirst() && cursor.count > 0) {
            user = User(
                cursor.getString(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("nickname")),
                cursor.getString(cursor.getColumnIndex("token")),
                cursor.getString(cursor.getColumnIndex("email"))
            )
        }

        cursor.close()
        db.close()
        return user
    }

    fun updateUser(id: String, email: String, nickname: String) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("email", email)
        values.put("nickname", nickname)
        db.update("user", values, "id = ?", arrayOf(id))
        db.close()
    }


    companion object {
        const val TAG = "Helper"
        const val DB_NAME = "data.db"
        const val DB_VERSION = 1
        const val CREATE_TABLE_USER = "create table user(_id integer primary key autoincrement, id varchar(32) not null unique, nickname varchar(32) not null unique, token text not null, email text not null, status integer not null, personal_signature text, exp integer, user_group text);"

        private var helper: Helper? = null
        fun getHelper(context: Context): Helper {
            if (helper == null) {
                helper = Helper(context)
            }
            return helper!!
        }
    }
}
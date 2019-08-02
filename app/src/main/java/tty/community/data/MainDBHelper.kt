package tty.community.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.json.JSONObject
import tty.community.model.Shortcut
import tty.community.network.AsyncTaskUtil
import tty.community.values.Values
import kotlin.collections.HashMap

class MainDBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(CREATE_TABLE_USER)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) { }

    fun login(context: Context, map: HashMap<String, String>) {
        val db = getHelper(context)
//        map["platform"] = "mobile"
//        map["login_type"] = "nickname"
//        map["nickname"] = "wcf"
//        map["password"] = "123456789"
        AsyncTaskUtil.AsyncNetUtils.post("${Values.api["user"]}/login", map, object : AsyncTaskUtil.AsyncNetUtils.Callback{
            override fun onResponse(response: String) {
                Log.d(TAG, response)
                val result  = JSONObject(response)
                when(Shortcut.phrase(result.optString("shortcut", "OTHER"))) {
                    Shortcut.OK -> {
                        
                    }

                    Shortcut.UPE -> {

                    }

                    Shortcut.AE -> {

                    }

                    Shortcut.FE -> {

                    }

                    Shortcut.UR -> {

                    }

                    Shortcut.UNE -> {

                    }

                    Shortcut.TE -> {

                    }

                    Shortcut.BNE -> {

                    }

                    Shortcut.OTHER -> {

                    }
                }
            }
        })
    }

    companion object {
        const val TAG = "MainDBHelper"
        const val DB_NAME = "data.db"
        const val DB_VERSION = 1
        const val CREATE_TABLE_USER = "create table user(_id integer primary key autoincrement, id varchar(32) not null unique, nickname varchar(32) not null unique, token text not null, password text not null, last_login_ip text not null, last_login_time text not null, email text not null);"
        var helper: MainDBHelper? = null
        fun getHelper(context: Context): MainDBHelper {
            if (helper != null) {
                helper = MainDBHelper(context)
            }
            return helper!!
        }

    }
}
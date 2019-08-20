package tty.community.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.Toast
import com.google.gson.reflect.TypeToken
import tty.community.database.Helper
import tty.community.network.AsyncNetUtils
import tty.community.util.CONF
import tty.community.util.Message

open class User(
    open val id: String,
    open val nickname: String,
    open val token: String,
    open val email: String
) {
    fun login(context: Context): Long {
        Log.d(TAG, "login, id = $id")
        // status = 0 > need to re-login
        // status = 1 > normal
        reset(context)
        val values = ContentValues()
        values.put("id", id)
        values.put("nickname", nickname)
        values.put("email", email)
        values.put("token", token)
        values.put("status", 1)
        val db = Helper.getHelper(context).writableDatabase
        val result = db.insertWithOnConflict("user", null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()

        return result
    }

    interface SimpleUser {
        val id: String
        val nickname: String
        val email: String

        data class User(override val id: String, override val nickname: String, override val email: String) :
            SimpleUser
    }

    interface SimpleDetail {
        val portrait: String
        val signature: String
        val userGroup: Int
        val exp: Int
        val school: String

        data class Detail(override val portrait: String, override val signature: String, override val userGroup: Int, override val exp: Int, override val school: String) :
            SimpleDetail
    }

    class Register(
        val nickname: String,
        val email: String,
        val password: String
    )

    class PrivateInfo(
        override val id: String,
        override val nickname: String,
        override val email: String,
        override val portrait: String,
        override val signature: String,
        override val userGroup: Int,
        override val exp: Int,
        override val school: String
    ) : SimpleUser, SimpleDetail {
        data class Item(val key: String, val value: String, val status: Shortcut)
    }


    companion object {
        const val TAG = "User"

        val type = object : TypeToken<Message.MsgData<User>>(){}

        fun reset(context: Context) {
            Log.d(TAG, "reset user")
            val db = Helper.getHelper(context).writableDatabase
            val invalidAll = "update user set status = 0"
            db.execSQL(invalidAll)
            db.close()
        }

        fun find(context: Context): User? {
            Log.d(TAG, "find user")
            val db = Helper.getHelper(context).writableDatabase
            val find = "select * from user where status = 1 limit 1"
            val cursor = db.rawQuery(find, arrayOf())
            val user: User?

            if (cursor.moveToFirst() && cursor.count > 0) {
                user = User(
                    cursor.getString(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("nickname")),
                    cursor.getString(cursor.getColumnIndex("token")),
                    cursor.getString(cursor.getColumnIndex("email"))
                )
            } else {
                user = null
            }
            cursor.close()
            db.close()

            Log.d(TAG, "user:${user != null}")
            return user
        }

        fun update(context: Context, user: SimpleUser.User): Int {
            Log.d(TAG, "update user")
            val db = Helper.getHelper(context).writableDatabase
            val values = ContentValues()
            values.put("email", user.email)
            values.put("nickname", user.nickname)
            val effect = db.update("user", values, "id = ?", arrayOf(user.id))
            db.close()
            return effect
        }

        fun autoLogin(context: Context) {
            val user = find(context)
            if (user != null) {
                AsyncNetUtils.post(CONF.API.user.autoLogin, Params.autoLogin(user), object : AsyncNetUtils.Callback {
                    fun notify(msg: String = "未知异常"): Int {
                        Log.e(TAG, msg)
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        return 1
                    }

                    fun onLoginFail(msg: String): Int {
                        reset(context)
                        return notify(msg)
                    }

                    override fun onFailure(msg: String): Int {
                        return notify(msg)
                    }

                    override fun onResponse(result: String?): Int {
                        val message: Message.MsgData<SimpleUser.User>? = Message.MsgData.parse(result, object : TypeToken<Message.MsgData<SimpleUser.User>>(){})
                        return if(message != null) {
                            when(message.shortcut) {
                                Shortcut.OK -> update(
                                    context,
                                    message.data
                                )
                                Shortcut.TE -> onLoginFail("登录状态过期，请重新登录")
                                Shortcut.UNE -> onLoginFail("用户不存在，请重新登录")
                                else -> notify("shortcut异常")
                            }
                        } else {
                            notify("解析异常")
                        }
                    }
                })
            } else {
                Toast.makeText(context, "您还未登录账号，请先登录", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
package tty.community.util

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import tty.community.model.Shortcut
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


interface Message {
    val shortcut: Shortcut
    val msg: String

    class MsgData<T>(override val shortcut: Shortcut, override val msg: String, val data: T) : Message {
        override fun json(): String {
            return gson.toJson(this, MsgData::class.java)
        }

        companion object {
            fun <T> parse(json: String?, type: TypeToken<T>): T? {
                if(json == null) {
                    Log.e(TAG, "json is null")
                    return null
                }
                return try {
                    Log.d(TAG, json)
                    val msg: T = gson.fromJson(json, type.type)
                    msg
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }

    class Msg(override val shortcut: Shortcut, override val msg: String) : Message {
        override fun json(): String {
            return gson.toJson(this, Msg::class.java)
        }

        companion object {

            fun parse(json: String?): Msg? {
                if(json == null) {
                    Log.e(TAG, "json is null")
                    return null
                }
                return try {
                    Log.d(TAG, json)
                    gson.fromJson(json, Msg::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }


    fun json(): String

    companion object {
        private val gson = CONF.gson
        const val TAG = "Message"
    }
}
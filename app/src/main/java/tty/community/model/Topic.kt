package tty.community.model

import com.google.gson.reflect.TypeToken
import tty.community.util.CONF
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

interface Topic {
    val id: String
    val name: String
    val parent: String
    val introduction: String

    class Outline(
        override val id: String,
        override val name: String,
        override val parent: String,
        override val introduction: String
    ) : Topic

    class Detail(
        override val id: String,
        override val name: String,
        override val parent: String,
        override val introduction: String,
        val picture: String,
        follower: String,
        val admin: String,
        val status: String,
        lastActiveTime: Timestamp
    ) : Topic {
        val follower: ArrayList<String> = CONF.gson.fromJson(follower, object : TypeToken<ArrayList<String>>(){}.type)
        val lastActiveTime: Date = Date(lastActiveTime.time)
    }
}
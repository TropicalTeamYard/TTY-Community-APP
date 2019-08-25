package tty.community.model

import tty.community.pages.activity.LoginActivity.Companion.LoginType
import tty.community.util.CONF
import tty.community.util.Time
import tty.community.util.Util
import java.util.*
import kotlin.collections.HashMap

object Params {
    fun login(account: String, password: String, loginType: LoginType): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["type"] = loginType.key
        map[loginType.key] = account
        map["password"] = Util.getMD5(password)
        map["platform"] = CONF.platform
        return map
    }

    fun autoLogin(user: User): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["id"] = user.id
        map["token"] = user.token
        map["platform"] = CONF.platform
        return map
    }

    fun blogListByTime(time: Date, tag: String, count: Int): HashMap<String, String> {
        val type = Blog.BlogListType.TIME
        val map = HashMap<String, String>()
        map["type"] = type.string
        map[type.string] = Time.getTime(time)
        map["tag"] = tag
        map["count"] = count.toString()
        return map
    }

    fun blogListById(blogId: String, tag: String, count: Int): HashMap<String, String> {
        val type = Blog.BlogListType.ID
        val map = HashMap<String, String>()
        map["type"] = type.string
        map[type.string] = blogId
        map["tag"] = tag
        map["count"] = count.toString()
        return map
    }

    fun changePassword(id: String, oldPassword: String, newPassword: String): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["id"] = id
        map["old"] = Util.getMD5(oldPassword)
        map["new"] = Util.getMD5(newPassword)
        return map
    }

    fun createBlog(user: User, title: String, type: Blog.Companion.BlogType, introduction: String, content: String, topic: Topic.Outline): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["author"] = user.id
        map["token"] = user.token
        map["title"] = title
        map["type"] = type.string()
        map["introduction"] = introduction
        map["content"] = content
        map["topic"] = topic.id
        return map
    }

    fun register(register: User.Register): HashMap<String, String> {
        return hashMapOf(
            Pair("nickname", register.nickname),
            Pair("email", register.email),
            Pair("password", register.password)
        )
    }

    fun privateInfo(user: User): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["id"] = user.id
        map["token"] = user.token
        return map
    }

    fun changeInfo(user: User, info: User.PrivateInfo, nickname: String, email: String, school: String, signature: String): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["id"] = user.id
        map["token"] = user.token
        if (nickname != info.nickname) {
            map["nickname"] = nickname
        }
        if (email != info.email) {
            map["email"] = email
        }
        if (school != info.school) {
            map["school"] = school
        }
        if (signature != info.signature) {
            map["signature"] = signature
        }
        return map
    }

    fun changePortrait(user: User): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["id"] = user.id
        map["token"] = user.token
        return map
    }

    fun similarTopic(name: String): HashMap<String, String> {
        return hashMapOf(Pair("name", name))
    }

}
package tty.community.util

import com.google.gson.GsonBuilder
import com.google.gson.Gson
import tty.community.model.User


object CONF {

//    private const val SERVER = "http://10.0.2.2:8080"
//    private const val SERVER = "http://192.168.123.182:8080"
    private const val SERVER = "http://47.102.200.155:8080"

    open class API{

        open val url = "$SERVER/community/api"
        class User: API() {

            override val url = super.url + "/user"
            val login = "$url/login"
            val autoLogin = "$url/auto_login"
            val register = "$url/register"
            val checkName = "$url/check_name"
            val info = "$url/info"
            val changeInfo = "$url/change_info"
            val changePortrait = "$url/change_portrait"
            val changePassword = "$url/change_password"
            val test = "$url/test"
        }

        class Blog: API() {
            override val url = super.url + "/blog"
            val test = "$url/test"
            val create = "$url/create"
            val get = "$url/get"
            val list = "$url/list"
            val picture = "$url/picture"
            val rawPicture = "$url/picture/raw"
        }

        class Public: API() {
            override val url = super.url + "/public"
            val info = "$url/info"
            val portrait = "$url/portrait"
            val test = "$url/test"
        }

        class Topic: API() {
            override val url = super.url + "/topic"
            val create = "$url/create"
            val find = "$url/find"
            val similar = "$url/similar"
            val list = "$url/list"
            val parent = "$url/parent"
            val child = "$url/child"
            val follow = "$url/follow"
            val unfollow = "$url/unfollow"
            val info = "$url/info"
            val picture = "$url/picture"
            val changePicture = "$url/change_picture"
            val changeInfo = "$url/change_info"
            val test = "$url/test"
        }

        companion object {
            val user = User()
            val public = Public()
            val blog = Blog()
            val topic = Topic()
        }
    }

    const val dateFormat = "yyyy/MM/dd-HH:mm:ss"

    const val platform = "mobile"

    var gson: Gson = GsonBuilder().setDateFormat(dateFormat).create()
}
package tty.community.values

import org.json.JSONObject

object Values{
//    private const val URL = "http://10.0.2.2:8080/community/api/"
    private const val URL = "http://192.168.123.182:8080/community/api/"

    val api = HashMap<String, String>()
    const val errorJson = "{\"shortcut\":\"IE\", \"\":\"Network Error\"}"
    init {
        api["user"] = URL + "user"
        api["blog"] = URL + "blog"
        api["public_user"] = URL + "public/user"
    }
}
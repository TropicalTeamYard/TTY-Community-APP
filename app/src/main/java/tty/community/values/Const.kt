package tty.community.values

object Const{
    
//    private const val SERVER = "http://10.0.2.2:8080"
//    private const val SERVER = "http://192.168.123.182:8080"
    private const val SERVER = "http://47.102.200.155:8080"
    private const val URL = "$SERVER/community/api/"

    val api = HashMap<Route, String>()

    init {
        api[Const.Route.User] = URL + "user"
        api[Const.Route.Blog] = URL + "blog"
        api[Const.Route.PublicUser] = URL + "public/user"
    }

    enum class Route {
        User, Blog, PublicUser
    }

    const val errorJson = "{\"shortcut\":\"IE\", \"msg\":\"Network Error\"}"
}
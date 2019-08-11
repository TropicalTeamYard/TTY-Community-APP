package tty.community.model


class Register(private val nickname: String, private val email: String, private val password: String) {
    fun getMap(): HashMap<String, String> {
        return hashMapOf(Pair("nickname", nickname), Pair("email", email), Pair("password", password))
    }
}

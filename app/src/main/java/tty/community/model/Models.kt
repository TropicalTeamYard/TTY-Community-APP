package tty.community.model

import android.content.ContentValues

object Models {

}

class Login(
    private val id: String?,
    private val nickname: String?,
    private val token: String?,
    private val email: String?
) {
//    private val personalSignature: String? = null
//    val follower = arrayOf<String>()
//    val following = arrayOf<String>()
//    val accountStatus: String? = null
//    val userGroup: String? = null
//    val exp = 0

    fun getValues(): ContentValues? {
        val values = ContentValues()
        if (id.isNullOrEmpty() || nickname.isNullOrEmpty() || token.isNullOrEmpty() || email.isNullOrEmpty()) {
            return null
        }
        values.put("id", id)
        values.put("nickname", nickname)
        values.put("email", email)
        values.put("token", token)
        values.put("status", 1)
        return values
    }

}

enum class Shortcut(name: String) {
    AE("AE"), FE("FE"), UR("UR"), OK("OK"),
    UNE("UNE"), UPE("UPE"),
    TE("TE"),
    BNE("BNE"),
    OTHER("OTHER");

    companion object {
        fun phrase(name: String): Shortcut {
            return when(name) {
                "AE" -> AE
                "FE" -> FE
                "UR" -> UR
                "OK" -> OK
                "TE" -> TE
                "BNE" -> BNE
                "UPE" -> UPE
                "UNE" -> UNE
                "OTHER" -> OTHER
                else -> OTHER
            }
        }
    }
}
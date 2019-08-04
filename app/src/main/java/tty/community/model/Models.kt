package tty.community.model

import android.content.ContentValues

object Models {

}

open class User(
    open val id: String?,
    open val nickname: String?,
    open val token: String?,
    open val email: String?)

class Login (
    override val id: String?,
    override val nickname: String?,
    override val token: String?,
    override val email: String?
): User(id, nickname, token, email) {

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

enum class Shortcut {
    AE, FE, UR, OK,
    UNE, UPE,
    TE,
    BNE,
    OTHER;

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
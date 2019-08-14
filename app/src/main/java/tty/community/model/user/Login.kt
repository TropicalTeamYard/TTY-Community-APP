package tty.community.model.user

import android.content.ContentValues

class Login(
    override val id: String,
    override val nickname: String,
    override val token: String,
    override val email: String
) : User(id, nickname, token, email) {

    fun getValues(): ContentValues? {
        val values = ContentValues()
        if (id.isEmpty() || nickname.isEmpty() || token.isEmpty() || email.isEmpty()) {
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
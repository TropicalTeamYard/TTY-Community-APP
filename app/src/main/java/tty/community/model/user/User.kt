package tty.community.model.user

open class User(
    open val id: String,
    open val nickname: String,
    open val token: String,
    open val email: String
)
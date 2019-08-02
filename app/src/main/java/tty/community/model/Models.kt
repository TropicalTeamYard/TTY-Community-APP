package tty.community.model

object Models {
    enum class Login(name: String) {
        Platform("platform"), LoginType("login_type"), Password("password"), Nickname("nickname"), Id("id")
    }

    enum class AutoLogin {

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
                "OTHER" -> OTHER
                else -> OTHER
            }
        }
    }
}
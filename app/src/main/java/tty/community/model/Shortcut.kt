package tty.community.model

enum class Shortcut {
    AE, // argument mismatch
    UR, // nickname have been registered
    OK, // success
    UNE, // user not found
    UPE, // password error
    TE, // invalid token
    BNE, // blog not found
    AIF, // argument format mismatch
    TNE, // topic/tag not found
    PME, // permission not allowed
    ITE, // internet error
    OTHER;

    companion object {
        fun parse(name: String?): Shortcut {
            return when (name) {
                "AE" -> AE
                "UR" -> UR
                "OK" -> OK
                "UNE" -> UNE
                "UPE" -> UPE
                "TE" -> TE
                "BNE" -> BNE
                "AIF" -> AIF
                "TNE" -> TNE
                "PME" -> PME
                "ITE" -> ITE

                else -> OTHER
            }
        }
    }
}
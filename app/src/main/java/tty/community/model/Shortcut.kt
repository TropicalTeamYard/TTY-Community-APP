package tty.community.model

enum class Shortcut {
    AE, FE, UR, OK,
    UNE, UPE,
    TE,
    BNE,
    AIF,
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
                "AIF" -> AIF
                "OTHER" -> OTHER
                else -> OTHER
            }
        }
    }
}
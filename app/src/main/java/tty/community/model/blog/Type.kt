package tty.community.model.blog

enum class Type {
    Short, Pro, Other;

    companion object {
        val Type.value: Int
            get() {
                return when (this) {
                    Short -> 0
                    Pro -> 1
                    Other -> -1
                }
            }

        val String.parse: Type
            get() {
                return when (this) {
                    "0", "Short" -> {
                        Short
                    }

                    "1", "Pro" -> {
                        Pro
                    }

                    else -> {
                        Other
                    }
                }
            }


    }
}
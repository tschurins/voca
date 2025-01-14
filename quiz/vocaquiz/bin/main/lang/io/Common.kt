package jal.voca.lang.io

inline fun <reified T : kotlin.Enum<T>> valueOf(type: String?): T? {
    return java.lang.Enum.valueOf(T::class.java, type)
}


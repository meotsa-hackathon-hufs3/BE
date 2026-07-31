package com.meotsa.user.entity

enum class Role(
    val key: String,
) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    ;

    companion object {
        fun of(key: String): Role =
            entries.firstOrNull { it.key == key }
                ?: throw IllegalArgumentException("존재하지 않는 권한입니다: $key")
    }
}

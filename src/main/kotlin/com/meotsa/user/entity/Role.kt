package com.meotsa.user.entity

enum class Role(
    val key: String,
) {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    ;

    companion object {
        fun of(key: String): Role = entries.first { it.key == key }
    }
}

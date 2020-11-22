package org.engrave.packup.data.account

data class PackupAccount(
    /* email */
    val userName: String,
    val password: String,
    val universityAbbr: String,

    /* auto generate according to hashcode of nickname if not specified. */
    val avatarUrl: String?,
    val nickname: String,

    val universityCredential: UniversityCredential?
)
package com.evandhardspace.yacca.data.user

interface UserDataSource {

    suspend fun getUserByUsername(username: String): User?

    suspend fun insertUser(user: User): Boolean
}

// todo migrate to db
class InMemoryUserDataSource : UserDataSource {
    private val users = mutableListOf<User>()

    override suspend fun getUserByUsername(username: String): User? =
        users.firstOrNull { it.username == username }

    override suspend fun insertUser(user: User): Boolean {
        if(users.any { it.username == user.username }) return false
        users += user
        return true
    }
}
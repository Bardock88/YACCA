package com.evandhardspace.yacca.data.user

import com.evandhardspace.yacca.db.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

interface UserDataSource {
    suspend fun getUser(email: String): User?
    suspend fun getUser(id: UUID): User?
    suspend fun insertUser(user: User): Boolean
    suspend fun deleteUser(deleteUserId: UUID): Boolean
}

class DatabaseUserDataSource : UserDataSource {
    override suspend fun getUser(email: String): User? = try {
        transaction {
            Users.select {
                Users.email eq email
            }.first().let {
                User(
                    id = it[Users.id],
                    email = it[Users.email],
                    hashedPassword = it[Users.hashedPassword],
                    salt = it[Users.salt],
                )
            }
        }
    } catch (e: ExposedSQLException) {
        println("Error getting user: ${e.localizedMessage}") // todo add logging
        null
    }

    override suspend fun getUser(id: UUID): User? = try {
        transaction {
            Users.select { Users.id eq id }.first().let {
                User(
                    id = it[Users.id],
                    email = it[Users.email],
                    hashedPassword = it[Users.hashedPassword],
                    salt = it[Users.salt],
                )
            }
        }
    } catch (e: ExposedSQLException) {
        println("Error getting user: ${e.localizedMessage}")
        null
    }

    override suspend fun insertUser(user: User): Boolean = try {
        transaction {
            val existingUser = Users.select { Users.email eq user.email }.singleOrNull()
            if (existingUser != null) {
                return@transaction false
            }

            Users.insert {
                it[id] = user.id
                it[email] = user.email
                it[hashedPassword] = user.hashedPassword
                it[salt] = user.salt
            }
            true
        }
    } catch (e: ExposedSQLException) {
        println("Error adding user: ${e.localizedMessage}")
        false
    }

    override suspend fun deleteUser(deleteUserId: UUID): Boolean = try {
        transaction {
            // Remove all favorites for the user
            FavoriteCurrencies.deleteWhere { userId eq deleteUserId } // todo check if it is necessary
            Users.deleteWhere { id eq deleteUserId }
        }
        true
    } catch (e: ExposedSQLException) {
        println("Error removing user: ${e.localizedMessage}")
        false
    }
}

class InMemoryUserDataSource : UserDataSource {
    private val usersById = ConcurrentHashMap<UUID, User>()
    private val usersByEmail = ConcurrentHashMap<String, User>()

    override suspend fun getUser(email: String): User? {
        return usersByEmail[email]
    }

    override suspend fun getUser(id: UUID): User? {
        return usersById[id]
    }

    override suspend fun insertUser(user: User): Boolean {
        if (usersByEmail.containsKey(user.email)) {
            return false
        }

        usersById[user.id] = user
        usersByEmail[user.email] = user
        return true
    }

    override suspend fun deleteUser(deleteUserId: UUID): Boolean {
        val user = usersById.remove(deleteUserId) ?: return false
        usersByEmail.remove(user.email)
        return true
    }
}
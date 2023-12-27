package com.example.traveldanang

class User(id: String, username: String, password: String, phone: String, email: String, avatar: String, role: String) {
    private val id: String = id
    private val username: String = username
    private val password: String = password
    private val phone: String = phone
    private val email: String = email
    private val avatar: String = avatar
    private val role: String = role
    fun getId() : String
    {
        return id
    }

    fun getUsername(): String
    {
        return username
    }

    fun getPassword(): String
    {
        return password
    }

    fun getPhone(): String
    {
        return phone
    }

    fun getEmail(): String
    {
        return email
    }

    fun getAvatar(): String
    {
        return avatar
    }

    fun getRole(): String {
        return role
    }
}
package com.example.messengerkotlin

import java.io.Serializable

class User() : Serializable{
    var priority = 0
    lateinit var id: String
    lateinit var name: String
    lateinit var surname: String
    var age: Int = 0
    var online: Boolean = false
    lateinit var email: String
    var userInfo = ""
    var countUnreadMessages = 0

    constructor(
        id: String,
        name: String,
        surname: String,
        age: Int,
        isOnline: Boolean,
        email: String
    ) : this() {
        this.id = id
        this.name = name
        this.surname = surname
        this.age = age
        this.online = isOnline
        this.email = email
    }


}
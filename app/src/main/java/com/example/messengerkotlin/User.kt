package com.example.messengerkotlin

class User () {
    lateinit var id: String
    lateinit var name: String
    lateinit var surname: String
    var age: Int = 0
    var isOnline: Boolean = false
    lateinit var email: String

    constructor(id: String, name: String, surname: String, age: Int, isOnline: Boolean, email: String) : this() {
        this.id = id
        this.name = name
        this.surname = surname
        this.age = age
        this.isOnline = isOnline
        this.email = email
    }
}
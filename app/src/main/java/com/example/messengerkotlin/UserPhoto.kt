package com.example.messengerkotlin

class UserPhoto () {
    lateinit var id: String
    lateinit var name: String

    constructor(id: String, name: String) : this() {
        this.id = id
        this.name = name
    }
}
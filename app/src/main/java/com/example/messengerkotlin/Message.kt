package com.example.messengerkotlin

class Message () {
    lateinit var text: String
    lateinit var myId: String
    lateinit var otherId: String

    constructor(text: String, myId: String, otherId: String) :this () {
        this.text = text
        this.myId = myId
        this.otherId = otherId
    }
}
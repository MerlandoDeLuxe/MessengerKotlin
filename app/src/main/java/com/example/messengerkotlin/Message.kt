package com.example.messengerkotlin

class Message () {
    lateinit var myId: String
    lateinit var otherId: String
    lateinit var text: String

    constructor(myId: String, otherId: String, text: String) :this () {
        this.myId = myId
        this.otherId = otherId
        this.text = text
    }
}
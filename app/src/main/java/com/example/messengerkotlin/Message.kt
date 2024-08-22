package com.example.messengerkotlin

class Message() {
    lateinit var senderId: String
    lateinit var recieverId: String
    lateinit var text: String
    var read = "false"

    constructor(
        senderId: String,
        recieverId: String,
        text: String,
        read: String
    ) : this() {
        this.recieverId = recieverId
        this.senderId = senderId
        this.text = text
        this.read = read
    }
}
package com.example.codelabx.models

class AuthResponse(){
     lateinit var message : String
     lateinit var token :String

    constructor(msg : String , tkn :String) : this(){
        this.message = msg
        this.token = tkn
    }
}
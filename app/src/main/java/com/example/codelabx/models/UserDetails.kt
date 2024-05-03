package com.example.codelabx.models

class UserDetails() {
    private lateinit var username : String
    private lateinit var  password : String

    constructor(name : String , pass : String) : this(){
        this.username = name
        this.password = pass
    }
}
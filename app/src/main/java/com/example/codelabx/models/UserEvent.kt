package com.example.codelabx.models

class UserEvent() {
    private lateinit var username : String
    private lateinit var language : String
    private lateinit var code : String
    private lateinit var filename : String

    constructor(name : String, lang :String, code : String , fileName : String) : this(){
        this.username = name
        this.language = lang
        this.code = code
        this.filename = fileName
    }
}
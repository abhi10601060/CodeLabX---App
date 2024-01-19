package com.example.codelabx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner

class MainActivity : AppCompatActivity() {

    lateinit var spinner : Spinner
    lateinit var codeEditor : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createView()

    }

    private fun createView() {
        spinner = findViewById(R.id.languages_spinner)
        codeEditor = findViewById(R.id.code_editor)

        setSpinner()
    }

    private fun setSpinner() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.languages , android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}
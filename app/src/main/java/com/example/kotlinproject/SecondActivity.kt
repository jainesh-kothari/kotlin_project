package com.example.kotlinproject

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_second)

        val txt_id = findViewById<TextView>(R.id.textview_id)
        val txt_name = findViewById<Button>(R.id.button_second)

        var bundle :Bundle ?=intent.extras

        txt_id.text = bundle!!.getString("id")
        txt_name.text = bundle!!.getString("name")


    }

}
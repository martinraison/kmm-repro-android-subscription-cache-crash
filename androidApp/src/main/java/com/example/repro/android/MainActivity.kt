package com.example.repro.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.repro.Greeting
import android.widget.TextView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = "pending"
        GlobalScope.launch {
            Greeting().greeting().collect {
                runOnUiThread {
                    tv.text = "[subscription] $it"
                }
            }
        }
    }
}

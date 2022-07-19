package com.nando.codegen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nando.codegen.enums.ExcellencyLevel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ex = ExcellencyLevel.Excellent
    }
}
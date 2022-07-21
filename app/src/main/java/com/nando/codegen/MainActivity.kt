package com.nando.codegen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nando.codegen.generated.models.AddUserRequestModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val addUserRequestModel: AddUserRequestModel
    }
}
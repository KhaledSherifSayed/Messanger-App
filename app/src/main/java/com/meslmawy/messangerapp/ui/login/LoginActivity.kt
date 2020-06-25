package com.meslmawy.messangerapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.meslmawy.messangerapp.R
import com.meslmawy.messangerapp.ui.home.HomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        else
        setContentView(R.layout.activity_login)
    }

}
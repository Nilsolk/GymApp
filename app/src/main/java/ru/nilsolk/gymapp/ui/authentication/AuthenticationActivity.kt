package ru.nilsolk.gymapp.ui.authentication


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.nilsolk.gymapp.databinding.ActivityAuthenticationBinding

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var authenticationBinding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticationBinding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(authenticationBinding.root)

    }


}
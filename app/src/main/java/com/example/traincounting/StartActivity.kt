package com.example.traincounting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.traincounting.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Проверяем, авторизован ли пользователь
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) {
            // Пользователь не авторизован, переходим к LoginActivity
            goToActivity(LoginActivity::class.java)
            finish()
        }

        val username = (sharedPreferences.getString("username", null)) ?:
            getString(R.string.player_name)

        binding.startText.text = getString(R.string.start_text, username)


        binding.startButton.setOnClickListener {
            goToActivity(MainActivity::class.java)
        }

        binding.logoutButton.setOnClickListener {
            sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()

            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    private fun goToActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)

    }
}

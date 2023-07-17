package com.example.storyapp.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.store.DataStorePreferences
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.main.MainActivity
import com.example.storyapp.utils.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(DataStorePreferences.getInstance(dataStore), this)
        )[LoginViewModel::class.java]

        viewModel.let {
            it.loginResult.observe(this) { login ->
                viewModel.saveUser(
                    login.loginResult?.userId ?: "",
                    login.loginResult?.name ?: "",
                    login.loginResult?.token ?: ""
                )
            }
            it.message.observe(this) { message ->
                if (message == "200") {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            it.error.observe(this) { error ->
                if (error == "400") {
                    Toast.makeText(this, "Email or Password is wrong", Toast.LENGTH_SHORT).show()
                }
                if (error == "401") {
                    Toast.makeText(this, "User Not Found", Toast.LENGTH_SHORT).show()
                }

            }
            it.isLoading.observe(this) { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() -> binding.edLoginEmail.error = "Email is required"
                password.isEmpty() -> binding.edLoginPassword.error = "Password is required"
                password.length < 8 -> binding.edLoginPassword.error =
                    "Password must be at least 8 characters"
                else -> {
                    viewModel.login(email, password)
                }
            }
        }
        playAnimation()
    }

    private fun playAnimation() {
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(300)
        val title = ObjectAnimator.ofFloat(binding.loginTitle, View.ALPHA, 1f).setDuration(300)
        val subtitle =
            ObjectAnimator.ofFloat(binding.loginSubtitle, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(300)
        val emailLayout =
            ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(300)
        val password =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(300)
        val passwordLayout =
            ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f).setDuration(300)
        AnimatorSet().apply {
            playSequentially(title, subtitle, email, emailLayout, password, passwordLayout, login)
            start()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.root.removeAllViewsInLayout()
    }

}
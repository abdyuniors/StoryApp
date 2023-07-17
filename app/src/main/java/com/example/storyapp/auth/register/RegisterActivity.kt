package com.example.storyapp.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.auth.login.LoginActivity
import com.example.storyapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[RegisterViewModel::class.java]

        viewModel.error.observe(this) {
            if (it == "400") {
                Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        viewModel.message.observe(this) {
            if (it == "201") {
                Toast.makeText(this, "Register Success", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            when {

                name.isEmpty() -> {
                    binding.edRegisterName.error = "Name cannot be empty"
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = "Email cannot be empty"
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.error = "Password cannot be empty"
                }
                password.length < 8 -> {
                    binding.edRegisterPassword.error = "Password must be more than 8 characters"
                }
                else -> {
                    viewModel.register(name, email, password)
                }
            }
        }
        playAnimation()
    }

    private fun playAnimation() {
        val register =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(300)
        val title =
            ObjectAnimator.ofFloat(binding.registerTitle, View.ALPHA, 1f).setDuration(300)
        val subtitle =
            ObjectAnimator.ofFloat(binding.registerSubtitle, View.ALPHA, 1f).setDuration(300)
        val name = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f)
            .setDuration(300)
        val nameLayout = ObjectAnimator.ofFloat(binding.etName, View.ALPHA, 1f)
            .setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f)
            .setDuration(300)
        val emailLayout = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f)
            .setDuration(300)
        val password = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f)
            .setDuration(300)
        val passwordLayout = ObjectAnimator.ofFloat(binding.etPassword, View.ALPHA, 1f)
            .setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                title,
                subtitle,
                name,
                nameLayout,
                email,
                emailLayout,
                password,
                passwordLayout,
                register
            )
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
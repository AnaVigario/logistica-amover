package com.example.amover

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.amover.databinding.ActivityLoginBinding
import com.example.amover.ui.database.DBHelper

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DBHelper(this)


        binding.buttonLogin.setOnClickListener {

            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()
            val logged = binding.checkboxLogged.isChecked

            if (username.isNotEmpty() && password.isNotEmpty()) {
                if (db.login(username, password)) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }else{
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.login_error),
                        Toast.LENGTH_SHORT).show()
                    binding.editTextUsername.setText("")
                    binding.editTextPassword.setText("")
                }
            }else{
                Toast.makeText(
                    applicationContext,
                    getString(R.string.please_insert_all_required_fields),
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

    }
}
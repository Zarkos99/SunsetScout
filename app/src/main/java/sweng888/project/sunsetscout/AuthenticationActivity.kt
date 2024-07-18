package sweng888.project.sunsetscout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import sweng888.project.sunsetscout.geo.GeoMapActivity

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.authentication_layout)

        auth = FirebaseAuth.getInstance()

        val usernameEditTextView = findViewById<EditText>(R.id.username_field)
        val passwordEditTextView = findViewById<EditText>(R.id.password_field)
        val loginButtonView = findViewById<Button>(R.id.login_button)
        val registerButtonView = findViewById<Button>(R.id.register_button)

        loginButtonView.setOnClickListener {
            val username = usernameEditTextView.text.toString()
            val password = passwordEditTextView.text.toString()

            // Error handling for empty username
            if (username.isEmpty()) {
                // Display error message
                Toast.makeText(
                    this@AuthenticationActivity,
                    "Please input your username",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Error handling for empty password
            if (password.isEmpty()) {
                // Display error message
                Toast.makeText(
                    this@AuthenticationActivity,
                    "Please input your password",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Firebase Authentication for user login
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, display Geo View - enlarged map
                        val intent = Intent(this@AuthenticationActivity, GeoMapActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user
                        Toast.makeText(
                            this@AuthenticationActivity,
                            "Authentication failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        registerButtonView.setOnClickListener {
            val username = usernameEditTextView.text.toString()
            val password = passwordEditTextView.text.toString()

            // Error handling for empty username
            if (username.isEmpty()) {
                // Display error message
                Toast.makeText(
                    this@AuthenticationActivity,
                    "Please input your username",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Error handling for empty password
            if (password.isEmpty()) {
                // Display error message
                Toast.makeText(
                    this@AuthenticationActivity,
                    "Please input your password",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Firebase Authentication for user registration
            auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration success, display Geo View - enlarged map
                        val intent = Intent(this@AuthenticationActivity, GeoMapActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If registration fails, display a message to the user
                        Toast.makeText(
                            this@AuthenticationActivity,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}

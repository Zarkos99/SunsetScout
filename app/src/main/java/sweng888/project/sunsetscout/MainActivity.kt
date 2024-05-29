package sweng888.project.sunsetscout

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.authentication_layout)

        val username_edit_text_view = findViewById<EditText>(R.id.username_field)
        val password_edit_text_view = findViewById<EditText>(R.id.password_field)
        val login_button_view = findViewById<Button>(R.id.login_button)

        login_button_view.setOnClickListener {
            val username = username_edit_text_view.getText().toString()
            val password = password_edit_text_view.getText().toString()

            // Error handling for empty username
            if (username.isEmpty()) {
                //Display error message
                Toast.makeText(
                    this@MainActivity,
                    "Please input your username",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Error handling for empty password
            if (password.isEmpty()) {
                //Display error message
                Toast.makeText(
                    this@MainActivity,
                    "Please input your password",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            //TODO: if invalid username and password, show toast that username or password is invalid
            //TODO: if valid username and password, display Geo View - enlarged map
        }
    }
}

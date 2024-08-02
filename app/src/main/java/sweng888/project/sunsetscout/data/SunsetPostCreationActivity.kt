package sweng888.project.sunsetscout.data

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import sweng888.project.sunsetscout.R
import sweng888.project.sunsetscout.database.uploadImageAndCreateNewPost
import java.time.Instant
import java.time.format.DateTimeFormatter


class SunsetPostCreationActivity : AppCompatActivity() {

    private lateinit var m_title_input_edit_text: EditText
    private lateinit var m_latitude_input_edit_text: EditText
    private lateinit var m_longitude_input_edit_text: EditText
    private lateinit var m_description_input_edit_text: EditText
    private lateinit var m_select_image_intent: ActivityResultLauncher<String>
    private lateinit var m_new_image_uri: Uri

    private var m_image_updated = false

    private val FAKE_TITLE = "Point Nemo"
    private val FAKE_LATITUDE = -48.876667
    private val FAKE_LONGITUDE = -123.393333
    private val FAKE_DESCRIPTION = "Not sure how I got here but I managed to snap a quick pic."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.sunset_creation_layout)

        m_title_input_edit_text = findViewById(R.id.title_input_field)
        m_latitude_input_edit_text = findViewById(R.id.latitude_input_field)
        m_longitude_input_edit_text = findViewById(R.id.longitude_input_field)
        m_description_input_edit_text = findViewById(R.id.description_input_field)
        val new_sunset_image_view = findViewById<ImageView>(R.id.add_new_image_view)
        val cancel_button_view = findViewById<Button>(R.id.cancel_button)
        val create_new_post_button_view = findViewById<Button>(R.id.create_new_post_button)

        initializeEditTexts()

        m_select_image_intent = registerForActivityResult(ActivityResultContracts.GetContent())
        { uri ->
            if (uri != null) {
                new_sunset_image_view.setImageURI(uri)
                m_new_image_uri = uri
                m_image_updated = true
            }
        }

        new_sunset_image_view.setOnClickListener {
            m_select_image_intent.launch("image/*")
        }

        cancel_button_view.setOnClickListener {
            finish()
        }

        create_new_post_button_view.setOnClickListener {
            handleCreateNewPost()
        }
    }

    fun handleCreateNewPost() {
        var new_title = m_title_input_edit_text.text.toString().trim()
        var new_latitude = m_latitude_input_edit_text.text.toString().trim()
        var new_longitude = m_longitude_input_edit_text.text.toString().trim()
        var new_description = m_description_input_edit_text.text.toString().trim()

        var error = false

        // Default to fake values if fields are empty
        if (new_title.isEmpty()) {
            new_title = FAKE_TITLE
        }
        if (new_latitude.isEmpty()) {
            new_latitude = FAKE_LATITUDE.toString()
        }
        if (new_longitude.isEmpty()) {
            new_longitude = FAKE_LONGITUDE.toString()
        }
        if (new_description.isEmpty()) {
            new_description = FAKE_DESCRIPTION
        }

        // Error handling for invalid Latitude
        try {
            new_latitude.toDouble()
        } catch (e: Exception) {
            // Display error
            m_latitude_input_edit_text.setHint("Latitude must a valid number (ex. $FAKE_LATITUDE)")
            m_latitude_input_edit_text.setHintTextColor(Color.RED)
            error = true
        }

        // Error handling for invalid Longitude
        try {
            new_longitude.toDouble()
        } catch (e: Exception) {
            // Display error
            m_longitude_input_edit_text.setHint("Longitude must a valid number (ex. $FAKE_LONGITUDE)")
            m_longitude_input_edit_text.setHintTextColor(Color.RED)
            error = true
        }

        if (!m_image_updated) {
            //Display error message
            Toast.makeText(
                this,
                "Please select an image for your post",
                Toast.LENGTH_LONG
            ).show()
            error = true
        }

        if (error) {
            return
        }

        //Create SunsetPost with real or fake data if empty
        val sunset_data = SunsetData(
            title = new_title,
            latitude = new_latitude,
            longitude = new_longitude,
            post_time = DateTimeFormatter.ISO_INSTANT.format(
                Instant.now()
            ),
            description = new_description
        )

        uploadImageAndCreateNewPost(sunset_data, m_new_image_uri)
        finish()
    }

    fun initializeEditTexts() {
        m_title_input_edit_text.hint = FAKE_TITLE
        m_latitude_input_edit_text.hint = FAKE_LATITUDE.toString()
        m_longitude_input_edit_text.hint = FAKE_LONGITUDE.toString()
        m_description_input_edit_text.hint = FAKE_DESCRIPTION
    }
}
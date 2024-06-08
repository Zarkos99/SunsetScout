package sweng888.project.sunsetscout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class GeoMapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.geo_map_layout)

        val search_edit_text_view = findViewById<EditText>(R.id.search_field)
        val preferences_button_view = findViewById<Button>(R.id.preferences_button)
        val gallery_button_view = findViewById<Button>(R.id.gallery_button)

        //TODO: Hook up search bar

        preferences_button_view.setOnClickListener {
            val intent = Intent(this@GeoMapActivity, PreferencesActivity::class.java)
            startActivity(intent)
        }
        
        gallery_button_view.setOnClickListener {
            val intent = Intent(this@GeoMapActivity, GalleryActivity::class.java)
            startActivity(intent)
        }
    }
}
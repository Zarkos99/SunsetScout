package sweng888.project.sunsetscout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class GalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.gallery_layout)

        val preferences_button_view = findViewById<Button>(R.id.preferences_button)
        val geo_map_button_view = findViewById<Button>(R.id.geo_map_button)


        preferences_button_view.setOnClickListener {
            val intent = Intent(this@GalleryActivity, PreferencesActivity::class.java)
            startActivity(intent)
        }
        geo_map_button_view.setOnClickListener {
            val intent = Intent(this@GalleryActivity, GeoMapActivity::class.java)
            startActivity(intent)
        }
    }
}
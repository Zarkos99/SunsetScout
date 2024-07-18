package sweng888.project.sunsetscout.preferences

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import sweng888.project.sunsetscout.gallery.GalleryActivity
import sweng888.project.sunsetscout.geo.GeoMapActivity
import sweng888.project.sunsetscout.R

class PreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.preferences_layout)

        val preferences = loadPreferenceOptions(this@PreferencesActivity)
        val adapter = PreferencesListAdapter(this@PreferencesActivity, preferences)

        val preferences_list_view = findViewById<ListView>(R.id.preferences_list_view)
        preferences_list_view.adapter = adapter

        val geo_map_button_view = findViewById<Button>(R.id.geo_map_button)
        val gallery_button_view = findViewById<Button>(R.id.gallery_button)


        geo_map_button_view.setOnClickListener {
            val intent = Intent(this@PreferencesActivity, GeoMapActivity::class.java)
            startActivity(intent)
        }
        gallery_button_view.setOnClickListener {
            val intent = Intent(this@PreferencesActivity, GalleryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadPreferenceOptions(context: Context): ArrayList<String> {
        val classes = ArrayList<String>()

        for (i in 1..8) {
            classes.add("Preference $i")
        }

        return classes
    }
}
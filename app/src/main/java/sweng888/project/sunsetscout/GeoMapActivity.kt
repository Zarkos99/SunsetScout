package sweng888.project.sunsetscout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.time.Instant
import java.time.format.DateTimeFormatter

class GeoMapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.geo_map_layout)

        val search_edit_text_view = findViewById<EditText>(R.id.search_field)
        val preferences_button_view = findViewById<Button>(R.id.preferences_button)
        val gallery_button_view = findViewById<Button>(R.id.gallery_button)

        val database_helper = UserDatabaseHelper(this)
        populateDatabaseWithFakeUsers(database_helper)

        var user =
            database_helper.getUser("JohnDoe123")

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

    fun populateDatabaseWithFakeUsers(db_helper: UserDatabaseHelper) {
//        var fake_sunset_1 = SunsetData(
//            "55.751244",
//            "37.618423",
//            DateTimeFormatter.ISO_INSTANT.format(
//                Instant.now()
//            ),
//            "The sunset here was incredible but the cold was biting, travel at your own risk!"
//        )
//        var fake_sunset_2 = SunsetData(
//            "-73.0500",
//            "-13.4167",
//            DateTimeFormatter.ISO_INSTANT.format(
//                Instant.now()
//            ),
//            "The sunset here was so beautiful... Photo is a bit blurry because I was being chased by a polar bear, so much fun!"
//        )
//        var fake_sunset_3 = SunsetData(
//            "-48.876667",
//            "-123.393333",
//            DateTimeFormatter.ISO_INSTANT.format(
//                Instant.now()
//            ),
//            "Not sure how I got here but I managed to snap a quick pic."
//        )

        var new_user =
            User(
                "JohnDoe123",
                "johndoeiscool@gmail.com",
                "I am John Doe. Fear me."
            )
        db_helper.addUserToDatabase(new_user)

        new_user =
            User(
                "C00lK1D",
                "coolkid24@hotmail.com",
                "Coolest kid on the block"
            )
        db_helper.addUserToDatabase(new_user)

        new_user =
            User(
                "RemoteWorker",
                "worksremote@gmail.com",
                "Working remotely"
            )
        db_helper.addUserToDatabase(new_user)
    }
}
package sweng888.project.sunsetscout

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.Instant
import java.time.format.DateTimeFormatter


class GalleryActivity : AppCompatActivity() {

    // this is the action code we use in our intent,
    // this way we know we're looking at the response from our own action
    private val SELECT_PHOTO = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.gallery_layout)

        val public_username_text_view = findViewById<TextView>(R.id.public_username)
        val num_posts_text_view = findViewById<TextView>(R.id.num_posts)
        val sunsets_recycler_view = findViewById<RecyclerView>(R.id.gallery_sunsets)
        val add_sunset_button_view = findViewById<FloatingActionButton>(R.id.add_sunset_button)
        val preferences_button_view = findViewById<Button>(R.id.preferences_button)
        val geo_map_button_view = findViewById<Button>(R.id.geo_map_button)

        val database_helper = UserDatabaseHelper(this)
        var user =
            database_helper.getUser("JohnDoe123")

        val sunset_list_adaptor = GallerySunsetPostsAdapter(this, user.posts)
        sunsets_recycler_view.adapter = sunset_list_adaptor
        val layout_manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        sunsets_recycler_view.layoutManager = layout_manager

        public_username_text_view.setText(user.username)
        num_posts_text_view.setText(
            resources.getString(
                R.string.num_posts,
                user.posts.size
            )
        )

        // Allow user to add sunsets
        val selectImageIntent = registerForActivityResult(ActivityResultContracts.GetContent())
        { uri ->

            var fake_sunset_data = SunsetData(
                "-48.876667",
                "-123.393333",
                DateTimeFormatter.ISO_INSTANT.format(
                    Instant.now()
                ),
                "Not sure how I got here but I managed to snap a quick pic.",
                uri
            )

            user.posts.add(fake_sunset_data)
            database_helper.updateUserPosts(user.username, user.posts)
            sunset_list_adaptor.notifyDataSetChanged()
        }
        add_sunset_button_view.setOnClickListener {
            selectImageIntent.launch("image/*")
        }

        // Navigation button click listeners
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
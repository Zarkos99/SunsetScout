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
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.Instant
import java.time.format.DateTimeFormatter


class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.gallery_layout)

        val public_username_text_view = findViewById<TextView>(R.id.public_username)
        val num_posts_text_view = findViewById<TextView>(R.id.num_posts)
        val biography_text_view = findViewById<TextView>(R.id.biography)
        val sunsets_recycler_view = findViewById<RecyclerView>(R.id.gallery_sunsets)
        val add_or_remove_sunset_button_view = findViewById<Button>(R.id.add_sunset_button)
        //Navigation buttons
        val preferences_button_view = findViewById<Button>(R.id.preferences_button)
        val geo_map_button_view = findViewById<Button>(R.id.geo_map_button)

        val database_helper = UserDatabaseHelper(this)
        var user =
            database_helper.getUser("JohnDoe123")

        // Initialize recyclerview adaptor
        val sunset_list_adaptor = GallerySunsetPostsAdapter(this, database_helper, user.username)
        sunset_list_adaptor.registerItemSelectedCallback {
            val selected_sunsets = sunset_list_adaptor.getSelectedSunsets()
            if (selected_sunsets.size > 0) {
                add_or_remove_sunset_button_view.text = "-"
            } else {
                add_or_remove_sunset_button_view.text = "+"
            }
        }
        sunsets_recycler_view.adapter = sunset_list_adaptor

        // Initialize FlexBox Layout Manager for recyclerview to allow wrapping items to next line
        val layout_manager = FlexboxLayoutManager(this)
        layout_manager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            flexWrap = FlexWrap.WRAP
        }
        sunsets_recycler_view.layoutManager = layout_manager

        // Fill user data text fields
        public_username_text_view.setText(user.username)
        biography_text_view.setText(user.biography)
        num_posts_text_view.setText(
            resources.getString(
                R.string.num_posts,
                user.posts.size
            )
        )

        // Allow user to add sunsets
        val selectImageIntent = registerForActivityResult(ActivityResultContracts.GetContent())
        { uri ->
            //TODO: Replace this fake data with new activity to input this data
            var fake_sunset_data = SunsetData(
                latitude = "-48.876667",
                longitude = "-123.393333",
                post_time = DateTimeFormatter.ISO_INSTANT.format(
                    Instant.now()
                ),
                description = "Not sure how I got here but I managed to snap a quick pic.",
                image_uri = uri
            )

            user.posts.add(fake_sunset_data)
            database_helper.updateUserPosts(user.username, user.posts)
            sunset_list_adaptor.notifyDataSetChanged()
        }
        add_or_remove_sunset_button_view.setOnClickListener {
            val selected_sunsets = sunset_list_adaptor.getSelectedSunsets()
            if (selected_sunsets.size > 0) {
                //Change add sunset button to remove sunset functionality when sunsets are selected
                database_helper.deleteSelectedSunsets(user.username, selected_sunsets)
                sunset_list_adaptor.clearSelectedSunsets()
                sunset_list_adaptor.notifyDataSetChanged()
            } else {
                selectImageIntent.launch("image/*")
            }
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
package sweng888.project.sunsetscout.gallery

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import sweng888.project.sunsetscout.R
import sweng888.project.sunsetscout.data.SunsetPostCreationActivity
import sweng888.project.sunsetscout.database.*
import sweng888.project.sunsetscout.geo.GeoMapActivity
import sweng888.project.sunsetscout.preferences.PreferencesActivity


class GalleryActivity : AppCompatActivity() {

    private lateinit var m_sunset_list_adaptor: GallerySunsetPostsAdapter
    private lateinit var m_select_profile_image_intent: ActivityResultLauncher<String>
    private lateinit var m_profile_image_view: ImageView
    private lateinit var m_firebase_data_service: FirebaseDataService
    private var m_bound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService().  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as FirebaseDataService.LocalBinder
            m_firebase_data_service = binder.getService()
            m_bound = true

            initializeRecyclerViewLayoutManager()
            initializeRecyclerViewAdapter()
            // Initial population of user info with existent data
            populateTextViewsWithUserInfo()
            populateProfileImage()

            // Populate user info on future updates
            m_firebase_data_service.registerCallback {
                populateTextViewsWithUserInfo()
                m_sunset_list_adaptor.unselectDeletedSunsets()
                m_sunset_list_adaptor.notifyDataSetChanged()

                populateProfileImage()
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            m_bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.gallery_layout)
        // Bind to LocalService.
        Intent(this, FirebaseDataService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        // Create intent to open device storage for image selection
        m_select_profile_image_intent =
            registerForActivityResult(ActivityResultContracts.GetContent())
            { uri ->
                if (uri != null) {
                    uploadProfileImage(uri)
                    m_profile_image_view.setImageURI(uri)
                }
            }

        //Navigation buttons
        val preferences_button_view = findViewById<Button>(R.id.preferences_button)
        val geo_map_button_view = findViewById<Button>(R.id.geo_map_button)
        m_profile_image_view = findViewById<ImageView>(R.id.profile_picture_view)

        // Profile Image selection listener
        m_profile_image_view.setOnClickListener {
            m_select_profile_image_intent.launch("image/*")
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


    override fun onStop() {
        super.onStop()
        if (m_bound) {
            unbindService(connection)
            m_bound = false
        }
    }

    fun initializeRecyclerViewAdapter() {
        val add_or_remove_sunset_button_view = findViewById<Button>(R.id.add_sunset_button)
        val sunsets_recycler_view = findViewById<RecyclerView>(R.id.gallery_sunsets)
        // Initialize recyclerview adaptor
        m_sunset_list_adaptor = GallerySunsetPostsAdapter(this, m_firebase_data_service)
        m_sunset_list_adaptor.registerItemSelectedCallback {
            val selected_sunsets = m_sunset_list_adaptor.getSelectedSunsets()
            // If some sunsets are selected and button is pressed we want to show option to
            // delete them, else show option to add sunset
            if (selected_sunsets.size > 0) {
                add_or_remove_sunset_button_view.text = "-"
            } else {
                add_or_remove_sunset_button_view.text = "+"
            }
        }
        sunsets_recycler_view.adapter = m_sunset_list_adaptor


        // Setup listener for image upload button
        add_or_remove_sunset_button_view.setOnClickListener {
            val selected_sunsets = m_sunset_list_adaptor.getSelectedSunsets()
            if (selected_sunsets.size > 0) {
                //Change add sunset button to remove sunset functionality when sunsets are selected
                deleteImagesAndPosts(selected_sunsets)
            } else {
                val intent = Intent(this, SunsetPostCreationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    fun initializeRecyclerViewLayoutManager() {
        val sunsets_recycler_view = findViewById<RecyclerView>(R.id.gallery_sunsets)
        // Initialize FlexBox Layout Manager for recyclerview to allow wrapping items to next line
        val layout_manager = FlexboxLayoutManager(this)
        layout_manager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            flexWrap = FlexWrap.WRAP
        }
        sunsets_recycler_view.layoutManager = layout_manager
    }

    /**
     * Dynamically obtains stored drawable images by name
     */
    private fun getImage(ImageName: String?): Drawable {
        return this@GalleryActivity.resources.getDrawable(
            this@GalleryActivity.resources.getIdentifier(
                ImageName,
                "drawable",
                this@GalleryActivity.packageName
            )
        )
    }

    fun populateProfileImage() {
        val current_user_data = m_firebase_data_service.current_user_data
        if (current_user_data?.profile_image_path.isNullOrEmpty()) {
            m_profile_image_view.setImageDrawable(getImage("default_profile_pic"))
        } else {
            loadCloudStoredImageIntoImageView(
                this@GalleryActivity,
                current_user_data?.profile_image_path,
                m_profile_image_view
            )
        }
    }

    fun populateTextViewsWithUserInfo() {
        val current_user = m_firebase_data_service.current_user_data
        val public_username_text_view = findViewById<TextView>(R.id.public_username)
        val num_posts_text_view = findViewById<TextView>(R.id.num_posts)
        val biography_text_view = findViewById<TextView>(R.id.biography)

        public_username_text_view.text = current_user?.user_id
        biography_text_view.text = current_user?.biography
        num_posts_text_view.text = resources.getString(
            R.string.num_posts,
            current_user?.posts?.size
        )
    }
}
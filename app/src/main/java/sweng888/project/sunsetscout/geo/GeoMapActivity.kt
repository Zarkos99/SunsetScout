package sweng888.project.sunsetscout.geo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import sweng888.project.sunsetscout.R
import sweng888.project.sunsetscout.data.SunsetPostCreationActivity
import sweng888.project.sunsetscout.data.User
import sweng888.project.sunsetscout.database.FirebaseDataService
import sweng888.project.sunsetscout.database.deleteImagesAndPosts
import sweng888.project.sunsetscout.gallery.GalleryActivity
import sweng888.project.sunsetscout.gallery.GallerySunsetPostsAdapter
import sweng888.project.sunsetscout.preferences.PreferencesActivity

class GeoMapActivity : AppCompatActivity() {

    private lateinit var m_recycler_view: RecyclerView
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

            // Listen to user data updates
            m_firebase_data_service.registerCallback {
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            m_bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.geo_map_layout)
        // Bind to LocalService.
        Intent(this, FirebaseDataService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        m_recycler_view = findViewById(R.id.geo_recycler_view)
        m_recycler_view.visibility = View.GONE
        val search_view = findViewById<SearchView>(R.id.geo_sunsets_search_field)
        val preferences_button_view = findViewById<Button>(R.id.preferences_button)
        val gallery_button_view = findViewById<Button>(R.id.gallery_button)

        //TODO: Hook up search bar
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                var nonnull_query = ""
                if (query != null) {
                    nonnull_query = query
                }

                m_recycler_view.visibility = View.VISIBLE
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        preferences_button_view.setOnClickListener {
            val intent = Intent(this@GeoMapActivity, PreferencesActivity::class.java)
            startActivity(intent)
        }

        gallery_button_view.setOnClickListener {
            val intent = Intent(this@GeoMapActivity, GalleryActivity::class.java)
            startActivity(intent)
        }
    }


    fun initializeRecyclerViewAdapter() {
        // Initialize recyclerview adaptor
        val sunset_list_adaptor = GeoSunsetListAdapter(this, m_firebase_data_service)
        m_recycler_view.adapter = sunset_list_adaptor
    }

    fun initializeRecyclerViewLayoutManager() {
        // Initialize FlexBox Layout Manager for recyclerview to allow wrapping items to next line
        val layout_manager = FlexboxLayoutManager(this)
        layout_manager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }
        m_recycler_view.layoutManager = layout_manager
    }

}
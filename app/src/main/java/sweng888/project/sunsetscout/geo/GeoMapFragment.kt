package sweng888.project.sunsetscout.geo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import sweng888.project.sunsetscout.R
import sweng888.project.sunsetscout.data.SunsetData
import sweng888.project.sunsetscout.database.FirebaseDataService
import sweng888.project.sunsetscout.databinding.GeoMapFragmentBinding

class GeoMapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: GeoMapFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var m_recycler_view: RecyclerView
    private lateinit var m_recycler_view_container: ConstraintLayout
    private lateinit var m_firebase_data_service: FirebaseDataService
    private lateinit var m_geo_sunset_adapter: GeoSunsetListAdapter
    private lateinit var mMap: GoogleMap
    private var m_service_bound: Boolean = false
    private var m_map_ready: Boolean = false
    private var m_query = ""


    /** Defines callbacks for service binding, passed to bindService().  */
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as FirebaseDataService.LocalBinder
            m_firebase_data_service = binder.getService()
            m_service_bound = true

            initializeRecyclerViewLayoutManager()
            initializeRecyclerViewAdapter()
            applyMarkersToMap()
            // Listen to user data updates
            m_firebase_data_service.registerCallback {
                // Handle data update
                applyMarkersToMap()
                calculatePostsForQuery()
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            m_service_bound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GeoMapFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        m_recycler_view_container = binding.geoRecyclerViewContainer
        val close_list_button = binding.closePostsList
        m_recycler_view = binding.geoRecyclerView
        m_recycler_view_container.visibility = View.GONE
        val search_view = binding.geoSunsetsSearchField

        // Bind to LocalService.
        Intent(requireContext(), FirebaseDataService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val nonnull_query = query?.trim() ?: ""
                m_query = nonnull_query
                m_recycler_view_container.visibility = View.VISIBLE
                calculatePostsForQuery()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        close_list_button.setOnClickListener {
            m_recycler_view_container.visibility = View.GONE
        }

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        m_map_ready = true
        val initialLocation = LatLng(-34.0, 151.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10f))
        applyMarkersToMap()
    }

    override fun onStart() {
        super.onStart()
        Intent(requireContext(), FirebaseDataService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unbindService(connection)
        m_service_bound = false
        m_map_ready = false
    }

    private fun initializeRecyclerViewAdapter() {
        // Initialize recyclerview adaptor
        m_geo_sunset_adapter = GeoSunsetListAdapter(requireContext())
        m_recycler_view.adapter = m_geo_sunset_adapter
        calculatePostsForQuery()
    }

    private fun initializeRecyclerViewLayoutManager() {
        // Initialize FlexBox Layout Manager for recyclerview to allow wrapping items to next line
        val layout_manager = FlexboxLayoutManager(requireContext())
        layout_manager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }
        m_recycler_view.layoutManager = layout_manager
    }

    private fun calculatePostsForQuery() {
        val user_posts = m_firebase_data_service.current_user_data?.posts
        val new_user_posts = arrayListOf<SunsetData>()
        user_posts?.forEach {
            if (m_query.isEmpty() || m_query == "*" || it.title?.lowercase() == m_query.lowercase()) new_user_posts.add(
                it
            )
        }

        m_geo_sunset_adapter.sunset_posts = new_user_posts
        m_geo_sunset_adapter.notifyDataSetChanged()
    }

    private fun applyMarkersToMap() {
        if (m_service_bound && m_map_ready) {
            val current_user_posts = m_firebase_data_service.current_user_data?.posts

            if (current_user_posts != null) {
                var new_lat_lng = LatLng(0.0, 0.0)
                for (post in current_user_posts) {
                    if (post.latitude != null && post.longitude != null) {
                        new_lat_lng =
                            LatLng(post.latitude.toDouble(), post.longitude.toDouble())
                        mMap.addMarker(
                            MarkerOptions()
                                .position(new_lat_lng)
                                .title(post.title)
                        )
                    }
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new_lat_lng))
            }
        }
    }
}
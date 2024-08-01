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
import sweng888.project.sunsetscout.R
import sweng888.project.sunsetscout.database.FirebaseDataService
import sweng888.project.sunsetscout.databinding.GeoMapFragmentBinding

class GeoMapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: GeoMapFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var m_recycler_view: RecyclerView
    private lateinit var m_firebase_data_service: FirebaseDataService
    private var m_bound: Boolean = false
    private lateinit var mMap: GoogleMap

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as FirebaseDataService.LocalBinder
            m_firebase_data_service = binder.getService()
            m_bound = true

            initializeRecyclerViewLayoutManager()
            initializeRecyclerViewAdapter()

            m_firebase_data_service.registerCallback {
                // Handle data update
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            m_bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Intent(requireContext(), FirebaseDataService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GeoMapFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        m_recycler_view = binding.geoRecyclerView
        m_recycler_view.visibility = View.GONE
        val search_view = binding.geoSunsetsSearchField

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val nonnull_query = query ?: ""
                m_recycler_view.visibility = View.VISIBLE
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val initialLocation = LatLng(-34.0, 151.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 10f))
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
        m_bound = false
    }

    private fun initializeRecyclerViewAdapter() {
        val sunset_list_adapter = GeoSunsetListAdapter(requireContext(), m_firebase_data_service)
        m_recycler_view.adapter = sunset_list_adapter
    }

    private fun initializeRecyclerViewLayoutManager() {
        val layout_manager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }
        m_recycler_view.layoutManager = layout_manager
    }
}

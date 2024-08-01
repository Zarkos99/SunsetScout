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
import sweng888.project.sunsetscout.database.FirebaseDataService
import sweng888.project.sunsetscout.databinding.GeoMapFragmentBinding

class GeoMapFragment : Fragment() {
    private var _binding: GeoMapFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        // Bind to LocalService.
        Intent(requireContext(), FirebaseDataService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

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

        return root
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

    fun initializeRecyclerViewAdapter() {
        // Initialize recyclerview adaptor
        val sunset_list_adaptor = GeoSunsetListAdapter(requireContext(), m_firebase_data_service)
        m_recycler_view.adapter = sunset_list_adaptor
    }

    fun initializeRecyclerViewLayoutManager() {
        // Initialize FlexBox Layout Manager for recyclerview to allow wrapping items to next line
        val layout_manager = FlexboxLayoutManager(requireContext())
        layout_manager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }
        m_recycler_view.layoutManager = layout_manager
    }

}
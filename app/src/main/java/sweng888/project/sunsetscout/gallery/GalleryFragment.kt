package sweng888.project.sunsetscout.gallery

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import sweng888.project.sunsetscout.R
import sweng888.project.sunsetscout.Strings
import sweng888.project.sunsetscout.data.SunsetPostCreationActivity
import sweng888.project.sunsetscout.database.*
import sweng888.project.sunsetscout.databinding.GalleryFragmentBinding


class GalleryFragment : Fragment() {
    private var _binding: GalleryFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        // Bind to LocalService.
        Intent(activity, FirebaseDataService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GalleryFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Create intent to open device storage for image selection
        m_select_profile_image_intent =
            registerForActivityResult(ActivityResultContracts.GetContent())
            { uri ->
                if (uri != null) {
                    uploadProfileImage(uri)
                    m_profile_image_view.setImageURI(uri)
                }
            }

        m_profile_image_view = binding.profilePictureView

        // Profile Image selection listener
        m_profile_image_view.setOnClickListener {
            m_select_profile_image_intent.launch("image/*")
        }

        return root
    }

    override fun onStart() {
        super.onStart()
        Intent(activity, FirebaseDataService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unbindService(connection)
        m_bound = false
    }

    fun initializeRecyclerViewAdapter() {
        val add_or_remove_sunset_button_view = binding.addSunsetButton
        val sunsets_recycler_view = binding.gallerySunsets
        // Initialize recyclerview adaptor
        m_sunset_list_adaptor = GallerySunsetPostsAdapter(requireContext(), m_firebase_data_service)
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
                val intent = Intent(activity, SunsetPostCreationActivity::class.java)
                startActivity(intent)
                // Not calling finish() here so that SunsetPostCreationActivity will come back to this fragment
            }
        }
    }

    fun initializeRecyclerViewLayoutManager() {
        val sunsets_recycler_view = binding.gallerySunsets
        // Initialize FlexBox Layout Manager for recyclerview to allow wrapping items to next line
        val layout_manager = FlexboxLayoutManager(requireContext())
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
        return activity?.resources?.getDrawable(
            activity?.resources?.getIdentifier(
                ImageName,
                "drawable",
                activity?.packageName
            )!!
        )!!
    }

    fun populateProfileImage() {
        val current_user_data = m_firebase_data_service.current_user_data
        if (current_user_data?.profile_image_path.isNullOrEmpty()) {
            m_profile_image_view.setImageDrawable(getImage("default_profile_pic"))
        } else {
            if (context != null) {
                loadCloudStoredImageIntoImageView(
                    requireContext(),
                    current_user_data?.profile_image_path,
                    m_profile_image_view
                )
            }
        }
    }

    fun populateTextViewsWithUserInfo() {
        val current_user = m_firebase_data_service.current_user_data
        val public_username_text_view = binding.publicUsername
        val num_posts_text_view = binding.numPosts
        val biography_text_view = binding.biography

        public_username_text_view.text = current_user?.user_id
        biography_text_view.text = current_user?.biography
        num_posts_text_view.text =
            Strings.get(
                R.string.num_posts,
                if (current_user?.posts?.size != null) current_user.posts.size else 0
            )
    }
}
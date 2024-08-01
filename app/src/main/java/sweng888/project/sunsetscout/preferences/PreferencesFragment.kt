package sweng888.project.sunsetscout.preferences

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import sweng888.project.sunsetscout.AuthenticationActivity
import sweng888.project.sunsetscout.databinding.PreferencesFragmentBinding

class PreferencesFragment : Fragment() {
    private var _binding: PreferencesFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PreferencesFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val logout_button = binding.logoutButton

        logout_button.setOnClickListener {
            // Log out user and open authentication activity
            FirebaseAuth.getInstance().signOut()
            val intent =
                Intent(activity, AuthenticationActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
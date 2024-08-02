package sweng888.project.sunsetscout.preferences

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import sweng888.project.sunsetscout.AuthenticationActivity
import sweng888.project.sunsetscout.R
import sweng888.project.sunsetscout.databinding.PreferencesFragmentBinding

class PreferencesFragment : Fragment() {

    lateinit var m_email_input_field: EditText
    lateinit var m_display_name_input_field: EditText
    lateinit var m_save_preferences_button: Button
    lateinit var m_logout_button: Button
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

        m_email_input_field = binding.emailInputField
        m_display_name_input_field = binding.displayNameInputField
        m_logout_button = binding.logoutButton
        m_save_preferences_button = binding.savePreferencesButton
        disableButton(m_save_preferences_button)

        val auth = FirebaseAuth.getInstance()
        val current_user = auth.currentUser
        initializeInputFields(current_user)


        m_email_input_field.doAfterTextChanged { new_email_editable ->
            val new_display_name = m_display_name_input_field.text.toString()
            val new_email = new_email_editable.toString()
            calculateSaveButtonClickability(current_user, new_display_name, new_email)
        }
        m_display_name_input_field.doAfterTextChanged { new_display_name_editable ->
            val new_display_name = new_display_name_editable.toString()
            val new_email = m_email_input_field.text.toString()
            calculateSaveButtonClickability(current_user, new_display_name, new_email)
        }

        m_save_preferences_button.setOnClickListener {
            val new_display_name = m_display_name_input_field.text.toString()
            val new_email = m_email_input_field.text.toString()

            if (current_user?.displayName != new_display_name) {
                current_user?.updateProfile(userProfileChangeRequest {
                    displayName = new_display_name
                })
            }
            if (current_user?.email != new_email) {
                current_user?.verifyBeforeUpdateEmail(new_email)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("NewEmailVerification", "New email successfully verified")
                    } else {
                        Log.e("NewEmailVerification", "New email failed to verify")
                    }
                }
            }

            disableButton(m_save_preferences_button)
        }

        m_logout_button.setOnClickListener {
            // Log out user and open authentication activity
            auth.signOut()
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

    private fun initializeInputFields(user: FirebaseUser?) {
        m_display_name_input_field.setText(user?.displayName)
        m_email_input_field.setText(user?.email)
    }

    private fun calculateSaveButtonClickability(
        user: FirebaseUser?,
        new_email: String,
        new_display_name: String
    ) {
        if (user?.displayName != new_display_name || user.email != new_email) {
            enableButton(m_save_preferences_button)
        } else {
            disableButton(m_save_preferences_button)
        }
    }

    private fun enableButton(button: Button) {
        button.isEnabled = true
        button.isClickable = true
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        button.background = m_logout_button.background
    }

    private fun disableButton(button: Button) {
        button.isEnabled = false
        button.isClickable = false
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_grey))
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
    }
}
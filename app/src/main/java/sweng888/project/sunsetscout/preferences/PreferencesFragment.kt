package sweng888.project.sunsetscout.preferences

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
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

        val preferences = loadPreferenceOptions()
        val adapter = PreferencesListAdapter(requireContext(), preferences)

        val preferences_list_view = binding.preferencesListView
        preferences_list_view.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadPreferenceOptions(): ArrayList<String> {
        val classes = ArrayList<String>()

        for (i in 1..8) {
            classes.add("Preference $i")
        }

        return classes
    }
}
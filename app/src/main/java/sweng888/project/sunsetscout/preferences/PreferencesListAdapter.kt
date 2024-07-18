package sweng888.project.sunsetscout.preferences

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import sweng888.project.sunsetscout.R

class PreferencesListAdapter(context: Context, preferences: ArrayList<String>) :
    ArrayAdapter<String>(context, R.layout.preferences_list_item_layout, preferences) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val retView: View

        retView = convertView
            ?: LayoutInflater.from(context)
                .inflate(R.layout.preferences_list_item_layout, parent, false)

        val preference = getItem(position)

        val text_view_preference_name =
            retView.findViewById<TextView>(R.id.preference_name)

        text_view_preference_name.text = preference
        return retView;
    }
}
package sweng888.project.sunsetscout

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * The adaptor for a recyclerview of products with the capability to have selectable items or not
 */
class GallerySunsetPostsAdapter(
    private val context: Context,
    private val sunsets: List<SunsetData>
) :
    RecyclerView.Adapter<GallerySunsetPostsAdapter.ViewHolder>() {

    // Array of product names
    var selected_sunsets = ArrayList<SunsetData>()

    /**
     * Handles creation of the view holder for each item in the recyclerview
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create new view with UI of weather item
        val view = LayoutInflater.from(context)
            .inflate(R.layout.sunset_gallery_item, parent, false)
        return ViewHolder(view)
    }

    /**
     * Handles binding of the view holder for each item in the recyclerview
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sunset = sunsets[position]
        // Fill the text views with high-level information on the products
        holder.sunset_image_view.setImageURI(sunset.image_uri)


        // Provides logic to track all selected products as the user selects them
        holder.setItemClickListener(object : ViewHolder.ItemClickListener {
            override fun onItemClick(v: View, pos: Int) {
                val current_sunset = sunsets[pos]

                if (selected_sunsets.contains(current_sunset)) {
                    selected_sunsets.remove(current_sunset)
                } else {
                    selected_sunsets.add(current_sunset)
                }
                Log.e("DEBUG", selected_sunsets.toString())
            }
        })
    }

    /**
     * Gets all of the selected items
     */
    fun getSelectedSunsets(): ArrayList<SunsetData> {
        return selected_sunsets
    }

    /**
     * Gets all of the items in the recyclerview
     */
    override fun getItemCount(): Int {
        return sunsets.size
    }

    /**
     * Handles logic for a ViewHolder instance
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        val sunset_image_view: ImageView = view.findViewById(R.id.gallery_sunset_image_view)
        val sunset_checkbox: FloatingActionButton = view.findViewById(R.id.checkbox_button)

        lateinit var sunset_click_listener: ItemClickListener

        init {
            // Make the checkbox selectable
            view.setOnClickListener(this)
        }

        fun setItemClickListener(ic: ItemClickListener) {
            this.sunset_click_listener = ic
        }

        /**
         * Uses the View.OnClickListener inheritance to allow each list item to have clickable functionality
         */
        override fun onClick(v: View) {
            this.sunset_click_listener.onItemClick(v, layoutPosition)

            if (sunset_checkbox.visibility == View.VISIBLE) {
                sunset_checkbox.visibility = View.GONE
            } else {
                sunset_checkbox.visibility = View.VISIBLE
            }
        }

        interface ItemClickListener {
            fun onItemClick(v: View, pos: Int)
        }
    }
}

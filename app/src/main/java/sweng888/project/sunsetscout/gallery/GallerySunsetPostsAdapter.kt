package sweng888.project.sunsetscout.gallery

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import sweng888.project.sunsetscout.R
import sweng888.project.sunsetscout.data.SunsetData
import sweng888.project.sunsetscout.database.FirebaseDataService
import sweng888.project.sunsetscout.database.loadCloudStoredImageIntoImageView

/**
 * The adaptor for a recyclerview of sunset posts with the capability to have selectable items or not
 */
class GallerySunsetPostsAdapter(
    private val context: Context,
    private val firebase_data_service: FirebaseDataService
) :
    RecyclerView.Adapter<GallerySunsetPostsAdapter.ViewHolder>() {

    // Array of product names
    var selected_sunsets = ArrayList<SunsetData>()
    var item_selected_callbacks = ArrayList<(() -> Unit)>()

    /**
     * Handles creation of the view holder for each item in the recyclerview
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create new view
        val view = LayoutInflater.from(context)
            .inflate(R.layout.sunset_gallery_item, parent, false)

        unselectDeletedSunsets()
        return ViewHolder(view)
    }

    /**
     * Handles binding of the view holder for each item in the recyclerview
     */
    override fun onBindViewHolder(holder: ViewHolder, dont_use: Int) {
        val user = firebase_data_service.current_user_data
        val sunset = user?.posts?.get(holder.adapterPosition)
        loadCloudStoredImageIntoImageView(
            context,
            sunset?.cloud_image_path,
            holder.sunset_image_view
        )

        // Below line fixes a bug where deleted sunset checkboxes would positionally
        // associate to next undeleted sunsets at same position by defaulting onBind checkbox
        // to not visible
        holder.sunset_checkbox.visibility = View.GONE

        // Provides logic to track all selected products as the user selects them
        holder.setItemClickListener(object : ViewHolder.ItemClickListener {
            override fun onItemClick(v: View, pos: Int) {
                val current_sunset =
                    firebase_data_service.current_user_data?.posts?.get(holder.adapterPosition)

                if (selected_sunsets.contains(current_sunset)) {
                    holder.sunset_checkbox.visibility = View.GONE
                    selected_sunsets.remove(current_sunset)
                } else {
                    if (current_sunset != null) {
                        selected_sunsets.add(current_sunset)
                    }
                    holder.sunset_checkbox.visibility = View.VISIBLE
                }

                callItemSelectedCallbacks()
            }
        })
    }

    fun registerItemSelectedCallback(callback: (() -> Unit)) {
        item_selected_callbacks.add(callback)
    }

    fun callItemSelectedCallbacks() {
        if (item_selected_callbacks.size > 0) {
            for (callback in item_selected_callbacks) {
                callback()
            }
        }
    }

    /**
     * Gets all of the selected items
     */
    fun getSelectedSunsets(): ArrayList<SunsetData> {
        return selected_sunsets
    }

    fun unselectDeletedSunsets() {
        val user = firebase_data_service.current_user_data
        val sunset_posts = user?.posts

        if (!sunset_posts.isNullOrEmpty()) {
            selected_sunsets.removeIf {
                val selected_sunset = it;
                !sunset_posts.any { obj -> obj.unique_id == selected_sunset.unique_id }
            }
        } else {
            selected_sunsets.clear()
        }
        callItemSelectedCallbacks()
    }

    /**
     * Gets all of the items in the recyclerview
     */
    fun getSelectedItemCount(): Int {
        return selected_sunsets.size
    }

    /**
     * Gets all of the items in the recyclerview
     */
    override fun getItemCount(): Int {
        // Returns 0 if posts array is null else returns current size of posts array
        return firebase_data_service.current_user_data?.posts?.size ?: return 0
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
        }

        interface ItemClickListener {
            fun onItemClick(v: View, pos: Int)
        }
    }
}

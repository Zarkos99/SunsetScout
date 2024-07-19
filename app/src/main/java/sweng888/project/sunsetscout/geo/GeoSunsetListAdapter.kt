package sweng888.project.sunsetscout.geo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sweng888.project.sunsetscout.R
import sweng888.project.sunsetscout.database.FirebaseDataService
import sweng888.project.sunsetscout.database.loadCloudStoredImageIntoImageView


/**
 * The adaptor for a recyclerview of sunset posts
 */
class GeoSunsetListAdapter(
    private val context: Context,
    private val firebase_data_service: FirebaseDataService
) : RecyclerView.Adapter<GeoSunsetListAdapter.ViewHolder>() {

    /**
     * Handles creation of the view holder for each item in the recyclerview
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create new view
        val view = LayoutInflater.from(context)
            .inflate(R.layout.geo_list_item, parent, false)
        return ViewHolder(view)
    }

    /**
     * Handles binding of the view holder for each item in the recyclerview
     */
    override fun onBindViewHolder(holder: ViewHolder, dont_use: Int) {
        val user = firebase_data_service.current_user_data
        val sunset = user?.posts?.get(holder.adapterPosition)
        holder.latitude_text_view.text = "Latitude: ${sunset?.latitude}"
        holder.longitude_text_view.text = "Longitude: ${sunset?.longitude}"
        loadCloudStoredImageIntoImageView(
            context,
            sunset?.cloud_image_path,
            holder.sunset_image_view
        )
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
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sunset_image_view: ImageView = view.findViewById(R.id.geo_list_image_view)
        val latitude_text_view: TextView = view.findViewById(R.id.latitude_view)
        val longitude_text_view: TextView = view.findViewById(R.id.longitude_view)
    }
}


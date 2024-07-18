package sweng888.project.sunsetscout.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.format.DateTimeFormatter

@Parcelize
data class User(
    val user_id: String = "",
    val biography: String? = "",
    var posts: ArrayList<SunsetData> = ArrayList()
) :
    Parcelable

@Parcelize
data class SunsetData(
    val latitude: String? = "",
    val longitude: String? = "",
    val post_time: String? = DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
    val description: String? = "",
    var cloud_image_path: String? = ""
) :
    Parcelable {
    val unique_id: Int get() = hashCode()
}
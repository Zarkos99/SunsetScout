package sweng888.project.sunsetscout

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SunsetData(
    val user: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val post_time: String = "",
    val image: String
) :
    Parcelable {
}
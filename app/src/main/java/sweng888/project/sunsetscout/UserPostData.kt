package sweng888.project.sunsetscout

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.format.DateTimeFormatter


@Parcelize
data class User(
    val username: String = "",
    val email: String = "",
    val biography: String = "",
    val posts: ArrayList<SunsetData> = ArrayList<SunsetData>()
) :
    Parcelable {
}

@Parcelize
data class SunsetData(
    val latitude: String = "",
    val longitude: String = "",
    val post_time: String = DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
    val description: String = "",
    val image: String = ""
) :
    Parcelable {
}


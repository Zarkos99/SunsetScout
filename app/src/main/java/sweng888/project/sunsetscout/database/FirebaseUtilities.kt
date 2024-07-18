package sweng888.project.sunsetscout.database

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import sweng888.project.sunsetscout.R
import sweng888.project.sunsetscout.Strings
import sweng888.project.sunsetscout.data.SunsetData
import sweng888.project.sunsetscout.data.User

fun getCurrentUsername(): String {
    var username: String? = ""
    val firebase_user = Firebase.auth.currentUser
    firebase_user?.let {
        username = it.email
    }

    if (username == null) {
        Log.e("Firebase Auth Error", "Null user_id")
        return ""
    }

    return username.toString()
}

fun addSunsetPostToDatabase(post: SunsetData) {
    val firebase_database = Firebase.firestore
    val username = getCurrentUsername()

    val user_ref =
        firebase_database.collection(
            Strings.get(R.string.firebase_collection_name)
        )
            .document(username)
    user_ref.update(Strings.get(R.string.posts_array_name), FieldValue.arrayUnion(post))
}

fun removeSunsetPostsFromDatabase(posts_to_remove: ArrayList<SunsetData>) {
    val firebase_database = Firebase.firestore
    val username = getCurrentUsername()

    // Delete post in database
    val user_ref =
        firebase_database.collection(
            Strings.get(R.string.firebase_collection_name)
        ).document(username)

    user_ref.get().addOnSuccessListener { document ->
        if (document != null) {
            val user = document.toObject<User>()
            if (user != null) {
                for (post_to_remove in posts_to_remove) {
                    user.posts.removeIf { it.unique_id == post_to_remove.unique_id }
                }
                user_ref.set(user)
            }
        }
    }
}

fun updateUserPost(map_of_updates: Map<String, Any>) {
    val firebase_database = Firebase.firestore
    val username = getCurrentUsername()

    firebase_database.collection(Strings.get(R.string.firebase_collection_name))
        .document(username).update(map_of_updates)
}


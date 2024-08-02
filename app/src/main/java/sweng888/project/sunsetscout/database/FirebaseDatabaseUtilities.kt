package sweng888.project.sunsetscout.database

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

fun getCurrentUserId(): String {
    var user_id: String? = ""
    val firebase_user = Firebase.auth.currentUser
    firebase_user?.let {
        user_id = it.uid
    }

    return user_id.toString()
}

fun addUser() {
    val firebase_database = Firebase.firestore
    val user_id = getCurrentUserId()

    val new_user = User(user_id = user_id)

    firebase_database.collection(
        Strings.get(R.string.firebase_collection_name)
    ).document(user_id).set(new_user)
}

fun updateUserDataField(field_name: String, field_value: Any) {
    val firebase_database = Firebase.firestore
    val user_id = getCurrentUserId()

    val user_ref =
        firebase_database.collection(
            Strings.get(R.string.firebase_collection_name)
        ).document(user_id)

    user_ref.update(field_name, field_value).addOnFailureListener {
        Log.w(
            "updateUserDataField Error",
            "Failed to update $field_name to $field_value"
        )
    }
}

fun addSunsetPostToDatabase(post: SunsetData) {
    val firebase_database = Firebase.firestore
    val user_id = getCurrentUserId()

    val user_ref =
        firebase_database.collection(
            Strings.get(R.string.firebase_collection_name)
        ).document(user_id)
    user_ref.update(Strings.get(R.string.posts_array_name), FieldValue.arrayUnion(post))
}

fun removeSunsetPostsFromDatabase(posts_to_remove: ArrayList<SunsetData>) {
    val firebase_database = Firebase.firestore
    val user_id = getCurrentUserId()

    // Delete post in database
    val user_ref =
        firebase_database.collection(
            Strings.get(R.string.firebase_collection_name)
        ).document(user_id)

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


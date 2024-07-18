package sweng888.project.sunsetscout.database

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import sweng888.project.sunsetscout.data.SunsetData
import java.io.File

fun uploadImageAndCreateNewPost(post: SunsetData, file: Uri) {
    val m_firebase_storage = Firebase.storage
    if (file.path == null) {
        Log.w("uploadImage Failure", "Local image uri is null.")
        return
    }

    // Create a storage reference from our app
    val storage_ref = m_firebase_storage.reference

    val storage_path_to_image = "images/Sunset_${post.unique_id}"
    val image_ref = storage_ref.child(storage_path_to_image)
    val upload_task = image_ref.putFile(file)

    // Register observers to listen for when the download is done or if it fails
    upload_task.addOnFailureListener {
        // Handle unsuccessful uploads
        Log.e("Image Storage Upload Error", "Unable to upload image: $it")
    }.addOnSuccessListener {
        // Set the image path to where it exists on the cloud storage
        post.cloud_image_path = storage_path_to_image
        addSunsetPostToDatabase(post)
    }
}

fun deleteImageAndPosts(posts_to_remove: ArrayList<SunsetData>) {
    val m_firebase_storage = Firebase.storage
    // Create a storage reference from our app
    val storage_ref = m_firebase_storage.reference

    // Delete images from cloud storage
    for (post_to_remove in posts_to_remove) {
        if (!post_to_remove.cloud_image_path.isNullOrEmpty()) {
            val image_ref = storage_ref.child(post_to_remove.cloud_image_path!!)
            image_ref.delete().addOnFailureListener {
                // Handle unsuccessful deletion
                Log.e("Image Storage Deletion Error", "Unable to delete image: $it")
            }.addOnSuccessListener { removeSunsetPostFromDatabase(post_to_remove) }
        }
    }


}

fun getImage(cloud_file_path: String) {
    val m_firebase_storage = Firebase.storage
    // Create a storage reference from our app
    val storage_ref = m_firebase_storage.reference

    val image_ref = storage_ref.child(cloud_file_path)
    image_ref.delete().addOnFailureListener {
        // Handle unsuccessful deletion
        Log.e("Image Storage Deletion Error", "Unable to delete image: $it")
    }
}

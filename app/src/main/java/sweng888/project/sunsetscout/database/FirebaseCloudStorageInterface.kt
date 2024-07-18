package sweng888.project.sunsetscout.database

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import sweng888.project.sunsetscout.data.SunsetData

fun uploadProfileImage(file: Uri) {
    val m_firebase_storage = Firebase.storage
    if (file.path == null) {
        Log.w("uploadProfileImage Failure", "Local image uri is null.")
        return
    }

    // Create a storage reference from our app
    val storage_ref = m_firebase_storage.reference

    val storage_path_to_image = "${getCurrentUsername()}/profile_image"
    val image_ref = storage_ref.child(storage_path_to_image)
    val upload_task = image_ref.putFile(file)

    // Register observers to listen for when the download is done or if it fails
    upload_task.addOnFailureListener {
        // Handle unsuccessful uploads
        Log.e("Image Storage Upload Error", "Unable to upload image: $it")
    }.addOnSuccessListener {
        // Set the image path to where it exists on the cloud storage
        updateUserDataField("profile_image_path", storage_path_to_image)
    }
}

fun uploadImageAndCreateNewPost(post: SunsetData, file: Uri) {
    val m_firebase_storage = Firebase.storage
    if (file.path == null) {
        Log.w("uploadImage Failure", "Local image uri is null.")
        return
    }

    // Create a storage reference from our app
    val storage_ref = m_firebase_storage.reference

    val storage_path_to_image = "${getCurrentUsername()}/post_images/Sunset_${post.unique_id}"
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

fun deleteImagesAndPosts(posts_to_remove: ArrayList<SunsetData>) {
    val m_firebase_storage = Firebase.storage
    // Create a storage reference from our app
    val storage_ref = m_firebase_storage.reference

    // Delete images from cloud storage
    posts_to_remove.forEach { post_to_remove ->
        if (!post_to_remove.cloud_image_path.isNullOrEmpty()) {
            val image_ref = storage_ref.child(post_to_remove.cloud_image_path!!)
            image_ref.delete().addOnFailureListener {
                // Handle unsuccessful deletion
                Log.e("Image Storage Deletion Error", "Unable to delete image: $it")
            }
        }
    }

    removeSunsetPostsFromDatabase(posts_to_remove)
}

fun loadCloudStoredImageIntoImageView(context: Context, image_path: String?, view: ImageView) {
    if (!image_path.isNullOrEmpty()) {
        val storage_ref = Firebase.storage.reference.child(image_path)
        storage_ref.downloadUrl.addOnSuccessListener {
            try {
                Glide.with(context).load(it).into(view)
            } catch (exception: Exception) {
                Log.e("Glide Image Download Error", exception.toString())
            }
        }
    }
}
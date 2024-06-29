package sweng888.project.sunsetscout

import android.R.id
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class UserDatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(
        context, context.getString(R.string.database_name), null, 1
    ) {

    override fun onCreate(db: SQLiteDatabase) {
        val query =
            ("CREATE TABLE " + table_name + " (" +
                    username_key + " TEXT," +
                    email_key + " TEXT," +
                    biography_key + " TEXT," +
                    posts_key + " TEXT" +
                    ")")
        db.execSQL(query)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS $table_name")
        onCreate(db)
    }

    /**
     * Acquire all users in the database.
     */
    fun getAllUsers(): ArrayList<User> {
        val db = readableDatabase
        val users_db_cursor = db.rawQuery("SELECT * FROM $table_name", null)
        val users_array_list: ArrayList<User> = ArrayList()

        if (users_db_cursor.moveToFirst()) {
            do {
                // on below line we are adding the data from
                // cursor to our array list.
                users_array_list.add(
                    User(
                        users_db_cursor.getString(
                            users_db_cursor.getColumnIndexOrThrow(
                                username_key
                            )
                        ),
                        users_db_cursor.getString(
                            users_db_cursor.getColumnIndexOrThrow(
                                email_key
                            )
                        ),
                        users_db_cursor.getString(
                            users_db_cursor.getColumnIndexOrThrow(
                                biography_key
                            )
                        ),
                        deserializePosts(
                            users_db_cursor.getString(
                                users_db_cursor.getColumnIndexOrThrow(
                                    posts_key
                                )
                            )
                        )
                    )
                )
            } while (users_db_cursor.moveToNext())
        }

        users_db_cursor.close()
        db.close()
        return users_array_list
    }

    /**
     * Acquire a single user based on username from the database
     */
    fun getUser(username: String): User {

        val db = readableDatabase
        val users_db_cursor =
            db.rawQuery("SELECT * FROM $table_name WHERE $username_key = '$username'", null)
        var user = User()

        if (users_db_cursor.moveToFirst()) {
            // on below line we are adding the data from
            // cursor to our array list.
            user = User(
                users_db_cursor.getString(
                    users_db_cursor.getColumnIndexOrThrow(
                        username_key
                    )
                ),
                users_db_cursor.getString(
                    users_db_cursor.getColumnIndexOrThrow(
                        email_key
                    )
                ),
                users_db_cursor.getString(
                    users_db_cursor.getColumnIndexOrThrow(
                        biography_key
                    )
                ),
                deserializePosts(
                    users_db_cursor.getString(
                        users_db_cursor.getColumnIndexOrThrow(
                            posts_key
                        )
                    )
                )
            )
        }

        users_db_cursor.close()
        db.close()
        return user
    }

    fun addUserToDatabase(
        user: User
    ) {
        val database = writableDatabase
        val values = ContentValues()

        values.put(username_key, user.username)
        values.put(email_key, user.email)
        values.put(biography_key, user.biography)
        database.insert(table_name, null, values)
        database.close()
    }

    fun getUserPosts(username: String): ArrayList<SunsetData> {

        val db = readableDatabase
        val users_db_cursor =
            db.rawQuery("SELECT * FROM $table_name WHERE $username_key = '$username'", null)
        var user_posts = ArrayList<SunsetData>()

        if (users_db_cursor.moveToFirst()) {
            // on below line we are adding the data from
            // cursor to our array list.
            user_posts =
                deserializePosts(
                    users_db_cursor.getString(
                        users_db_cursor.getColumnIndexOrThrow(
                            posts_key
                        )
                    )
                )
        }

        users_db_cursor.close()
        db.close()
        return user_posts
    }

    fun updateUserPosts(username: String, user_posts: ArrayList<SunsetData>): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(posts_key, serializePosts(user_posts))
        return db.update(
            table_name,
            contentValues,
            username_key + " = ?",
            arrayOf<String>(username)
        ) > 0
    }

    /**
     * Convert the posts member variable to a serialized json string for database storage or activity communication
     */
    fun serializePosts(posts: ArrayList<SunsetData>): String {
        return Gson().toJson(posts)
    }

    /**
     * Convert a serialized json string of ArrayList<SunsetData> back to an ArrayList<SunsetData>
     */
    fun deserializePosts(json_string_posts: String): ArrayList<SunsetData> {
        val type: Type = object : TypeToken<ArrayList<SunsetData?>?>() {}.type
        val gson =
            GsonBuilder().registerTypeHierarchyAdapter(Uri::class.java, UriTypeAdapter).create()
        val unserialized_posts: ArrayList<SunsetData> = gson.fromJson(json_string_posts, type)

        return unserialized_posts
    }


    companion object {
        val table_name: String = "Users"
        val username_key: String = "username"
        val email_key: String = "email"
        val biography_key: String = "biography"
        val posts_key: String = "posts"
    }

}

object UriTypeAdapter : JsonDeserializer<Uri?>, JsonSerializer<Uri?> {
    override fun deserialize(
        json: JsonElement,
        type: Type?,
        context: JsonDeserializationContext?
    ): Uri = runCatching {
        Uri.parse(json.asString)
    }.getOrDefault(Uri.EMPTY)

    override fun serialize(
        src: Uri?,
        type: Type?,
        context: JsonSerializationContext?
    ): JsonElement =
        JsonPrimitive(src.toString())
}
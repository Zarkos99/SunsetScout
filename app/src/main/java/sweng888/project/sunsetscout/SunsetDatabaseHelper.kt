package sweng888.project.sunsetscout

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SunsetDatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(
        context, context.getString(R.string.database_name), null, 1
    ) {

    override fun onCreate(db: SQLiteDatabase) {
        val query =
            ("CREATE TABLE " + table_name + " (" +
                    user_key + " TEXT," +
                    latitude_key + " TEXT," +
                    longitude_key + " TEXT," +
                    post_time_key + " TEXT," +
                    image_key + " TEXT" +
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

    fun getAllSunsets(): ArrayList<SunsetData> {
        val db = readableDatabase
        val products_cursor = db.rawQuery("SELECT * FROM $table_name", null)
        val product_array_list: ArrayList<SunsetData> = ArrayList()

        if (products_cursor.moveToFirst()) {
            do {
                // on below line we are adding the data from
                // cursor to our array list.
                product_array_list.add(
                    SunsetData(
                        products_cursor.getString(
                            products_cursor.getColumnIndexOrThrow(
                                user_key
                            )
                        ),
                        products_cursor.getString(
                            products_cursor.getColumnIndexOrThrow(
                                latitude_key
                            )
                        ),
                        products_cursor.getString(
                            products_cursor.getColumnIndexOrThrow(
                                longitude_key
                            )
                        ),
                        products_cursor.getString(
                            products_cursor.getColumnIndexOrThrow(
                                post_time_key
                            )
                        ),
                        products_cursor.getString(
                            products_cursor.getColumnIndexOrThrow(
                                image_key
                            )
                        )
                    )
                )
            } while (products_cursor.moveToNext())
        }

        products_cursor.close()
        db.close()
        return product_array_list
    }

    fun addSunsetToDatabase(
        sunset: SunsetData
    ) {
        val database = writableDatabase
        val values = ContentValues()

        values.put(user_key, sunset.user)
        values.put(latitude_key, sunset.latitude)
        values.put(longitude_key, sunset.longitude)
        values.put(post_time_key, sunset.post_time)
        values.put(image_key, sunset.image)
        database.insert(table_name, null, values)
        database.close()
    }


    companion object {
        val table_name: String = "Sunsets"
        val user_key: String = "user"
        val latitude_key: String = "latitude"
        val longitude_key: String = "longitude"
        val post_time_key: String = "post_time"
        val image_key: String = "image"
    }

}
package com.genesys.v1.xxpermission_ktx

import android.content.ContentUris
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class PhotoPickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_picker)

        val imageView = findViewById<ImageView>(R.id.imagePreview)
        val infoText = findViewById<TextView>(R.id.infoText)

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"

        try {
            contentResolver.query(collection, projection, null, null, sortOrder)?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(idCol)
                    val name = cursor.getString(nameCol)
                    val date = cursor.getLong(dateCol)
                    val uri = ContentUris.withAppendedId(collection, id)

                    Timber.tag("PhotoPicker").d("Latest image -> id=%d name=%s uri=%s", id, name, uri)
                    imageView.setImageURI(uri)
                    infoText.text = "Latest image: ${name} (date=${date})"
                } else {
                    infoText.text = "No images found or no access"
                    Timber.tag("PhotoPicker").w("MediaStore returned no images")
                }
            }
        } catch (e: Exception) {
            infoText.text = "Failed to load images: ${e.message}"
            Timber.tag("PhotoPicker").e(e, "Query MediaStore failed")
        }
    }
}


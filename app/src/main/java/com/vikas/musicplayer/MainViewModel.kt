package com.vikas.musicplayer

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel


class MainViewModel: ViewModel() {

    @SuppressLint("Range", "Recycle")
    fun getAllAudioFromDevice(context: Context): List<AudioModel> {
        val cursor: Cursor? = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        val audioModel: MutableList<AudioModel> = mutableListOf()
        while (cursor?.moveToNext() == true) {
            val title: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
            val artist: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
            val album: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
            val path: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
            //val duration: String = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
            Log.d(MainActivity.TAG, "getAllAudioFromDevice: duration $path")
            audioModel.add(AudioModel(aPth = path, aName = title, aArtist = artist, aAlbum = album))

        }
        return audioModel
    }


}

/*
android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
 */
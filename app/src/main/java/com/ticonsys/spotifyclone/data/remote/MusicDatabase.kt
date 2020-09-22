package com.ticonsys.spotifyclone.data.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ticonsys.spotifyclone.data.entities.Song
import com.ticonsys.spotifyclone.internal.Constant.SONG_COLLECTION
import kotlinx.coroutines.tasks.await

class MusicDatabase {

    private val fireStore = Firebase.firestore
    private val songCollection = fireStore.collection(SONG_COLLECTION)

    suspend fun getAllSongs() = try {
        songCollection.get().await().toObjects(Song::class.java)
    } catch (e: Exception){
        emptyList()
    }

}
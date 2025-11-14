package com.example.moviesapp.data.local.entity

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class MovieEntity : RealmObject {
    @PrimaryKey
    var id: Long = 0
    var title: String = ""
    var overview: String = ""
    var posterPath: String? = null
    var voteAverage: Double = 0.0
    var releaseDate: String? = null
}
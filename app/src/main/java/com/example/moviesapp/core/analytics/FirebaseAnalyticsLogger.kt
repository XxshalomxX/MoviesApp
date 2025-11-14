package com.example.moviesapp.core.analytics

import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsLogger @Inject constructor(
    app: Application
) : AnalyticsLogger {

    private val analytics = Firebase.analytics

    override fun logViewMovieDetail(id: Long, title: String) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "movie")
            putLong(FirebaseAnalytics.Param.ITEM_ID, id)
            putString(FirebaseAnalytics.Param.ITEM_NAME, title)
        }

        analytics.logEvent("view_movie_detail", bundle)
    }
}

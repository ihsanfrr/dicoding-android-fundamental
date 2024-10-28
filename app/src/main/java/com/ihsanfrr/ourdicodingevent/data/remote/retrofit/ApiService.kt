package com.ihsanfrr.ourdicodingevent.data.remote.retrofit

import retrofit2.http.GET
import retrofit2.http.Query
import com.ihsanfrr.ourdicodingevent.data.remote.response.DicodingEventResponse

interface ApiService {
    @GET("events")
    suspend fun getEvents(@Query("active") active: Int): DicodingEventResponse
}
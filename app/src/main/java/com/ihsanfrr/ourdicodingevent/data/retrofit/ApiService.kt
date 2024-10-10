package com.ihsanfrr.ourdicodingevent.data.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.ihsanfrr.ourdicodingevent.data.response.DicodingEventResponse
import com.ihsanfrr.ourdicodingevent.data.response.DetailDicodingEventResponse

interface ApiService {
    @GET("events/{id}")
    fun getDicodingEventDetail(
        @Path("id") id: String
    ): Call<DetailDicodingEventResponse>

    @GET("events")
    fun getDicodingEvent(
        @Query("active") active: Int
    ): Call<DicodingEventResponse>

    @GET("events")
    fun searchDicodingEvent(
        @Query("active") active: Int,
        @Query("q") query: String
    ): Call<DicodingEventResponse>
}
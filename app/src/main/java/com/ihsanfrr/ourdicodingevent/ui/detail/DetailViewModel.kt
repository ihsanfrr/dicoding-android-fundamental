package com.ihsanfrr.ourdicodingevent.ui.detail

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ihsanfrr.ourdicodingevent.data.retrofit.ApiConfig
import com.ihsanfrr.ourdicodingevent.data.response.ListEventsItem
import com.ihsanfrr.ourdicodingevent.data.response.DetailDicodingEventResponse

class DetailViewModel: ViewModel() {
    private val _event = MutableLiveData<ListEventsItem>()
    val event: LiveData<ListEventsItem> get() = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchEvent(id: String) {
        _isLoading.value = true

        val client = ApiConfig.getApiService().getDicodingEventDetail(id)
        client.enqueue(object: Callback<DetailDicodingEventResponse> {
            override fun onResponse(
                call: Call<DetailDicodingEventResponse>,
                response: Response<DetailDicodingEventResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val body = response.body()
                    _event.value = body?.event
                } else {
                    _error.value = "Error: ${response.message()}"
                }
            }
            override fun onFailure(call: Call<DetailDicodingEventResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = "Error: ${t.message}"
            }
        })
    }
}
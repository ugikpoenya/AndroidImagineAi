package com.ugikpoenya.imagineai.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @POST
    fun generateImages(@Url url: String, @Header("Authorization") Authorization: String?, @Body body: MultipartBody?): Call<ResponseBody>

}
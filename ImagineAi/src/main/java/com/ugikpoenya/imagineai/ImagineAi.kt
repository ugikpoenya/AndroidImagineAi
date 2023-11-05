package com.ugikpoenya.imagineai

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.gson.Gson
import com.ugikpoenya.imagineai.api.ApiClient
import com.ugikpoenya.imagineai.api.ApiService
import com.ugikpoenya.imagineai.api.ImagineAiModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ImagineAi(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(context.packageName, 0)
    var IMAGINE_API_KEY: String
        get() = prefs.getString("IMAGINE_API_KEY", "").toString()
        set(value) = prefs.edit().putString("IMAGINE_API_KEY", value).apply()

    private fun generateMultipartBody(model: ImagineAiModel): MultipartBody {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        if (!model.prompt.isNullOrEmpty()) builder.addFormDataPart("prompt", model.prompt.toString())
        if (model.style_id > 0) builder.addFormDataPart("style_id", model.style_id.toString())

        //Text To Image
        if (!model.aspect_ratio.isNullOrEmpty()) builder.addFormDataPart("aspect_ratio", model.aspect_ratio.toString())
        if (!model.negative_prompt.isNullOrEmpty()) builder.addFormDataPart("negative_prompt", model.negative_prompt.toString())
        if (model.cfg > 0) builder.addFormDataPart("cfg", model.cfg.toString())
        if (model.seed > 0) builder.addFormDataPart("seed", model.seed.toString())
        if (model.steps > 0) builder.addFormDataPart("steps", model.steps.toString())
        if (model.high_res_results > 0) builder.addFormDataPart("high_res_results", model.high_res_results.toString())


        //Image Remix
        if (!model.image.isNullOrEmpty()) {
            val image = File(model.image.toString())
            builder.addFormDataPart("image", image.name, image.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
        }

        if (model.strength > 0) builder.addFormDataPart("strength", model.strength.toString())
        if (!model.control.isNullOrEmpty()) builder.addFormDataPart("control", model.control.toString())

        //Image Inpaint
        if (!model.neg_prompt.isNullOrEmpty()) builder.addFormDataPart("neg_prompt", model.neg_prompt.toString())
        if (!model.mask.isNullOrEmpty()) {
            val mask = File(model.mask.toString())
            builder.addFormDataPart("mask", mask.name, mask.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
        }
        if (model.inpaint_strength > 0) builder.addFormDataPart("inpaint_strength", model.inpaint_strength.toString())

        //Image Upscale
        if (model.model_version > 0) builder.addFormDataPart("model_version", model.model_version.toString())


        return builder.build()
    }

    class ErrorResponse {
        var error: String? = null
        var code: String? = null
        var details: ArrayList<String>? = null
    }

    private fun generate(
        url: String,
        imagineAiModel: ImagineAiModel,
        function: (response: Bitmap?, error: ErrorResponse?) -> (Unit),
    ) {
        val requestBody = generateMultipartBody(imagineAiModel)
        val apiService = ApiClient.client!!.create(ApiService::class.java)
        val call: Call<ResponseBody> = apiService.generateImages(url, "Bearer $IMAGINE_API_KEY", requestBody)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val bytes = response.body()!!.bytes()
                    val btm = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    function(btm, null)
                } else {
                    Log.d("LOG", "Error " + response)
                    try {
                        val errorResponse = Gson().fromJson(
                            response.errorBody()!!.charStream(),
                            ErrorResponse::class.java
                        )
                        function(null, errorResponse)
                    } catch (e: Exception) {
                        Log.d("LOG", "Parsing Error " + e.message)
                        function(null, null)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("LOG", "onFailure " + t.localizedMessage)
                function(null, null)
            }
        })
    }

    fun generationsImages(
        imagineAiModel: ImagineAiModel,
        function: (response: Bitmap?, error: ErrorResponse?) -> (Unit),
    ) {
        generate("generations", imagineAiModel, function)
    }

    fun editsRemix(
        imagineAiModel: ImagineAiModel,
        function: (response: Bitmap?, error: ErrorResponse?) -> (Unit),
    ) {
        generate("edits/remix", imagineAiModel, function)
    }

    fun editsInpaint(
        imagineAiModel: ImagineAiModel,
        function: (response: Bitmap?, error: ErrorResponse?) -> (Unit),
    ) {
        generate("edits/inpaint", imagineAiModel, function)
    }

    fun upscale(
        imagineAiModel: ImagineAiModel,
        function: (response: Bitmap?, error: ErrorResponse?) -> (Unit),
    ) {
        generate("upscale", imagineAiModel, function)
    }

    fun variations(
        imagineAiModel: ImagineAiModel,
        function: (response: Bitmap?, error: ErrorResponse?) -> (Unit),
    ) {
        generate("generations/variations", imagineAiModel, function)
    }


}
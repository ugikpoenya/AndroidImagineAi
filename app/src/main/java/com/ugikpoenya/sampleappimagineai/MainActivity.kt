package com.ugikpoenya.sampleappimagineai

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.ugikpoenya.imagepicker.tools.saveBitmap
import com.ugikpoenya.imagineai.ImagineAi
import com.ugikpoenya.imagineai.api.ImagineAiModel
import com.ugikpoenya.sampleappimagineai.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    var bitmapResult: Bitmap? = null
    val prompt =
        "Cartoony, happy joe rogan portrait painting of a rabbit character from overwatch, armor, girly pink color scheme design, full shot, asymmetrical, splashscreen, organic painting, sunny day, matte painting, bold shapes, hard edges, cybernetic, moon in background, street art, trending on artstation, by huang guangjian and gil elvgren and sachin teng"

    var imageSource = ""
    var imageMask = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        ImagineAi(this).IMAGINE_API_KEY = ""
        generate()

        imageSource = saveBitmap(BitmapFactory.decodeResource(resources, R.drawable.image_source))
        imageMask = saveBitmap(BitmapFactory.decodeResource(resources, R.drawable.image_source_mask))

    }

    fun inpaint(v: View) {
        isLoading(true)
        val model = ImagineAiModel()
        model.prompt = "Add flower"
        model.image = imageSource
        model.mask = imageMask
        ImagineAi(this).editsInpaint(model) { response, error -> setResult(response, error) }
    }

    fun remix(v: View) {
        isLoading(true)
        val model = ImagineAiModel()
        model.image = imageSource
        model.prompt = "Add flower"
        model.style_id = 22
        ImagineAi(this).editsRemix(model) { response, error -> setResult(response, error) }
    }

    fun variations(v: View) {
        isLoading(true)
        val model = ImagineAiModel()
        model.image = imageSource
        model.prompt = "black and white"
        model.style_id = 27
        ImagineAi(this).variations(model) { response, error -> setResult(response, error) }
    }

    fun upscale(v: View) {
        isLoading(true)
        val model = ImagineAiModel()
        model.image = imageSource
        ImagineAi(this).upscale(model) { response, error -> setResult(response, error) }
    }

    fun generate(v: View) {
        generate()
    }

    fun generate() {
        isLoading(true)
        val model = ImagineAiModel()
        model.prompt = prompt
        model.style_id = 27
        ImagineAi(this).generationsImages(model) { response, error -> setResult(response, error) }
    }

    private fun setResult(response: Bitmap?, error: ImagineAi.ErrorResponse?) {
        isLoading(false)
        if (response != null) {
            bitmapResult = response
            binding?.imageView?.setImageBitmap(bitmapResult)
            Log.d("LOG", "Success " + response.width + "x" + response.height)
        }
        Log.d("LOG", "code " + error?.code)
        Log.d("LOG", "error " + error?.error)
        error?.details?.forEach {
            Log.d("LOG", "Detail $it")

        }
    }

    private fun isLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) VISIBLE else GONE
        binding?.imageView?.visibility = if (isLoading) GONE else VISIBLE
        binding?.btnGenerate?.visibility = binding?.imageView?.visibility!!
        binding?.lyButton?.visibility = binding?.imageView?.visibility!!

    }
}
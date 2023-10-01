package com.ugikpoenya.sampleappimagineai

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ugikpoenya.imagineai.ImagineAi
import com.ugikpoenya.imagineai.api.ImagineAiModel
import com.ugikpoenya.sampleappimagineai.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val prompt =
            "Cartoony, happy joe rogan portrait painting of a rabbit character from overwatch, armor, girly pink color scheme design, full shot, asymmetrical, splashscreen, organic painting, sunny day, matte painting, bold shapes, hard edges, cybernetic, moon in background, street art, trending on artstation, by huang guangjian and gil elvgren and sachin teng"

        ImagineAi(this).IMAGINE_API_KEY = ""

        val model = ImagineAiModel()
        model.prompt = prompt
        model.style_id = 27

        ImagineAi(this).generationsImages(model) { response, error ->
            if (response != null) {
                Log.d("LOG", "Success")
                binding?.imageView?.setImageBitmap(response)
            }
            Log.d("LOG", "code " + error?.code)
            Log.d("LOG", "error " + error?.error)
            error?.details?.forEach {
                Log.d("LOG", "Detail $it")

            }
        }
    }
}
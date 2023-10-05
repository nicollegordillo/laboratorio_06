package com.example.laboratorio06

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import com.bumptech.glide.Glide

class CityImageViewActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_image)

        val cityName = intent.getStringExtra("cityName")


        if (cityName != null) {
            fetchCityImage(cityName.replace(" ", "-").replace(",","").lowercase())
        }
        println(cityName)

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun fetchCityImage(cityName: String?) {
        if (cityName.isNullOrEmpty()) {

            return
        }


        val apiUrl = "https://api.teleport.org/api/urban_areas/slug:$cityName/images/"

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(apiUrl)
            .addHeader("Accept", "application/vnd.teleport.v1+json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {

                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody)


                    val photosArray = jsonResponse.getJSONArray("photos")
                    if (photosArray.length() > 0) {
                        val photoObject = photosArray.getJSONObject(0)
                        val imageObject = photoObject.getJSONObject("image")
                        val mobileImageUrl = imageObject.optString("mobile")


                        val imageView = findViewById<ImageView>(R.id.cityImageView)
                        runOnUiThread {
                            Glide.with(this@CityImageViewActivity)
                                .load(mobileImageUrl)
                                .into(imageView)
                        }
                    } else {

                    }

                } else {

                }
            }
        })

    }
}
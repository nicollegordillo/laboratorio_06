package com.example.laboratorio06

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import okhttp3.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val cities = mutableListOf<String>()
    private lateinit var citySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        citySpinner=findViewById(R.id.citySpinner)
        fetchCities()
        val button = findViewById<Button>(R.id.viewCityButton)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = adapter


        button.setOnClickListener {
            val selectedCity = citySpinner.selectedItem.toString()
            if (selectedCity.isNotEmpty()) {

                val intent = Intent(applicationContext, CityImageViewActivity::class.java)
                intent.putExtra("cityName", selectedCity)
                startActivity(intent)
            }
        }

    }

    private fun fetchCities() {

        val baseUrl = "https://api.teleport.org/api/urban_areas/"
        val endpoint = ""

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$baseUrl$endpoint")
            .addHeader("Accept", "application/vnd.teleport.v1+json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {

            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody)
                    val cityNames = mutableListOf<String>()
                    if (jsonResponse.has("_links")) {
                        val items = jsonResponse
                            .getJSONObject("_links")
                            .getJSONArray("ua:item")
                        println("hi")
                        for (i in 0 until items.length()) {
                            val cityObject = items.getJSONObject(i)
                            val cityName = cityObject.getString("name")
                            cityNames.add(cityName)
                        }
                    } else {
                        // Handle the case where "ua:item" is missing
                    }


                    runOnUiThread {
                        // Populate the spinner with city names
                        val adapter = ArrayAdapter(
                            applicationContext,
                            android.R.layout.simple_spinner_item,
                            cityNames
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        citySpinner.adapter = adapter
                    }
                } else {

                }
            }
        })

    }
}
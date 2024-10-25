package com.example.weather_app

import android.app.DownloadManager.Query
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat


import androidx.core.view.WindowInsetsCompat
import com.example.weather_app.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

//    86cdf383c06eaa5522a9ef6ae6b77ee5
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchWeatherData("delhi")

        searchcity()


    }

    private fun searchcity() {
        val searchView = binding.searchView
          searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
              override fun onQueryTextSubmit(query: String?): Boolean {
                  if (query != null) {
                      fetchWeatherData(query)
                  }
                  return true
              }

              override fun onQueryTextChange(newText: String?): Boolean {
                return true
              }

          })
    }

    private fun fetchWeatherData(cityname: String) {

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getweatherData(cityname, "86cdf383c06eaa5522a9ef6ae6b77ee5", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {

                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windspeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise
                    val sunset = responseBody.sys.sunset
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val maxtemp = responseBody.main.temp_max.toString()
                    val mintemp = responseBody.main.temp_min.toString()

                    binding.Temperature.text= "$temperature °C"
                    binding.Weather.text = condition
                    binding.MaxTemp.text = "Max Temp: $maxtemp °C"
                    binding.MinTemp.text = "Min Temp: $mintemp °C"
                    binding.Humidity.text = "$humidity %"
                    binding.WindSpeed.text = "$windspeed m/s"
                    binding.Sunrise.text= "$sunrise"
                    binding.Sunset.text= "$sunset"
                    binding.Sea.text = "$seaLevel hPa"
                    binding.Condition.text = condition

                    binding.Day.text= dayname(System.currentTimeMillis())
                        binding.Date.text= date()
                        binding.CityName.text="$cityname"

                    changebackgroundimage(condition)




//                    Log.d("Tag", "Temperature is: $temperature")

                }
            }




            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }
    private fun changebackgroundimage(conditions: String) {

        when (conditions){
            "Haze", "Partly Cloud", "Clouds", "Mist", "overcast" ->{
                binding.root.setBackgroundResource(R.drawable.haze_cloud)
                binding.imageView.setBackgroundResource(R.drawable.clouds)
            }
            "Clear Sky", "Sunny", "Clear", "Smoke"->{
                binding.root.setBackgroundResource(R.drawable.sunny_sky_bc)
                binding.imageView.setBackgroundResource(R.drawable.baseline_sunny_24)
            }

            "Light Rain", "Rain", "Heavy Rain", "Moderate Rain", "Showers"->{
                binding.root.setBackgroundResource(R.drawable.rain_bc)
                binding.imageView.setBackgroundResource(R.drawable.baseline_umbrella_24)
            }

            "Light Snow", "Snow", "Heavy Snow", "Blizzard", "Moderate Snow"->{
                binding.root.setBackgroundResource(R.drawable.snow_bc)
                binding.imageView.setBackgroundResource(R.drawable.baseline_cloudy_snowing_24)
            }
        }
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun dayname (timestamp:Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}




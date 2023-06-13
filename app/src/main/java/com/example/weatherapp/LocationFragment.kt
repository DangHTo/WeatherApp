package com.example.weatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class LocationFragment : Fragment() {

    private val model: LocationViewModel by activityViewModels()
    private val API: String = "9bfa7628ec79a98c32a6f59b519e1d2d"
    private var CITY: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.location_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.location_back_button).setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        CITY = model.locationsList?.get(model.position).toString()
        Log.d("CITY IN LOCATION", CITY.toString())

        var picture = view.findViewById<ImageView>(R.id.imageView)
        when(model.weatherItemsList.get(model.position).weather){
            "Sunny" -> picture.setImageResource(R.drawable.sun_png)
            "Snow" -> picture.setImageResource(R.drawable.snow_png)
            "Clouds" -> picture.setImageResource(R.drawable.clouds_png)
            "Mist" -> picture.setImageResource(R.drawable.clouds_png)
            else -> picture.setImageResource(R.drawable.sun_png)
        }

        weatherTask().execute()
    }


    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt*1000)
                )
                val temp = main.getString("temp")+"°C"
                val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
                val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")

                view?.findViewById<TextView>(R.id.location_name_text)?.text = address
                view?.findViewById<TextView>(R.id.updatedText)?.text =  updatedAtText
                view?.findViewById<TextView>(R.id.weatherDesc)?.text = weatherDescription.capitalize()
                view?.findViewById<TextView>(R.id.minTemp)?.text = tempMin
                view?.findViewById<TextView>(R.id.maxTemp)?.text = tempMax
                view?.findViewById<TextView>(R.id.sunrise)?.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                view?.findViewById<TextView>(R.id.sunset)?.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
                view?.findViewById<TextView>(R.id.wind)?.text = windSpeed
                view?.findViewById<TextView>(R.id.pressure)?.text = pressure
                view?.findViewById<TextView>(R.id.humidity)?.text = humidity

            } catch (e: Exception) {

            }

        }
    }

}
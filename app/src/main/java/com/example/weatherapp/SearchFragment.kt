package com.example.weatherapp

import android.os.AsyncTask
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.android.gms.common.internal.ImagesContract.URL
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.net.URL

class SearchFragment : Fragment() {

    private val model: LocationViewModel by activityViewModels()

    private lateinit var database: DatabaseReference

    private val API: String = "9bfa7628ec79a98c32a6f59b519e1d2d"
    private var CITY: String = ""

    private var response: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.search_back_button).setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }


        view.findViewById<ImageButton>(R.id.search_add_button).setOnClickListener{
            weatherTask().execute()
        }

        database = Firebase.database.reference

    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            CITY = view?.findViewById<EditText>(R.id.search_edit_text)?.text.toString()
            Log.d("Search Text", CITY)
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?q=${CITY}&appid=${API}").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                Log.d("Exception", e.toString())
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            Log.d("Response", result.toString())
            if (result.toString().equals("null")) {
                Toast.makeText(view?.context, "City not found in database", Toast.LENGTH_LONG)
                    .show()
                view?.findViewById<TextView>(R.id.toastText)?.text = "City not found in database"
            }


            else if (model.locationsList?.isEmpty() == false) {
                var JSONObject = JSONObject(result)

                if (model.locationsList!!.contains(JSONObject.getString("name"))) {
                    Toast.makeText(
                        view?.context,
                        "City already in your cities list",
                        Toast.LENGTH_LONG
                    ).show()
                    view?.findViewById<TextView>(R.id.toastText)?.text = "City already in your cities list"
                } else {
                    Toast.makeText(
                        view?.context,
                        "City successfully added to your list",
                        Toast.LENGTH_LONG
                    ).show()
                    view?.findViewById<TextView>(R.id.toastText)?.text = "City successfully added to your list"

                    model.locationsList?.add(JSONObject.getString("name"))

                    val main = JSONObject.getJSONObject("main")
                    val weather = JSONObject.getJSONArray("weather").getJSONObject(0)
                    Log.d("Weather", weather.toString())

                    var newWeatherItem = WeatherItem(
                        JSONObject.getString("name"),
                        main.getDouble("temp") - 273.0, main.getDouble("humidity"),
                        weather.getString("main")
                    )
                    model.weatherItemsList?.add(newWeatherItem)
                    model.weatherItemsLiveData?.postValue(model.weatherItemsList)
                    database.setValue(model.weatherItemsList)
                }
            } else {
                var JSONObject = JSONObject(result)

                Toast.makeText(
                    view?.context,
                    "City successfully added to your list",
                    Toast.LENGTH_LONG
                ).show()
                view?.findViewById<TextView>(R.id.toastText)?.text = "City successfully added to your list"

                model.locationsList?.add(JSONObject.getString("name"))

                val main = JSONObject.getJSONObject("main")
                val weather = JSONObject.getJSONArray("weather").getJSONObject(0)
                Log.d("Weather", weather.toString())

                var newWeatherItem = WeatherItem(
                    JSONObject.getString("name"),
                    main.getDouble("temp") - 273.0, main.getDouble("humidity"),
                    weather.getString("main")
                )
                model.weatherItemsList?.add(newWeatherItem)
                model.weatherItemsLiveData?.postValue(model.weatherItemsList)
                database.setValue(model.weatherItemsList)
            }
        }
    }
}
package com.example.weatherapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject

class LocationViewModel(application: Application): AndroidViewModel(application){
    var something = "PLEASE WORK"
    var position = -1

    var weatherItemsList: ArrayList<WeatherItem> = ArrayList<WeatherItem>()
    var locationsList: ArrayList<String> = ArrayList<String>()

    var weatherItemsLiveData: MutableLiveData<ArrayList<WeatherItem>>? = null
}
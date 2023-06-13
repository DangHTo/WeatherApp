package com.example.weatherapp

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    lateinit var navigationTool: NavController
    private val model: LocationViewModel by activityViewModels()

    lateinit var recyclerAdapter: WeatherItemListAdapter
    private lateinit var database: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationTool = Navigation.findNavController(view)

        database = Firebase.database.reference

        view.findViewById<Button>(R.id.Add_City_Button).setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_searchFragment)
        }

        view?.findViewById<RecyclerView>(R.id.recyclerView)?.apply{
            layoutManager = LinearLayoutManager(view?.context)
            recyclerAdapter = WeatherItemListAdapter()
            adapter = recyclerAdapter
        }

        view.findViewById<Button>(R.id.remove_from_db).setOnClickListener{
            model.weatherItemsList = ArrayList<WeatherItem>()
            model.locationsList = ArrayList<String>()
            model.weatherItemsLiveData?.postValue( model.weatherItemsList)
            database.setValue(model.weatherItemsList)
        }


        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the U
                model.weatherItemsList?.clear()
                model.locationsList?.clear()

                for(newSnapshot: DataSnapshot in dataSnapshot.children){
                    var weatherItem: WeatherItem? = newSnapshot.getValue(WeatherItem::class.java)

                    if (weatherItem != null) {
                        model.weatherItemsList?.add(weatherItem)
                        model.locationsList.add(weatherItem.city)
                    }
                }

                Log.d("WHAT IS THIS", model.weatherItemsList.toString())
                recyclerAdapter.setWeatherItems(model.weatherItemsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(postListener)




        if (model.weatherItemsList != null) {
            recyclerAdapter.setWeatherItems(model.weatherItemsList)
        }

        model.weatherItemsLiveData?.observe(
            viewLifecycleOwner,
            { weatherItems ->
                weatherItems?.let { recyclerAdapter.setWeatherItems(it as ArrayList<WeatherItem>) }
            }
        )

    }

    inner class WeatherItemListAdapter():
        RecyclerView.Adapter<WeatherItemListAdapter.LocationViewHolder>(){

        private var weatherList = emptyList<WeatherItem?>()


        internal fun setWeatherItems(locations: ArrayList<WeatherItem>) {
            if (locations != null) {
                this.weatherList = locations
            }
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return weatherList.size
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {


            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view, parent, false)
            return LocationViewHolder(v)
        }

        override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {

            holder.view.findViewById<TextView>(R.id.city).text=weatherList[position]?.city
            holder.view.findViewById<TextView>(R.id.celsius).text=weatherList[position]?.temperatureCelsius.toString() + " C"
            holder.view.findViewById<TextView>(R.id.humidity).text=weatherList[position]?.humidity.toString() + "%"

            var picture = holder.view.findViewById<ImageView>(R.id.poster)
            when(weatherList[position]?.weather){
                "Sunny" -> picture.setImageResource(R.drawable.sun_png)
                "Snow" -> picture.setImageResource(R.drawable.snow_png)
                "Clouds" -> picture.setImageResource(R.drawable.clouds_png)
                "Mist" -> picture.setImageResource(R.drawable.clouds_png)
                else -> picture.setImageResource(R.drawable.sun_png)
            }


            holder.itemView.setOnClickListener(){
                model.position = position
                Log.d("POSITION", position.toString())
                Log.d("ArrayList", model.locationsList.toString())
                Log.d("Weather Items ArrayList", model.weatherItemsList.toString())

                navigationTool.navigate(R.id.action_homeFragment_to_locationFragment)
            }

        }

        inner class LocationViewHolder(val view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

            override fun onClick(itemView: View?){

            }


        }
    }



}



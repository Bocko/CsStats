package hu.bme.aut.android.stats.detail

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import hu.bme.aut.android.stats.R
import hu.bme.aut.android.stats.databinding.ActivityInspectBinding
import hu.bme.aut.android.stats.model.ItemPrice.ItemPriceData
import hu.bme.aut.android.stats.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class InspectActivity : AppCompatActivity(),CoroutineScope {

    lateinit var binding: ActivityInspectBinding
    private val TAG = "ItemInspect"

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var playerDataHolder: PlayerDataHolder? = null

    private val imgURL = "https://steamcommunity-a.akamaihd.net/economy/image/"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityInspectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var intent = intent
        var mhName = intent.getStringExtra("name")
        var icon = intent.getStringExtra("icon")
        var color = intent.getStringExtra("color")
        loadItemData(mhName!!)

        binding.llBg.setBackgroundColor(Color.parseColor("#${color}"))
        binding.tvItemName.text = mhName
        Glide.with(binding.root)
            .load("${imgURL}${icon}")
            .transition(DrawableTransitionOptions().crossFade())
            .into(binding.ivItemImg)
    }

    private fun loadItemData(mhName: String) = launch{
        NetworkManager.getItemPrice(mhName)!!.enqueue(object : Callback<ItemPriceData?> {

            override fun onResponse(call: Call<ItemPriceData?>, response: Response<ItemPriceData?>) {

                Log.d(TAG,"Item Price onResponse: " + response.code() + " - " + response.message())
                if (response.isSuccessful) {
                    displayItemData(response.body())
                } else {
                    if(response.code() == 500){
                        Toast.makeText(binding.root.context,"Item Not Found", Toast.LENGTH_SHORT).show()
                        displayItemData(response.body())
                    }else{
                        Toast.makeText(binding.root.context,"Inventory Error: " + response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ItemPriceData?>, throwable: Throwable) {
                throwable.printStackTrace()
                Toast.makeText(binding.root.context,"Network request error occurred, check LOG", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayItemData(receivedItemData: ItemPriceData?) {
        if (receivedItemData != null) {
            binding.tvItemMP.text = if(receivedItemData.median_price !=null) receivedItemData.median_price else "NA"
            binding.tvItemLP.text = if(receivedItemData.lowest_price !=null) receivedItemData.lowest_price else "NA"
            binding.tvItemVolume.text = if(receivedItemData.volume !=null) receivedItemData.volume else "NA"
        } else {
            binding.tvMP.text = getString(R.string.not_found)
            binding.tvMP.gravity = Gravity.CENTER
            binding.tvLP.visibility = View.GONE
            binding.tvVolume.visibility = View.GONE
        }

    }
}
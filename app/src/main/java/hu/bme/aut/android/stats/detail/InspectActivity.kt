package hu.bme.aut.android.stats.detail

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import hu.bme.aut.android.stats.R
import hu.bme.aut.android.stats.databinding.ActivityInspectBinding
import hu.bme.aut.android.stats.model.ItemPrice.ItemPriceData
import hu.bme.aut.android.stats.model.itemInfo.ItemInfoData
import hu.bme.aut.android.stats.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class InspectActivity : AppCompatActivity(),CoroutineScope {

    lateinit var binding: ActivityInspectBinding
    private val TAG = "ItemInspect"

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val imgURL = "https://steamcommunity-a.akamaihd.net/economy/image/"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityInspectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val mhName = intent.getStringExtra("name")
        val icon = intent.getStringExtra("icon")
        val color = intent.getStringExtra("color")
        val inspect = intent.getStringExtra("inspect")
        val stickerPics = intent.getStringArrayListExtra("stickerPics")
        val stickerNames = intent.getStringExtra("stickerNames")
        val stickerNamesSplit = stickerNames?.split(", ")

        if(stickerPics == null){
            binding.llStickers.visibility = View.GONE
        } else {
            for (i in 0 until 4){
                if (i < stickerPics.size){
                    Glide.with(binding.root)
                            .load(stickerPics[i])
                            .transition(DrawableTransitionOptions().crossFade())
                            .into(intToImageView(i))
                    intToTextView(i).text = stickerNamesSplit?.get(i)
                } else {
                    intToImageView(i).visibility = View.GONE
                    intToTextView(i).visibility = View.GONE
                }
            }
        }

        loadItemData(mhName!!)
        if (inspect != null) {
            loadItemInfo(inspect)
        } else {
            binding.tvitem.visibility = View.GONE
            binding.tvItemFloat.visibility = View.GONE
            binding.tvFloat.visibility = View.GONE
            binding.tvItemPaintSeed.visibility = View.GONE
            binding.tvPaintSeed.visibility = View.GONE
            binding.tvItemOrigin.visibility = View.GONE
            binding.tvOrigin.visibility = View.GONE
            binding.tvItemFloatRange.visibility = View.GONE
            binding.tvFloatRange.visibility = View.GONE
        }

        val finalColor = Color.parseColor("#${color}")
        binding.tvLP.setBackgroundColor(finalColor)
        binding.tvMP.setBackgroundColor(finalColor)
        binding.tvVolume.setBackgroundColor(finalColor)
        binding.tvFloat.setBackgroundColor(finalColor)
        binding.tvPaintSeed.setBackgroundColor(finalColor)
        binding.tvOrigin.setBackgroundColor(finalColor)
        binding.tvFloatRange.setBackgroundColor(finalColor)

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

    private fun intToImageView(i:Int): ImageView{
        when(i){
            0 -> return binding.ivSticker1
            1 -> return binding.ivSticker2
            2 -> return binding.ivSticker3
            3 -> return binding.ivSticker4
        }
        return binding.ivSticker4
    }

    private fun intToTextView(i:Int): TextView{
        when(i){
            0 -> return binding.tvSticker1
            1 -> return binding.tvSticker2
            2 -> return binding.tvSticker3
            3 -> return binding.tvSticker4
        }
        return binding.tvSticker4
    }

    private fun loadItemInfo(url: String) = launch{
        NetworkManager.getItemInfo(url)!!.enqueue(object : Callback<ItemInfoData?> {

            override fun onResponse(call: Call<ItemInfoData?>, response: Response<ItemInfoData?>) {

                Log.d(TAG,"Item Info onResponse: " + response.code() + " - " + response.message())
                if (response.isSuccessful) {
                    displayItemInfo(response.body())
                } else {
                    Toast.makeText(binding.root.context,"Item Error: " + response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ItemInfoData?>, throwable: Throwable) {
                throwable.printStackTrace()
                Toast.makeText(binding.root.context,"Network request error occurred, check LOG", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayItemInfo(receivedItemInfo: ItemInfoData?) {
        binding.tvItemFloat.text = receivedItemInfo?.iteminfo?.floatvalue.toString()
        binding.tvItemPaintSeed.text = receivedItemInfo?.iteminfo?.paintseed.toString()
        binding.tvItemOrigin.text = receivedItemInfo?.iteminfo?.origin_name.toString()
        binding.tvItemFloatRange.text = binding.root.context.resources.getString(R.string.floatRange,receivedItemInfo?.iteminfo?.min.toString(),receivedItemInfo?.iteminfo?.max.toString())
    }
}
package hu.bme.aut.android.stats.detail.fragment.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import hu.bme.aut.android.stats.databinding.ItemFriendBinding
import hu.bme.aut.android.stats.databinding.ItemInventoryBinding
import hu.bme.aut.android.stats.databinding.ItemPlayerBinding
import hu.bme.aut.android.stats.menu.adapter.MenuAdapter
import hu.bme.aut.android.stats.model.inventory.InventoryData
import hu.bme.aut.android.stats.model.inventory.InventoryFullItem
import hu.bme.aut.android.stats.model.profile.Player
import hu.bme.aut.android.stats.model.profile.ProfileData
import hu.bme.aut.android.stats.model.url.UrlData
import hu.bme.aut.android.stats.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class InventoryAdapter : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    private var invenotry: MutableList<InventoryFullItem?> = ArrayList()
    private val imgURL = "https://steamcommunity-a.akamaihd.net/economy/image/"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = InventoryViewHolder(
            ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = invenotry[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = invenotry.size

    fun addItems(ID: InventoryData) {
        ID.rgInventory?.forEach {
            val decs = "${it.value.classid}_${it.value.instanceid}"
            val item = InventoryFullItem()
            item.amount = it.value.amount
            item.decs = ID.rgDescriptions?.get(decs)
            invenotry.add(item)
            notifyItemInserted(invenotry.size - 1)
        }
    }

    inner class InventoryViewHolder(val binding: ItemInventoryBinding): RecyclerView.ViewHolder(binding.root) {
        var item: InventoryFullItem? = null

        fun bind(newItem: InventoryFullItem?) {
            item = newItem!!
            binding.tvItemName.text = item?.decs?.market_name
            binding.tvItemAmount.text = "Amount: ${item?.amount}"
            item?.decs?.tags?.forEach {
                if (!it.color.isNullOrEmpty()){
                    binding.llBorder.setBackgroundColor(Color.parseColor("#${it.color}"))
                }
            }
            Glide.with(binding.root)
                    .load("${imgURL}${item?.decs?.icon_url}")
                    .transition(DrawableTransitionOptions().crossFade())
                    .into(binding.ivItemImg)
        }
    }
}
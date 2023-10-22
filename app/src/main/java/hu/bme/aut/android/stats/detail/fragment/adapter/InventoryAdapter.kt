package hu.bme.aut.android.stats.detail.fragment.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import hu.bme.aut.android.stats.R
import hu.bme.aut.android.stats.databinding.ItemInventoryBinding
import hu.bme.aut.android.stats.model.inventory.DescriptionItem
import hu.bme.aut.android.stats.model.inventory.InventoryData
import hu.bme.aut.android.stats.model.inventory.InventoryFullItem

class InventoryAdapter(private val listener: OnItemSelectedListener) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>(){

    private var invenotry: MutableList<InventoryFullItem?> = ArrayList()
    private var invenotryAll: MutableList<InventoryFullItem?> = ArrayList()
    private val imgURL = "https://steamcommunity-a.akamaihd.net/economy/image/"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = InventoryViewHolder(
            ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = invenotry[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = invenotry.size

    fun addItems(data: InventoryData) {
        setupInventory(data)
    }

    inner class InventoryViewHolder(val binding: ItemInventoryBinding): RecyclerView.ViewHolder(binding.root) {
        var item: InventoryFullItem? = null

        init {
            binding.root.setOnClickListener {
                listener.onItemSelected(item!!)
            }
        }

        fun bind(newItem: InventoryFullItem?) {
            item = newItem!!
            binding.tvItemName.text = item?.desc?.market_name
            binding.tvItemAmount.text = binding.root.context.resources.getString(R.string.amountx,item?.amount.toString())
            item?.desc?.tags?.forEach {
                if (!it.color.isNullOrEmpty()){
                    binding.llBorder.setBackgroundColor(Color.parseColor("#${it.color}"))
                }
            }
            Glide.with(binding.root)
                    .load("${imgURL}${item?.desc?.icon_url}")
                    .transition(DrawableTransitionOptions().crossFade())
                    .into(binding.ivItemImg)
        }
    }

    interface OnItemSelectedListener {
        fun onItemSelected(item: InventoryFullItem)
    }

    private fun setupInventory(inventoryData: InventoryData) {
        val descriptionMap: Map<String, DescriptionItem> = createDescriptionMap(inventoryData.descriptions)

        val itemList: MutableList<InventoryFullItem?> = ArrayList()
        var inIt = false

        for(asset in inventoryData.assets!!) {
            val descKey = "${asset.classid}_${asset.instanceid}"
            val item = InventoryFullItem()
            item.assetid = asset.assetid
            item.amount = 1
            item.desc = descriptionMap[descKey]
            if(itemList.size == 0){
                itemList.add(item)
            }else{
                for (it in itemList){
                    if(it?.desc?.market_name.equals(item.desc?.market_name)){
                        if (it?.desc?.type!!.contains("Container") || it.desc?.type!!.contains("Graffiti") || it.desc?.type!!.contains("Sticker")){
                            it.amount = it.amount?.plus(1)
                            inIt = false
                            break
                        }
                    }else{
                        inIt = true
                    }
                }
                if (inIt){
                    itemList.add(item)
                }
            }
        }
        invenotryAll = itemList
        invenotry = invenotryAll
        sortByRarity(true)
    }

    private fun createDescriptionMap(descriptionsList: List<DescriptionItem>?): MutableMap<String, DescriptionItem> {
        val map: MutableMap<String, DescriptionItem> = mutableMapOf()

        descriptionsList?.forEach {
            map["${it.classid}_${it.instanceid}"] = it
        }

        return map
    }

    fun sortByRarity(desc: Boolean) {

        val comparator = Comparator { g1: InventoryFullItem, g2: InventoryFullItem ->
            val g1Color = getRarityColorFromInventoryFullItem(g1)
            val g2Color = getRarityColorFromInventoryFullItem(g2)

            return@Comparator (colorToInt(g2Color) - colorToInt(g1Color)) * if (desc) 1 else -1
        }

        invenotry = invenotry.sortedWith(comparator).toMutableList()
        invenotryAll = invenotryAll.sortedWith(comparator).toMutableList()

        notifyItemRangeChanged(0, invenotry.size)
    }

    private fun getRarityColorFromInventoryFullItem(item: InventoryFullItem): String
    {
        var color = ""
        run getColorBlock@{
            item.desc?.tags?.forEach {
                if (it.category.equals("Rarity")) {
                    color = it.color!!
                    return@getColorBlock
                }
            }
        }
        return color
    }

    fun sortByName(desc: Boolean){

        val comparator = Comparator { g1: InventoryFullItem, g2: InventoryFullItem ->
            if (desc) {
                return@Comparator g2.desc?.market_name?.compareTo(g1.desc?.market_name!!,ignoreCase = true)!!
            }else{
                return@Comparator g1.desc?.market_name?.compareTo(g2.desc?.market_name!!,ignoreCase = true)!!
            }
        }

        invenotry = invenotry.sortedWith(comparator).toMutableList()
        invenotryAll = invenotryAll.sortedWith(comparator).toMutableList()
        notifyItemRangeChanged(0, invenotry.size)
    }

    private fun colorToInt(color:String): Int{
        when(color){
            "b0c3d9" -> return 1
            "5e98d9" -> return 2
            "4b69ff" -> return 3
            "8847ff" -> return 4
            "d32ce6" -> return 5
            "eb4b4b" -> return 6
            "e4ae39" -> return 7
        }
        return 0
    }

    fun search(searchText: String){
        if (searchText.isEmpty()){
            invenotry.clear()
            invenotry.addAll(invenotryAll)
        } else {
            invenotry.clear()
            invenotryAll.forEach {
                if (it?.desc?.market_name!!.contains(searchText,ignoreCase = true)){
                    invenotry.add(it)
                }
            }
        }
        notifyDataSetChanged()
    }
}
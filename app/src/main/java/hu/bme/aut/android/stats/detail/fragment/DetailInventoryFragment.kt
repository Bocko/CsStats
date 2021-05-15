package hu.bme.aut.android.stats.detail.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.stats.databinding.FragmentDetailFriendlistBinding
import hu.bme.aut.android.stats.databinding.FragmentDetailInventoryBinding
import hu.bme.aut.android.stats.databinding.FragmentDetailProfileBinding
import hu.bme.aut.android.stats.detail.PlayerDataHolder
import hu.bme.aut.android.stats.detail.fragment.adapter.FriendAdapter
import hu.bme.aut.android.stats.detail.fragment.adapter.InventoryAdapter
import hu.bme.aut.android.stats.menu.adapter.MenuAdapter
import hu.bme.aut.android.stats.model.inventory.InventoryFullItem

class DetailInventoryFragment : Fragment(){

    private var _binding: FragmentDetailInventoryBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: InventoryAdapter

    private var playerDataHolder: PlayerDataHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerDataHolder = if (activity is PlayerDataHolder) {
            activity as PlayerDataHolder?
        } else {
            throw RuntimeException(
                    "Activity must implement PlayerDataHolder interface!"
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentDetailInventoryBinding.inflate(LayoutInflater.from(context))
        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        binding.rvInventory.layoutManager = LinearLayoutManager(binding.rvInventory.context)
        adapter = InventoryAdapter()
        if(playerDataHolder?.getInventory()?.rgInventory != null) {
            setupInventory()
        }
        binding.rvInventory.adapter = adapter
    }

    private fun setupInventory(){
        val inv = playerDataHolder?.getInventory()!!
        var itemList: MutableList<InventoryFullItem?> = ArrayList()
        var inIt = false

        for(entry in inv.rgInventory!!) {
            val decs = "${entry.value.classid}_${entry.value.instanceid}"
            val item = InventoryFullItem()
            item.amount = entry.value.amount
            item.decs = inv.rgDescriptions?.get(decs)
            if(itemList.size == 0){
                itemList.add(item)
            }else{
                for (it in itemList){
                    if(it?.decs?.market_name.equals(item.decs?.market_name)){
                        it?.amount = it?.amount?.toInt()?.plus(1).toString()
                        inIt = false
                        break
                    }else{
                        inIt = true
                    }
                }
                if (inIt){
                    itemList.add(item)
                }
            }
        }
        itemList = sortInventory(itemList)
        adapter.addItems(itemList)
    }

    private fun sortInventory(itemList: MutableList<InventoryFullItem?>): MutableList<InventoryFullItem?> {

        for (i in 0 until itemList.size-1){
            var min:Int = i
            for (j in i+1 until itemList.size){
                var color = ""
                var colorMin = ""
                itemList[j]?.decs?.tags?.forEach {
                    if (!it.color.isNullOrEmpty()){
                        color = it.color!!
                    }
                }
                itemList[min]?.decs?.tags?.forEach {
                    if (!it.color.isNullOrEmpty()){
                        colorMin = it.color!!
                    }
                }
                if (colorToInt(color) > colorToInt(colorMin)){
                    min = j
                }
            }
            if(min != i){
                itemList[i] = itemList[min].also { itemList[min] = itemList[i] }
            }
        }

        return itemList
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
            else -> 0
        }
        return 0
    }
}
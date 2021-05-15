package hu.bme.aut.android.stats.detail.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.stats.databinding.FragmentDetailFriendlistBinding
import hu.bme.aut.android.stats.databinding.FragmentDetailInventoryBinding
import hu.bme.aut.android.stats.databinding.FragmentDetailProfileBinding
import hu.bme.aut.android.stats.detail.PlayerDataHolder
import hu.bme.aut.android.stats.detail.fragment.adapter.FriendAdapter
import hu.bme.aut.android.stats.detail.fragment.adapter.InventoryAdapter
import hu.bme.aut.android.stats.menu.adapter.MenuAdapter
import hu.bme.aut.android.stats.model.inventory.InventoryData
import hu.bme.aut.android.stats.model.inventory.InventoryFullItem
import hu.bme.aut.android.stats.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class DetailInventoryFragment : Fragment(),CoroutineScope{

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private var _binding: FragmentDetailInventoryBinding? = null
    private val binding get() = _binding!!

    private val TAG = "InventoryActivity"

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
        binding.btnLoad.setOnClickListener {
            loadInvData(playerDataHolder?.getProfileData()?.response?.players?.get(0)?.steamid!!)
        }
        return binding.root
    }

    private fun initRecyclerView() {
        binding.rvInventory.layoutManager = LinearLayoutManager(binding.rvInventory.context)
        adapter = InventoryAdapter()

        binding.rvInventory.adapter = adapter
    }

    private fun loadInvData(playerID: Long) = launch{
        NetworkManager.getInventory(playerID)!!.enqueue(object : Callback<InventoryData?> {

            override fun onResponse(call: Call<InventoryData?>, response: Response<InventoryData?>) {

                Log.d(TAG,"Inv onResponse: " + response.code() + " - " + response.message())
                if (response.isSuccessful) {
                    displayInvData(response.body())
                } else {
                    if(response.code() == 429){
                        Toast.makeText(binding.root.context,"Too Many Requests! Please Wait a bit!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(binding.root.context,"Inventory Error: " + response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<InventoryData?>, throwable: Throwable) {
                throwable.printStackTrace()
                Toast.makeText(binding.root.context,"Network request error occurred, check LOG", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayInvData(receivedInvData: InventoryData?) {
        if(receivedInvData?.success.equals("true")){
            adapter.addItems(receivedInvData!!)
            binding.btnLoad.visibility = View.GONE
            binding.rvInventory.visibility = View.VISIBLE
        } else {
            binding.btnLoad.visibility = View.GONE
            binding.tvInfo.visibility = View.VISIBLE
        }
    }
}
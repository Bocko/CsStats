package hu.bme.aut.android.stats.detail.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.stats.R
import hu.bme.aut.android.stats.databinding.FragmentDetailInventoryBinding
import hu.bme.aut.android.stats.detail.InspectActivity
import hu.bme.aut.android.stats.detail.PlayerDataHolder
import hu.bme.aut.android.stats.detail.fragment.adapter.InventoryAdapter
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


class DetailInventoryFragment : Fragment(),CoroutineScope, InventoryAdapter.OnItemSelectedListener, AdapterView.OnItemSelectedListener{

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

        initSpinner()
        binding.SSort.onItemSelectedListener = this

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("inventory","text: " + s.toString())
                //adapter.search(s.toString())
            }
        })

        return binding.root
    }

    private fun initRecyclerView() {
        binding.rvInventory.layoutManager = LinearLayoutManager(binding.rvInventory.context)
        adapter = InventoryAdapter(this)

        binding.rvInventory.adapter = adapter
    }

    override fun onItemSelected(item: InventoryFullItem) {
        val picRegex = "https://([\\w_-]+(?:\\.[\\w_-]+)+)([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?".toRegex()
        val nameRegex = "(Sticker|Patch): (.*)</center>".toRegex()
        val showInspectIntent = Intent()
        showInspectIntent.setClass(binding.root.context,InspectActivity::class.java)
        showInspectIntent.putExtra("icon", item.decs?.icon_url)
        showInspectIntent.putExtra("name",item.decs?.market_hash_name)
        var color = ""
        item.decs?.tags?.forEach {
            if (it.color != null){
                color = it.color!!
            }
        }
        showInspectIntent.putExtra("color",color)

        var inspect = item.decs?.actions?.get(0)?.link
        if (inspect != null){
            inspect = inspect.replace("%owner_steamid%",playerDataHolder?.getProfileData()?.response?.players?.get(0)?.steamid.toString())
            inspect = inspect.replace("%assetid%", item.id.toString())
        }

        var stickerHtml = ""
        item.decs?.descriptions?.forEach {
            if (it.value!!.contains("sticker_info")){
                stickerHtml = it.value!!
            }
        }

        val stickerPics: ArrayList<String> = ArrayList()
        var stickerNames = ""
        if(stickerHtml.isNotEmpty()){
            picRegex.findAll(stickerHtml).forEach {
                stickerPics.add(it.value)
            }
            stickerNames = nameRegex.find(stickerHtml)!!.groupValues[2]

        }

        showInspectIntent.putExtra("inspect", inspect)
        showInspectIntent.putExtra("stickerPics",stickerPics)
        showInspectIntent.putExtra("stickerNames",stickerNames)
        startActivity(showInspectIntent)
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
            binding.llInventory.visibility = View.VISIBLE
        } else {
            binding.btnLoad.visibility = View.GONE
            binding.tvInfo.visibility = View.VISIBLE
        }
    }

    private fun initSpinner(){
        ArrayAdapter.createFromResource(this.requireContext(), R.array.inventorySortArray, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.SSort.adapter = adapter
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        Log.d("inv",pos.toString())
        /*when(pos){
            0 -> adapter.name(true)
            1 -> adapter.name(false)
        }*/
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}
}
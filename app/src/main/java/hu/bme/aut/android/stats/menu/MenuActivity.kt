package hu.bme.aut.android.stats.menu

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.stats.data.PlayerDatabase
import hu.bme.aut.android.stats.data.PlayerItem
import hu.bme.aut.android.stats.databinding.ActivityMenuBinding
import hu.bme.aut.android.stats.detail.DetailActivity
import hu.bme.aut.android.stats.menu.adapter.MenuAdapter
import hu.bme.aut.android.stats.model.playercount.CountData
import hu.bme.aut.android.stats.model.profile.Player
import hu.bme.aut.android.stats.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class MenuActivity : AppCompatActivity(), MenuAdapter.OnPlayerSelectedListener, CoroutineScope{

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    lateinit var binding: ActivityMenuBinding
    lateinit var adapter: MenuAdapter
    private lateinit var database: PlayerDatabase

    private var countData: CountData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = PlayerDatabase.getDatabase(applicationContext)

        binding.toolbar.title = "Current Player Count: "

        loadPlayerCountData()
        initFab()
        initRecyclerView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            launch {
                val player = data?.getStringExtra("player")
                adapter.addPlayer(player!!,this@MenuActivity)
            }
        }
    }

    private fun initFab() {
        binding.fab.setOnClickListener{
            val showAddPlayIntent = Intent()
            showAddPlayIntent.setClass(this@MenuActivity, AddPlayerActivity::class.java)
            startActivityForResult(showAddPlayIntent,1)
        }
    }

    private fun initRecyclerView() {
        binding.rvMenu.layoutManager = LinearLayoutManager(this)
        adapter = MenuAdapter(this)
        loadPlayersInBackground()
        binding.rvMenu.adapter = adapter
    }

    override fun onPlayerSelected(player: Long?) {
        val showDetailIntent = Intent()
        showDetailIntent.setClass(this@MenuActivity, DetailActivity::class.java)
        showDetailIntent.putExtra(DetailActivity.PLAYER_NAME, player.toString())
        startActivityForResult(showDetailIntent,1)
    }

    override fun onPlayerDeleted(player: PlayerItem) {
        deletePlayerInBackground(player)
        adapter.removePlayer(player)
    }

    private fun deletePlayerInBackground(player: PlayerItem) = launch {
        withContext(Dispatchers.IO) {
            database.shoppingItemDao().deleteItem(player)
        }
    }

    override fun onPlayerAdded(player: PlayerItem) {
        addPlayerInBackgound(player)
    }

    private fun addPlayerInBackgound(item: PlayerItem) = launch {
        withContext(Dispatchers.IO) {
            database.shoppingItemDao().insert(item)
        }
    }

    private fun loadPlayersInBackground() = launch {
        val items = withContext(Dispatchers.IO) {
            database.shoppingItemDao().getAll()
        }
        adapter.update(items)
    }

    private fun loadPlayerCountData(){
        NetworkManager.getPlayerCount()!!.enqueue(object : Callback<CountData?> {

            override fun onResponse(call: Call<CountData?>,response: Response<CountData?>) {
                Log.d(TAG, "player count onResponse: " + response.code())
                if (response.isSuccessful) {
                    displayPlayerCountData(response.body())
                } else {
                    Toast.makeText(this@MenuActivity, "Player Count Error: " + response.message(), Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<CountData?>,throwable: Throwable) {
                throwable.printStackTrace()
                Toast.makeText(this@MenuActivity, "Network request error occurred, check LOG", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayPlayerCountData(receivedWeatherData: CountData?) {
        countData = receivedWeatherData
        binding.toolbar.title = "Current Player Count: " + countData?.response?.player_count.toString()
    }

}
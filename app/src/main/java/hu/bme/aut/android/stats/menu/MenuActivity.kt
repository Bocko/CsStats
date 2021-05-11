package hu.bme.aut.android.stats.menu

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.stats.databinding.ActivityMenuBinding
import hu.bme.aut.android.stats.detail.DetailActivity
import hu.bme.aut.android.stats.menu.adapter.MenuAdapter
import hu.bme.aut.android.stats.menu.fragment.AddPlayerDialogFragment
import hu.bme.aut.android.stats.model.playercount.CountData
import hu.bme.aut.android.stats.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity(), MenuAdapter.OnPlayerSelectedListener,
        AddPlayerDialogFragment.AddPlayerDialogListener{


    lateinit var binding: ActivityMenuBinding
    lateinit var adapter: MenuAdapter



    private var countData: CountData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "Current Player Count: "
        loadPlayerCountData()
        initFab()
        initRecyclerView()
    }

    private fun initFab() {
        binding.fab.setOnClickListener{
            AddPlayerDialogFragment().show(supportFragmentManager, AddPlayerDialogFragment::class.java.simpleName)
        }
    }

    private fun initRecyclerView() {
        binding.rvMenu.layoutManager = LinearLayoutManager(this)
        adapter = MenuAdapter(this)
        adapter.addPlayer("bockoofficial")
        binding.rvMenu.adapter = adapter
    }

    override fun onPlayerSelected(player: Long?) {
        val showDetailIntent = Intent()
        showDetailIntent.setClass(this@MenuActivity, DetailActivity::class.java)
        showDetailIntent.putExtra(DetailActivity.PLAYER_NAME, player.toString())
        startActivity(showDetailIntent)
    }

    override fun onPlayerDeleted(position: Int) {
        adapter.removePlayer(position)
    }

    override fun onPlayerAdded(idOrUrl: String) {
        adapter.addPlayer(idOrUrl)
    }

    private  fun loadPlayerCountData(){

        NetworkManager.getPlayerCount()!!.enqueue(object : Callback<CountData?> {

            override fun onResponse(call: Call<CountData?>,response: Response<CountData?>) {
                Log.d(TAG, "player count onResponse: " + response.code())
                if (response.isSuccessful) {
                    displayPlayerCountData(response.body())
                } else {
                    Toast.makeText(this@MenuActivity, "Error: " + response.message(), Toast.LENGTH_SHORT).show()
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
package hu.bme.aut.android.stats.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.stats.R
import hu.bme.aut.android.stats.databinding.ActivityGamesBinding
import hu.bme.aut.android.stats.detail.adapter.GamesAdapter
import hu.bme.aut.android.stats.model.games.GamesData
import hu.bme.aut.android.stats.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class GamesActivity : AppCompatActivity(),CoroutineScope,AdapterView.OnItemSelectedListener {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    lateinit var binding: ActivityGamesBinding
    lateinit var adapter: GamesAdapter

    private val TAG = "LibraryActivity"
    private var steamID: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        steamID = intent.getStringExtra("steamID")?.toLong()
        initRecyclerView()
        initSpinner()
        loadGamesData()
        binding.SSort.onItemSelectedListener = this
    }

    private fun initRecyclerView() {
        binding.rvGames.layoutManager = LinearLayoutManager(binding.rvGames.context)
        adapter = GamesAdapter()
        binding.rvGames.adapter = adapter
    }

    private fun loadGamesData() = launch{
        NetworkManager.getAllGames(steamID)!!.enqueue(object : Callback<GamesData?> {

            override fun onResponse(call: Call<GamesData?>, response: Response<GamesData?>) {

                Log.d(TAG, "Games onResponse: " + response.code())
                if (response.isSuccessful) {
                    adapter.addItems(response.body()!!)
                    adapter.Name(true)
                } else {
                    Toast.makeText(this@GamesActivity,"Games Error:" + response.message(),Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GamesData?>,throwable: Throwable) {
                throwable.printStackTrace()
                Toast.makeText(this@GamesActivity,"Network request error occurred, check LOG",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initSpinner(){
        ArrayAdapter.createFromResource(this,R.array.gamesSortArray, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.SSort.adapter = adapter
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        when(pos){
            0 -> adapter.Name(true)
            1 -> adapter.Name(false)
            2 -> adapter.Time(true)
            3 -> adapter.Time(false)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        Log.d("games", "nothing")
    }
}
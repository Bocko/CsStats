package hu.bme.aut.android.stats.menu.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import hu.bme.aut.android.stats.databinding.ItemPlayerBinding
import hu.bme.aut.android.stats.detail.adapter.DetailPagerAdapter
import hu.bme.aut.android.stats.model.profile.Player
import hu.bme.aut.android.stats.model.profile.ProfileData
import hu.bme.aut.android.stats.model.url.UrlData
import hu.bme.aut.android.stats.network.NetworkManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class MenuAdapter(private val listener: OnPlayerSelectedListener) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val TAG = "DetailsActivity"
    private var players: MutableList<Player?> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MenuViewHolder(
            ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = players[position]
        holder.bind(item)
        holder.binding.ibRemove.setOnClickListener {
            listener.onPlayerDeleted(position)
        }
    }

    override fun getItemCount(): Int = players.size

    fun addPlayer(IDorUrl: String) {
        if(IDorUrl.isNotEmpty()){
            try {
                loadProfileData(IDorUrl)
            }catch (e :Exception){
                loadUrlData(IDorUrl)
            }

        }
    }

    fun removePlayer(position: Int) {
        players.removeAt(position)
        notifyItemRemoved(position)
        if (position < players.size) {
            notifyItemRangeChanged(position, players.size - position)
        }
    }

    inner class MenuViewHolder(val binding: ItemPlayerBinding) :
            RecyclerView.ViewHolder(binding.root) {
        var item: Player? = null

        init {
            binding.root.setOnClickListener {
                listener.onPlayerSelected(item?.steamid)
            }
        }

        fun bind(newPlayer: Player?) {
            item = newPlayer!!
            binding.playerItemNameTextView.text = item?.personaname
            Glide.with(binding.root)
                    .load(item?.avatarfull)
                    .transition(DrawableTransitionOptions().crossFade())
                    .into(binding.ivPlayerImg)
        }
    }

    interface OnPlayerSelectedListener {
        fun onPlayerSelected(player: Long?)
        fun onPlayerDeleted(position: Int)
    }

    private fun loadUrlData(playerIDorURL: String?){
        NetworkManager.getIDFromURL(playerIDorURL)!!.enqueue(object : Callback<UrlData?> {

            override fun onResponse(call: Call<UrlData?>, response: Response<UrlData?>) {
                Log.d(TAG, "url onResponse: " + response.code())
                if (response.isSuccessful) {
                    displayUrlData(response.body())
                } else {
                    Log.d(TAG, "Error: " + response.message())
                }
            }

            override fun onFailure(call: Call<UrlData?>,throwable: Throwable) {
                throwable.printStackTrace()
                Log.d(TAG, "Network request error occurred")
            }
        })
    }

    private fun displayUrlData(receivedURLData: UrlData?) {
        if (receivedURLData?.response?.success == "1".toString()){
            loadProfileData(receivedURLData.response?.steamid.toString())
        }
    }

    private fun loadProfileData(playerIDorURL: String?){
        NetworkManager.getProfile(playerIDorURL?.toLong())!!.enqueue(object : Callback<ProfileData?> {

            override fun onResponse( call: Call<ProfileData?>, response: Response<ProfileData?>) {
                Log.d(TAG, "profile onResponse: " + response.code())
                if (response.isSuccessful) {
                    displayProfileData(response.body())
                } else {
                    Log.d(TAG, "Error: " + response.message())
                }
            }

            override fun onFailure(call: Call<ProfileData?>, throwable: Throwable) {
                throwable.printStackTrace()
                Log.d(TAG, "Network request error occurred")
            }
        })
    }

    private fun displayProfileData(receivedProfileData: ProfileData?) {
        players.add(receivedProfileData?.response?.players?.get(0))
        notifyItemInserted(players.size - 1)
    }

}
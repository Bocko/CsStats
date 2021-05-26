package hu.bme.aut.android.stats.menu.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import hu.bme.aut.android.stats.data.PlayerItem
import hu.bme.aut.android.stats.databinding.ItemPlayerBinding
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
    private var players: MutableList<PlayerItem> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MenuViewHolder(
            ItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = players[position]
        holder.bind(item)
        holder.binding.ibRemove.setOnClickListener {
            listener.onPlayerDeleted(item)
        }
    }

    override fun getItemCount(): Int = players.size

    fun update(player: List<PlayerItem>) {
        players.clear()
        players.addAll(player)
        notifyDataSetChanged()
    }

    fun addPlayer(IDorUrl: String,ctx: Context) {
        if(IDorUrl.isNotEmpty()){
            try {
                loadProfileData(IDorUrl, ctx)
            } catch (e: Exception) {
                loadUrlData(IDorUrl, ctx)
            }
        }
    }

    fun removePlayer(player: PlayerItem) {
        val pos = players.indexOf(player)
        players.remove(player)
        notifyItemRemoved(pos)
        if (pos < players.size) {
            notifyItemRangeChanged(pos, players.size - pos)
        }
    }

    inner class MenuViewHolder(val binding: ItemPlayerBinding) :
            RecyclerView.ViewHolder(binding.root) {
        var item: PlayerItem? = null

        init {
            binding.root.setOnClickListener {
                listener.onPlayerSelected(item?.steam_id)
            }
        }

        fun bind(newPlayer: PlayerItem?) {
            item = newPlayer!!
            binding.playerItemNameTextView.text = item?.persona_name
            Glide.with(binding.root)
                    .load(item?.pic_url)
                    .transition(DrawableTransitionOptions().crossFade())
                    .into(binding.ivPlayerImg)
        }
    }

    interface OnPlayerSelectedListener {
        fun onPlayerAdded(player: PlayerItem)
        fun onPlayerSelected(player: Long?)
        fun onPlayerDeleted(player: PlayerItem)
    }

    private fun loadProfileData(playerIDorURL: String?,ctx: Context){
        if(!players.any { p -> p.steam_id == playerIDorURL?.toLong() }) {
            NetworkManager.getProfile(playerIDorURL?.toLong())!!.enqueue(object : Callback<ProfileData?> {

                override fun onResponse(call: Call<ProfileData?>, response: Response<ProfileData?>) {
                    Log.d(TAG, "profile onResponse: " + response.code())
                    if (response.isSuccessful) {
                        displayProfileData(response.body())
                    } else {
                        Toast.makeText(ctx, "Profile Error: " + response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ProfileData?>, throwable: Throwable) {
                    throwable.printStackTrace()
                    Toast.makeText(ctx, "Network request error occurred, check LOG", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(ctx, "Player Already In The List", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayProfileData(receivedProfileData: ProfileData?) {
        val receivedPlayer = receivedProfileData?.response?.players?.get(0)!!
        val p = PlayerItem(steam_id = receivedPlayer.steamid!!,persona_name = receivedPlayer.personaname!!,pic_url = receivedPlayer.avatarfull!!)
        listener.onPlayerAdded(p)
        players.add(p)
        notifyItemInserted(players.size - 1)
    }

    private fun loadUrlData(playerIDorURL: String?,ctx: Context){
        NetworkManager.getIDFromURL(playerIDorURL)!!.enqueue(object : Callback<UrlData?> {

            override fun onResponse(call: Call<UrlData?>, response: Response<UrlData?>) {
                Log.d(TAG, "url onResponse: " + response.code())
                if (response.isSuccessful) {
                    displayUrlData(response.body(),ctx)
                } else {
                    Toast.makeText(ctx, "URL Error: " + response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UrlData?>,throwable: Throwable) {
                throwable.printStackTrace()
                Toast.makeText(ctx, "Network request error occurred, check LOG", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayUrlData(receivedURLData: UrlData?,ctx: Context) {
        if (receivedURLData?.response?.success.equals("1")){
            loadProfileData(receivedURLData?.response?.steamid.toString(),ctx)
        } else {
            Toast.makeText(ctx, "Profile Not Found", Toast.LENGTH_SHORT).show()
        }
    }
}
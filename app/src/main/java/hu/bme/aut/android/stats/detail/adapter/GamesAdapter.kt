package hu.bme.aut.android.stats.detail.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import hu.bme.aut.android.stats.R
import hu.bme.aut.android.stats.databinding.ItemGameBinding
import hu.bme.aut.android.stats.model.games.GameItem
import hu.bme.aut.android.stats.model.games.GamesData


class GamesAdapter : RecyclerView.Adapter<GamesAdapter.GamesViewHolder>(){

    private var games: MutableList<GameItem?> = ArrayList()
    private val imgURL = "https://cdn.cloudflare.steamstatic.com/steamcommunity/public/images/apps/"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GamesViewHolder(
        ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        val item = games[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = games.size

    fun addItems(gamesData: GamesData) {
        games = gamesData.response?.games!!.toMutableList()
        notifyDataSetChanged()
    }

    inner class GamesViewHolder(val binding: ItemGameBinding): RecyclerView.ViewHolder(binding.root) {
        var item: GameItem? = null

        fun bind(newItem: GameItem?) {
            item = newItem!!
            binding.tvGameName.text = item?.name
            val forever = "%.2f".format(item?.playtime_forever!!.toDouble()/60)
            binding.tvPlayTimeForever.text = binding.root.context.resources.getString(R.string.foreverhours,forever)
            if(item?.playtime_2weeks != null) {
                val weeks = "%.2f".format(item?.playtime_2weeks!!.toDouble() / 60)
                binding.tvPlayTime2week.text = binding.root.context.resources.getString(R.string.twoweekshours,weeks)
            }else{
                binding.tvPlayTime2week.text = binding.root.context.resources.getString(R.string.twoweekshours,"0")
            }
            Glide.with(binding.root)
                .load("${imgURL}${item?.appid}/${item?.img_logo_url}.jpg")
                .transition(DrawableTransitionOptions().crossFade())
                .into(binding.ivGameImg)
        }
    }

    fun Name(desc: Boolean){
        val comparator = Comparator { g1: GameItem, g2: GameItem ->
            if (desc) {
                return@Comparator g1.name?.compareTo(g2.name!!)!!
            }
            else{
                return@Comparator g2.name?.compareTo(g1.name!!)!!
            }
        }
        games = games.sortedWith(comparator).toMutableList()
        notifyDataSetChanged()
    }

    fun Time(desc: Boolean){
        val comparator = Comparator { g1: GameItem, g2: GameItem ->
            if (desc) {
                return@Comparator g1.playtime_forever?.toInt()!! - g2.playtime_forever?.toInt()!!
            }
            else{
                return@Comparator g2.playtime_forever?.toInt()!! - g1.playtime_forever?.toInt()!!
            }
        }
        games = games.sortedWith(comparator).toMutableList()
        notifyDataSetChanged()
    }
}
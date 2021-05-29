package hu.bme.aut.android.stats.detail.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import hu.bme.aut.android.stats.R
import hu.bme.aut.android.stats.databinding.ItemGameBinding
import hu.bme.aut.android.stats.model.games.GameItem
import hu.bme.aut.android.stats.model.games.GamesData
import hu.bme.aut.android.stats.model.games.RecentlyData

class RecentlyAdapter: RecyclerView.Adapter<RecentlyAdapter.RecentlyViewHolder>(){

    private var games: MutableList<GameItem?> = ArrayList()
    private val imgURL = "https://cdn.cloudflare.steamstatic.com/steamcommunity/public/images/apps/"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecentlyViewHolder(
            ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: RecentlyViewHolder, position: Int) {
        val item = games[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = games.size

    fun addItems(gamesData: RecentlyData) {
        games = gamesData.response?.games!!.toMutableList()
        notifyDataSetChanged()
    }

    inner class RecentlyViewHolder(val binding: ItemGameBinding): RecyclerView.ViewHolder(binding.root) {
        var item: GameItem? = null

        fun bind(newItem: GameItem?) {
            item = newItem!!
            binding.tvGameName.text = item?.name
            val forever = "%.2f".format(item?.playtime_forever!!.toDouble()/60)
            val weeks = "%.2f".format(item?.playtime_2weeks!!.toDouble()/60)
            binding.tvPlayTimeForever.text = binding.root.context.resources.getString(R.string.foreverhours,forever)
            binding.tvPlayTime2week.text = binding.root.context.resources.getString(R.string.twoweekshours,weeks)
            Glide.with(binding.root)
                    .load("${imgURL}${item?.appid}/${item?.img_logo_url}.jpg")
                    .transition(DrawableTransitionOptions().crossFade())
                    .into(binding.ivGameImg)
        }
    }
}
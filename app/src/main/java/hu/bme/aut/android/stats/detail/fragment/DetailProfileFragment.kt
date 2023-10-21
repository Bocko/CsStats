package hu.bme.aut.android.stats.detail.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import hu.bme.aut.android.stats.R
import hu.bme.aut.android.stats.databinding.FragmentDetailProfileBinding
import hu.bme.aut.android.stats.detail.GamesActivity
import hu.bme.aut.android.stats.detail.PlayerDataHolder
import hu.bme.aut.android.stats.detail.fragment.adapter.RecentlyAdapter
import hu.bme.aut.android.stats.model.games.RecentlyData
import hu.bme.aut.android.stats.model.profile.Player
import hu.bme.aut.android.stats.model.profile.level.LevelData
import hu.bme.aut.android.stats.network.NetworkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class DetailProfileFragment: Fragment(),CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val TAG = "ProfileFragment"

    private var _binding: FragmentDetailProfileBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: RecentlyAdapter

    private var playerDataHolder: PlayerDataHolder? = null
    private var steamID:Long? = null

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
        _binding = FragmentDetailProfileBinding.inflate(LayoutInflater.from(context))
        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        binding.rvRecently.layoutManager = LinearLayoutManager(binding.rvRecently.context)
        adapter = RecentlyAdapter()
        binding.rvRecently.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (playerDataHolder!!.getBanData() != null){
            displayProfileData()
        }

        binding.btnGames.setOnClickListener {
            val showLibraryIntent = Intent()
            showLibraryIntent.setClass(binding.root.context, GamesActivity::class.java)
            showLibraryIntent.putExtra("steamID", steamID.toString())
            startActivity(showLibraryIntent)
        }
    }

    private fun displayProfileData() {
        val profile = playerDataHolder?.getProfileData()?.response?.players?.get(0) ?: return

        binding.tvPlayerName.text = profile.personaname
        binding.tvPlayerID.text = profile.steamid.toString()
        binding.tvPlayerProfileLink.text = profile.profileurl
        steamID = profile.steamid

        setVis(profile)
        setState(profile)
        Glide.with(this)
                .load(profile.avatarfull)
                .transition(DrawableTransitionOptions().crossFade())
                .into(binding.ivPlayerImg)
        setBans()
        loadLevelData()
        loadRecentlyData()
    }

    private fun setState(profile: Player?){
        if(profile?.gameextrainfo == null) {
            binding.tvPlayerState.text = when (profile?.personastate) {
                0 -> "Offline"
                1 -> "Online"
                2 -> "Busy"
                3 -> "Away"
                4 -> "Snooze"
                else -> ""
            }
            val color: Int = when(profile?.personastate){
                0 -> Color.LTGRAY
                else -> Color.rgb(87,203,222)
            }
            binding.tvPlayerState.setTextColor(color)
            binding.llBorder.setBackgroundColor(color)
        } else {

            binding.tvPlayerState.text = getString(R.string.playingx, profile.gameextrainfo)
            binding.tvPlayerState.setTextColor(Color.rgb(144,200,60))
            binding.llBorder.setBackgroundColor(Color.rgb(144,200,60))
        }
    }

    private fun setVis(profile: Player?){

        binding.tvPlayerCommProfileState.text = when(profile?.communityvisibilitystate){
            1 -> "Private"
            2 -> "Friends Only"
            3 -> "Public"
            else -> ""
        }
        val color: Int = when(profile?.communityvisibilitystate){
            1 -> Color.RED
            2 -> Color.rgb(255,69,0)
            else -> Color.GREEN
        }
        binding.tvPlayerCommProfileState.setTextColor(color)
    }

    private fun setBans(){

        val bans = playerDataHolder?.getBanData()?.players?.get(0)
        if (bans?.CommunityBanned!!){
            binding.tvPlayerCommBan.text = getString(R.string.banned)
            binding.tvPlayerCommBan.setTextColor(Color.RED)
        }else{
            binding.tvPlayerCommBan.text = getString(R.string.bannedNone)
            binding.tvPlayerCommBan.setTextColor(Color.GREEN)
        }
        if(bans.VACBanned!!){
            binding.tvPlayerVacBan.text = getString(R.string.banned)
            binding.tvPlayerVacBan.setTextColor(Color.RED)
        }else{
            binding.tvPlayerVacBan.text = getString(R.string.bannedNone)
            binding.tvPlayerVacBan.setTextColor(Color.GREEN)
        }
        if(bans.EconomyBan.equals("banned")){
            binding.tvPlayerTradeBan.text = getString(R.string.banned)
            binding.tvPlayerTradeBan.setTextColor(Color.RED)
        }else{
            binding.tvPlayerTradeBan.text = getString(R.string.bannedNone)
            binding.tvPlayerTradeBan.setTextColor(Color.GREEN)
        }

        binding.tvPlayerNumVacBan.text = bans.NumberOfVACBans.toString()
        binding.tvPlayerVacBanDays.text = bans.DaysSinceLastBan.toString()
        binding.tvPlayerNumBan.text = bans.NumberOfGameBans.toString()
    }

    private fun loadRecentlyData() = launch{
        NetworkManager.getRecentlyGames(steamID)!!.enqueue(object : Callback<RecentlyData?> {

            override fun onResponse(call: Call<RecentlyData?>, response: Response<RecentlyData?>) {

                Log.d(TAG, "Recently onResponse: " + response.code())
                if (response.isSuccessful) {
                    sendRecently(response.body())
                } else {
                    Toast.makeText(binding.root.context,"Recently Error:" + response.message(), Toast.LENGTH_SHORT).show()
                    Log.d(TAG, response.message())
                }
            }

            override fun onFailure(call: Call<RecentlyData?>, throwable: Throwable) {
                throwable.printStackTrace()
                Toast.makeText(binding.root.context,"Network request error occurred, check LOG", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendRecently(receivedRecently: RecentlyData?){
        if(receivedRecently?.response?.games.isNullOrEmpty()){
            binding.rvRecently.visibility = View.GONE
            binding.tvRecently.visibility = View.GONE
            binding.btnGames.visibility = View.GONE
        }else{
            adapter.addItems(receivedRecently!!)
        }
    }

    private fun loadLevelData() = launch{
        NetworkManager.getSteamLevel(steamID)!!.enqueue(object : Callback<LevelData?> {

            override fun onResponse(call: Call<LevelData?>, response: Response<LevelData?>) {

                Log.d(TAG, "Level onResponse: " + response.code())
                if (response.isSuccessful) {
                    displayLevelData(response.body())
                } else {
                    Toast.makeText(binding.root.context,"Level Error:" + response.message(),Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LevelData?>, throwable: Throwable) {
                throwable.printStackTrace()
                Toast.makeText(binding.root.context,"Network request error occurred, check LOG",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayLevelData(receivedLevelData: LevelData?){
        val level = receivedLevelData?.response?.player_level.toString()
        if (level.equals("null")){
            binding.tvPlayerProfileLevel.text = getString(R.string.privateTag)
        } else {
            binding.tvPlayerProfileLevel.text = level
        }
    }
}
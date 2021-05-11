package hu.bme.aut.android.stats.detail.fragment

import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import hu.bme.aut.android.stats.R
import hu.bme.aut.android.stats.databinding.FragmentDetailProfileBinding
import hu.bme.aut.android.stats.detail.PlayerDataHolder
import hu.bme.aut.android.stats.model.profile.Player
import hu.bme.aut.android.stats.model.profile.ProfileData

class DetailProfileFragment: Fragment() {

    private var _binding: FragmentDetailProfileBinding? = null
    private val binding get() = _binding!!

    private var playerDataHolder: PlayerDataHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerDataHolder = if (activity is PlayerDataHolder) {
            activity as PlayerDataHolder?
        } else {
            throw RuntimeException(
                "Activity must implement WeatherDataHolder interface!"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailProfileBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (playerDataHolder!!.getProfileData() != null) {
            displayProfileData()
        }
    }

    private fun displayProfileData() {
        val profile = playerDataHolder?.getProfileData()?.response?.players?.get(0)

        binding.tvPlayerName.text = profile?.personaname
        binding.tvPlayerID.text = profile?.steamid.toString()
        binding.tvPlayerProfileLink.text = profile?.profileurl
        setVis(profile)
        setState(profile)
        setBans()
        Glide.with(this)
            .load(profile?.avatarfull)
            .transition(DrawableTransitionOptions().crossFade())
            .into(binding.ivPlayerImg)
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

            binding.tvPlayerState.text = "Playing: " + profile.gameextrainfo
            binding.tvPlayerState.setTextColor(Color.rgb(144,200,60))
            binding.llBorder.setBackgroundColor(Color.rgb(144,200,60))
        }
    }

    private fun setVis(profile: Player?){

        binding.tvPlayerCommProfileState.text = when(profile?.communityvisibilitystate){
            1 -> "Private"
            3 -> "Public"
            else -> ""
        }
        val color: Int = when(profile?.communityvisibilitystate){
            1 -> Color.RED
            else -> Color.GREEN
        }
        binding.tvPlayerCommProfileState.setTextColor(color)
    }

    private fun setBans(){

        val bans = playerDataHolder?.getBanData()?.players?.get(0)
        Log.d("asd", bans?.CommunityBanned.toString())
        if (bans?.CommunityBanned!!){
            binding.tvPlayerCommBan.text = getString(R.string.banned)
            binding.tvPlayerCommBan.setTextColor(Color.RED)
        }else{
            binding.tvPlayerCommBan.text = getString(R.string.bannedNone)
            binding.tvPlayerCommBan.setTextColor(Color.GREEN)
        }
        if(bans?.VACBanned!!){
            binding.tvPlayerVacBan.text = getString(R.string.banned)
            binding.tvPlayerVacBan.setTextColor(Color.RED)
        }else{
            binding.tvPlayerVacBan.text = getString(R.string.bannedNone)
            binding.tvPlayerVacBan.setTextColor(Color.GREEN)
        }
        Log.d("asd", bans?.CommunityBanned.toString())
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
}
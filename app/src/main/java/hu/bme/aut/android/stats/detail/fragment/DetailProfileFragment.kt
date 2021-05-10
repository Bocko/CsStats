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
        if(playerDataHolder?.getProfileData()?.response == null)
        {
            Log.d("ProfilePage: ","asdasd")
        }

        val profile = playerDataHolder?.getProfileData()?.response?.players?.get(0)
        binding.tvPlayerName.text = profile?.personaname
        binding.tvPlayerID.text = profile?.steamid.toString()
        binding.tvPlayerProfileLink.text = profile?.profileurl
        setVis(profile)
        setState(profile)
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
            var color: Int = when(profile?.personastate){
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
        var color: Int = when(profile?.communityvisibilitystate){
            1 -> Color.RED
            else -> Color.GREEN
        }
        binding.tvPlayerCommProfileState.setTextColor(color)
    }

}
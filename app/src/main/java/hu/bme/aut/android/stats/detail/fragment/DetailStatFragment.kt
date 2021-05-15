package hu.bme.aut.android.stats.detail.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.bme.aut.android.stats.databinding.FragmentDetailStatBinding
import hu.bme.aut.android.stats.detail.PlayerDataHolder

class DetailStatFragment: Fragment() {

    private var _binding: FragmentDetailStatBinding? = null
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
        _binding = FragmentDetailStatBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (playerDataHolder!!.getStatsData() != null) {
            displayStatData()
        } else {
            binding.tvInfo.visibility = View.VISIBLE
            binding.svStats.visibility = View.GONE
        }
    }

    private fun displayStatData() {
        val stats = playerDataHolder?.getStatsData()?.playerstats?.stats
        val map = stats?.map { it.name to it.value }!!.toMap()

        binding.killsPlayer.text = map["total_kills"].toString()
        binding.deathsPlayer.text = map["total_deaths"].toString()
        binding.timeplayedPlayer.text = (((map["total_time_played"])?.div(60))?.div(60)).toString()
        binding.totalplantedPlayer.text = map["total_planted_bombs"].toString()
        binding.totaldefusedPlayer.text = map["total_defused_bombs"].toString()
        binding.totaldamagePlayer.text = map["total_damage_done"].toString()
        binding.totalmoneyPlayer.text = map["total_money_earned"].toString()
        binding.totalhsPlayer.text = map["total_kills_headshot"].toString()
        binding.d2RoundWonPlayer.text = map["total_wins_map_de_dust2"].toString()
        binding.infernoRoundWonPlayer.text = map["total_wins_map_de_inferno"].toString()
        binding.nukeRoundWonPlayer.text = map["total_wins_map_de_nuke"].toString()
        binding.trainRoundWonPlayer.text = map["total_wins_map_de_train"].toString()
        binding.totalShotHitPlayer.text = map["total_shots_hit"].toString()
        binding.totalShotFiredPlayer.text = map["total_shots_fired"].toString()
        binding.d2RoundPlayer.text = map["total_rounds_map_de_dust2"].toString()
        binding.infernoRoundPlayer.text = map["total_rounds_map_de_inferno"].toString()
        binding.nukeRoundPlayer.text = map["total_rounds_map_de_nuke"].toString()
        binding.trainRoundPlayer.text = map["total_rounds_map_de_train"].toString()
        binding.totalmvpPlayer.text = map["total_mvps"].toString()
    }

}
package hu.bme.aut.android.stats.detail.fragment

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import hu.bme.aut.android.stats.databinding.FragmentDetailChartBinding
import hu.bme.aut.android.stats.detail.PlayerDataHolder

class DetailChartFragment: Fragment() {

    private var _binding: FragmentDetailChartBinding? = null
    private val binding get() = _binding!!

    private var playerDataHolder: PlayerDataHolder? = null

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailChartBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (playerDataHolder!!.getStatsData() != null) {
            displayStatData()
        } else {
            binding.tvInfo.visibility = View.VISIBLE
            binding.svCharts.visibility = View.GONE
        }
    }

    private fun displayStatData() {
        val stats = playerDataHolder!!.getStatsData()?.playerstats?.stats
        val map = stats?.map { it.name to it.value }!!.toMap()
        loadChart(binding.pieKD,map["total_kills"],"Kills",map["total_deaths"],"Deaths")
        loadChart(binding.pieAccu,map["total_shots_hit"],"Hits",map["total_shots_fired"],"Shots")
        loadChart(binding.pieHSAccu,map["total_kills_headshot"],"HS Kills",map["total_kills"],"Kills")
    }

    private fun loadChart(chart: PieChart,valueOne: Int?,labelOne:String,valueTwo: Int?,labeltwo: String){
        val entries: ArrayList<PieEntry> = ArrayList()


        entries.add(PieEntry(valueOne!!.toFloat(), labelOne))
        entries.add(PieEntry(valueTwo!!.toFloat(), labeltwo))

        val dataSet = PieDataSet(entries,"")
        dataSet.setColors(Color.rgb(76,150,0),Color.rgb(150,23,23))

        val data = PieData(dataSet)
        data.setValueTextSize(15f)
        data.setValueTextColor(Color.WHITE)
        chart.description.text = ""
        val kd: Double = valueOne.toDouble()/valueTwo.toDouble()
        chart.centerText = "%.2f".format(kd)
        chart.setCenterTextSize(30f)
        chart.setCenterTextColor(Color.WHITE)
        chart.isDrawHoleEnabled = false
        chart.legend.textSize = 20f
        chart.legend.textColor = Color.WHITE
        chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        chart.setEntryLabelColor(Color.WHITE)
        chart.isRotationEnabled = false
        chart.data = data
        chart.invalidate()
    }
}
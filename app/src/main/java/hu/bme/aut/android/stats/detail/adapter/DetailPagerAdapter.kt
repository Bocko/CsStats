package hu.bme.aut.android.stats.detail.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import hu.bme.aut.android.stats.detail.fragment.DetailChartFragment
import hu.bme.aut.android.stats.detail.fragment.DetailFriendlistFragment
import hu.bme.aut.android.stats.detail.fragment.DetailProfileFragment
import hu.bme.aut.android.stats.detail.fragment.DetailStatFragment

class DetailPagerAdapter (fa: FragmentActivity): FragmentStateAdapter(fa) {

    companion object{
        private const val NUM_PAGES: Int = 4
    }

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> DetailProfileFragment()
            1 -> DetailFriendlistFragment()
            2 -> DetailStatFragment()
            3 -> DetailChartFragment()
            else -> DetailProfileFragment()
        }
    }
}
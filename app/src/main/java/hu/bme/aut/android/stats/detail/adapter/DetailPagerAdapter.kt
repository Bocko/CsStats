package hu.bme.aut.android.stats.detail.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import hu.bme.aut.android.stats.detail.fragment.*

class DetailPagerAdapter (fa: FragmentActivity): FragmentStateAdapter(fa) {

    companion object{
        private const val NUM_PAGES: Int = 5
    }

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> DetailProfileFragment()
            1 -> DetailFriendlistFragment()
            2 -> DetailStatFragment()
            3 -> DetailChartFragment()
            4 -> DetailInventoryFragment()
            else -> DetailProfileFragment()
        }
    }
}
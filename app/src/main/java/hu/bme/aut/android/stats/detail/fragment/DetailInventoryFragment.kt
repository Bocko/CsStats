package hu.bme.aut.android.stats.detail.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.stats.databinding.FragmentDetailFriendlistBinding
import hu.bme.aut.android.stats.databinding.FragmentDetailInventoryBinding
import hu.bme.aut.android.stats.databinding.FragmentDetailProfileBinding
import hu.bme.aut.android.stats.detail.PlayerDataHolder
import hu.bme.aut.android.stats.detail.fragment.adapter.FriendAdapter
import hu.bme.aut.android.stats.detail.fragment.adapter.InventoryAdapter
import hu.bme.aut.android.stats.menu.adapter.MenuAdapter

class DetailInventoryFragment : Fragment(){

    private var _binding: FragmentDetailInventoryBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: InventoryAdapter

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentDetailInventoryBinding.inflate(LayoutInflater.from(context))
        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        binding.rvInventory.layoutManager = LinearLayoutManager(binding.rvInventory.context)
        adapter = InventoryAdapter()
        adapter.addItems(playerDataHolder?.getInventory()!!)
        binding.rvInventory.adapter = adapter
    }
}
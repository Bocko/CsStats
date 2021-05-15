package hu.bme.aut.android.stats.detail.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hu.bme.aut.android.stats.databinding.FragmentDetailFriendlistBinding
import hu.bme.aut.android.stats.databinding.FragmentDetailProfileBinding
import hu.bme.aut.android.stats.detail.PlayerDataHolder
import hu.bme.aut.android.stats.detail.fragment.adapter.FriendAdapter
import hu.bme.aut.android.stats.menu.adapter.MenuAdapter

class DetailFriendlistFragment : Fragment(),FriendAdapter.OnFriendSelectedListener {

    private var _binding: FragmentDetailFriendlistBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: FriendAdapter

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
        _binding = FragmentDetailFriendlistBinding.inflate(LayoutInflater.from(context))
        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        binding.rvFriends.layoutManager = LinearLayoutManager(binding.rvFriends.context)
        adapter = FriendAdapter(this)
        var IDs = ""
        playerDataHolder?.getFriendlistData()?.friendslist?.friends?.forEach {
            IDs += it.steamid.toString() + ","
        }
        adapter.addFriend(IDs)
        binding.rvFriends.adapter = adapter
    }

    override fun onFriendSelected(friend: Long?) {
        val id = Intent()
        id.putExtra("player",friend.toString())
        activity?.setResult(1,id)
        activity?.finish()
    }
}
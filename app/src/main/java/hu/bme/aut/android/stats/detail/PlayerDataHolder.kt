package hu.bme.aut.android.stats.detail

import hu.bme.aut.android.stats.model.friends.FriendlistData
import hu.bme.aut.android.stats.model.profile.ProfileData
import hu.bme.aut.android.stats.model.stats.PlayerStatsData

interface PlayerDataHolder {
    fun getProfileData(): ProfileData?
    fun getStatsData(): PlayerStatsData?
    fun getFriendlistData(): FriendlistData?
}
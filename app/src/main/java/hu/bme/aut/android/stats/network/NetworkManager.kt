package hu.bme.aut.android.stats.network

import hu.bme.aut.android.stats.model.friends.FriendlistData
import hu.bme.aut.android.stats.model.playercount.CountData
import hu.bme.aut.android.stats.model.profile.ProfileData
import hu.bme.aut.android.stats.model.stats.PlayerStatsData
import hu.bme.aut.android.stats.model.url.UrlData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {
    private val retrofit: Retrofit
    private val STEAM_WEB_API: SteamWebApi

    private const val SERVICE_URL = "https://api.steampowered.com"
    private const val KEY = "686EAC68C74B3321C6FB3FF28F0B994D"

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(SERVICE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        STEAM_WEB_API = retrofit.create(SteamWebApi::class.java)
    }

    fun getStats(steamID: Long?): Call<PlayerStatsData?>? {
        return STEAM_WEB_API.getStats(KEY, steamID, 730)
    }

    fun getIDFromURL(url: String?): Call<UrlData?>?{
        return STEAM_WEB_API.getID(KEY,url)
    }

    fun getPlayerCount(): Call<CountData?>?{
        return STEAM_WEB_API.getPlayerCount(730)
    }

    fun getProfile(steamID: Long?): Call<ProfileData?>?{
        return STEAM_WEB_API.getProfile(KEY,steamID)
    }

    fun getFriendsProfiles(steamIDs: String?): Call<ProfileData?>?{
        return STEAM_WEB_API.getFriendsProfiles(KEY,steamIDs)
    }

    fun getFriends(steamID: Long?): Call<FriendlistData?>?{
        return STEAM_WEB_API.getFriends(KEY,steamID)
    }

}
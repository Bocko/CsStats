package hu.bme.aut.android.stats.network

import com.google.gson.GsonBuilder
import hu.bme.aut.android.stats.model.ItemPrice.ItemPriceData
import hu.bme.aut.android.stats.model.ban.BanData
import hu.bme.aut.android.stats.model.friends.FriendlistData
import hu.bme.aut.android.stats.model.games.GamesData
import hu.bme.aut.android.stats.model.games.RecentlyData
import hu.bme.aut.android.stats.model.inventory.InventoryData
import hu.bme.aut.android.stats.model.itemInfo.ItemInfoData
import hu.bme.aut.android.stats.model.playercount.CountData
import hu.bme.aut.android.stats.model.profile.ProfileData
import hu.bme.aut.android.stats.model.profile.level.LevelData
import hu.bme.aut.android.stats.model.stats.PlayerStatsData
import hu.bme.aut.android.stats.model.url.UrlData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkManager {
    private val retrofit: Retrofit
    private val retrofitInv: Retrofit
    private val retrofitItem: Retrofit
    private val STEAM_WEB_API: SteamWebApi
    private val inventoryApi: InventoryApi
    private val ItemInfoApi: InventoryApi

    private const val SERVICE_URL = "https://api.steampowered.com"
    private const val INVENTORY_URL = "https://steamcommunity.com"
    private const val ITEM_INFO_URL = "https://api.csgofloat.com"
    private const val KEY = "C1207E72D0424364AE7457A57D6B29D6"//686EAC68C74B3321C6FB3FF28F0B994D

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(SERVICE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        STEAM_WEB_API = retrofit.create(SteamWebApi::class.java)

        retrofitInv = Retrofit.Builder()
                .baseUrl(INVENTORY_URL)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        inventoryApi = retrofitInv.create(InventoryApi::class.java)

        retrofitItem = Retrofit.Builder()
                .baseUrl(ITEM_INFO_URL)
                .client(OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        ItemInfoApi = retrofitItem.create(InventoryApi::class.java)
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

    fun getBans(steamID: Long?): Call<BanData?>?{
        return STEAM_WEB_API.getBans(KEY,steamID)
    }

    fun getInventory(steamID: Long?): Call<InventoryData?>?{
        return inventoryApi.getInventory(steamID)
    }

    fun getItemPrice(mhName: String?): Call<ItemPriceData?>?{
        return inventoryApi.getItemPrice(3,730,mhName)
    }

    fun getItemInfo(url: String?): Call<ItemInfoData?>?{
        return ItemInfoApi.getItemInfo(url)
    }

    fun getSteamLevel(steamid: Long?): Call<LevelData?>?{
        return STEAM_WEB_API.getSteamLevel(KEY,steamid)
    }

    fun getRecentlyGames(steamid: Long?): Call<RecentlyData?>?{
        return STEAM_WEB_API.getRecentlyGames(KEY,steamid,5)
    }

    fun getAllGames(steamid: Long?): Call<GamesData?>?{
        return STEAM_WEB_API.getAllGames(KEY,steamid,true,true)
    }
}
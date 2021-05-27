package hu.bme.aut.android.stats.network

import hu.bme.aut.android.stats.model.ban.BanData
import hu.bme.aut.android.stats.model.friends.FriendlistData
import hu.bme.aut.android.stats.model.games.GamesData
import hu.bme.aut.android.stats.model.games.RecentlyData
import hu.bme.aut.android.stats.model.playercount.CountData
import hu.bme.aut.android.stats.model.profile.ProfileData
import hu.bme.aut.android.stats.model.profile.level.LevelData
import hu.bme.aut.android.stats.model.stats.PlayerStatsData
import hu.bme.aut.android.stats.model.url.UrlData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamWebApi {
    @GET("/ISteamUser/ResolveVanityURL/v1/")
    fun getID(
            @Query("key") key: String?,
            @Query("vanityurl") vanityURL: String?
    ): Call<UrlData?>?

    @GET("/ISteamUserStats/GetUserStatsForGame/v2/")
    fun getStats(
            @Query("key") key: String?,
            @Query("steamid") steamid: Long?,
            @Query("appid") appId: Long?
    ): Call<PlayerStatsData?>?

    @GET("/ISteamUser/GetPlayerSummaries/v2/")
    fun getProfile(
            @Query("key") key: String?,
            @Query("steamids") steamids: Long?
    ): Call<ProfileData?>?

    @GET("/ISteamUser/GetPlayerSummaries/v2/")
    fun getFriendsProfiles(
            @Query("key") key: String?,
            @Query("steamids") steamids: String?
    ): Call<ProfileData?>?

    @GET("/ISteamUserStats/GetNumberOfCurrentPlayers/v1/")
    fun getPlayerCount(
            @Query("appid") appid: Int?
    ): Call<CountData?>?

    @GET("/ISteamUser/GetFriendList/v1/")
    fun getFriends(
            @Query("key") key: String?,
            @Query("steamid") steamid: Long?
    ): Call<FriendlistData?>?

    @GET("/ISteamUser/GetPlayerBans/v1/")
    fun getBans(
            @Query("key") key: String?,
            @Query("steamids") steamids: Long?
    ): Call<BanData?>?

    @GET("/IPlayerService/GetSteamLevel/v1/")
    fun getSteamLevel(
            @Query("key") key: String?,
            @Query("steamid") steamid: Long?
    ): Call<LevelData?>?

    @GET("/IPlayerService/GetRecentlyPlayedGames/v1/")
    fun getRecentlyGames(
            @Query("key") key: String?,
            @Query("steamid") steamid: Long?
    ): Call<RecentlyData?>?

    @GET("/IPlayerService/GetOwnedGames/v1/")
    fun getAllGames(
            @Query("key") key: String?,
            @Query("steamid") steamid: Long?,
            @Query("include_played_free_games") freeGames: Boolean?,
            @Query("include_appinfo") appinfo: Boolean?
    ): Call<GamesData?>?
}
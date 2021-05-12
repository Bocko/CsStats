package hu.bme.aut.android.stats.network

import hu.bme.aut.android.stats.model.inventory.InventoryData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface InventoryApi {
    @GET("/profiles/{steamID}/inventory/json/730/2")
    fun getInventory(
            @Path("steamID") steamID: Long?
    ): Call<InventoryData?>?
}
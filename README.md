# csStats

csStats is an Android app that allows you add steam users, check their stats and inventory in cs(go/2).
It was made as the second homework for my Client Apps class in my 4th semester at [BME](https://www.bme.hu/?language=en).
Although this was a homework, I did some work on it after submittion

# Features
## Player List
Here you can add the players you qould like to view. You can add player by its SteamID64 or by vanity URL.  
  
When adding by vanity URL that first gets [resolved](https://partner.steamgames.com/doc/webapi/ISteamUser#ResolveVanityURL) to the SteamID64 of the user, with that the players current name and picture gets loaded using the [GetPlayerSummeries](https://partner.steamgames.com/doc/webapi/ISteamUser#GetPlayerSummaries) endpoint.  
The player list is being handled in a [Recycle View](https://developer.android.com/develop/ui/views/layout/recyclerview). And the players data are stored in a [Room](https://developer.android.com/jetpack/androidx/releases/room) database.  
  
<img src="https://github.com/Bocko/csStats/assets/61477246/7697a699-3f99-446a-98f3-5352a40115f1" height="600"> 
<img src="https://github.com/Bocko/csStats/assets/61477246/115f8964-917f-4950-8489-555a747af29c"  height="600">

## Profile infos
Selecting a player will bring you to the players profile page. Here you can see the players Steam level, visibility status, online statuc, ban status and recently played games. The visibility of these are dependet on the players privacy settings.  

<img src="https://github.com/Bocko/csStats/assets/61477246/e9277c43-193a-42da-a415-939102e95567" height="600"> 
<img src="https://github.com/Bocko/csStats/assets/61477246/a0816455-764d-429a-baa5-aa52ac0b83aa" height="600">  
  
Below the recently player games there is a button to check all the games that the player owns (if it's public)  

<img src="https://github.com/Bocko/csStats/assets/61477246/88e26859-be40-48a9-a15f-a49b0a209982" height="600">  

The recently played games and the whole library page  are each using a Recycle view.
## Friends list
asd
## Stats
asd
### Raw stats
asd
### Charts
asd
## Inventory
asd

# TODO
- [ ] Finish README
- [x] Fix random color issues appearing on actual phone
- [x] Fix disappeared plus button on friends list
- [ ] make the float api work or find a new one

# csStats

csStats is an Android app that allows you add steam users, check their stats and inventory in cs(go/2).
It was made as the second homework for my Client Apps class in my 4th semester at [BME](https://www.bme.hu/?language=en).
Although this was a homework, I did some work on it after submittion

# Features
## Player List
Here you can add the players you qould like to view. You can add player by its SteamID64 or by vanity URL.  
  
When adding by vanity URL that first gets [resolved](https://partner.steamgames.com/doc/webapi/ISteamUser#ResolveVanityURL) to the SteamID64 of the user, with that the players current name and picture gets loaded using the [GetPlayerSummeries](https://partner.steamgames.com/doc/webapi/ISteamUser#GetPlayerSummaries) endpoint.  
The player list is being handled in a [Recycle View](https://developer.android.com/develop/ui/views/layout/recyclerview). And the players data are stored in a [Room](https://developer.android.com/jetpack/androidx/releases/room) database.  
The images are being loaded with [Glide](https://github.com/bumptech/glide).
  
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
On the next page is the players friends list, all of the players steam friend will show up here, if it's set to public. To make it easier to find players you can search and sort by names.
Also, if you click the button with the plus icon next to a player in the friends list the player will be added to the apps player list.  
  
<img src="https://github.com/Bocko/csStats/assets/61477246/7ff4d3ee-f406-4e65-8cf5-591e65ca4b2e" height="600">  
  
## Stats
There are two pages where you can find the stats of the selected player. The statistics are only visible if the player is set them to public. On the first page you cans e a bunch of data and on the second 3 charts. The charts are using [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart).  
  
<img src="https://github.com/Bocko/csStats/assets/61477246/796c8342-ba8a-4e88-97b2-8f0d95778ff5" height="600">
<img src="https://github.com/Bocko/csStats/assets/61477246/4bcd535f-870b-4bd0-a62b-3b6bb404a6d9" height="600">  


## Inventory  
And finally the inventory page. To load the inventory of a player you need to press on the inventory screen. This is because the API endpoint that is used for this is pretty fast at limiting you and if it were loading every inventory when you just scrolled near it, it would not time you out pretty quickly. You can sort by rarity and name, and you can also search by name. If you click on one of the skins it will bring up a detailed page about the price and other properties of the skin. Sadly the endpoint that I used when I created this app is no longer working and I could not find any replacement so the float, pattern and other skin properties are unavailable currently.
  
<img src="https://github.com/Bocko/csStats/assets/61477246/9eb435e7-7580-4bb4-8972-ac7d296b53aa" height="600">
<img src="https://github.com/Bocko/csStats/assets/61477246/4bc54168-1ad8-45f5-9d5d-38db04af015d" height="600">  

# TODO
- [ ] Finish README
- [x] Fix random color issues appearing on actual phone
- [x] Fix disappeared plus button on friends list
- [ ] make the float api work or find a new one

# Mod Info Command
Adds a simple command that lets you list the installed mods and get simple information about them in the chat.  
Originally I was planning to also create a client-side command that would work just like the server-side one, but for the mods installed on user's client, but it turned out the way Fabric API's client commands work don't let me do that without very complicated stuff I failed to do (I don't want to waste too much time on making the command work client-side while you can just use [ModMenu](https://www.curseforge.com/minecraft/mc-mods/modmenu)).  
This is a [Fabric](https://fabricmc.net/) mod and it requires both [Fabric Loader](https://fabricmc.net/use/) and [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) installed.  
## Command syntax
Available commands:  
`/mod list` - lists installed mods. Click any mod on the list to get the info quickly.  
`/mod info <mod>` - prints information about the mod with a given id.  
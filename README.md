This plugin can reload all the RainbowPlugins. Before it's first usage, is highly suggested to [b]BACKUP[/b] your whole server, because it's not guaranted, to will work.

As you know, BukkitPluginBridge uses /reload command for reloading bukkit plugins, so this plugin uses [b]/rreload[/b] command for it. You need [b]rainbow.reload[/b] permission for using it.

[b][size=14pt]So how does this reloading exactly work?[/size][/b]

1. Sets his command execution priority to highest for faster reloading
2. Calls PlayerLogout methods in every plugin for every players
3. Calls Shutdown methods in every plugin
4. Removes plugin list
5. Removes commands
6. Removes packet listeners
7. Kills all the not-whitelisted threads (whitelisted thread names: "Finalizer","Thread-5","Keep-Alive-Timer","DestroyJavaVM","Thread-4","Server Infinisleeper","Java2D Disposer","Server console handler","Thread-3","Reference Handler","Signal Dispatcher","SVR","Server Watchdog","Thread-6","Snooper Timer","Attach Listener","File IO Thread" and all threads, starting with name "Netty Server IO #")

8. Sets class loader to default, and executes System.gc() for trying to unload classes (java don't offers manual class unloading, so this thing will only forces executing automatic class unloading)

9. Loads plugins same as they were loaded in the server startup
10. Calls onServerFullyLoaded event in them
11. Calls PlayerLogin and PlayerJoin events in them.
12. Resets the thread priority to previous value

So theoretically it shouldn't cause any problems, but it can. It's also highly recommended to relog after reloading.


Please report every bugs in the forum with [b]STACKTRACES[/b]!

[center][b][size=18pt][url=http://www.project-rainbow.org/site/plugin-releases/rainbowreload/new/#new]Forum[/url][/size][/b][/center]

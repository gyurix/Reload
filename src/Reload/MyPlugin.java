package Reload;

import PluginReference.MC_Command;
import PluginReference.MC_Player;
import PluginReference.MC_Server;
import PluginReference.PluginBase;
import PluginReference.PluginInfo;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import joebkt.CmdAchievement;
import joebkt.CmdBan;
import joebkt.CmdBanIP;
import joebkt.CmdBanList;
import joebkt.CmdBlockData;
import joebkt.CmdClear;
import joebkt.CmdClone;
import joebkt.CmdDebug;
import joebkt.CmdDefaultGameMode;
import joebkt.CmdDeop;
import joebkt.CmdDifficulty;
import joebkt.CmdEffect;
import joebkt.CmdEnchant;
import joebkt.CmdEntityData;
import joebkt.CmdExecute;
import joebkt.CmdFill;
import joebkt.CmdGameMode;
import joebkt.CmdGameRule;
import joebkt.CmdGive;
import joebkt.CmdHelp;
import joebkt.CmdKick;
import joebkt.CmdKill;
import joebkt.CmdList;
import joebkt.CmdMe;
import joebkt.CmdMsg;
import joebkt.CmdOp;
import joebkt.CmdPardon;
import joebkt.CmdPardonIP;
import joebkt.CmdParticle;
import joebkt.CmdPlaySound;
import joebkt.CmdPublish;
import joebkt.CmdReplaceItem;
import joebkt.CmdSaveAll;
import joebkt.CmdSaveOff;
import joebkt.CmdSaveOn;
import joebkt.CmdSay;
import joebkt.CmdScoreboard;
import joebkt.CmdSeed;
import joebkt.CmdSetBlock;
import joebkt.CmdSetIdleTimeout;
import joebkt.CmdSetWorldSpawn;
import joebkt.CmdSpawnPoint;
import joebkt.CmdSpreadPlayers;
import joebkt.CmdStats;
import joebkt.CmdStop;
import joebkt.CmdSummon;
import joebkt.CmdTP;
import joebkt.CmdTellRaw;
import joebkt.CmdTestFor;
import joebkt.CmdTestForBlock;
import joebkt.CmdTestForBlocks;
import joebkt.CmdTime;
import joebkt.CmdTitle;
import joebkt.CmdToggleDownFall;
import joebkt.CmdTrigger;
import joebkt.CmdWeather;
import joebkt.CmdWhitelist;
import joebkt.CmdWorldBorder;
import joebkt.CmdXP;
import joebkt.CommandAbstract;
import joebkt.CommandExecutorRelated;
import joebkt.CommandSetup;
import joebkt._CmdAnnouncer;
import joebkt._CmdBackpack;
import joebkt._CmdBal;
import joebkt._CmdCron;
import joebkt._CmdDIW;
import joebkt._CmdDelWarp;
import joebkt._CmdDivorce;
import joebkt._CmdEcon;
import joebkt._CmdEnderchest;
import joebkt._CmdHome;
import joebkt._CmdIgnore;
import joebkt._CmdJEmote;
import joebkt._CmdJot;
import joebkt._CmdMP;
import joebkt._CmdMarry;
import joebkt._CmdName;
import joebkt._CmdNameColor;
import joebkt._CmdPay;
import joebkt._CmdPayday;
import joebkt._CmdPerm;
import joebkt._CmdPlugins;
import joebkt._CmdReward;
import joebkt._CmdRide;
import joebkt._CmdSell;
import joebkt._CmdSetHome;
import joebkt._CmdSetWarp;
import joebkt._CmdSetWorth;
import joebkt._CmdSpawn;
import joebkt._CmdThrow;
import joebkt._CmdTpAccept;
import joebkt._CmdTpaHere;
import joebkt._CmdVer;
import joebkt._CmdWarp;
import joebkt._CmdWorth;
import joebkt._JoeUtils;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.ArrayUtils;

public class MyPlugin extends PluginBase
{
  String[] allowedThreads = { "Finalizer", "Thread-5", "Keep-Alive-Timer", "DestroyJavaVM", "Thread-4", "Server Infinisleeper", "Java2D Disposer", "Server console handler", "Thread-3", "Reference Handler", "Signal Dispatcher", "SVR", "Server Watchdog", "Thread-6", "Snooper Timer", "Attach Listener", "File IO Thread" };
  MC_Server srv;

  private static Object getField(Object obj, String fieldName)
  {
    try
    {
      Field f = obj.getClass().getDeclaredField(fieldName);
      f.setAccessible(true);
      Object o = f.get(obj);
      f.setAccessible(false);
      return o;
    }
    catch (Throwable h) {
      h.printStackTrace();
    }return null;
  }

  private static boolean setField(Object obj, String fieldName, Object field) {
    try {
      Field f = obj.getClass().getDeclaredField(fieldName);
      f.setAccessible(true);
      f.set(obj, field);
      f.setAccessible(false);
      return true;
    } catch (Throwable h) {
    }
    return false;
  }

  public void onStartup(MC_Server s) {
    this.srv = s;
    this.srv.registerCommand(new MC_Command()
    {
      public boolean hasPermissionToUse(MC_Player plr) {
        return (plr == null) || (plr.hasPermission("rainbow.reload"));
      }

      public void handleCommand(MC_Player plr, String[] arg1) {
        int oldpr = Thread.currentThread().getPriority();
        Thread.currentThread().setPriority(10);
        for (PluginInfo pl : _JoeUtils.plugins) {
          try {
            for (MC_Player p : MyPlugin.this.srv.getPlayers()) {
              try {
                pl.ref.onPlayerLogout(p.getName(), p.getUUID());
              }
              catch (Throwable e) {
                MyPlugin.this.srv.log("[Reload] ERROR ON CALLING PLAYER LOGOUT IN " + pl.name + " PLUGIN FOR PLAYER " + p.getName() + ":");
                e.printStackTrace();
              }
            }
            pl.ref.onShutdown();
          }
          catch (Throwable e) {
            MyPlugin.this.srv.log("[Reload] ERROR ON STOPPING " + pl.name + " PLUGIN: ");
            e.printStackTrace();
          }
        }
        _JoeUtils.plugins.clear();
        MyPlugin.this.srv.log("[Reload] Unloaded plugins.");
        CommandSetup cs = (CommandSetup)MinecraftServer.getServer().getCommandSender();
        try {
          Field f = CommandExecutorRelated.class.getDeclaredField("b_cmdNames");
          f.setAccessible(true);
          ((Map)f.get(cs)).clear();
          f = CommandExecutorRelated.class.getDeclaredField("c_cmdHandlers");
          f.setAccessible(true);
          ((Set)f.get(cs)).clear();
        } catch (Throwable e) {
          MyPlugin.this.srv.log("[Reload] ERROR ON UNREGISTRING COMMANDS: ");
          e.printStackTrace();
        }
        MyPlugin.this.srv.log("[Reload] Removed all the commands.");
        joebkt.__PacketMaster.packetIncomingListeners = null;
        joebkt.__PacketMaster.packetOutgoingListeners = null;
        MyPlugin.this.srv.log("[Reload] Unregistered packet listeners.");
        MyPlugin.this.srv.log("[Reload] Killing threads...");
        Throwable localThrowable1 = (e = (Thread[])MyPlugin.getField(Thread.currentThread().getThreadGroup(), "threads")).length; for (e = 0; e < localThrowable1; e++) { Thread t = e[e];
          if (t != null)
          {
            if ((!t.getName().startsWith("Netty Server IO #")) && (!ArrayUtils.contains(MyPlugin.this.allowedThreads, t.getName()))) {
              MyPlugin.this.srv.log("[Reload] Killing " + t.getName() + " thread...");
              try {
                t.interrupt();
                ThreadGroup tg = t.getThreadGroup();
                Thread[] tt = (Thread[])ArrayUtils.removeElement((Thread[])MyPlugin.getField(tg, "threads"), t);
                int i = tt.length - 1;
                while (tt[i] == null) i--;
                tt = (Thread[])ArrayUtils.subarray(tt, 0, i + 2);
                System.out.print("Running threads: ");
                for (Thread t2 : tt) {
                  if (t2 != null)
                    System.out.print(t2.getName() + ", ");
                }
                MyPlugin.setField(tg, "threads", tt);
                MyPlugin.setField(tg, "nthreads", Integer.valueOf(tt.length - 1));
                System.out.println(tg.getName() + " thread group:\ngroup active: " + tg.activeGroupCount() + "\nthread active: " + tg.activeCount());
              }
              catch (Throwable e) {
                MyPlugin.this.srv.log("[Reload] ERROR ON KILLING " + t.getName() + " thread:");
                e.printStackTrace();
              }
            }
          } }
        MyPlugin.this.srv.log("[Reload] Killed all the threads.");
        Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
        System.gc();
        MyPlugin.this.srv.log("[Reload] Unloaded remaining classes.");
        _JoeUtils.LoadPlugins();
        _JoeUtils.ReorderPlugins();
        registerCMDS(cs);
        MyPlugin.this.srv.log("[Reload] Registered vanilla and Rainbow commands.");
        MyPlugin.this.srv.log("[Reload] Plugins loaded, activating them...");
        for (PluginInfo pl : _JoeUtils.plugins) {
          try {
            try {
              pl.ref.onServerFullyLoaded();
            }
            catch (Throwable e) {
              MyPlugin.this.srv.log("[Reload] ERROR ON CALLING ON SERVER FULLY LOADED IN " + pl.name);
              e.printStackTrace();
            }
            for (MC_Player p : MyPlugin.this.srv.getPlayers()) {
              try {
                pl.ref.onPlayerLogin(p.getName(), p.getUUID(), p.getIPAddress());
              }
              catch (Throwable e) {
                MyPlugin.this.srv.log("[Reload] ERROR ON CALLING PLAYER LOGIN IN " + pl.name + " PLUGIN FOR PLAYER " + p.getName() + ":");
                e.printStackTrace();
              }
              try {
                pl.ref.onPlayerJoin(p);
              }
              catch (Throwable e) {
                MyPlugin.this.srv.log("[Reload] ERROR ON CALLING PLAYER JOIN IN " + pl.name + " PLUGIN FOR PLAYER " + p.getName() + ":");
                e.printStackTrace();
              }
            }
            pl.ref.onShutdown();
          }
          catch (Throwable e) {
            MyPlugin.this.srv.log("[Reload] ERROR ON STOPPING " + pl.name + " PLUGIN: ");
            e.printStackTrace();
          }
        }
        MyPlugin.this.srv.log("[Reload] Reload complete");
        for (MC_Player p : MyPlugin.this.srv.getPlayers()) {
          p.sendMessage("§a[Reload]§c The server was reloaded, it is strongly recommended to rejoin!");
        }
        Thread.currentThread().setPriority(oldpr);
      }
      public void registerCMDS(CommandSetup cs) {
        if (!_JoeUtils.IsCommandRestricted("cron")) cs.addCommand(new _CmdCron());

        if (!_JoeUtils.IsCommandRestricted("diw")) cs.addCommand(new _CmdDIW());
        if (!_JoeUtils.IsCommandRestricted("home")) cs.addCommand(new _CmdHome());
        if (!_JoeUtils.IsCommandRestricted("sethome")) cs.addCommand(new _CmdSetHome());
        if (!_JoeUtils.IsCommandRestricted("tpahere")) cs.addCommand(new _CmdTpaHere());
        if (!_JoeUtils.IsCommandRestricted("tpaccept")) cs.addCommand(new _CmdTpAccept());
        if (!_JoeUtils.IsCommandRestricted("reward")) cs.addCommand(new _CmdReward());
        if (!_JoeUtils.IsCommandRestricted("jot")) cs.addCommand(new _CmdJot());
        if (!_JoeUtils.IsCommandRestricted("announcer")) cs.addCommand(new _CmdAnnouncer());

        if (!_JoeUtils.IsCommandRestricted("throw")) cs.addCommand(new _CmdThrow());
        if (!_JoeUtils.IsCommandRestricted("ride")) cs.addCommand(new _CmdRide());
        if (!_JoeUtils.IsCommandRestricted("namecolor")) cs.addCommand(new _CmdNameColor());
        if (!_JoeUtils.IsCommandRestricted("jemote")) cs.addCommand(new _CmdJEmote());
        if (!_JoeUtils.IsCommandRestricted("bp")) cs.addCommand(new _CmdBackpack());

        if (!_JoeUtils.IsCommandRestricted("marry")) cs.addCommand(new _CmdMarry());
        if (!_JoeUtils.IsCommandRestricted("divorce")) cs.addCommand(new _CmdDivorce());
        if (!_JoeUtils.IsCommandRestricted("spawn")) cs.addCommand(new _CmdSpawn());
        if (!_JoeUtils.IsCommandRestricted("ec")) cs.addCommand(new _CmdEnderchest());

        if (!_JoeUtils.IsCommandRestricted("warp")) cs.addCommand(new _CmdWarp());
        if (!_JoeUtils.IsCommandRestricted("delwarp")) cs.addCommand(new _CmdDelWarp());
        if (!_JoeUtils.IsCommandRestricted("setwarp")) cs.addCommand(new _CmdSetWarp());
        if (!_JoeUtils.IsCommandRestricted("bal")) cs.addCommand(new _CmdBal());

        if (!_JoeUtils.IsCommandRestricted("econ")) cs.addCommand(new _CmdEcon());
        if (!_JoeUtils.IsCommandRestricted("pay")) cs.addCommand(new _CmdPay());
        if (!_JoeUtils.IsCommandRestricted("payday")) cs.addCommand(new _CmdPayday());
        if (!_JoeUtils.IsCommandRestricted("setworth")) cs.addCommand(new _CmdSetWorth());
        if (!_JoeUtils.IsCommandRestricted("worth")) cs.addCommand(new _CmdWorth());
        if (!_JoeUtils.IsCommandRestricted("sell")) cs.addCommand(new _CmdSell());

        if (!_JoeUtils.IsCommandRestricted("ignore")) cs.addCommand(new _CmdIgnore());
        if (!_JoeUtils.IsCommandRestricted("name")) cs.addCommand(new _CmdName());

        if (!_JoeUtils.IsCommandRestricted("perm")) cs.addCommand(new _CmdPerm());
        if (!_JoeUtils.IsCommandRestricted("plugins")) cs.addCommand(new _CmdPlugins());

        if (!_JoeUtils.IsCommandRestricted("ver")) cs.addCommand(new _CmdVer());

        if (_JoeUtils.PlotsEnabled)
        {
          if (!_JoeUtils.IsCommandRestricted("mp2")) cs.addCommand(new _CmdMP());
        }

        if (!_JoeUtils.IsCommandRestricted("time")) cs.addCommand(new CmdTime());
        if (!_JoeUtils.IsCommandRestricted("gamemode")) cs.addCommand(new CmdGameMode());
        if (!_JoeUtils.IsCommandRestricted("difficulty")) cs.addCommand(new CmdDifficulty());
        if (!_JoeUtils.IsCommandRestricted("defaultgamemode")) cs.addCommand(new CmdDefaultGameMode());
        if (!_JoeUtils.IsCommandRestricted("kill")) cs.addCommand(new CmdKill());
        if (!_JoeUtils.IsCommandRestricted("toggledownfall")) cs.addCommand(new CmdToggleDownFall());
        if (!_JoeUtils.IsCommandRestricted("weather")) cs.addCommand(new CmdWeather());
        if (!_JoeUtils.IsCommandRestricted("xp")) cs.addCommand(new CmdXP());
        if (!_JoeUtils.IsCommandRestricted("tp")) cs.addCommand(new CmdTP());
        if (!_JoeUtils.IsCommandRestricted("give")) cs.addCommand(new CmdGive());
        if (!_JoeUtils.IsCommandRestricted("replaceitem")) cs.addCommand(new CmdReplaceItem());
        if (!_JoeUtils.IsCommandRestricted("stats")) cs.addCommand(new CmdStats());
        if (!_JoeUtils.IsCommandRestricted("effect")) cs.addCommand(new CmdEffect());
        if (!_JoeUtils.IsCommandRestricted("enchant")) cs.addCommand(new CmdEnchant());
        if (!_JoeUtils.IsCommandRestricted("particle")) cs.addCommand(new CmdParticle());
        if (!_JoeUtils.IsCommandRestricted("me")) cs.addCommand(new CmdMe());
        if (!_JoeUtils.IsCommandRestricted("seed")) cs.addCommand(new CmdSeed());
        if (!_JoeUtils.IsCommandRestricted("help")) cs.addCommand(new CmdHelp());
        if (!_JoeUtils.IsCommandRestricted("debug")) cs.addCommand(new CmdDebug());
        if (!_JoeUtils.IsCommandRestricted("msg")) cs.addCommand(new CmdMsg());
        if (!_JoeUtils.IsCommandRestricted("say")) cs.addCommand(new CmdSay());
        if (!_JoeUtils.IsCommandRestricted("spawnpoint")) cs.addCommand(new CmdSpawnPoint());
        if (!_JoeUtils.IsCommandRestricted("setworldspawn")) cs.addCommand(new CmdSetWorldSpawn());
        if (!_JoeUtils.IsCommandRestricted("gamerule")) cs.addCommand(new CmdGameRule());
        if (!_JoeUtils.IsCommandRestricted("clear")) cs.addCommand(new CmdClear());
        if (!_JoeUtils.IsCommandRestricted("testfor")) cs.addCommand(new CmdTestFor());
        if (!_JoeUtils.IsCommandRestricted("spreadplayers")) cs.addCommand(new CmdSpreadPlayers());
        if (!_JoeUtils.IsCommandRestricted("playsound")) cs.addCommand(new CmdPlaySound());
        if (!_JoeUtils.IsCommandRestricted("scoreboard")) cs.addCommand(new CmdScoreboard());
        if (!_JoeUtils.IsCommandRestricted("execute")) cs.addCommand(new CmdExecute());
        if (!_JoeUtils.IsCommandRestricted("trigger")) cs.addCommand(new CmdTrigger());
        if (!_JoeUtils.IsCommandRestricted("achievement")) cs.addCommand(new CmdAchievement());
        if (!_JoeUtils.IsCommandRestricted("summon")) cs.addCommand(new CmdSummon());
        if (!_JoeUtils.IsCommandRestricted("setblock")) cs.addCommand(new CmdSetBlock());
        if (!_JoeUtils.IsCommandRestricted("fill")) cs.addCommand(new CmdFill());
        if (!_JoeUtils.IsCommandRestricted("clone")) cs.addCommand(new CmdClone());
        if (!_JoeUtils.IsCommandRestricted("testforblocks")) cs.addCommand(new CmdTestForBlocks());
        if (!_JoeUtils.IsCommandRestricted("blockdata")) cs.addCommand(new CmdBlockData());
        if (!_JoeUtils.IsCommandRestricted("testforblock")) cs.addCommand(new CmdTestForBlock());
        if (!_JoeUtils.IsCommandRestricted("tellraw")) cs.addCommand(new CmdTellRaw());
        if (!_JoeUtils.IsCommandRestricted("worldborder")) cs.addCommand(new CmdWorldBorder());
        if (!_JoeUtils.IsCommandRestricted("title")) cs.addCommand(new CmdTitle());
        if (!_JoeUtils.IsCommandRestricted("entitydata")) cs.addCommand(new CmdEntityData());
        if (MinecraftServer.getServer().isDedicated())
        {
          if (!_JoeUtils.IsCommandRestricted("op")) cs.addCommand(new CmdOp());
          if (!_JoeUtils.IsCommandRestricted("deop")) cs.addCommand(new CmdDeop());
          if (!_JoeUtils.IsCommandRestricted("stop")) cs.addCommand(new CmdStop());
          if (!_JoeUtils.IsCommandRestricted("save-all")) cs.addCommand(new CmdSaveAll());
          if (!_JoeUtils.IsCommandRestricted("save-off")) cs.addCommand(new CmdSaveOff());
          if (!_JoeUtils.IsCommandRestricted("save-on")) cs.addCommand(new CmdSaveOn());
          if (!_JoeUtils.IsCommandRestricted("ban-ip")) cs.addCommand(new CmdBanIP());
          if (!_JoeUtils.IsCommandRestricted("pardon-ip")) cs.addCommand(new CmdPardonIP());
          if (!_JoeUtils.IsCommandRestricted("ban")) cs.addCommand(new CmdBan());
          if (!_JoeUtils.IsCommandRestricted("banlist")) cs.addCommand(new CmdBanList());
          if (!_JoeUtils.IsCommandRestricted("pardon")) cs.addCommand(new CmdPardon());
          if (!_JoeUtils.IsCommandRestricted("kick")) cs.addCommand(new CmdKick());
          if (!_JoeUtils.IsCommandRestricted("list")) cs.addCommand(new CmdList());
          if (!_JoeUtils.IsCommandRestricted("whitelist")) cs.addCommand(new CmdWhitelist());
          if (!_JoeUtils.IsCommandRestricted("setidletimeout")) cs.addCommand(new CmdSetIdleTimeout());

        }
        else if (!_JoeUtils.IsCommandRestricted("publish")) { cs.addCommand(new CmdPublish()); }

        CommandAbstract.a(cs);
      }

      public List<String> getAliases() {
        return null;
      }

      public String getCommandName()
      {
        return "rreload";
      }

      public String getHelpLine(MC_Player arg0)
      {
        return "Reloads all the plugins.";
      }

      public List<String> getTabCompletionList(MC_Player arg0, String[] arg1)
      {
        return null;
      }
    });
  }
}

/* Location:           D:\GitHub\Reload.jar
 * Qualified Name:     Reload.MyPlugin
 * JD-Core Version:    0.6.2
 */
# BanSystem-WD

Many new features and code improvements!\
This Plugin only supports Waterdog Proxy (latest).

> * Download the latest version here: [Download](https://github.com/llamadevelopment/BanSystem-WD/releases/tag/2.5.0)

### Join our Discord: 
[![d](https://img.shields.io/discord/323953253458903040.svg)](https://discord.gg/Qcuv2f6)
* If you have questions or problems with the plugin, write to us on Discord or Github!
* You can get faster support on our Discord server! Just open a ticket and we will respond within a short time.

---

## Features

* Timed bans & mutes
* Log data of bans, mutes and warnings
* Change reason and time of running punishments
* Cancel punishments
* Warning system
* [MongoDB](https://mongodb.com) support
* YAML support
* MySql support
* Everything is editable
* API

---

## Installation

1. [Download](https://github.com/llamadevelopment/BanSystem-WD/releases/tag/2.5.0) the .jar file.
2. Paste the file into your plugin folder.
3. Start your server.
4. And have fun using BanSystem.

---

## Addons

Addons for this Plugin are available soon.

---

## Commands & Permissions

Command | Usage | Default Permission
------------ | ------------- | -------------
ban |    /ban \<Player> <ID> |    bansystem.command.ban
banlog    |/banlog \<Player>    | bansystem.command.banlog
checkban|    /checkban \<Player>|    bansystem.command.checkban
checkmute|    /checkmute \<Player>|    bansystem.command.checkmute
clearbanlog    |/clearbanlog \<Player>    | bansystem.command.clearbanlog
clearmutelog|    /clearmutelog \<Player>|    bansystem.command.clearmutelog
clearwarnings    |/clearwarnings \<Player>|    bansystem.command.clearwarnings
editban    | /editban \<Player> reason \<Reason> <br> /editban \<Player> time \<hours/days> \<Time>|    bansystem.command.editban
editmute    | /editmute \<Player> reason \<Reason> <br> /editmute \<Player> time \<hours/days> \<Time>|    bansystem.command.editmute
kick    |/kick \<Player> \<Reason>|    bansystem.command.kick
mute    |/mute \<Player> \<ID>    |bansystem.command.mute
mutelog    |/mutelog \<Player>    |bansystem.command.mutelog
tempban    |/tempban \<Player> \<hours/days> \<Time>|    bansystem.command.tempban
tempmute    |/tempmute \<Player> \<hours/days> \<Time>    |bansystem.command.tempmute
unban    |/unban \<Player>    |bansystem.command.unban
unmute    |/unmute \<Player>|    bansystem.command.unmute
warn    |/warn \<Player> \<Reason>|    bansystem.command.warn
warnlog    |/warnlog \<Player>    |bansystem.command.warnlog
deleteban    |/deleteban \<ID>    |bansystem.command.deleteban
deletemute    |/deletemute \<ID>|    bansystem.command.deletemute
deletewarn    |/deletewarn \<ID>|    bansystem.command.deletewarn

**All commands are editable: Permission, Usage, Command name and Command aliases.**

## Developer API

---

Maven
```xml

<repository>
    <id>lldv-repo</id>
    <url>http://system01.lldv.net:8082/artifactory/libs-snapshot</url>
</repository>

<dependency>
<groupId>net.llamadevelopment.bansystemwd</groupId>
<artifactId>BanSystem-WD</artifactId>
<version>2.5.0-20210110.101145-1</version>
</dependency>

```

API

```java
import net.llamadevelopment.bansystemwd.BanSystemWD;
import net.llamadevelopment.bansystemwd.components.api.API;

public class Test {

    private Provider api = BanSystemWD.getApi().getProvider();

    public void test() {
        this.api.getBan("testuser", ban -> {
            System.out.println("Player is banned for: " + ban.getReason());
        });

        this.api.unbanPlayer("testuser", "byME");
    }

}
```

Events

```java
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.llamadevelopment.bansystemwd.components.event.PlayerBanEvent;
import net.llamadevelopment.bansystemwd.components.event.PlayerUnbanEvent;

public class Test implements Listener {

    @EventHandler
    public void on(PlayerBanEvent event) {
        System.out.println("Player " + event.getBan().getPlayer() + " was banned for " + event.getBan().getReason() + "!");
    }

    @EventHandler
    public void on(PlayerUnbanEvent event) {
        System.out.println("Player " + event.getTarget() + " was unbanned by " + event.getExecutor() + "!");
    }

}
```

---

## Screenshots

![Alt Text](https://cloudburstmc.org/attachments/bansystem_1-png.1814/)
![Alt Text](https://cloudburstmc.org/attachments/bansystem_2-png.1815/)
![Alt Text](https://cloudburstmc.org/attachments/bansystem_3-png.1816/)
![Alt Text](https://cloudburstmc.org/attachments/bansystem_4-png.1817/)

---


## Thanks to ZAP-Hosting!
This project wouldn't be possible without the help of ZAP-Hosting!
***
### ZAP-Hosting
Zap-Hosting is a VPS, Rootserver, (Lifetime) Gameserver, Domain & TeamSpeak 3/5 hosting company. They offer **good quality** servers **at a low price**. This project was also tested on a ZAP-Hosting Server. So if you need a **cheap** vps/gameserver/rootserver/teamspeak3/etc. hoster, ZAP-Hosting is the best choice.

Get your own **Server** today here: [Click me](https://zap-hosting.com/lldv)

### Special offer
Use the code `proxma-20` for a 20% discount
***

![YourKit](https://www.yourkit.com/images/yklogo.png)
------
YourKit supports open source projects with innovative and intelligent tools for monitoring and profiling Java and .NET applications. YourKit is the creator of [YourKit Java Profiler](https://www.yourkit.com/java/profiler/),
[YourKit .NET Profiler](https://www.yourkit.com/.net/profiler/")
and [YourKit YouMonitor](https://www.yourkit.com/youmonitor/)

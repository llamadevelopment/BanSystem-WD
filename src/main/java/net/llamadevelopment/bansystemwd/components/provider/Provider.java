package net.llamadevelopment.bansystemwd.components.provider;

import lombok.RequiredArgsConstructor;
import net.llamadevelopment.bansystemwd.components.data.*;
import net.llamadevelopment.bansystemwd.components.event.*;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.llamadevelopment.bansystemwd.components.provider.data.Collection;
import net.llamadevelopment.bansystemwd.components.provider.data.CollectionFields;
import net.llamadevelopment.bansystemwd.components.provider.data.UDocument;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class Provider {

    private final UniversalClient client;

    public final LinkedHashMap<String, BanReason> banReasons = new LinkedHashMap<>();
    public final LinkedHashMap<String, MuteReason> muteReasons = new LinkedHashMap<>();
    public final Map<String, Mute> cachedMutes = new HashMap<>();

    private Collection banCollection, muteCollection, banlogCollection, mutelogCollection, warnCollection;

    public void init(final Plugin plugin) {
        this.banCollection = this.client.getCollection("bans");
        this.banCollection.createCollection("id",
                new CollectionFields("player", CollectionFields.Type.VARCHAR, 64)
                        .append("reason", CollectionFields.Type.VARCHAR, 128)
                        .append("id", CollectionFields.Type.VARCHAR, 64)
                        .append("banner", CollectionFields.Type.VARCHAR, 64)
                        .append("date", CollectionFields.Type.VARCHAR, 64)
                        .append("time", CollectionFields.Type.BIGINT)
        );

        this.muteCollection = this.client.getCollection("mutes");
        this.muteCollection.createCollection("id",
                new CollectionFields("player", CollectionFields.Type.VARCHAR, 64)
                        .append("reason", CollectionFields.Type.VARCHAR, 128)
                        .append("id", CollectionFields.Type.VARCHAR, 64)
                        .append("banner", CollectionFields.Type.VARCHAR, 64)
                        .append("date", CollectionFields.Type.VARCHAR, 64)
                        .append("time", CollectionFields.Type.BIGINT)
        );

        this.banlogCollection = this.client.getCollection("banlogs");
        this.banlogCollection.createCollection("id",
                new CollectionFields("player", CollectionFields.Type.VARCHAR, 64)
                        .append("reason", CollectionFields.Type.VARCHAR, 128)
                        .append("id", CollectionFields.Type.VARCHAR, 64)
                        .append("banner", CollectionFields.Type.VARCHAR, 64)
                        .append("date", CollectionFields.Type.VARCHAR, 64)
        );

        this.mutelogCollection = this.client.getCollection("mutelogs");
        this.mutelogCollection.createCollection("id",
                new CollectionFields("player", CollectionFields.Type.VARCHAR, 64)
                        .append("reason", CollectionFields.Type.VARCHAR, 128)
                        .append("id", CollectionFields.Type.VARCHAR, 64)
                        .append("banner", CollectionFields.Type.VARCHAR, 64)
                        .append("date", CollectionFields.Type.VARCHAR, 64)
        );

        this.warnCollection = this.client.getCollection("warns");
        this.warnCollection.createCollection("id",
                new CollectionFields("player", CollectionFields.Type.VARCHAR, 64)
                        .append("reason", CollectionFields.Type.VARCHAR, 128)
                        .append("id", CollectionFields.Type.VARCHAR, 64)
                        .append("creator", CollectionFields.Type.VARCHAR, 64)
                        .append("date", CollectionFields.Type.VARCHAR, 64)
        );
    }

    public void disconnect() {
        this.client.disconnect();
    }

    public final void playerIsBanned(final String player, final Consumer<Boolean> isBanned) {
        CompletableFuture.runAsync(() -> {
            final UDocument document = this.banCollection.find("player", player).first();
            isBanned.accept(document != null);
        });
    }

    public final void playerIsMuted(final String player, final Consumer<Boolean> isMuted) {
        CompletableFuture.runAsync(() -> {
            final UDocument document = this.muteCollection.find("player", player).first();
            isMuted.accept(document != null);
        });
    }

    public final void banIdExists(final String id, final boolean history, final Consumer<Boolean> exists) {
        CompletableFuture.runAsync(() -> {
            final UDocument document;
            if (history) {
                document = this.banlogCollection.find("id", id).first();
            } else {
                document = this.banCollection.find("id", id).first();
            }
            exists.accept(document != null);
        });
    }

    public final void muteIdExists(final String id, final boolean history, final Consumer<Boolean> exists) {
        CompletableFuture.runAsync(() -> {
            final UDocument document;
            if (history) {
                document = this.mutelogCollection.find("id", id).first();
            } else {
                document = this.muteCollection.find("id", id).first();
            }
            exists.accept(document != null);
        });
    }

    public final void warnIdExists(final String id, final Consumer<Boolean> exists) {
        CompletableFuture.runAsync(() -> {
            final UDocument document = this.warnCollection.find("id", id).first();
            exists.accept(document != null);
        });
    }

    public final void banPlayer(final String player, final String reason, final String banner, final int seconds) {
        CompletableFuture.runAsync(() -> {
            long end = System.currentTimeMillis() + seconds + 1000L;
            if (seconds == -1) end = -1;
            final String id = this.getRandomIDCode(5);
            final String date = this.getDate();

            final UDocument document = new UDocument("player", player)
                    .append("reason", reason)
                    .append("id", id)
                    .append("banner", banner)
                    .append("date", date)
                    .append("time", end);
            this.banCollection.insert(document);

            final Ban ban = new Ban(player, reason, id, banner, date, end);
            this.createBanlog(ban);

            final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
            if (proxiedPlayer != null) {
                proxiedPlayer.disconnect(Language.getNP("screen.ban", ban.getReason(), ban.getBanID(), this.getRemainingTime(ban.getTime())));
            }

            ProxyServer.getInstance().getPluginManager().callEvent(new PlayerBanEvent(ban));
        });
    }

    public final void mutePlayer(final String player, final String reason, final String banner, final int seconds) {
        CompletableFuture.runAsync(() -> {
            long end = System.currentTimeMillis() + seconds + 1000L;
            if (seconds == -1) end = -1;
            final String id = this.getRandomIDCode(5);
            final String date = this.getDate();

            final UDocument document = new UDocument("player", player)
                    .append("reason", reason)
                    .append("id", id)
                    .append("banner", banner)
                    .append("date", date)
                    .append("time", end);
            this.mutelogCollection.insert(document);

            final Mute mute = new Mute(player, reason, id, banner, date, end);
            this.createMutelog(mute);

            final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
            if (proxiedPlayer != null) {
                this.cachedMutes.put(player, mute);
            }

            ProxyServer.getInstance().getPluginManager().callEvent(new PlayerMuteEvent(mute));
        });
    }

    public final void warnPlayer(final String player, final String reason, final String creator) {
        CompletableFuture.runAsync(() -> {
            final String id = this.getRandomIDCode(7);
            final String date = this.getDate();

            final UDocument document = new UDocument("player", player)
                    .append("reason", reason)
                    .append("id", id)
                    .append("creator", creator)
                    .append("date", date);
            this.warnCollection.insert(document);

            final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
            if (proxiedPlayer != null) {
                proxiedPlayer.disconnect(Language.getNP("screen.warn", reason, creator));
            }

            ProxyServer.getInstance().getPluginManager().callEvent(new PlayerWarnEvent(new Warn(player, reason, id, creator, date)));
        });
    }

    public final void unbanPlayer(final String player, final String executor) {
        CompletableFuture.runAsync(() -> {
            this.banCollection.delete("player", player);

            ProxyServer.getInstance().getPluginManager().callEvent(new PlayerUnbanEvent(player, executor));
        });
    }

    public final void unmutePlayer(final String player, final String executor) {
        CompletableFuture.runAsync(() -> {
            this.muteCollection.delete("player", player);

            ProxyServer.getInstance().getPluginManager().callEvent(new PlayerUnmuteEvent(player, executor));
        });
    }

    public final void getBan(final String player, final Consumer<Ban> cBan) {
        CompletableFuture.runAsync(() -> {
            Ban ban = null;

            final UDocument document = this.banCollection.find("player", player).first();
            if (document != null) {
                ban = new Ban(document.getString("player"), document.getString("reason"), document.getString("id"),
                        document.getString("banner"), document.getString("date"), document.getLong("time"));
            }

            cBan.accept(ban);
        });
    }

    public final void getMute(final String player, final Consumer<Mute> cMute) {
        CompletableFuture.runAsync(() -> {
            Mute mute = null;

            final UDocument document = this.muteCollection.find("player", player).first();
            if (document != null) {
                mute = new Mute(document.getString("player"), document.getString("reason"), document.getString("id"),
                        document.getString("banner"), document.getString("date"), document.getLong("time"));
            }

            cMute.accept(mute);
        });
    }

    public final void getBanById(final String id, final boolean history, final Consumer<Ban> cBan) {
        CompletableFuture.runAsync(() -> {
            Ban ban = null;

            final UDocument document;
            if (history) {
                document = this.banlogCollection.find("id", id).first();
            } else {
                document = this.banCollection.find("id", id).first();
            }
            if (document != null) {
                ban = new Ban(document.getString("player"), document.getString("reason"), document.getString("id"),
                        document.getString("banner"), document.getString("date"), document.getLong("time"));
            }

            cBan.accept(ban);
        });
    }

    public final void getMuteById(final String id, final boolean history, final Consumer<Mute> cMute) {
        CompletableFuture.runAsync(() -> {
            Mute mute = null;

            final UDocument document;
            if (history) {
                document = this.mutelogCollection.find("id", id).first();
            } else {
                document = this.muteCollection.find("id", id).first();
            }
            if (document != null) {
                mute = new Mute(document.getString("player"), document.getString("reason"), document.getString("id"),
                        document.getString("banner"), document.getString("date"), document.getLong("time"));
            }

            cMute.accept(mute);
        });
    }

    public final void createBanlog(final Ban ban) {
        CompletableFuture.runAsync(() -> {
            final UDocument document = new UDocument("player", ban.getPlayer())
                    .append("reason", ban.getReason())
                    .append("id", ban.getBanID())
                    .append("banner", ban.getBanner())
                    .append("date", ban.getDate());
            this.banlogCollection.insert(document);
        });
    }

    public final void createMutelog(final Mute mute) {
        CompletableFuture.runAsync(() -> {
            final UDocument document = new UDocument("player", mute.getPlayer())
                    .append("reason", mute.getReason())
                    .append("id", mute.getMuteID())
                    .append("banner", mute.getMuter())
                    .append("date", mute.getDate());
            this.mutelogCollection.insert(document);
        });
    }

    public final void getBanLog(final String player, final Consumer<Set<Ban>> banlog) {
        CompletableFuture.runAsync(() -> {
            final Set<Ban> bans = new HashSet<>();

            this.banlogCollection.find("player", player).getAll().forEach(document -> {
                bans.add(new Ban(document.getString("player"), document.getString("reason"), document.getString("id"),
                        document.getString("banner"), document.getString("date"), 0));
            });

            banlog.accept(bans);
        });
    }

    public final void getMuteLog(final String player, final Consumer<Set<Mute>> mutelog) {
        CompletableFuture.runAsync(() -> {
            final Set<Mute> mutes = new HashSet<>();

            this.mutelogCollection.find("player", player).getAll().forEach(document -> {
                mutes.add(new Mute(document.getString("player"), document.getString("reason"), document.getString("id"),
                        document.getString("banner"), document.getString("date"), 0));
            });

            mutelog.accept(mutes);
        });
    }

    public final void getWarnLog(final String player, final Consumer<Set<Warn>> warnlog) {
        CompletableFuture.runAsync(() -> {
            final Set<Warn> warns = new HashSet<>();

            this.warnCollection.find("player", player).getAll().forEach(document -> {
                warns.add(new Warn(document.getString("player"), document.getString("reason"), document.getString("id"),
                        document.getString("creator"), document.getString("date")));
            });

            warnlog.accept(warns);
        });
    }

    public final void clearBanlog(final String player, final String executor) {
        CompletableFuture.runAsync(() -> {
            this.banlogCollection.find("player", player).getAll().forEach(document -> {
                this.banlogCollection.delete("id", document.getString("id"));
            });

            ProxyServer.getInstance().getPluginManager().callEvent(new ClearBanlogEvent(player, executor));
        });
    }

    public final void clearMutelog(final String player, final String executor) {
        CompletableFuture.runAsync(() -> {
            this.mutelogCollection.find("player", player).getAll().forEach(document -> {
                this.mutelogCollection.delete("id", document.getString("id"));
            });

            ProxyServer.getInstance().getPluginManager().callEvent(new ClearMutelogEvent(player, executor));
        });
    }

    public final void clearWarns(final String player, final String executor) {
        CompletableFuture.runAsync(() -> {
            this.warnCollection.find("player", player).getAll().forEach(document -> {
                this.warnCollection.delete("id", document.getString("id"));
            });

            ProxyServer.getInstance().getPluginManager().callEvent(new ClearWarnlogEvent(player, executor));
        });
    }

    public final void setBanReason(final String player, final String reason, final String executor) {
        CompletableFuture.runAsync(() -> {
            this.banCollection.update("player", player, new UDocument("reason", reason));

            ProxyServer.getInstance().getPluginManager().callEvent(new EditBanReasonEvent(player, reason, executor));
        });
    }

    public final void setMuteReason(final String player, final String reason, final String executor) {
        CompletableFuture.runAsync(() -> {
            this.muteCollection.update("player", player, new UDocument("reason", reason));

            final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
            if (proxiedPlayer != null) {
                this.getMute(player, mute -> {
                    this.cachedMutes.remove(player);
                    this.cachedMutes.put(player, mute);
                });
            }

            ProxyServer.getInstance().getPluginManager().callEvent(new EditMuteReasonEvent(player, reason, executor));
        });
    }

    public final void setBanTime(final String player, final long time, final String executor) {
        CompletableFuture.runAsync(() -> {
            this.banCollection.update("player", player, new UDocument("time", time));

            ProxyServer.getInstance().getPluginManager().callEvent(new EditBanTimeEvent(player, time, executor));
        });
    }

    public final void setMuteTime(final String player, final long time, final String executor) {
        CompletableFuture.runAsync(() -> {
            this.muteCollection.update("player", player, new UDocument("time", time));

            final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);
            if (proxiedPlayer != null) {
                this.getMute(player, mute -> {
                    this.cachedMutes.remove(player);
                    this.cachedMutes.put(player, mute);
                });
            }

            ProxyServer.getInstance().getPluginManager().callEvent(new EditMuteTimeEvent(player, time, executor));
        });
    }

    public final void deleteBan(final String id, final String executor) {
        CompletableFuture.runAsync(() -> {
            this.banlogCollection.delete("id", id);

            ProxyServer.getInstance().getPluginManager().callEvent(new DeleteBanEvent(id, executor));
        });
    }

    public final void deleteMute(final String id, final String executor) {
        CompletableFuture.runAsync(() -> {
            this.mutelogCollection.delete("id", id);

            ProxyServer.getInstance().getPluginManager().callEvent(new DeleteMuteEvent(id, executor));
        });
    }

    public final void deleteWarn(final String id, final String executor) {
        CompletableFuture.runAsync(() -> {
            this.warnCollection.delete("id", id);

            ProxyServer.getInstance().getPluginManager().callEvent(new DeleteWarnEvent(id, executor));
        });
    }

    public final String getRandomIDCode(final int l) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        final StringBuilder stringBuilder = new StringBuilder();
        final Random rnd = new Random();
        while (stringBuilder.length() < l) {
            final int index = (int) (rnd.nextFloat() * chars.length());
            stringBuilder.append(chars.charAt(index));
        }
        return stringBuilder.toString();
    }

    public final String getDate() {
        final Date now = new Date();
        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");
        return dateFormat.format(now);
    }

    public final String getRemainingTime(final long duration) {
        if (duration == -1L) {
            return Language.getNP("unit.permanent");
        } else {
            final SimpleDateFormat today = new SimpleDateFormat("dd.MM.yyyy");
            today.format(System.currentTimeMillis());
            final SimpleDateFormat future = new SimpleDateFormat("dd.MM.yyyy");
            future.format(duration);
            final long time = future.getCalendar().getTimeInMillis() - today.getCalendar().getTimeInMillis();
            final int days = (int) (time / 86400000L);
            final int hours = (int) (time / 3600000L % 24L);
            final int minutes = (int) (time / 60000L % 60L);
            String day = Language.getNP("unit.days");
            if (days == 1) {
                day = Language.getNP("unit.day");
            }

            String hour = Language.getNP("unit.hours");
            if (hours == 1) {
                hour = Language.getNP("unit.hour");
            }

            String minute = Language.getNP("unit.minutes");
            if (minutes == 1) {
                minute = Language.getNP("unit.minute");
            }

            if (minutes < 1 && days == 0 && hours == 0) {
                return Language.getNP("unit.seconds");
            } else if (hours == 0 && days == 0) {
                return minutes + " " + minute;
            } else {
                return days == 0 ? hours + " " + hour + " " + minutes + " " + minute : days + " " + day + " " + hours + " " + hour + " " + minutes + " " + minute;
            }
        }
    }

}

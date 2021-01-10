package net.llamadevelopment.bansystemwd;

import lombok.Getter;
import net.llamadevelopment.bansystemwd.commands.*;
import net.llamadevelopment.bansystemwd.components.api.API;
import net.llamadevelopment.bansystemwd.components.config.Config;
import net.llamadevelopment.bansystemwd.components.data.BanReason;
import net.llamadevelopment.bansystemwd.components.data.MuteReason;
import net.llamadevelopment.bansystemwd.components.language.Language;
import net.llamadevelopment.bansystemwd.components.provider.Provider;
import net.llamadevelopment.bansystemwd.components.provider.UniversalClient;
import net.llamadevelopment.bansystemwd.components.provider.data.clientdetails.MongoDbDetails;
import net.llamadevelopment.bansystemwd.components.provider.data.clientdetails.MySqlDetails;
import net.llamadevelopment.bansystemwd.components.provider.data.clientdetails.YamlDetails;
import net.llamadevelopment.bansystemwd.listeners.EventListener;
import net.md_5.bungee.api.plugin.Plugin;

public class BanSystemWD extends Plugin {

    @Getter
    private static API api;

    @Getter
    private Provider provider;

    @Getter
    private Config config;

    @Override
    public void onEnable() {
        try {
            Config.saveResource("config.yml", this);
            this.config = new Config(this.getDataFolder() + "/config.yml", Config.YAML);
            UniversalClient client = null;
            switch (this.config.getString("Provider").toLowerCase()) {
                case "mysql":
                    client = new UniversalClient(
                            UniversalClient.Type.MySql,
                            new MySqlDetails(
                                    this.config.getString("MySql.Host"),
                                    this.config.getString("MySql.Port"),
                                    this.config.getString("MySql.User"),
                                    this.config.getString("MySql.Password"),
                                    this.config.getString("MySql.Database")
                            )
                    );
                    break;
                case "mongodb":
                    client = new UniversalClient(
                            UniversalClient.Type.MongoDB,
                            new MongoDbDetails(
                                    this.config.getString("MongoDB.Uri"),
                                    this.config.getString("MongoDB.Database")
                            )
                    );
                    break;
                case "yaml":
                    client = new UniversalClient(
                            UniversalClient.Type.Yaml,
                            new YamlDetails(
                                    this.getDataFolder().toString() + "/data"
                            )
                    );
                    break;
                default:
                    this.getLogger().info("§4Please specify a valid provider: Yaml, MySql, MongoDB");
                    break;
            }
            this.provider = new Provider(client);
            this.provider.init(this);
            this.getLogger().info("§aSuccessfully loaded provider.");
            api = new API(this.provider);
            Language.init(this);
            this.loadPlugin();
            this.getLogger().info("§aBanSystem successfully started.");
        } catch (Exception e) {
            e.printStackTrace();
            this.getLogger().warning("§4Failed to load BanSystem.");
        }
    }

    private void loadPlugin() {
        this.getProxy().getPluginManager().registerListener(this, new EventListener(this));

        this.getProxy().getPluginManager().registerCommand(this, new BanCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new BanlogCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new CheckbanCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new CheckmuteCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new ClearbanlogCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new ClearmutelogCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new ClearwarningsCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new DeletebanCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new DeletemuteCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new DeletewarnCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new EditbanCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new EditmuteCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new KickCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new MuteCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new MutelogCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new TempbanCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new TempmuteCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new UnbanCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new UnmuteCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new WarnCommand(this.config, this));
        this.getProxy().getPluginManager().registerCommand(this, new WarnlogCommand(this.config, this));

        for (String s : this.config.getSection("Reasons.BanReasons").getAll().getKeys(false))
            this.provider.banReasons.put(s, new BanReason(this.config.getString("Reasons.BanReasons." + s + ".Reason"), s, this.config.getInt("Reasons.BanReasons." + s + ".Seconds")));
        for (String s : this.config.getSection("Reasons.MuteReasons").getAll().getKeys(false))
            this.provider.muteReasons.put(s, new MuteReason(this.config.getString("Reasons.MuteReasons." + s + ".Reason"), s, this.config.getInt("Reasons.MuteReasons." + s + ".Seconds")));
    }

    @Override
    public void onDisable() {
        this.provider.disconnect();
    }

}

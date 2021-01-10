package net.llamadevelopment.bansystemwd.components.config;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.md_5.bungee.api.plugin.Plugin;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/*
 * Code by https://github.com/CloudburstMC/Nukkit
 */

public class Config {

    public static final int DETECT = -1;
    public static final int PROPERTIES = 0;
    public static final int CNF = 0;
    public static final int JSON = 1;
    public static final int YAML = 2;
    public static final int ENUM = 5;
    public static final int ENUMERATION = 5;
    private ConfigSection config;
    private File file;
    private boolean correct;
    private int type;
    public static final Map<String, Integer> format = new TreeMap();

    public Config(int type) {
        this.config = new ConfigSection();
        this.correct = false;
        this.type = -1;
        this.type = type;
        this.correct = true;
        this.config = new ConfigSection();
    }

    public Config() {
        this(2);
    }

    public Config(String file) {
        this(file, -1);
    }

    public Config(String file, int type) {
        this(file, type, new ConfigSection());
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Config(String file, int type, LinkedHashMap<String, Object> defaultMap) {
        this.config = new ConfigSection();
        this.correct = false;
        this.type = -1;
        this.load(file, type, new ConfigSection(defaultMap));
    }

    public Config(String file, int type, ConfigSection defaultMap) {
        this.config = new ConfigSection();
        this.correct = false;
        this.type = -1;
        this.load(file, type, defaultMap);
    }

    public Config(File file, int type, ConfigSection defaultMap) {
        this.config = new ConfigSection();
        this.correct = false;
        this.type = -1;
        this.load(file.toString(), type, defaultMap);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Config(File file, int type, LinkedHashMap<String, Object> defaultMap) {
        this(file.toString(), type, new ConfigSection(defaultMap));
    }

    public static InputStream getResource(String filename) {
        return Config.class.getClassLoader().getResourceAsStream(filename);
    }

    public static void saveResource(String filename, Plugin instance) {
        saveResource(filename, filename, false, instance);
    }

    public static void saveResource(String filename, String outputName, boolean replace, Plugin instance) {
        Preconditions.checkArgument(filename != null && outputName != null, "Filename can not be null!");
        Preconditions.checkArgument(filename.trim().length() != 0 && outputName.trim().length() != 0, "Filename can not be empty!");
        File out = new File(instance.getDataFolder(), outputName);
        if (!out.exists() || replace) {
            try {
                InputStream resource = getResource(filename);
                Throwable var6 = null;
                try {
                    if (resource == null) {
                        return;
                    }
                    File outFolder = out.getParentFile();
                    if (!outFolder.exists()) {
                        outFolder.mkdirs();
                    }
                    FileUtils.writeFile(out, resource);
                } catch (Throwable var19) {
                    var6 = var19;
                    throw var19;
                } finally {
                    if (resource != null) {
                        if (var6 != null) {
                            try {
                                resource.close();
                            } catch (Throwable var18) {
                                var6.addSuppressed(var18);
                            }
                        } else {
                            resource.close();
                        }
                    }
                }
            } catch (IOException var21) {
                var21.printStackTrace();
            }
        }
    }

    public void reload() {
        this.config.clear();
        this.correct = false;
        if (this.file == null) {
            throw new IllegalStateException("Failed to reload Config. File object is undefined.");
        } else {
            this.load(this.file.toString(), this.type);
        }
    }

    public boolean load(String file) {
        return this.load(file, -1);
    }

    public boolean load(String file, int type) {
        return this.load(file, type, new ConfigSection());
    }

    public boolean load(String file, int type, ConfigSection defaultMap) {
        this.correct = true;
        this.type = type;
        this.file = new File(file);
        if (!this.file.exists()) {
            try {
                this.file.getParentFile().mkdirs();
                this.file.createNewFile();
            } catch (IOException var7) {
                var7.printStackTrace();
            }

            this.config = defaultMap;
            this.save();
        } else {
            String content;
            if (this.type == -1) {
                content = "";
                if (this.file.getName().lastIndexOf(".") != -1 && this.file.getName().lastIndexOf(".") != 0) {
                    content = this.file.getName().substring(this.file.getName().lastIndexOf(".") + 1);
                }

                if (format.containsKey(content)) {
                    this.type = (Integer) format.get(content);
                } else {
                    this.correct = false;
                }
            }

            if (!this.correct) {
                return false;
            }

            content = "";

            try {
                content = FileUtils.readFile(this.file);
            } catch (IOException var6) {
                var6.printStackTrace();
            }

            this.parseContent(content);
            if (!this.correct) {
                return false;
            }

            if (this.setDefault(defaultMap) > 0) {
                this.save();
            }
        }

        return true;
    }

    public boolean load(InputStream inputStream) {
        if (inputStream == null) {
            return false;
        } else {
            if (this.correct) {
                String content;
                try {
                    content = FileUtils.readFile(inputStream);
                } catch (IOException var4) {
                    var4.printStackTrace();
                    return false;
                }

                this.parseContent(content);
            }

            return this.correct;
        }
    }

    public boolean check() {
        return this.correct;
    }

    public boolean isCorrect() {
        return this.correct;
    }

    public boolean save(File file) {
        this.file = file;
        return this.save();
    }

    public boolean save() {
        if (this.file == null) {
            throw new IllegalStateException("Failed to save Config. File object is undefined.");
        } else if (!this.correct) {
            return false;
        } else {
            StringBuilder content = new StringBuilder();
            switch (this.type) {
                case 0:
                    content = new StringBuilder(this.writeProperties());
                    break;
                case 1:
                    content = new StringBuilder((new GsonBuilder()).setPrettyPrinting().create().toJson(this.config));
                    break;
                case 2:
                    DumperOptions dumperOptions = new DumperOptions();
                    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                    Yaml yaml = new Yaml(dumperOptions);
                    content = new StringBuilder(yaml.dump(this.config));
                case 3:
                case 4:
                default:
                    break;
                case 5:
                    Iterator var5 = this.config.entrySet().iterator();

                    while (var5.hasNext()) {
                        Object o = var5.next();
                        Map.Entry entry = (Map.Entry) o;
                        content.append(entry.getKey()).append("\r\n");
                    }
            }
            try {
                FileUtils.writeFile(this.file, content.toString());
            } catch (IOException var8) {
                var8.printStackTrace();
            }
            return true;
        }
    }

    public void set(String key, Object value) {
        this.config.set(key, value);
    }

    public Object get(String key) {
        return this.get(key, (Object) null);
    }

    public <T> T get(String key, T defaultValue) {
        return this.correct ? this.config.get(key, defaultValue) : defaultValue;
    }

    public ConfigSection getSection(String key) {
        return this.correct ? this.config.getSection(key) : new ConfigSection();
    }

    public boolean isSection(String key) {
        return this.config.isSection(key);
    }

    public ConfigSection getSections(String key) {
        return this.correct ? this.config.getSections(key) : new ConfigSection();
    }

    public ConfigSection getSections() {
        return this.correct ? this.config.getSections() : new ConfigSection();
    }

    public int getInt(String key) {
        return this.getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return this.correct ? this.config.getInt(key, defaultValue) : defaultValue;
    }

    public boolean isInt(String key) {
        return this.config.isInt(key);
    }

    public long getLong(String key) {
        return this.getLong(key, 0L);
    }

    public long getLong(String key, long defaultValue) {
        return this.correct ? this.config.getLong(key, defaultValue) : defaultValue;
    }

    public boolean isLong(String key) {
        return this.config.isLong(key);
    }

    public double getDouble(String key) {
        return this.getDouble(key, 0.0D);
    }

    public double getDouble(String key, double defaultValue) {
        return this.correct ? this.config.getDouble(key, defaultValue) : defaultValue;
    }

    public boolean isDouble(String key) {
        return this.config.isDouble(key);
    }

    public String getString(String key) {
        return this.getString(key, "");
    }

    public String getString(String key, String defaultValue) {
        return this.correct ? this.config.getString(key, defaultValue) : defaultValue;
    }

    public boolean isString(String key) {
        return this.config.isString(key);
    }

    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return this.correct ? this.config.getBoolean(key, defaultValue) : defaultValue;
    }

    public boolean isBoolean(String key) {
        return this.config.isBoolean(key);
    }

    public List getList(String key) {
        return this.getList(key, (List) null);
    }

    public List getList(String key, List defaultList) {
        return this.correct ? this.config.getList(key, defaultList) : defaultList;
    }

    public boolean isList(String key) {
        return this.config.isList(key);
    }

    public List<String> getStringList(String key) {
        return this.config.getStringList(key);
    }

    public List<Integer> getIntegerList(String key) {
        return this.config.getIntegerList(key);
    }

    public List<Boolean> getBooleanList(String key) {
        return this.config.getBooleanList(key);
    }

    public List<Double> getDoubleList(String key) {
        return this.config.getDoubleList(key);
    }

    public List<Float> getFloatList(String key) {
        return this.config.getFloatList(key);
    }

    public List<Long> getLongList(String key) {
        return this.config.getLongList(key);
    }

    public List<Byte> getByteList(String key) {
        return this.config.getByteList(key);
    }

    public List<Character> getCharacterList(String key) {
        return this.config.getCharacterList(key);
    }

    public List<Short> getShortList(String key) {
        return this.config.getShortList(key);
    }

    public List<Map> getMapList(String key) {
        return this.config.getMapList(key);
    }

    public void setAll(LinkedHashMap<String, Object> map) {
        this.config = new ConfigSection(map);
    }

    public void setAll(ConfigSection section) {
        this.config = section;
    }

    public boolean exists(String key) {
        return this.config.exists(key);
    }

    public boolean exists(String key, boolean ignoreCase) {
        return this.config.exists(key, ignoreCase);
    }

    public void remove(String key) {
        this.config.remove(key);
    }

    public Map<String, Object> getAll() {
        return this.config.getAllMap();
    }

    public ConfigSection getRootSection() {
        return this.config;
    }

    public int setDefault(LinkedHashMap<String, Object> map) {
        return this.setDefault(new ConfigSection(map));
    }

    public int setDefault(ConfigSection map) {
        int size = this.config.size();
        this.config = this.fillDefaults(map, this.config);
        return this.config.size() - size;
    }

    private ConfigSection fillDefaults(ConfigSection defaultMap, ConfigSection data) {
        Iterator var3 = defaultMap.keySet().iterator();

        while (var3.hasNext()) {
            String key = (String) var3.next();
            if (!data.containsKey(key)) {
                data.put(key, defaultMap.get(key));
            }
        }

        return data;
    }

    private void parseList(String content) {
        content = content.replace("\r\n", "\n");
        String[] var2 = content.split("\n");
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String v = var2[var4];
            if (!v.trim().isEmpty()) {
                this.config.put(v, true);
            }
        }

    }

    private String writeProperties() {
        StringBuilder content = new StringBuilder("#Properties Config file\r\n#" + (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(new Date()) + "\r\n");

        Object v;
        Object k;
        for (Iterator var2 = this.config.entrySet().iterator(); var2.hasNext(); content.append(k).append("=").append(v).append("\r\n")) {
            Object o = var2.next();
            Map.Entry entry = (Map.Entry) o;
            v = entry.getValue();
            k = entry.getKey();
            if (v instanceof Boolean) {
                v = (Boolean) v ? "on" : "off";
            }
        }

        return content.toString();
    }

    private void parseProperties(String content) {
        String[] var2 = content.split("\n");
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String line = var2[var4];
            if (Pattern.compile("[a-zA-Z0-9\\-_.]*+=+[^\\r\\n]*").matcher(line).matches()) {
                int splitIndex = line.indexOf(61);
                if (splitIndex != -1) {
                    String key = line.substring(0, splitIndex);
                    String value = line.substring(splitIndex + 1);
                    String valueLower = value.toLowerCase();

                    byte var11 = -1;
                    switch (valueLower.hashCode()) {
                        case 3521:
                            if (valueLower.equals("no")) {
                                var11 = 5;
                            }
                            break;
                        case 3551:
                            if (valueLower.equals("on")) {
                                var11 = 0;
                            }
                            break;
                        case 109935:
                            if (valueLower.equals("off")) {
                                var11 = 3;
                            }
                            break;
                        case 119527:
                            if (valueLower.equals("yes")) {
                                var11 = 2;
                            }
                            break;
                        case 3569038:
                            if (valueLower.equals("true")) {
                                var11 = 1;
                            }
                            break;
                        case 97196323:
                            if (valueLower.equals("false")) {
                                var11 = 4;
                            }
                    }

                    switch (var11) {
                        case 0:
                        case 1:
                        case 2:
                            this.config.put(key, true);
                            break;
                        case 3:
                        case 4:
                        case 5:
                            this.config.put(key, false);
                            break;
                        default:
                            this.config.put(key, value);
                    }
                }
            }
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    public Object getNested(String key) {
        return this.get(key);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public <T> T getNested(String key, T defaultValue) {
        return this.get(key, defaultValue);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public <T> T getNestedAs(String key, Class<T> type) {
        return (T) this.get(key);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void removeNested(String key) {
        this.remove(key);
    }

    private void parseContent(String content) {
        switch (this.type) {
            case 0:
                this.parseProperties(content);
                break;
            case 1:
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                this.config = new ConfigSection((LinkedHashMap) gson.fromJson(content, (new TypeToken<LinkedHashMap<String, Object>>() {
                }).getType()));
                break;
            case 2:
                DumperOptions dumperOptions = new DumperOptions();
                dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                Yaml yaml = new Yaml(dumperOptions);
                this.config = new ConfigSection((LinkedHashMap) yaml.loadAs(content, LinkedHashMap.class));
                break;
            case 3:
            case 4:
            default:
                this.correct = false;
                break;
            case 5:
                this.parseList(content);
        }

    }

    public Set getKeys() {
        return this.correct ? this.config.getKeys() : new HashSet<>();
    }

    public Set getKeys(boolean child) {
        return this.correct ? this.config.getKeys(child) : new HashSet<>();
    }

    static {
        format.put("properties", 0);
        format.put("con", 0);
        format.put("conf", 0);
        format.put("config", 0);
        format.put("js", 1);
        format.put("json", 1);
        format.put("yml", 2);
        format.put("yaml", 2);
        format.put("txt", 5);
        format.put("list", 5);
        format.put("enum", 5);
    }
}

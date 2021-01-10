package net.llamadevelopment.bansystemwd.components.config;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Code by https://github.com/CloudburstMC/Nukkit
 */

public class FileUtils {

    public static void writeFile(String fileName, String content) throws IOException {
        writeFile(fileName, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public static void writeFile(String fileName, InputStream content) throws IOException {
        writeFile(new File(fileName), content);
    }

    public static void writeFile(File file, String content) throws IOException {
        writeFile((File)file, (InputStream)(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))));
    }

    public static void writeFile(File file, InputStream content) throws IOException {
        if (content == null) {
            throw new IllegalArgumentException("content must not be null");
        } else {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream stream = new FileOutputStream(file);
            Throwable var3 = null;

            try {
                byte[] buffer = new byte[1024];

                int length;
                while((length = content.read(buffer)) != -1) {
                    stream.write(buffer, 0, length);
                }
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if (stream != null) {
                    if (var3 != null) {
                        try {
                            stream.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        stream.close();
                    }
                }

            }

            content.close();
        }
    }

    public static String readFile(File file) throws IOException {
        if (file.exists() && !file.isDirectory()) {
            return readFile((InputStream)(new FileInputStream(file)));
        } else {
            throw new FileNotFoundException();
        }
    }

    public static String readFile(String filename) throws IOException {
        File file = new File(filename);
        if (file.exists() && !file.isDirectory()) {
            return readFile((InputStream)(new FileInputStream(file)));
        } else {
            throw new FileNotFoundException();
        }
    }

    public static String readFile(InputStream inputStream) throws IOException {
        return readFile((Reader)(new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
    }

    private static String readFile(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        Throwable var2 = null;

        String var5;
        try {
            StringBuilder stringBuilder = new StringBuilder();

            for(String temp = br.readLine(); temp != null; temp = br.readLine()) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append("\n");
                }

                stringBuilder.append(temp);
            }

            var5 = stringBuilder.toString();
        } catch (Throwable var14) {
            var2 = var14;
            throw var14;
        } finally {
            if (br != null) {
                if (var2 != null) {
                    try {
                        br.close();
                    } catch (Throwable var13) {
                        var2.addSuppressed(var13);
                    }
                } else {
                    br.close();
                }
            }

        }

        return var5;
    }

    public static void copyFile(File from, File to) throws IOException {
        if (!from.exists()) {
            throw new FileNotFoundException();
        } else if (!from.isDirectory() && !to.isDirectory()) {
            FileInputStream fi = null;
            FileChannel in = null;
            FileOutputStream fo = null;
            FileChannel out = null;

            try {
                if (!to.exists()) {
                    to.createNewFile();
                }

                fi = new FileInputStream(from);
                in = fi.getChannel();
                fo = new FileOutputStream(to);
                out = fo.getChannel();
                in.transferTo(0L, in.size(), out);
            } finally {
                if (fi != null) {
                    fi.close();
                }

                if (in != null) {
                    in.close();
                }

                if (fo != null) {
                    fo.close();
                }

                if (out != null) {
                    out.close();
                }

            }

        } else {
            throw new FileNotFoundException();
        }
    }

    public static String getAllThreadDumps() {
        ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
        StringBuilder builder = new StringBuilder();
        ThreadInfo[] var2 = threads;
        int var3 = threads.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            ThreadInfo info = var2[var4];
            builder.append('\n').append(info);
        }

        return builder.toString();
    }

    public static String getExceptionMessage(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        Throwable var3 = null;

        try {
            e.printStackTrace(printWriter);
            printWriter.flush();
        } catch (Throwable var12) {
            var3 = var12;
            throw var12;
        } finally {
            if (printWriter != null) {
                if (var3 != null) {
                    try {
                        printWriter.close();
                    } catch (Throwable var11) {
                        var3.addSuppressed(var11);
                    }
                } else {
                    printWriter.close();
                }
            }

        }

        return stringWriter.toString();
    }

    public static UUID dataToUUID(String... params) {
        StringBuilder builder = new StringBuilder();
        String[] var2 = params;
        int var3 = params.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String param = var2[var4];
            builder.append(param);
        }

        return UUID.nameUUIDFromBytes(builder.toString().getBytes(StandardCharsets.UTF_8));
    }

    public static UUID dataToUUID(byte[]... params) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[][] var2 = params;
        int var3 = params.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            byte[] param = var2[var4];

            try {
                stream.write(param);
            } catch (IOException var7) {
                break;
            }
        }

        return UUID.nameUUIDFromBytes(stream.toByteArray());
    }

    public static String rtrim(String s, char character) {
        int i;
        for(i = s.length() - 1; i >= 0 && s.charAt(i) == character; --i) {
        }

        return s.substring(0, i + 1);
    }

    public static boolean isByteArrayEmpty(byte[] array) {
        byte[] var1 = array;
        int var2 = array.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            byte b = var1[var3];
            if (b != 0) {
                return false;
            }
        }

        return true;
    }

    public static long toRGB(byte r, byte g, byte b, byte a) {
        long result = (long)(r & 255);
        result |= (long)((g & 255) << 8);
        result |= (long)((b & 255) << 16);
        result |= (long)((a & 255) << 24);
        return result & 4294967295L;
    }

    public static long toABGR(int argb) {
        long result = (long)argb & 4278255360L;
        result |= (long)(argb << 16) & 16711680L;
        result |= (long)(argb >>> 16) & 255L;
        return result & 4294967295L;
    }

    public static Object[][] splitArray(Object[] arrayToSplit, int chunkSize) {
        if (chunkSize <= 0) {
            return (Object[][])null;
        } else {
            int rest = arrayToSplit.length % chunkSize;
            int chunks = arrayToSplit.length / chunkSize + (rest > 0 ? 1 : 0);
            Object[][] arrays = new Object[chunks][];

            for(int i = 0; i < (rest > 0 ? chunks - 1 : chunks); ++i) {
                arrays[i] = Arrays.copyOfRange(arrayToSplit, i * chunkSize, i * chunkSize + chunkSize);
            }

            if (rest > 0) {
                arrays[chunks - 1] = Arrays.copyOfRange(arrayToSplit, (chunks - 1) * chunkSize, (chunks - 1) * chunkSize + rest);
            }

            return arrays;
        }
    }

    public static <T> void reverseArray(T[] data) {
        reverseArray(data, false);
    }

    public static <T> T[] reverseArray(T[] array, boolean copy) {
        T[] data = array;
        if (copy) {
            data = Arrays.copyOf(array, array.length);
        }

        int left = 0;

        for(int right = data.length - 1; left < right; --right) {
            T temp = data[left];
            data[left] = data[right];
            data[right] = temp;
            ++left;
        }

        return data;
    }

    public static <T> T[][] clone2dArray(T[][] array) {
        T[][] newArray = (T[][]) Arrays.copyOf(array, array.length);

        for(int i = 0; i < array.length; ++i) {
            newArray[i] = Arrays.copyOf(array[i], array[i].length);
        }

        return newArray;
    }

    public static <T, U, V> Map<U, V> getOrCreate(Map<T, Map<U, V>> map, T key) {
        Map<U, V> existing = (Map)map.get(key);
        if (existing == null) {
            ConcurrentHashMap<U, V> toPut = new ConcurrentHashMap();
            existing = (Map)map.putIfAbsent(key, toPut);
            if (existing == null) {
                existing = toPut;
            }
        }

        return (Map)existing;
    }

    public static <T, U, V extends U> U getOrCreate(Map<T, U> map, Class<V> clazz, T key) {
        U existing = map.get(key);
        if (existing != null) {
            return existing;
        } else {
            try {
                U toPut = clazz.newInstance();
                existing = map.putIfAbsent(key, toPut);
                return existing == null ? toPut : existing;
            } catch (IllegalAccessException | InstantiationException var5) {
                throw new RuntimeException(var5);
            }
        }
    }

    public static int toInt(Object number) {
        return number instanceof Integer ? (Integer)number : (int)Math.round((Double)number);
    }

    public static byte[] parseHexBinary(String s) {
        int len = s.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        } else {
            byte[] out = new byte[len / 2];

            for(int i = 0; i < len; i += 2) {
                int h = hexToBin(s.charAt(i));
                int l = hexToBin(s.charAt(i + 1));
                if (h == -1 || l == -1) {
                    throw new IllegalArgumentException("contains illegal character for hexBinary: " + s);
                }

                out[i / 2] = (byte)(h * 16 + l);
            }

            return out;
        }
    }

    private static int hexToBin(char ch) {
        if ('0' <= ch && ch <= '9') {
            return ch - 48;
        } else if ('A' <= ch && ch <= 'F') {
            return ch - 65 + 10;
        } else {
            return 'a' <= ch && ch <= 'f' ? ch - 97 + 10 : -1;
        }
    }
}

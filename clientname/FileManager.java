package clientname;

import com.google.gson.*;
import java.io.*;

public class FileManager
{
    private static Gson gson;
    private static File ROOT_DIR;
    private static File MODS_DIR;
    
    static {
        FileManager.gson = new Gson();
        FileManager.ROOT_DIR = new File("VorkksClient");
        FileManager.MODS_DIR = new File(FileManager.ROOT_DIR, "Mods");
    }
    
    public static void init() {
        if (!FileManager.ROOT_DIR.exists()) {
            FileManager.ROOT_DIR.mkdirs();
        }
        if (!FileManager.MODS_DIR.exists()) {
            FileManager.MODS_DIR.mkdirs();
        }
    }
    
    public static Gson getGson() {
        return FileManager.gson;
    }
    
    public static File getModsDirectory() {
        return FileManager.MODS_DIR;
    }
    
    public static boolean writeJsonToFile(final File file, final Object obj) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            final FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(FileManager.gson.toJson(obj).getBytes());
            outputStream.flush();
            outputStream.close();
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static <T> T readFromJson(final File file, final Class<T> c) {
        try {
            final FileInputStream fileInputStream = new FileInputStream(file);
            final InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final StringBuilder builder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
            return (T)FileManager.gson.fromJson(builder.toString(), (Class)c);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

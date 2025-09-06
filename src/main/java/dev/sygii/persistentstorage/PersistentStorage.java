package dev.sygii.persistentstorage;

import com.google.gson.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PersistentStorage implements ModInitializer {
	public static final String MOD_ID = "persistentstorage";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static final Path STORAGE_FILE = Path.of(FabricLoader.getInstance().getGameDir() + File.separator + "persistent_storage.json");

	public static final Map<String, Object> STORAGE = new HashMap<>();

	public static JsonObject JSON;
	public static JsonArray TAGS;

	@Override
	public void onInitialize() {
	}

	public static void initStorage() {
		try {
			if (!Files.exists(PersistentStorage.STORAGE_FILE)) {
				Files.write(STORAGE_FILE, GSON.toJson(new Storage()).getBytes());
			}

			BufferedReader inputStream = Files.newBufferedReader(STORAGE_FILE);
			JSON = JsonParser.parseReader(inputStream).getAsJsonObject();
			if (!JSON.has("tags")) {
				put("tags", new JsonArray());
			}
			TAGS = JSON.get("tags").getAsJsonArray();
		} catch (IOException ignored) {
			ignored.printStackTrace();
		}
	}

	public static boolean getTag(String tag) {
		return TAGS.contains(GSON.toJsonTree(tag));
	}

	public static void putTag(String tag) {
		if (!TAGS.contains(GSON.toJsonTree(tag))) {
			TAGS.add(tag);
			saveStorage();
		}
	}

	public static JsonElement get(String key) {
		return JSON.get(key);
	}

	public static void put(String key, Object value) {
		JSON.add(key, GSON.toJsonTree(value));
		saveStorage();
	}

	public static boolean getBoolean(String key) {
		if (get(key) != null && get(key).getAsJsonPrimitive().isBoolean()) {
			return get(key).getAsBoolean();
		}
		return false;
	}

	public static void saveStorage() {
		try {
			Files.write(STORAGE_FILE, GSON.toJson(JSON).getBytes());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static class Storage {
		public String[] tags = {};
	}
}

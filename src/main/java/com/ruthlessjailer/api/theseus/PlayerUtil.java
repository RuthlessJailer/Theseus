//package com.ruthlessjailer.api.theseus;
//
//import com.google.gson.Gson;
//import lombok.SneakyThrows;
//import lombok.With;
//import org.bukkit.Bukkit;
//import org.bukkit.OfflinePlayer;
//import org.bukkit.entity.Player;
//import org.json.simple.JSONObject;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.concurrent.CompletableFuture;
//
//@SuppressWarnings("unchecked")
//public final class PlayerUtil {
//
//	private static final Map<String, UUID> cached = new HashMap<>();
//
//	static {
//		Gson gson = new Gson();
//		String json = gson.toJson(cached);
//
//		File file = new File(Theseus.getInstance().getDataFolder().getPath() + "/cache.json");
//		if(file.exists()){
//			try {
//				Map<String, UUID> result = (Map<String, UUID>) gson.fromJson(new String(Files.readAllBytes(Paths.get(file.toURI())), StandardCharsets.UTF_8), Map.class);
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public static CompletableFuture<OfflinePlayer> getPlayerAsync(String name){
//		return (cached.containsKey(name.toUpperCase()) ? CompletableFuture.supplyAsync(() -> Bukkit.getOfflinePlayer(cached.get(name.toUpperCase()))) : CompletableFuture.supplyAsync(() -> {
//			final OfflinePlayer player = Bukkit.getOfflinePlayer(name);
//			if(player.getName() != null) {
//				cached.put(player.getName().toUpperCase(), player.getUniqueId());
//			}
//			return player;
//		}));
//	}
//
//	@SneakyThrows
//	public static void saveCache(){
//		Gson gson = new Gson();
//		String json = gson.toJson(cached);
//
//		File file = new File(Theseus.getInstance().getDataFolder().getPath() + "/cache.json");
//		if(!file.exists()){
//			file.createNewFile();
//		}
//		FileWriter writer = new FileWriter(file);
//		writer.write(json);
//		writer.close();
//	}
//
//}

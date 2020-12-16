package com.ruthlessjailer.api.theseus.io.update;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import com.ruthlessjailer.api.theseus.io.DiskUtil;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author RuthlessJailer
 */
@Getter
public class Updater {

	private static final String USER_AGENT = "Theseus API Updater by RuthlessJailer";

	private static final String RESOURCE = "https://api.spiget.org/v2/resources/";
	private static final String DOWNLOAD = "/download";
	private static final String VERSIONS = "/versions";
	private static final String PAGE     = "?page=";

	private final JavaPlugin plugin;
	private final int        id;
	private final File       jar;
	private final File       folder;
	private       Result     result;

	public Updater(final int id) {
		this(Checks.instanceCheck(), id);
	}

	public Updater(@NonNull final JavaPlugin plugin, final int id) {
		this.plugin = plugin;
		this.id     = id;
		this.jar    = ReflectUtil.invokeMethod(JavaPlugin.class, "getFile", plugin);
		this.folder = DiskUtil.getFile(Bukkit.getWorldContainer().getPath() + "/plugins");

	}

	public String getResourceURL() {
		return RESOURCE + this.id;
	}

	public boolean isValidResource() {
		try {
			final URL               url        = new URL(getResourceURL());
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.addRequestProperty("User-Agent", USER_AGENT);

			final int code = connection.getResponseCode();

			if (code != 200) {//bad id
				connection.disconnect();
				this.result = Result.BAD_ID;
				return false;
			}

		} catch (final IOException e) {
			e.printStackTrace();
			this.result = Result.FAIL;
			return false;
		}

		return true;
	}

	public boolean canUpdate() {
		try {

			JsonElement element;
			JsonArray   array;

			int i = 1;
			while ((array = getPage(i).getAsJsonArray()).size() != 0) {
				i++;
			}

			array = getPage(i - 1).getAsJsonArray();

			element = array.get(array.size() - 1);

			final JsonObject object = element.getAsJsonObject();
			element = object.get("name");

			final String version = element.getAsString().replaceAll("\"", "").replaceFirst("v", "");

			System.out.println(object.get("name").getAsString());
			System.out.println(version);
			return true;

		} catch (final IOException e) {
			e.printStackTrace();
			this.result = Result.FAIL;
			return false;
		}
	}

	private JsonElement getPage(final int page) throws IOException {

		final URL url = new URL(getResourceURL() + VERSIONS + PAGE + page);

		final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.addRequestProperty("User-Agent", USER_AGENT);

		return new JsonParser().parse(new InputStreamReader(connection.getInputStream()));
	}

	public enum Result {
		FAIL,
		SUCCESS,
		BAD_ID,
		UPDATE_EXISTS,
		NO_UPDATE_FOUND
	}
}

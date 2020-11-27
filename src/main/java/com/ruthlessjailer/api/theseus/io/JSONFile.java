package com.ruthlessjailer.api.theseus.io;

import com.google.gson.*;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.PluginBase;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author RuthlessJailer
 */
@Getter
public abstract class JSONFile implements IFile {

	protected final Gson   GSON;
	protected final String path;
	protected final File   file;

	@Setter
	private Charset charset = StandardCharsets.UTF_8;

	public JSONFile(final String path) {
		this(new GsonBuilder().setPrettyPrinting().create(), path);
	}

	@SneakyThrows
	public JSONFile(final Gson gson, final String path) {
		this.GSON = gson;
		this.path = File.separator + (path.startsWith("/") || path.startsWith("\\") || path.startsWith(File.separator) ? path.substring(1) : path);
		this.file = new File(PluginBase.getFolder().getPath() + this.path);

		if (!this.file.exists()) {
			if (this.file.getParentFile() == null) {
				this.file.mkdirs();
			}

			this.file.createNewFile();

			final InputStream in = PluginBase.getPluginResource(getResourcePath());

			if (in != null) {//copy over default file from src/main/resources
				Files.copy(in, this.file.toPath(), StandardCopyOption.REPLACE_EXISTING);
//					final FileOutputStream out = new FileOutputStream(this.file);
//					final byte[]           buf = new byte[8192];
//					int                    n;
//					while ((n = in.read(buf)) > 0) {
//						out.write(buf, 0, n);
//					}
//					out.close();
//					in.close();
			}
		}
	}

	/**
	 * Checks the config file to make sure that it has all the values that the class contains. Only {@code public final} fields will be checked.
	 * Case will be ignored.<p>
	 * If a value is missing or null the default value will be written to the file.<p>
	 * Call this method before reading the file.
	 *
	 * @param file the {@link JSONFile} config instance to modify.
	 */
	@SneakyThrows
	protected static void fixConfig(@NonNull final JSONFile file) {
		final String contents = Common.getString(file.read());

		final List<Field> fields = Arrays.stream(file.getClass().getFields()).filter(field ->
																							 Modifier.isPublic(field.getModifiers()) &&
																							 Modifier.isFinal(field.getModifiers()))
										 .collect(Collectors.toList());

		JsonElement element = null;
		try {
			element = new JsonParser().parse(contents);
		} catch (final JsonParseException ignored) {}//malformed json

		final Map<String, JsonElement> content = new HashMap<>();

		if (element != null && !element.isJsonNull()) {
			for (final Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
				content.put(entry.getKey(), entry.getValue());//fill the content map
				if (entry.getValue().isJsonNull()) {//it's null; this one needs to be fixed
					continue;
				}
				Field match = null;
				for (final Field field : fields) {
					if (field.getName().equalsIgnoreCase(entry.getKey())) {//this one's fine
						match = field;
						break;
					}
				}
				fields.remove(match);
			}
		}

		if (fields.isEmpty()) {//all values were present
			return;
		}

		//some values are missing
		final InputStream in = PluginBase.getPluginResource(file.getResourcePath());

		if (in == null) {//it's not a resource; we can't do anything
			throw new UnsupportedOperationException("No resource found for file " + file.getPath());
		}

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(in, out);//get it to a byte array
		out.close();

		//loop through the default resource and replace all values in the file that are null or missing
		final JsonObject object = new JsonParser().parse(new String(out.toByteArray())).getAsJsonObject();
		for (final Map.Entry<String, JsonElement> entry : object.entrySet()) {
			for (final Field field : fields) {
				if (entry.getKey().equalsIgnoreCase(field.getName())) {//yay we found one that needs fixing
					content.put(entry.getKey(), entry.getValue());//add it to or replace it in the map so we can write it back to the config file
					break;
				}
			}
		}

		file.write(file.getGSON().toJson(content));//fill the config with all the repaired values

	}

	/**
	 * Reads the file and returns the {@link JsonObject} representation of it.
	 *
	 * @return the {@link JsonObject} of the file
	 *
	 * @throws IllegalStateException if the file could not be read or is empty
	 */
	protected JsonObject readFile() {
		final JsonElement element = new JsonParser().parse(read());

		if (element.isJsonNull()) {
			throw new IllegalStateException("Unable to load config.");
		}

		return element.getAsJsonObject();
	}

	/**
	 * Returns the path without a directory separator at the beginning. Useful if getting as a resource.
	 *
	 * @return the path substringed 1
	 */
	public String getResourcePath() {
		return this.path.substring(1);
	}

	@Override
	public String getFullPath() {
		return this.file.getPath();
	}

	@Override
	public void write(final String string, final boolean append) {
		DiskUtil.write(this.file, string, this.charset, append);
	}

	@Override
	public String read() {
		return DiskUtil.read(this.file, this.charset);
	}
}

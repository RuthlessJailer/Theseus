package com.ruthlessjailer.api.theseus.delete.io;

import com.google.gson.*;
import com.ruthlessjailer.api.theseus.Common;
import com.ruthlessjailer.api.theseus.delete.PluginBase;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author RuthlessJailer
 */
@Getter
public abstract class JSONFile extends TextFile {

	protected final Gson GSON;

	public JSONFile(final String path) {
		this(new GsonBuilder().setPrettyPrinting().create(), path);
	}

	@SneakyThrows
	public JSONFile(final Gson gson, final String path) {
		super(path);

		this.GSON = gson;
	}

	/**
	 * Checks the config file to make sure that it has all the values that the class contains. Only {@code public final} fields will be checked.
	 * Case will be ignored.<p>
	 * If a value is missing or null the default value will be written to the file.<p>
	 * This method does not read the file or save it. Do so if it has been modified prior to this method call. Non-blocking.
	 *
	 * @param file the {@link JSONFile} config instance to modify.
	 */
	@SneakyThrows
	protected static IFile fixConfig(@NonNull final JSONFile file) {
		final String contents = Common.getString(file.getContents());

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
			return file;
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
		final JsonObject object = new JsonParser().parse(out.toString()).getAsJsonObject();
		for (final Map.Entry<String, JsonElement> entry : object.entrySet()) {
			for (final Field field : fields) {
				if (entry.getKey().equalsIgnoreCase(field.getName())) {//yay we found one that needs fixing
					content.put(entry.getKey(), entry.getValue());//add it to or replace it in the map so we can write it back to the config file
					break;
				}
			}
		}

		return file.setContents(file.getGSON().toJson(content));//fill the config with all the repaired values
	}

	/**
	 * Reads the file and returns the {@link JsonElement} representation of it.<p>
	 * This method does not read the file or save it. Do so if it has been modified prior to this method call. Non-blocking.
	 *
	 * @return the {@link JsonElement} representation of the file
	 */
	protected JsonElement readFile() {
		return new JsonParser().parse(getContents());
	}
}

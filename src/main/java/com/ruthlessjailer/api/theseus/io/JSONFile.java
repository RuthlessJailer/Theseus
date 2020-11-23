package com.ruthlessjailer.api.theseus.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ruthlessjailer.api.theseus.Chat;
import com.ruthlessjailer.api.theseus.PluginBase;
import com.ruthlessjailer.api.theseus.ReflectUtil;
import com.ruthlessjailer.api.theseus.typeadapter.TypeAdapterRegistry;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
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

	protected final Gson    GSON;
	protected final String  path;
	protected final File    file;
	@Setter
	private         Charset charset = StandardCharsets.UTF_8;

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

			final URL resource = getClass().getClassLoader().getResource(this.path.substring(1));

			if (resource != null) {//copy over default file from src/main/resources
				Files.copy(resource.openStream(), this.file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

	/**
	 * Sets all constants to what is currently stored at their lowercase value in the file. Constants should be in UPPER_SNAKE_CASE, and file value names must
	 * correspond to their constant fields' names except in lower_snake_case. The reason for this is because when fetching the values field names will be
	 * lowercased.</p>
	 * Remember constant expressions:
	 * <pre>
	 *     {@code
	 *     public static final Integer SOME_INT = null;//IMPOSSIBLE to set
	 *
	 *     ...
	 *
	 *     public static final Integer SOME_INT;//CAN be set later so no JSON parsing is needed
	 *
	 *     static{
	 * 			SOME_INT = null;//to get rid of errors
	 *     }
	 *
	 *     }
	 * </pre>
	 * Call this method from your constructor to auto-fill all these constant values.
	 *
	 * @param file the {@link JSONFile} config instance to modify.
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> void setConstants(@NonNull final JSONFile file) {
		final List<Field> fields = Arrays.stream(file.getClass().getFields()).filter(field ->
																							 Modifier.isPublic(field.getModifiers()) &&
																							 Modifier.isStatic(field.getModifiers()) &&
																							 Modifier.isFinal(field.getModifiers()))
										 .collect(Collectors.toList());

		final Map<String, String> map     = new HashMap<>();
		final JsonElement         element = new JsonParser().parse(file.read());

		for (final Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
			map.put(entry.getKey(), entry.getValue().getAsString());
		}

		for (final Field field : fields) {
			final Object converted;
			if (field.getType().isEnum()) {
				converted = ReflectUtil.getEnum((Class<E>) field.getType(), map.get(field.getName().toLowerCase()));
			} else {
				converted = TypeAdapterRegistry.get(field.getType()).convert(map.get(field.getName().toLowerCase()));
			}
			Chat.debug("JSON Config", "Setting field " + field.getName() + " to " + converted + " which is type " + converted.getClass());

			ReflectUtil.setField(Field.class, "modifiers", field, field.getModifiers() & ~Modifier.FINAL);//make it non-final
			ReflectUtil.setField(field, null, converted);//set it
			ReflectUtil.setField(Field.class, "modifiers", field, field.getModifiers() | Modifier.FINAL);//make it final again
		}

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

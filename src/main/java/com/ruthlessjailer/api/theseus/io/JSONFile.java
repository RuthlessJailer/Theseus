package com.ruthlessjailer.api.theseus.io;

import com.google.gson.*;
import com.ruthlessjailer.api.theseus.PluginBase;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
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

//			try {
			this.file.createNewFile();
//			} catch (final IOException e) {
//				e.printStackTrace();
//			}

			final URL resource = getClass().getClassLoader().getResource(getResourcePath());

			if (resource != null) {//copy over default file from src/main/resources
//				try {
//					final InputStream in = resource.openStream();
				Files.copy(resource.openStream(), this.file.toPath(), StandardCopyOption.REPLACE_EXISTING);//why this doesn't work is beyond me
//					final FileOutputStream out = new FileOutputStream(this.file);
//					final byte[]           buf = new byte[8192];
//					int                    n;
//					while ((n = in.read(buf)) > 0) {
//						out.write(buf, 0, n);
//					}
//					out.close();
//					in.close();
//				} catch (final IOException e) {
//					e.printStackTrace();
//				}
			}
		}
	}

	/**
	 * Checks the config file to make sure that it has all the values that the class contains. Only {@code public static final} fields will be checked.
	 * Case will be ignored.<p>
	 * If a value is missing, the default value will be written to the file.
	 *
	 * @param file the {@link JSONFile} config instance to modify.
	 */
	@SneakyThrows
	public static void checkConfig(@NonNull final JSONFile file) {
		final List<Field> fields = Arrays.stream(file.getClass().getFields()).filter(field ->
																							 Modifier.isPublic(field.getModifiers()) &&
																							 Modifier.isStatic(field.getModifiers()) &&
																							 Modifier.isFinal(field.getModifiers()))
										 .collect(Collectors.toList());

		final JsonElement element = new JsonParser().parse(file.read());

		for (final Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
			Field match = null;
			for (final Field field : fields) {
				if (field.getName().equalsIgnoreCase(entry.getKey())) {//it's a match
					match = field;
					break;
				}
			}
			fields.remove(match);
		}

		if (fields.isEmpty()) {//all values were present
			return;
		}

		//some values are missing
		final URL resource = file.getClass().getClassLoader().getResource(file.getResourcePath());

		if (resource == null) {//it's not a resource; we can't do anything
			return;
		}

		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(resource.openStream(), out);

		final JsonObject object = new JsonParser().parse(new String(out.toByteArray())).getAsJsonObject();


		/*for (final Field field : fields) {
			System.out.println("FIELD VALUE: " + ReflectUtil.getFieldValue(field, null));
			final Object converted;
			if (field.getType().isEnum()) {
				converted = ReflectUtil.getEnum((Class<E>) field.getType(), map.get(field.getName().toLowerCase()));
			} else {
				converted = TypeAdapterRegistry.get(field.getType()).convert(map.get(field.getName().toLowerCase()));
			}
			Chat.debug("JSON Config", "Setting field " + field.getName() + " to " + converted + " which is type " + converted.getClass());

			ReflectUtil.setField(Field.class, "modifiers", field, field.getModifiers() & ~Modifier.FINAL);//make it non-final
			System.out.println("IS FIELD FINAL? " + Modifier.isFinal(field.getModifiers()));

			ReflectUtil.setField(field, null, converted);//set it
			System.out.println("FIELD VALUE: " + ReflectUtil.getFieldValue(field, null));

			ReflectUtil.setField(Field.class, "modifiers", field, field.getModifiers() | Modifier.FINAL);//make it final again
			System.out.println("IS FIELD FINAL? " + Modifier.isFinal(field.getModifiers()));
		}*///removed due to lack of implementation possibilities

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

package com.ruthlessjailer.api.theseus.io;

import com.ruthlessjailer.api.theseus.Checks;
import com.ruthlessjailer.api.theseus.Common;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author RuthlessJailer
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DiskUtil {

	/**
	 * Attempts to fetch the given file.
	 *
	 * @param path the path to the file; slashes {@literal /} will be replaced with {@link File#separator}
	 *
	 * @return the found file or {@code null}
	 */
	@SneakyThrows
	public static File parseFile(@NonNull final String path) {
		return new File(path.replaceAll("/", Common.escape(File.separator)));
	}

	/**
	 * Attempts to fetch or create the given file.
	 *
	 * @param path the path to the file; slashes {@literal /} will be replaced with {@link File#separator}
	 *
	 * @return the found or created file
	 *
	 * @throws IOException if the file or its parent files were unable to be created
	 */
	@SneakyThrows
	public static File getFile(@NonNull final String path) {
		final File file = new File(path.replaceAll("/", Common.escape(File.separator)));

		createFile(file);

		return file;
	}

	/**
	 * Attempts to create a file and its parent directories if it doesn't exist.
	 *
	 * @param file the file
	 *
	 * @throws IOException if the file or its parent files were unable to be created
	 */
	@SneakyThrows
	public static void createFile(@NonNull final File file) {
		if (!file.exists()) {
			Checks.verify(file.mkdirs() && file.createNewFile(), "Failed to create file.", IOException.class);
		}
	}

	/**
	 * Writes to the given file.
	 *
	 * @param file    the {@link File} to write to
	 * @param content what to write to the file
	 */
	public static void write(@NonNull final File file, @NonNull final String content) {
		write(file, content, false);
	}

	/**
	 * Writes to the given file.
	 *
	 * @param file    the {@link File} to write to
	 * @param content what to write to the file
	 * @param append  whether or not to append to the end of the file or overwrite
	 */
	public static void write(@NonNull final File file, @NonNull final String content, final boolean append) {
		write(file, content, StandardCharsets.UTF_8, append);
	}

	/**
	 * Writes to the given file.
	 *
	 * @param file    the {@link File} to write to
	 * @param content what to write to the file
	 * @param charset the {@link Charset} to encode the string with
	 * @param append  whether or not to append to the end of the file or overwrite
	 */
	public static void write(@NonNull final File file, @NonNull final String content, @NonNull final Charset charset, final boolean append) {
		write(file, content.getBytes(charset), append);
	}

	/**
	 * Writes to the given file.
	 *
	 * @param file    the {@link File} to write to
	 * @param content what to write to the file
	 * @param append  whether or not to append to the end of the file or overwrite
	 */
	@SneakyThrows
	public static void write(@NonNull File file, @NonNull final byte[] content, final boolean append) {
//		Files.write(Paths.get(file.toURI()), content, append ? StandardOpenOption.APPEND : StandardOpenOption.WRITE);
// WRITE will just write to front; won't erase whole file, unlike second option

		file = getFile(file.getPath());

		final FileOutputStream out = new FileOutputStream(file, append);//using this for append option
		out.write(content);
		out.close();
	}

	/**
	 * Reads in a file as a string.
	 *
	 * @param file the {@link File} to read
	 *
	 * @return The string representation of the contents or {@code null} if the file does not exist.
	 */
	public static String read(@NonNull final File file) {
		return read(file, StandardCharsets.UTF_8);
	}

	/**
	 * Reads in a file as a string.
	 *
	 * @param file    the {@link File} to read
	 * @param charset the {@link Charset} to decode the string with
	 *
	 * @return The string representation of the contents or {@code null} if the file does not exist.
	 */
	public static String read(@NonNull final File file, @NonNull final Charset charset) {
		final byte[] bytes = readBytes(file);
		if (bytes == null) { return null; }
		return new String(bytes, charset);
	}

	/**
	 * Reads in a file as a byte array.
	 *
	 * @param file the {@link File} to read
	 *
	 * @return The byte array of the contents or {@code null} if the file does not exist.
	 */
	@SneakyThrows
	public static byte[] readBytes(@NonNull final File file) {
		if (!file.exists()) { return new byte[0]; }
		if (file.length() == 0) { return new byte[0]; }

		return Files.readAllBytes(file.toPath());
	}

}

package com.ruthlessjailer.api.theseus.io;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author RuthlessJailer
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DiskUtil {

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
	public static void write(@NonNull final File file, @NonNull final byte[] content, final boolean append) {
//		Files.write(Paths.get(file.toURI()), content, append ? StandardOpenOption.APPEND : StandardOpenOption.WRITE);
// WRITE will just write to front; won't erase whole file, unlike second option

		if (!file.exists()) {
			if (file.getParentFile() == null) {
				file.mkdirs();
			}
			file.createNewFile();
		}

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
		if (!file.exists()) { return null; }
		if (file.length() == 0) { return null; }

		return Files.readAllBytes(file.toPath());
	}


}

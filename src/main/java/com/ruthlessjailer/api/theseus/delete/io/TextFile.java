package com.ruthlessjailer.api.theseus.delete.io;

import com.ruthlessjailer.api.theseus.delete.PluginBase;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author RuthlessJailer
 */
@Getter
public abstract class TextFile implements IFile {

	private final Lock lock = new ReentrantLock();

	protected final String path;
	protected final File   file;

	private volatile String contents = read();

	@Setter
	private Charset charset = StandardCharsets.UTF_8;

	@SneakyThrows
	public TextFile(@NonNull final String path) {
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
	public IFile write(final String string, final boolean append) {
		DiskUtil.write(this.file, string, this.charset, append);
		return this;
	}

	@Override
	public String read() {
		return DiskUtil.read(this.file, this.charset);
	}

	@Override
	public IFile setContents(final String string, final boolean append) {
		synchronized (this.lock) {
			this.contents = append ? this.contents + string : string;
			return this;
		}
	}

	@Override
	public IFile save() {
		return write(this.contents);
	}

	@Override
	public IFile load() {
		return setContents(read());
	}
}

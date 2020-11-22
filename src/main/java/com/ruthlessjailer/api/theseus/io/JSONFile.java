package com.ruthlessjailer.api.theseus.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ruthlessjailer.api.theseus.PluginBase;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author RuthlessJailer
 */
@Getter
public abstract class JSONFile implements IFile {

	private final Gson    GSON;
	private final String  path;
	private final File    file;
	@Setter
	private       Charset charset = StandardCharsets.UTF_8;

	public JSONFile(final String path) {
		this(new GsonBuilder().setPrettyPrinting().create(), path);
	}

	@SneakyThrows
	public JSONFile(final Gson gson, final String path) {
		this.GSON = gson;
		this.path = path;
		this.file = new File(PluginBase.getFolder().getPath() + File.separator + path);

		if (!this.file.exists()) {
			if (this.file.getParentFile() == null) {
				this.file.mkdirs();
			}
			this.file.createNewFile();
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

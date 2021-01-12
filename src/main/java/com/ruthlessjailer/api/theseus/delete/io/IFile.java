package com.ruthlessjailer.api.theseus.delete.io;

import java.io.File;

/**
 * @author RuthlessJailer
 */
public interface IFile {

	/**
	 * Returns the {@link File} object representing the file.
	 *
	 * @return the {@link File} object
	 */
	File getFile();

	/**
	 * Returns the local path (from the plugin's base folder) to the file.
	 *
	 * @return the local path
	 */
	String getPath();

	/**
	 * Returns the full path to the file.
	 *
	 * @return the full path
	 */
	String getFullPath();

	/**
	 * Get the cached contents of the file. Non-blocking.
	 *
	 * @return the cache contents of the file
	 */
	String getContents();

	/**
	 * Set the cached contents of the file. Non-blocking.
	 *
	 * @param string the new content
	 *
	 * @return the {@link IFile instance}
	 */
	default IFile setContents(final String string) {
		return setContents(string, false);
	}

	/**
	 * Set the cached contents of the file. Non-blocking.
	 *
	 * @param string the new content
	 * @param append {@code true} to replace existing contents, {@code false} to append to the end
	 *
	 * @return the {@link IFile instance}
	 */
	IFile setContents(final String string, final boolean append);

	/**
	 * Writes the cached contents to the file.
	 *
	 * @return the {@link IFile instance}
	 */
	IFile save();

	/**
	 * Caches the contents of the file.
	 *
	 * @return the {@link IFile instance}
	 */
	IFile load();

	/**
	 * Appends the given string to the end of the file. If the string is null nothing will be appended.
	 *
	 * @param string the string to append
	 *
	 * @return the {@link IFile instance}
	 */
	default IFile write(final String string) {
		return write(string, false);
	}

	/**
	 * Writes a given string to the file, optionally appending. If the string is null nothing will be appended or the file will be cleared.
	 *
	 * @param string the string to write
	 * @param append whether to append to or overwrite the file's contents
	 *
	 * @return the {@link IFile instance}
	 */
	IFile write(final String string, final boolean append);

	/**
	 * Reads in the current contents of the file.
	 *
	 * @return the string representation of the file
	 */
	String read();

}

package com.ruthlessjailer.api.theseus.io;

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
	 * Appends the given string to the end of the file. If the string is null nothing will be appended.
	 *
	 * @param string the string to append
	 */
	default void write(final String string) {
		write(string, false);
	}

	/**
	 * Writes a given string to the file, optionally appending. If the string is null nothing will be appended or the file will be cleared.
	 *
	 * @param string the string to write
	 * @param append whether to append to or overwrite the file's contents
	 */
	void write(final String string, final boolean append);

	/**
	 * Reads in the current contents of the file.
	 *
	 * @return the string representation of the file
	 */
	String read();

}

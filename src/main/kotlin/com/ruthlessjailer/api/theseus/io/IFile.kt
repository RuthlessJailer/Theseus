package com.ruthlessjailer.api.theseus.io

import java.io.File

/**
 * @author RuthlessJailer
 */
interface IFile {
	/**
	 * Returns the [File] object representing the file.
	 *
	 * @return the [File] object
	 */
	val file: File?

	/**
	 * Returns the path to the file.
	 *
	 * @return the path
	 */
	val path: String

	/**
	 * Get the cached contents of the file. Non-blocking.
	 *
	 * @return the cache contents of the file
	 */
	var contents: String

	/**
	 * Set the cached contents of the file. Non-blocking.
	 *
	 * @param string the new content
	 *
	 * @return the [instance][IFile]
	 */
	fun setContents(string: String?): IFile = setContents(string, false)

	/**
	 * Set the cached contents of the file. Non-blocking.
	 *
	 * @param string the new content
	 * @param append `true` to replace existing contents, `false` to append to the end
	 *
	 * @return the [instance][IFile]
	 */
	fun setContents(string: String?, append: Boolean): IFile

	/**
	 * Writes the cached contents to the file.
	 *
	 * @return the [instance][IFile]
	 */
	fun save(): IFile

	/**
	 * Caches the contents of the file.
	 *
	 * @return the [instance][IFile]
	 */
	fun load(): IFile

	/**
	 * Appends the given string to the end of the file. If the string is null nothing will be appended.
	 *
	 * @param string the string to append
	 *
	 * @return the [instance][IFile]
	 */
	fun write(string: String?): IFile = write(string, false)

	/**
	 * Writes a given string to the file, optionally appending. If the string is null nothing will be appended or the file will be cleared.
	 *
	 * @param string the string to write
	 * @param append whether to append to or overwrite the file's contents
	 *
	 * @return the [instance][IFile]
	 */
	fun write(string: String?, append: Boolean): IFile

	/**
	 * Reads in the current contents of the file.
	 *
	 * @return the string representation of the file
	 */
	fun read(): String
}

package com.ruthlessjailer.api.theseus.io

import com.google.gson.*
import org.apache.commons.io.IOUtils
import java.io.ByteArrayOutputStream
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.collections.HashMap

/**
 * @author RuthlessJailer
 */
abstract class JSONFile(path: String, val GSON: Gson = GsonBuilder().setPrettyPrinting().create(), content: String = "") : TextFile(path, content) {

	companion object {

		@JvmStatic
		private fun getConfigFields(clazz: Class<out JSONFile>): Stream<Field> = Arrays.stream(clazz.declaredFields).filter { field ->
			Modifier.isPrivate(field.modifiers) &&//val SOME_STRING: String -> private final String SOME_STRING (with getter)
			Modifier.isFinal(field.modifiers) &&//basically, this only works with kotlin
			!Modifier.isStatic(field.modifiers)//as it would be extremely inconvenient in java
		}

		@JvmStatic
		private val GSON: Gson = GsonBuilder().setPrettyPrinting().create()

		@JvmStatic
		fun emptyJSON(clazz: Class<out JSONFile>): JsonElement {
			val content: MutableMap<String, String> = HashMap()

			getConfigFields(clazz).forEach {
				content[it.name.toLowerCase()] = if (it.type.isAssignableFrom(List::class.java) || it.type.isArray) {
					"[]"
				} else {
					""
				}
			}

			return JsonParser().parse(GSON.toJson(content))
		}
	}

	/**
	 * Checks the config file to make sure that it has all the values that the class contains. Only `public final` fields will be checked.
	 * Case will be ignored.
	 *
	 *
	 * If a value is missing or null the default value will be written to the file.
	 *
	 *
	 * This method does not read the file or save it. Do so if it has been modified prior to this method call. Non-blocking.
	 *
	 */
	fun fixConfig(): JSONFile {
		val fields = getConfigFields(javaClass).collect(Collectors.toList())

		val element: JsonElement = try {
			readFile()
		} catch (ignored: JsonParseException) {//malformed json
			JsonNull.INSTANCE
		}

		val content: MutableMap<String, JsonElement> = HashMap()
		if (!element.isJsonNull) {
			for ((key, value) in element.asJsonObject.entrySet()) {
				content[key] = value //fill the content map
				if (value.isJsonNull) { //it's null; this one needs to be fixed
					continue
				}
				var match: Field? = null
				for (field in fields) {
					if (field.name.equals(key, true)) { //this one's fine
						match = field
						break
					}
				}
				fields.remove(match)
			}
		}

		if (fields.isEmpty()) { //all values were present
			return this
		}

		//some values are missing
		val `in` = getResourceAsStream(removeStartingSeparatorChar(path))
				   ?: //it's not a resource; we can't do anything
				   throw UnsupportedOperationException("No resource found for file ${file.path}.")
		val out = ByteArrayOutputStream()
		IOUtils.copy(`in`, out) //get it to a byte array
		out.close()

		//loop through the default resource and replace all values in the file that are null or missing
		val `object` = JsonParser().parse(out.toString(charset.displayName())).asJsonObject
		for ((key, value) in `object`.entrySet()) {
			for (field in fields) {
				if (key.equals(field.name, true)) { //yay we found one that needs fixing
					content[key] = value //add it to or replace it in the map so we can write it back to the config file
					break
				}
			}
		}

		return setContents(GSON.toJson(content)) as JSONFile //fill the config with all the repaired values
	}

	/**
	 * Reads the file and returns the [JsonElement] representation of it.
	 *
	 *
	 * This method does not read the file or save it. Do so if it has been modified prior to this method call. Non-blocking.
	 *
	 * @return the [JsonElement] representation of the file
	 */
	fun readFile(): JsonElement = JsonParser().parse(contents)

	fun reload(): JSONFile {
		load()
		fixConfig()
		save()
		return getNewInstance(contents)
	}

	/**
	 * Creates a new instance of the class.
	 *
	 * @return a new instance of the child class
	 */
	abstract fun getNewInstance(content: String): JSONFile


}
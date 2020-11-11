package com.ruthlessjailer.api.theseus;

import lombok.NonNull;

import java.util.*;

/**
 * @author RuthlessJailer
 */
public final class Toggle<T> {

	private static final Map<String, Toggle<?>> instances = new HashMap<>();

	private final Set<T> set;

	public Toggle(@NonNull final String id) {
		this.set = new HashSet<>();
		if (Toggle.instances.containsKey(id)) {
			throw new IllegalArgumentException("ID " + id + " already in use.");
		}
		this.putInstance(id);
	}

	public static Toggle<?> getInstance(final String id) { return Toggle.instances.get(id); }

	private void putInstance(final String id) {
		Toggle.instances.put(id, this);
	}

	public Set<T> getSet() {
		return Collections.unmodifiableSet(this.set);
	}

	public boolean toggle(@NonNull final T object) {

		if (this.set.remove(object)) {
			return false;
		}

		this.set.add(object);

		return true;
	}

	public boolean getState(@NonNull final T object) {
		return this.set.contains(object);
	}

}

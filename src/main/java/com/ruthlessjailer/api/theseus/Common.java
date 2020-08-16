package com.ruthlessjailer.api.theseus;

import java.util.Arrays;

public final class Common {

	public static String getString(final String string) { return string == null ? "" : string; }

	public static <T> T[] copyToEnd(final T[] objects, final int start){
		return Arrays.copyOfRange(objects,start,objects.length-1);
	}

	public static <T> T[] copyFromStart(final T[] objects, final int end){
		return Arrays.copyOfRange(objects, 0,end);
	}

}

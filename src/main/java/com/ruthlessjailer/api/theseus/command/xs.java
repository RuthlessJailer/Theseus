package com.ruthlessjailer.api.theseus.command;

public class xs {

	public static void main(final String... args) {

		final String arg = "%e";

		if (arg.toLowerCase().matches("%[sideb]")) {
			switch (arg.toLowerCase()) {
				case "%e":
					System.out.println("%e");
					break;
				case "%s":
					System.out.println("NO");
					break;
			}
		}


	}


}

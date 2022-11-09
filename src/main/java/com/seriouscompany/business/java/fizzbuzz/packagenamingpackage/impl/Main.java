package com.seriouscompany.business.java.fizzbuzz.packagenamingpackage.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.seriouscompany.business.java.fizzbuzz.packagenamingpackage.impl.parameters.DefaultFizzBuzzUpperLimitParameter;
import com.seriouscompany.business.java.fizzbuzz.packagenamingpackage.interfaces.FizzBuzz;
import com.seriouscompany.business.java.fizzbuzz.packagenamingpackage.interfaces.parameters.FizzBuzzUpperLimitParameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;

/**
 * Main
 */
public final class Main {

	static {
		try {
			URL main = Main.class.getResource("Main.class");
			String packagePath = main.getPath()
					.replace("impl/Main.class", "")
					.replace("/C:", "C:")
					.replace("target/classes", "src/main/java");
			String libName = packagePath + "lib/win/NativeIntegerDivider.dll";

			String property = System.getProperty("java.library.path") + ";" + libName;
			System.setProperty("java.library.path", property);

			try {
				loadNativeLibraryInJavaVersionBeforeSixteen(libName);
			} catch (Exception e) {
				System.load(libName);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void loadNativeLibraryInJavaVersionBeforeSixteen(String libName) throws Exception {
		final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
		usrPathsField.setAccessible(true);

		//get array of paths
		final String[] paths = (String[])usrPathsField.get(null);

		//check if the path to add is already present
		for(String path : paths) {
			if(path.equals(libName)) {
				return;
			}
		}

		//add the new path
		final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
		newPaths[newPaths.length-1] = libName;
		usrPathsField.set(null, newPaths);
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final ApplicationContext context = new ClassPathXmlApplicationContext(Constants.SPRING_XML);
		final FizzBuzz myFizzBuzz = (FizzBuzz) context.getBean(Constants.STANDARD_FIZZ_BUZZ);
		final FizzBuzzUpperLimitParameter fizzBuzzUpperLimit = new DefaultFizzBuzzUpperLimitParameter();
		myFizzBuzz.fizzBuzz(fizzBuzzUpperLimit.obtainUpperLimitValue());

		((ConfigurableApplicationContext) context).close();

	}

}

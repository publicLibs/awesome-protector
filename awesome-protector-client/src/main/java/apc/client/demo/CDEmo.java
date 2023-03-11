/**
 *
 */
package apc.client.demo;

import java.io.File;

/**
 * @author freedom1b2830
 * @date 2023-февраля-20 18:07:59
 */
public class CDEmo {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		for (final String string : System.getProperty("java.class.path").split(File.pathSeparator)) {
			System.err.println(string);
		}

	}

}

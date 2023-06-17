/**
 *
 */
package com.github.publiclibs.awesome.protector.api.securitymanager;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * @author freedom1b2830
 * @date 2023-февраля-21 23:11:28
 */
public class Demo {
	static {
		ProtectorSecurityManager.enableMe(ProtectorSecurityManager.ProtectorSecurityManagerMSGKEY + " is enabled");
	}

	public static void main(final String[] args) throws MalformedURLException, IOException, ClassNotFoundException {
		// final URLClassLoader aa = new URLClassLoader(new URL[] { new
		// URL("http://aaaa") });
		// final URLClassLoader aa2 = URLClassLoader.newInstance(new URL[] { new
		// URL("http://aaaa2") }, aa);
		// aa.loadClass("test");
		// final Socket socket = new Socket();
		// socket.connect();
		// final InetSocketAddress aa = new InetSocketAddress("127.0.0.1", 10000);

		/*
		 * final InetAddress[] aae = Inet4Address.getAllByName("vk.com"); for (final
		 * InetAddress inetAddress : aae) { System.err.println(inetAddress); }
		 */
	}
}
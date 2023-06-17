package com.github.publiclibs.awesome.protector.api.securitymanager;

import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.security.SecurityPermission;
import java.util.PropertyPermission;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class MinimalPolicy extends Policy {

	private static MyPermissionCollection perms;
	private ProtectorSecurityManager protectorSecurityManager;

	public MinimalPolicy() {
		super();
		if (perms == null) {
			perms = new MyPermissionCollection();
			addPermissions();
		}
	}

	/**
	 * @param protectorSecurityManager
	 */
	public MinimalPolicy(final ProtectorSecurityManager protectorSecurityManagerIn) {
		this.protectorSecurityManager = protectorSecurityManagerIn;
	}

	private void addPermissions() {
		/*
		 * final SocketPermission socketPermission = new SocketPermission("*:1024-",
		 * "connect, resolve"); final PropertyPermission propertyPermission = new
		 * PropertyPermission("*", "read, write"); final FilePermission filePermission =
		 * new FilePermission("<<ALL FILES>>", "read");
		 *
		 * perms.add(socketPermission); perms.add(filePermission);
		 */
		final PropertyPermission propertyPermission = new PropertyPermission("java.protocol.handler.pkgs", "read");
		perms.add(propertyPermission);
	}

	@SuppressFBWarnings(value = "EI_EXPOSE_REP")
	public @Override PermissionCollection getPermissions(final CodeSource codesource) {
		return perms;
	}

	public @Override boolean implies(final ProtectionDomain domain, final Permission permission) {
		System.out.println(permission);

		final String permName = permission.getName();

		if (permission instanceof PropertyPermission) {
			final PropertyPermission propertyPermission = (java.util.PropertyPermission) permission;

			if ("read".equals(propertyPermission.getActions()) && "java.protocol.handler.pkgs".equals(permName)) {
				return true;
			}
		} else if (permission instanceof SecurityPermission) {
			if ("getPolicy".equals(permName)) {
				return true;
			}
		}
		if (loadNetwork(domain, permission)) {
			return true;
		}

		if (socketCanResolve(domain, permission)) {
			return true;
		}

		System.out.println(permission);
		// System.out.println(domain);
		/*
		 * AccessController.doPrivileged(new PrivilegedAction<Object>() {
		 *
		 * @Override public Object run() { return null; } },
		 * AccessController.getContext(), new FilePermission("/", "read"));
		 */

		return true;// fixme
	}

	/**
	 * @param domain
	 * @param permission
	 * @return
	 */
	private boolean loadNetwork(final ProtectionDomain domain, final Permission permission) {
		domain.getClass();// FIXME
		final String name = permission.getName();
		final String action = permission.getActions();
		if (permission instanceof RuntimePermission) {
			if ("loadLibrary.net".equals(name)) {
				return true;
			}
			if ("getProtectionDomain".equals(name)) {
				return true;
			}
		} else if (permission instanceof ReflectPermission) {
			if ("suppressAccessChecks".equals(name)) {
				// at java.net.PlainSocketImpl.initProto(Native Method)
				// at java.net.PlainSocketImpl.<clinit>(PlainSocketImpl.java:45)
				// at java.net.Socket.setImpl(Socket.java:521)
				// at java.net.Socket.<init>(Socket.java:86)
				return true;
			}

		} else if (permission instanceof FilePermission) {
			final FilePermission filePermission = (FilePermission) permission;
			final String filePath = name;
			if ("/usr/lib/jvm/java-8-openjdk/jre/lib/amd64/libnet.so".equals(filePath) && "read".equals(action)) {
				return true;
			}
			System.err.println(filePath + " -> " + filePermission.getActions());
		} else if (permission instanceof PropertyPermission) {
			if ("read".equals(action)) {

				if ("jdk.internal.lambda.dumpProxyClasses".equals(name)) {
					return true;
				}
				if ("http.agent".equals(name)) {
					return true;
				}
				if ("java.version".equals(name)) {
					return true;
				}
				if ("http.maxRedirects".equals(name)) {
					return true;
				}

				// at java.net.PlainSocketImpl.initProto(Native Method)
				// at java.net.PlainSocketImpl.<clinit>(PlainSocketImpl.java:45)
				// at java.net.Socket.setImpl(Socket.java:521)
				// at java.net.Socket.<init>(Socket.java:86)
				if ("java.net.preferIPv6Addresses".equals(name)) {
					return true;
				}
				if ("java.net.preferIPv4Stack".equals(name)) {
					return true;
				}
				if ("impl.prefix".equals(name)) {
					return true;
				}
				if ("sun.net.spi.nameservice.provider.1".equals(name)) {

					return true;
				}
				// other
			}
		}

		return false;
		// return true;
	}

	/**
	 * @param domain
	 * @param permission
	 * @return
	 */
	private boolean socketCanResolve(final ProtectionDomain domain, final Permission permission) {
		if (permission instanceof SocketPermission) {
			final SocketPermission socketPermission = (SocketPermission) permission;
//return socketCanResolve(domain, socketPermission)
			if (socketPermission.getActions().equals("resolve")) {
				domain.getClass();
				// return socketCanResolve(domain, socketPermission,
				// socketPermission.getName());
			}
		}

		return false;
	}

}
/**
 *
 */
package com.github.publiclibs.awesome.protector.api.securitymanager;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;
import java.security.Policy;

/**
 * @author freedom1b2830
 * @date 2023-февраля-21 23:09:35
 */
public class ProtectorSecurityManager extends SecurityManager {
	/**
	 *
	 */
	static final String ProtectorSecurityManagerMSGKEY = "#PSM";

	public static boolean enableMe() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new ProtectorSecurityManager());
			return true;
		}
		return false;
	}

	public static boolean enableMe(final String message) {
		final boolean status = enableMe();
		if (status) {
			System.err.println(
					message.replaceAll(ProtectorSecurityManagerMSGKEY, ProtectorSecurityManager.class.getSimpleName()));
		}
		return status;
	}

	private final MinimalPolicy policy;

	/**
	 *
	 */
	public ProtectorSecurityManager() {
		this.policy = new MinimalPolicy(this);
		Policy.setPolicy(policy);
	}

	@Override
	public void checkAccept(final String host, final int port) {
		// TODO Auto-generated method stub
		super.checkAccept(host, port);
	}

	@Override
	public void checkAccess(final Thread t) {
		// TODO Auto-generated method stub
		super.checkAccess(t);
	}

	@Override
	public void checkAccess(final ThreadGroup g) {
		// TODO Auto-generated method stub
		super.checkAccess(g);
	}

	public @Override void checkConnect(final String host, final int port) {
		super.checkConnect(host, port);
	}

	@Override
	public void checkConnect(final String host, final int port, final Object context) {
		// TODO Auto-generated method stub
		super.checkConnect(host, port, context);
	}

	@Override
	public void checkCreateClassLoader() {
		// TODO Auto-generated method stub
		super.checkCreateClassLoader();
	}

	@Override
	public void checkDelete(final String file) {
		// TODO Auto-generated method stub
		super.checkDelete(file);
	}

	@Override
	public void checkExec(final String cmd) {
		// TODO Auto-generated method stub
		super.checkExec(cmd);
	}

	@Override
	public void checkExit(final int status) {
		// TODO Auto-generated method stub
		super.checkExit(status);
	}

	@Override
	public void checkLink(final String lib) {
		// TODO Auto-generated method stub
		super.checkLink(lib);
	}

	@Override
	public void checkListen(final int port) {
		// TODO Auto-generated method stub
		super.checkListen(port);
	}

	@Override
	public void checkMulticast(final InetAddress maddr) {
		// TODO Auto-generated method stub
		super.checkMulticast(maddr);
	}

	@Override
	public void checkPackageAccess(final String pkg) {
		// TODO Auto-generated method stub
		super.checkPackageAccess(pkg);
	}

	@Override
	public void checkPackageDefinition(final String pkg) {
		// TODO Auto-generated method stub
		super.checkPackageDefinition(pkg);
	}

	@Override
	public void checkPermission(final Permission perm) {
		// TODO Auto-generated method stub
		super.checkPermission(perm);
	}

	@Override
	public void checkPermission(final Permission perm, final Object context) {
		// TODO Auto-generated method stub
		super.checkPermission(perm, context);
	}

	@Override
	public void checkPrintJobAccess() {
		// TODO Auto-generated method stub
		super.checkPrintJobAccess();
	}

	@Override
	public void checkPropertiesAccess() {
		// TODO Auto-generated method stub
		super.checkPropertiesAccess();
	}

	@Override
	public void checkRead(final FileDescriptor fd) {
		// TODO Auto-generated method stub
		super.checkRead(fd);
	}

	@Override
	public void checkRead(final String file) {
		// TODO Auto-generated method stub
		super.checkRead(file);
	}

	@Override
	public void checkRead(final String file, final Object context) {
		// TODO Auto-generated method stub
		super.checkRead(file, context);
	}

	@Override
	public void checkSecurityAccess(final String target) {
		// TODO Auto-generated method stub
		super.checkSecurityAccess(target);
	}

	@Override
	public void checkSetFactory() {
		// TODO Auto-generated method stub
		super.checkSetFactory();
	}

	@Override
	public void checkWrite(final FileDescriptor fd) {
		// TODO Auto-generated method stub
		super.checkWrite(fd);
	}

	@Override
	public void checkWrite(final String file) {
		// TODO Auto-generated method stub
		super.checkWrite(file);
	}

}

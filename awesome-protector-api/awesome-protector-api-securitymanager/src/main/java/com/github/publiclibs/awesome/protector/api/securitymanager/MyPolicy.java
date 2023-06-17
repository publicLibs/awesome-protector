package com.github.publiclibs.awesome.protector.api.securitymanager;

import java.io.FilePermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.util.PropertyPermission;

class MyPolicy extends Policy {
	public @Override PermissionCollection getPermissions(final CodeSource codesource) {
		final Permissions p = new Permissions();
		p.add(new PropertyPermission("java.class.path", "read"));
		p.add(new FilePermission("/home/.../classes/*", "read"));
		return p;
	}
}
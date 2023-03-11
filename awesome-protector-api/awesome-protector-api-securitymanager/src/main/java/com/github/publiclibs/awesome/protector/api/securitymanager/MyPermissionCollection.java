package com.github.publiclibs.awesome.protector.api.securitymanager;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

class MyPermissionCollection extends PermissionCollection {

	private static final long serialVersionUID = 614300921365729272L;

	ArrayList<Permission> perms = new ArrayList<>();

	public @Override void add(final Permission p) {
		perms.add(p);
	}

	public @Override Enumeration<Permission> elements() {
		return Collections.enumeration(perms);
	}

	public @Override boolean implies(final Permission p) {
		if (perms.stream().anyMatch(perm -> perm.implies(p))) {
			return true;
		}

		for (final Iterator<Permission> i = perms.iterator(); i.hasNext();) {
			if (i.next().implies(p)) {
				return true;
			}
		}
		return false;
	}

	public @Override boolean isReadOnly() {
		return false;
	}

}
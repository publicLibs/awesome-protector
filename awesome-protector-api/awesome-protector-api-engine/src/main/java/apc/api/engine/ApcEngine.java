/**
 *
 */
package apc.api.engine;

import java.util.TreeMap;
import java.util.concurrent.Callable;

public abstract class ApcEngine {
	protected TreeMap<Long, Callable<?>> initTasks = new TreeMap<>();

	protected abstract void init() throws InterruptedException;

	protected abstract void initKeyStore(char[] keystoreSecret) throws InterruptedException;

	protected final void run() {
		System.out.println(getClass().getSimpleName() + ".run()");
	}

}

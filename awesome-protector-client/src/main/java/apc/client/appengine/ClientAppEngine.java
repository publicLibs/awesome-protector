/**
 *
 */
package apc.client.appengine;

import apc.api.engine.ApcEngine;
import apc.client.gui.common.ProtectorGuiCommon;
import apc.client.gui.promt.password.JdialogPromtPassword;
import apc.client.resources.ResourceManager;
import lombok.Getter;

/**
 * @author freedom1b2830
 * @date 2023-февраля-20 18:07:59
 */
public class ClientAppEngine extends ApcEngine {

	public static void main(final String[] args) throws Exception {
		final ClientAppEngine clientAppEngine = new ClientAppEngine();

		clientAppEngine.init();
		clientAppEngine.run();
	}

	private @Getter ResourceManager resourceManager;
	private ProtectorGuiCommon commonWindow;

	protected @Override void init() throws InterruptedException {
		final JdialogPromtPassword keystorePromtPassword = new JdialogPromtPassword();
		keystorePromtPassword.setSize(300, 300);
		final char[] keystorePassword = keystorePromtPassword.getResult();
		if (keystorePassword.length == 0) {
			throw new IllegalArgumentException("password not promt");
		}
		resourceManager = new ResourceManager("protector-resources");
		commonWindow = new ProtectorGuiCommon(this);
		commonWindow.setSize(300, 300);
		commonWindow.setVisible(true);
		initKeyStore(keystorePassword);
		// decrypt data
	}

	protected @Override void initKeyStore(final char[] keystoreSecret) throws InterruptedException {
		System.out.println("ClientAppEngine.initKeyStore():" + keystoreSecret);
	}

}

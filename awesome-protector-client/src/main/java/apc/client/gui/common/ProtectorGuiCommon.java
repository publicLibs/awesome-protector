/**
 *
 */
package apc.client.gui.common;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.SwingConstants.LEFT;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import apc.client.appengine.ClientAppEngine;
import apc.client.gui.DirectMessagesPanel;
import apc.client.gui.common.settings.ProtectorGuiSettings;
import lombok.Getter;

/**
 * @author freedom1b2830
 * @date 2023-апреля-05 03:56:56
 */
public class ProtectorGuiCommon extends JFrame {
	final DirectMessagesPanel dmPanel = new DirectMessagesPanel();
	final ProtectorGuiSettings settings = new ProtectorGuiSettings();
	private final ClientAppEngine clientAppEngine;

	final @Getter JTabbedPane tabbedPane = new JTabbedPane(LEFT);
	CopyOnWriteArrayList<JPanel> serverList = new CopyOnWriteArrayList<>();

	public ProtectorGuiCommon(final ClientAppEngine clientAppEngineInput) {
		this.clientAppEngine = clientAppEngineInput;

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new ProtectorGuiCommonListener(this));

		final JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		getContentPane().add(tabbedPane, CENTER);

		tabbedPane.addTab("DM", null, dmPanel, null);

		final ImageIcon settingsIcon = this.clientAppEngine.getResourceManager().loadIconOrNull("gear.png", 15, 15);
		tabbedPane.addTab("settings", settingsIcon, settings, null);

		final JPanel server1 = new JPanel();
		tabbedPane.addTab("server1", null, server1, null);
		final JPanel server2 = new JPanel();
		tabbedPane.addTab("server2", null, server2, null);

	}

}

/**
 *
 */
package apc.client.gui;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.SwingConstants.LEFT;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * Окно со всеми личными сообщениями
 *
 * @author freedom1b2830
 * @date 2023-апреля-05 16:07:06
 */
public class DirectMessagesPanel extends JPanel {
	final JScrollPane scrollPane = new JScrollPane();
	final JTabbedPane contactsMessages = new JTabbedPane(LEFT);

	public DirectMessagesPanel() {
		setLayout(new BorderLayout(0, 0));

		add(scrollPane, CENTER);

		scrollPane.setViewportView(contactsMessages);
		// TODO собственный класс для контакта-окна личных сообщений

		final JPanel contact1 = new JPanel();
		contactsMessages.addTab("contact1", null, contact1, null);

		final JPanel contact2 = new JPanel();
		contactsMessages.addTab("contact2", null, contact2, null);

	}

}

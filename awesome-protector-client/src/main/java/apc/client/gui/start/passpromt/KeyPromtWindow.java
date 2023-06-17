/**
 *
 */
package apc.client.gui.start.passpromt;

import java.awt.BorderLayout;
import java.awt.Button;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

/**
 * @author freedom1b2830
 * @date 2023-апреля-05 04:38:28
 */
public class KeyPromtWindow extends JFrame {
	private final JPasswordField passwordField;

	public KeyPromtWindow() {

		final JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.CENTER);

		final JPanel btns = new JPanel();
		getContentPane().add(btns, BorderLayout.SOUTH);
		btns.setLayout(new BorderLayout(0, 0));

		final Button button = new Button("New button");
		btns.add(button, BorderLayout.EAST);

		passwordField = new JPasswordField();
		passwordField.setToolTipText("password");
		btns.add(passwordField, BorderLayout.CENTER);

	}
}

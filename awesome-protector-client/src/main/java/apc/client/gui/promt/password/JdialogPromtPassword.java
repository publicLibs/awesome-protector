/**
 *
 */
package apc.client.gui.promt.password;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import apc.client.gui.promt.JdialogPromt;

/**
 * @author freedom1b2830
 * @date 2023-апреля-05 05:14:58
 */
public class JdialogPromtPassword extends JdialogPromt<char[]> {
	public static final class Chars {
		private static final char[] charsSpec = "!\"#$%&'\\''()*+,-./:;<=>?@[\\]^_`{|}~".toCharArray();
		private static final char[] charsLow = "qwertyuiplkjhgfdsazxcvbnm".toCharArray();
		private static final char[] charsHig = "QWERTYUIOPLKJHGFDSAZXCVBNM".toCharArray();
		private static final char[] charsDig = "0987654321".toCharArray();

		private Chars() {
		}
	}

	private static final long serialVersionUID = 6306781563138729438L;

	private static boolean passwdContain(final JTextArea status2, final String charsName, final char[] storage,
			final char[] chars, int countToZero) {
		for (final char currentChar : storage) {
			for (final char specChar : chars) {
				if (currentChar == specChar) {
					countToZero--;
					if (countToZero == 0) {
						return true;
					}
				}
			}
		}
		status2.append(String.format("charsName:%s need:%s%n", charsName, Integer.valueOf(countToZero)));
		return false;
	}

	private final transient JTextArea status = new JTextArea("password status here", 1, 4);

	private final transient JPasswordField passwordField = new JPasswordField();
	private Integer specialCount = Integer.valueOf(3);
	private Integer specialL = Integer.valueOf(5);
	private Integer specialH = Integer.valueOf(4);
	private Integer specialD = Integer.valueOf(2);

	public JdialogPromtPassword() {
		super(new Object());
		passwordField.setHorizontalAlignment(SwingConstants.CENTER);
		passwordField.addKeyListener(new KeyListener() {
			public @Override void keyPressed(final KeyEvent e) {
				e.getClass();
				check();
			}

			public @Override void keyReleased(final KeyEvent e) {
				e.getClass();
				check();
			}

			public @Override void keyTyped(final KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER && check()) {
					setResult(getPassword());
				}
			}
		});
		getContentPane().add(passwordField, BorderLayout.CENTER);
		getContentPane().add(status, BorderLayout.SOUTH);
		pack();
	}

	public JdialogPromtPassword(final Integer specialCountIpnut, final Integer specialLIpnut,
			final Integer specialHIpnut, final Integer specialDIpnut) {
		this();
		specialCount = specialCountIpnut;
		specialL = specialLIpnut;
		specialH = specialHIpnut;
		specialD = specialDIpnut;
		check();
	}

	protected boolean check() {
		status.setText("");
		boolean checkResult = false;

		final char[] passwd = getPassword();
		final int ost = getNeedLen().intValue() - passwd.length;
		final boolean checkResultSpec = passwdContain(status, "spec", passwd, Chars.charsSpec, specialCount.intValue());
		final boolean checkResultLow = passwdContain(status, "low", passwd, Chars.charsLow, specialL.intValue());
		final boolean checkResultHig = passwdContain(status, "high", passwd, Chars.charsHig, specialH.intValue());
		final boolean checkResultDig = passwdContain(status, "dig", passwd, Chars.charsDig, specialD.intValue());

		checkResult = checkResultSpec && checkResultLow && checkResultHig && checkResultDig;
		if (ost > 0) {
			status.append(String.format("need chars: %s%n", Integer.valueOf(ost)));
			checkResult = false;
		}
		if (checkResult) {
			status.setText("OK");
		}
		return checkResult;
	}

	@Override
	public char[] createEmptyResult() {
		return new char[0];
	}

	public Integer getNeedLen() {
		return Integer
				.valueOf(specialCount.intValue() + specialL.intValue() + specialH.intValue() + specialD.intValue());
	}

	private final char[] getPassword() {
		return passwordField.getPassword();
	}
}

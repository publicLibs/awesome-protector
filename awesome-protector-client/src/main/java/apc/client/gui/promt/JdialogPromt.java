/**
 *
 */
package apc.client.gui.promt;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Objects;

import javax.swing.JDialog;

/**
 * @author freedom1b2830
 * @param <T> result Type
 * @date 2023-апреля-05 05:09:42
 */
public abstract class JdialogPromt<T> extends JDialog {
	private static final long serialVersionUID = 1390267560683105917L;
	private final transient Object resultLock = new Object();
	private transient T result;
	private final transient Object finalObject;

	protected JdialogPromt(final Object input) {
		Objects.requireNonNull(input);
		setDefault();
		finalObject = input;
		finalObject.getClass();
	}

	public abstract T createEmptyResult();

	public final T getResult() throws InterruptedException {
		synchronized (resultLock) {
			setVisible(true);
			while (result == null) {
				resultLock.wait(300);
			}
		}
		dispose();
		return result;
	}

	/*
	 * public T name() throws InstantiationException, IllegalAccessException { final
	 * java.lang.reflect.Type genericSuperclass = getClass().getGenericSuperclass();
	 * final ParameterizedType parameterizedType = (ParameterizedType)
	 * genericSuperclass; final java.lang.reflect.Type actualTypeArguments =
	 * parameterizedType.getActualTypeArguments()[0];
	 * System.err.println(actualTypeArguments); System.err.println(Character.TYPE);
	 *
	 * final T instance2 = (T) Array.newInstance(Character.TYPE, Integer.MAX_VALUE);
	 *
	 *
	 * final Class<T> instanceClass = (Class<T>) actualTypeArguments; final T
	 * instance = instanceClass.newInstance(); instance.getClass();
	 *
	 * return instance; }
	 */

	protected final void setDefault() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowListener() {

			private void ignore(final WindowEvent e) {// ignore
				e.getClass();
			}

			public @Override void windowActivated(final WindowEvent e) {
				ignore(e);
			}

			public @Override void windowClosed(final WindowEvent e) {
				if (result == null) {
					setResult(createEmptyResult());
				}
				synchronized (resultLock) {
					resultLock.notifyAll();
				}
				ignore(e);
			}

			public @Override void windowClosing(final WindowEvent e) {
				ignore(e);
			}

			public @Override void windowDeactivated(final WindowEvent e) {
				ignore(e);
			}

			public @Override void windowDeiconified(final WindowEvent e) {
				ignore(e);
			}

			public @Override void windowIconified(final WindowEvent e) {
				ignore(e);
			}

			public @Override void windowOpened(final WindowEvent e) {
				ignore(e);
			}
		});
	}

	protected final void setResult(final T input) {
		result = input;
		synchronized (resultLock) {
			dispose();
			resultLock.notifyAll();
		}
	}
}

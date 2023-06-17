/**
 *
 */
package apc.client.resources;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * @author freedom1b2830
 * @date 2023-апреля-05 18:22:04
 */
public class ResourceManager {

	public static BufferedImage scaleBufferedImage(final BufferedImage img, final int width, final int height) {
		final Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		final BufferedImage dimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		final Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public static ImageIcon scaleIcon(final ImageIcon icon, final int width, final int height) {
		return new ImageIcon(icon.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH));
	}

	private final String rootDirName;

	public ResourceManager(final String rootDirNameInput) {
		Objects.requireNonNull(rootDirNameInput);
		this.rootDirName = rootDirNameInput;
	}

	public BufferedImage loadBufferedImage(final String imageName) throws IOException {
		Objects.requireNonNull(rootDirName);
		Objects.requireNonNull(imageName);
		if (imageName.isEmpty()) {
			throw new IllegalArgumentException(String.format("need name:%s", imageName));
		}
		final String imagePath = String.format("%s/%s", rootDirName, imageName);
		//
		// FIXME rewrite!!!!!!
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(imagePath)) {
			return ImageIO.read(is);
		}
		//
	}

	public BufferedImage loadBufferedImageOrNull(final String imageName) {
		try {
			return loadBufferedImage(imageName);
		} catch (final Exception e) {
			return null;
		}
	}

	public ImageIcon loadIcon(final String imageName) throws IOException {
		return new ImageIcon(loadBufferedImage(imageName));
	}

	public ImageIcon loadIcon(final String imageName, final int x, final int y) throws IOException {
		final ImageIcon icon = new ImageIcon(loadBufferedImage(imageName));
		return scaleIcon(icon, x, y);
	}

	public ImageIcon loadIconOrNull(final String imageName) {
		try {
			return loadIcon(imageName);
		} catch (final Exception e) {
			return null;
		}
	}

	public ImageIcon loadIconOrNull(final String imageName, final int width, final int height) {
		try {
			final ImageIcon icon = loadIcon(imageName);
			return scaleIcon(icon, width, height);
		} catch (final Exception e) {
			return null;
		}
	}
}

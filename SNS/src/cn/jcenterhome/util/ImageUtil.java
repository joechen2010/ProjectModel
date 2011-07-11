package cn.jcenterhome.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

public class ImageUtil {

	private static final boolean USE_TRANSFORM = false;

	
	public static String makeThumb(HttpServletRequest request, HttpServletResponse response, String srcImgPath) {
		Map<String, Object> settings = Common.getCacheDate(request, response,
				"/data/cache/cache_setting.jsp", "globalSetting");
		int tow = (Integer) settings.get("thumbwidth");
		int toh = (Integer) settings.get("thumbheight");
		int maxtow = (Integer) settings.get("maxthumbwidth");
		int maxtoh = (Integer) settings.get("maxthumbheight");
		return makeThumb(request, response, srcImgPath, tow, toh, maxtow, maxtoh);
	}

	
	public static String makeThumb(HttpServletRequest request, HttpServletResponse response,
			String srcImgPath, int tow, int toh, int maxtow, int maxtoh) {
		File srcFile = new File(srcImgPath);
		if (!srcFile.exists()) {
			return null;
		}

		String destPath = srcImgPath + ".thumb.jpg";
		File destFile = new File(destPath);

		if (tow < 60) {
			tow = 60;
		}
		if (toh < 60) {
			toh = 60;
		}

		boolean make_max = false;
		if (maxtow >= 300 && maxtoh >= 300) {
			make_max = true;
		}

		String srcImgType = Common.getImageType(srcFile);
		if ("gif".equals(srcImgType)) {
			make_max = false;
		}
		try {
			BufferedImage srcImg = ImageIO.read(srcFile);

			float src_w = srcImg.getWidth();
			float src_h = srcImg.getHeight();
			if (src_w <= maxtow && src_h <= maxtoh)
				make_max = false;

			float thumb_ratio = tow / toh;
			float src_ratio = src_w / src_h;
			if (thumb_ratio <= src_ratio) {
				toh = (int) (tow / src_ratio);
				maxtoh = (int) (maxtow * (src_h / src_w));
			} else {
				tow = (int) (toh * src_ratio);
				maxtow = (int) (maxtoh * (src_w / src_h));
			}

			if (src_w > tow || src_h > toh) {
				double x_ratio = (double) tow / src_w;
				double y_ratio = (double) toh / src_h;

				AffineTransform tx = new AffineTransform();
				tx.setToScale(x_ratio, y_ratio);
				BufferedImage thumbImg = new BufferedImage(tow, toh, BufferedImage.TYPE_3BYTE_BGR);
				Graphics2D g2d = thumbImg.createGraphics();
				if (USE_TRANSFORM) {
					g2d.drawImage(srcImg, tx, null);
				} else {
					Image scaleImg = getScaledInstance(srcImg, tow, toh);
					g2d.drawImage(scaleImg, 0, 0, null);
				}
				g2d.dispose();
				ImageIO.write(thumbImg, "jpeg", destFile);
				if (make_max) {
					BufferedImage maxImg = new BufferedImage(maxtow, maxtoh, BufferedImage.TYPE_3BYTE_BGR);
					g2d = maxImg.createGraphics();
					if (USE_TRANSFORM) {
						g2d.drawImage(srcImg, tx, null);
					} else {
						Image scaleImg = getScaledInstance(srcImg, maxtow, maxtoh);
						g2d.drawImage(scaleImg, 0, 0, null);
					}
					g2d.dispose();
					ImageIO.write(maxImg, "jpeg", srcFile);
				}
			}
		} catch (IOException e) {
			return null;
		}

		return destFile.exists() ? destPath : null;
	}

	private static Image getScaledInstance(BufferedImage srcImage, int imageWidth, int imageHeight) {
		ImageFilter filter = new java.awt.image.AreaAveragingScaleFilter(imageWidth, imageHeight);
		ImageProducer prod = new FilteredImageSource(srcImage.getSource(), filter);
		Image newImage = Toolkit.getDefaultToolkit().createImage(prod);
		ImageIcon imageIcon = new ImageIcon(newImage);
		Image scaleImg = imageIcon.getImage();
		return scaleImg;
	}

	
	public static void makeWaterMark(HttpServletRequest request, HttpServletResponse response, String srcImg) {
		try {
			File srcFile = new File(srcImg);
			String srcImgType = Common.getImageType(srcFile);
			if (srcImgType.equals("gif")) {
				byte[] bytes = new byte[1024];
				FileInputStream fis = new FileInputStream(srcImg);
				fis.read(bytes);
				String srcContent = new String(bytes, JavaCenterHome.JCH_CHARSET);
				if (srcContent.indexOf("NETSCAPE2.0") != -1) {
					return;
				}
			}
			Map<String, Object> settings = Common.getCacheDate(request, response,
					"/data/cache/cache_setting.jsp", "globalSetting");
			String wm = (String) settings.get("watermarkfile");
			String waterMark = JavaCenterHome.jchRoot + (Common.empty(wm) ? "./image/watermark.png" : wm);

			Image water = ImageIO.read(new File(waterMark));
			Image src = ImageIO.read(srcFile);
			int water_w = water.getWidth(null);
			int water_h = water.getHeight(null);
			int src_w = src.getWidth(null);
			int src_h = src.getHeight(null);
			if ((src_w < water_w + 150) || (src_h < water_h + 150)) {
				return;
			}
			int waterMarkPos = (Integer) settings.get("watermarkpos");
			int x = 0, y = 0;
			switch (waterMarkPos) {
				case 1:
					x = 0;
					y = 0;
					break;
				case 2:
					x = src_w - water_w;
					y = 0;
					break;
				case 3:
					x = 0;
					y = src_h - water_h;
					break;
				case 4:
					x = src_w - water_w;
					y = src_h - water_h;
					break;
				default:
					x = Common.rand(0, src_w - water_w);
					y = Common.rand(0, src_h - water_h);
			}

			BufferedImage image = new BufferedImage(src_w, src_h, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = image.createGraphics();
			g2d.drawImage(src, 0, 0, src_w, src_h, null);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.7f));
			g2d.drawImage(water, x, y, water_w, water_h, null);
			g2d.dispose();

			ImageIO.write(image, srcImgType, srcFile);
		} catch (IOException e) {
			return;
		}
	}
}

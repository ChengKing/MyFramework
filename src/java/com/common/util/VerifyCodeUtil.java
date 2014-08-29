package com.common.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedString;
import java.util.Random;

import javax.imageio.ImageIO;

public class VerifyCodeUtil {

	private static Random imgGenerator = new Random();
	
	private static char[] captchars = new char[] { 'a', 'b', 'c', 'd', 'e', '2', '3', '4', '5', '6', '7', '8', 'g', 'f', 'y', 'n', 'm', 'n', 'p', 'w', 'x' };

	
	private static char getSingalVerifyCode() {
		int car = captchars.length - 1;
		return captchars[imgGenerator.nextInt(car) + 1];
	}
	
	public static String getVerifyCode(int length) {
		String verifyCode = "";
		for (int i = 0; i < length; i++) {
			verifyCode += VerifyCodeUtil.getSingalVerifyCode();
		}
		
		return verifyCode;
	}
	
	/**
	 * 指定字体，生成基于AWT的带有验证码的图片
	 * 
	 * @param verifyCode 传入的验证码
	 * @param font 验证码使用的字体 使用的字体可以传入空值，传入空值时，表示不指定字体，随机从系统字体中选取
	 * @return  BufferedImage实例对象
	 */
	public static BufferedImage drawChar(String verifyCode, Font font) {

		BufferedImage bi = new BufferedImage(320, 100, BufferedImage.TYPE_BYTE_INDEXED);
		Graphics2D graphics = bi.createGraphics();
		/**
		 * 设置背景色
		 */
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		graphics.setColor(Color.black);
		for (int i = 0; i < 600; i++) {
			graphics.setColor(getRandColor());
			graphics.drawRect(imgGenerator.nextInt(320), imgGenerator.nextInt(100), 1, 1);
		}
		AffineTransform fontAT = new AffineTransform();
		fontAT.shear(+3.0, -2.0);
		fontAT.rotate(Math.toRadians(imgGenerator.nextInt(10)));
		fontAT.scale(0.8, 2.0);
		AttributedString as = new AttributedString(verifyCode);
		for (int i = 0; i < verifyCode.length(); i++) {
			//yaonengjun@2008-12-24 现在是从linux的指定字体中取的
			Font actualFont = font == null ? getRandomFontFromSelectedOnLinux() : font;
			as.addAttribute(TextAttribute.FONT, actualFont, i, i + 1);
			as.addAttribute(TextAttribute.FOREGROUND, getRandColor(), i, i + 1);
			as.addAttribute(TextAttribute.TRANSFORM, fontAT, i, i + 1);
			as.addAttribute(TextAttribute.WIDTH, new Integer(imgGenerator.nextInt(1) * 10 + 20), i, i + 1);
			as.addAttribute(TextAttribute.STRIKETHROUGH, new Boolean(imgGenerator.nextInt(2) == 1), i, i + 1);
			as
					.addAttribute(TextAttribute.SUPERSCRIPT, imgGenerator.nextInt(2) == 1 ? TextAttribute.SUPERSCRIPT_SUB : TextAttribute.SUPERSCRIPT_SUPER, i,
							i + 1);
		}
		graphics.drawString(as.getIterator(), 10 + imgGenerator.nextInt(1) * 10, 70);
		int w = bi.getWidth();
		int h = bi.getHeight();
		shear(graphics, w, h, Color.white);
		return bi;
	}

	/**
	 * 生成字体随机的、基于AWT的带有验证码的图片
	 * 
	 * @param verifyCode
	 * @return BufferedImage实例对象,带有有验证码的图片
	 */
	public static BufferedImage drawChar(String verifyCode) {
		return drawChar(verifyCode, null);

	}

	/**
	 * 随机取系统中所有的字体
	 * 
	 * @return Font的实例对象
	 */
	public static Font getRandomFont() {
		Random random = new Random();
		Font[] font = getAllFonts();
		Font result = font[random.nextInt(font.length)];
		return result.deriveFont(Font.PLAIN, 45);
	}
	
	public static Font getRandomFontFromSelectedOnLinux() {
		Random random = new Random();
		Font[] font = getAllSelectedFontsOnLinux();
		
		Font result = font[random.nextInt(font.length)];
		return result;
	}

	/**
	 * 随机产生定义的颜色
	 * 
	 * @return
	 */
	public static Color getRandColor() {
		Random random = new Random();
		Color color[] = new Color[10];
		color[0] = new Color(32, 158, 25);
		color[1] = new Color(218, 42, 19);
		color[2] = new Color(31, 75, 208);
		return color[random.nextInt(3)];
	}

	private static void shear(Graphics g, int w1, int h1, Color color) {
		shearX(g, w1, h1, color);
		shearY(g, w1, h1, color);
	}

	private static void shearX(Graphics g, int w1, int h1, Color color) {

		int period = imgGenerator.nextInt(2);

		boolean borderGap = true;
		int frames = 10;
		int phase = imgGenerator.nextInt(2);

		for (int i = 0; i < h1; i++) {
			double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
			g.copyArea(0, i, w1, 1, (int) d, 0);
			if (borderGap) {
				g.setColor(color);
				g.drawLine((int) d, i, 0, i);
				g.drawLine((int) d + w1, i, w1, i);
			}
		}

	}

	/**
	 * 
	 * @param g
	 * @param w1
	 * @param h1
	 * @param color
	 */
	private static void shearY(Graphics g, int w1, int h1, Color color) {
		int period = imgGenerator.nextInt(40) + 10; // 50;
		boolean borderGap = true;
		int frames = 20;
		int phase = 10;
		for (int i = 0; i < w1; i++) {
			double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
			g.copyArea(i, 0, 1, h1, 0, (int) d);
			if (borderGap) {
				g.setColor(color);
				g.drawLine(i, (int) d, i, 0);
				g.drawLine(i, (int) d + h1, i, h1);
			}

		}
	}
	
	public static Font[] getAllFonts() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		return ge.getAllFonts();
	}
	
	public static Font[] getAllSelectedFontsOnLinux() {
		int fontStyle = Font.PLAIN;
		int fontSize = 45;
		Font[] font = new Font[31];
		font[0] = new Font("AR PL ShanHeiSun Uni", fontStyle, fontSize);
		font[1] = new Font("AR PL ZenKai Uni", fontStyle, fontSize);
		font[2] = new Font("Bitstream Charter Bold", fontStyle, fontSize);
		font[3] = new Font("Bitstream Charter Italic", fontStyle, fontSize);
		font[4] = new Font("Bitstream Vera Sans", fontStyle, fontSize);
		font[5] = new Font("Bitstream Vera Sans Bold1", fontStyle, fontSize);
		font[6] = new Font("Bitstream Vera Sans Bold Oblique", fontStyle, fontSize);
		font[7] = new Font("Bitstream Vera Sans Mono1", fontStyle, fontSize);
		font[8] = new Font("Bitstream Vera Sans Mono Bold", fontStyle, fontSize);
		font[9] = new Font("Bitstream Vera Sans Mono Bold Oblique", fontStyle, fontSize);
		font[10] = new Font("Bitstream Vera Sans Mono Oblique", fontStyle, fontSize);
		font[11] = new Font("Bitstream Vera Serif Bold", fontStyle, fontSize);
		font[12] = new Font("Century Schoolbook L Bold", fontStyle, fontSize);
		font[13] = new Font("Century Schoolbook L Italic", fontStyle, fontSize);
		font[14] = new Font("Century Schoolbook L Bold Italic", fontStyle, fontSize);
		font[15] = new Font("Courier 10 Pitch Bold", fontStyle, fontSize);
		font[16] = new Font("Courier Italic", fontStyle, fontSize);
		font[17] = new Font("Courier Bold Italic", fontStyle, fontSize);
		font[18] = new Font("DejaVu LGC Sans ExtraLight", fontStyle, fontSize);
		font[19] = new Font("DejaVu LGC Sans Mono", fontStyle, fontSize);
		font[20] = new Font("DejaVu LGC Serif Bold", fontStyle, fontSize);
		font[21] = new Font("DejaVu LGC Serif Condensed Bold Oblique", fontStyle, fontSize);
		font[22] = new Font("DialogInput.italic", fontStyle, fontSize);
		font[23] = new Font("Luxi Serif Bold", fontStyle, fontSize);
		font[24] = new Font("Monospaced.bold", fontStyle, fontSize);
		font[25] = new Font("Nimbus Mono L Regular", fontStyle, fontSize);
		font[26] = new Font("Nimbus Roman No9 L Bold", fontStyle, fontSize);
		font[27] = new Font("Nimbus Sans L Bold Italic", fontStyle, fontSize);
		font[28] = new Font("URW Bookman L Demi Bold Italic", fontStyle, fontSize);
		font[29] = new Font("URW Chancery L Medium Italic", fontStyle, fontSize);
		font[30] = new Font("URW Palladio L Bold Italic", Font.PLAIN, fontSize);
		
		return font;
	}
	
	public static void main(String[] args) {
		File fRandom = new File("server-work/java2d/picture.jpg");
		try {
			ImageIO.write(drawChar("small小小鸟"), "jpg", fRandom);
		} catch (IOException e) {
			System.err.println("err: " + e);
			e.printStackTrace();
		}
		
		Font[] fonts = getAllFonts();
		for (int i = 0; i < fonts.length; i++) {
			System.out.println(fonts[i].getFontName());
			File f = new File("server-work/java2d/picture-" + fonts[i].getFontName() + ".jpg");
			try {
				ImageIO.write(drawChar("small小小鸟", fonts[i].deriveFont(Font.PLAIN, 45)), "jpg", f);
			} catch (IOException e) {
				System.err.println("err: " + fonts[i].getFontName());
				e.printStackTrace();
			}
		}
	}
	
}
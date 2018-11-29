package node;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

import de.pi3g.pi.oled.OLEDDisplay;

public class TestMain
{
	public static Font font;
	public static void main(String[] args) throws IOException, ReflectiveOperationException, UnsupportedBusNumberException, InterruptedException, FontFormatException
	{
		

		font = Font.createFont(Font.TRUETYPE_FONT, TestMain.class.getResourceAsStream("/font/neodgm.ttf"));
		font = font.deriveFont(Font.PLAIN, 14);

		OLEDDisplay display = new OLEDDisplay();
		int x = 0;
		while(true)
		{
			display.clear();
			BufferedImage img = stringToBufferedImage("테스트카운트:"+x);
			System.out.println(img.getWidth() + " " + img.getHeight());
			for(int i = 0; i < img.getWidth(); ++i)
			{
				for(int j = 0; j < img.getHeight(); ++j)
				{
					if(i == 0 || j == 0 || i == img.getWidth() - 1 || j == img.getHeight() - 1)
					{
						display.setPixel(i, j, true);
					}
					if(img.getRGB(i, j) != 0)
					{
						display.setPixel(i, j, true);
					}
				}
			}

			
			display.update();
			++x;
		
			Thread.sleep(100);
		}

		
	}
	public static BufferedImage stringToBufferedImage(String s) {
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
   
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(s);
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        g2d.drawString(s, 0, fm.getAscent());
        g2d.dispose();
        return img;

	}
}

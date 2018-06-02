package co.bashscript.utils;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

public class BSSystemUtils
{
	public static Dimension getPrimaryMonitorScreenSize()
	{
		Rectangle widow_size = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		return new Dimension(widow_size.width, widow_size.height);
	}
	
	public static Dimension getTaskbarSize()
	{
		Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension window_size = getPrimaryMonitorScreenSize();
		int task_bar_height = screen_size.height - window_size.height;
		return new Dimension(window_size.width, task_bar_height);
	}
	public static List<Dimension> getScreenSize()
	{
		List<Dimension> sizes = new ArrayList<>();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for(GraphicsDevice curGs : gs)
		{
		      DisplayMode dm = curGs.getDisplayMode();
		      sizes.add(new Dimension(dm.getWidth(), dm.getHeight()));
		}
		return sizes;
	}
}

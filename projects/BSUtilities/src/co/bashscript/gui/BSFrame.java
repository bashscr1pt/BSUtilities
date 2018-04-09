package co.bashscript.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import co.bashscript.utils.BSSystemUtils;

public class BSFrame extends JFrame
{
	// Static Variables
	public static enum POSITION { TOP_LEFT, TOP_MIDDLE, TOP_RIGHT, LEFT, MIDDLE, RIGHT, BOTTOM_LEFT, BOTTOM_MIDDLE, BOTTOM_RIGHT };
	
	// Variables
	private Frame parent = null;
	private POSITION position = POSITION.MIDDLE;
	
	public BSFrame()
	{
		this(null);
	}
	
	public BSFrame(Frame parent)
	{
		this.parent = parent;
		setLookAndFeel();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 600);
	}
	
	@Override
	public void setVisible(boolean value)
	{
		if(value)
			setPosition(getPosition());
		super.setVisible(value);
	}
	
	// Getters and Setters
	public POSITION getPosition() { return position; }
	public void setPosition(POSITION position) 
	{ 
		this.position = position;
		
		Dimension dimension = null; 
		
		if(parent == null)
			dimension = BSSystemUtils.getPrimaryMonitorScreenSize();
		else
			dimension = parent.getSize();
		
		int screen_width = dimension.width;
		int screen_height = dimension.height;
		int width = getSize().width;
		int height = getSize().height;
		int x = 0;
		int y = 0;
		
		switch(position)
		{
			case TOP_LEFT: break;
			case TOP_MIDDLE:
				x = screen_width/2 - width / 2;
				break;
			case TOP_RIGHT:
				x = screen_width - width;
				break;

			case LEFT:
				y = screen_height / 2 - height / 2;
				break;
			case MIDDLE:
				x = screen_width / 2 - width / 2;
				y = screen_height / 2 - height / 2;
				break;
			case RIGHT:
				x = screen_width - width;
				y = screen_height / 2 - height / 2;
				break;
				
			case BOTTOM_LEFT:
				y = screen_height - height;
				break;
			case BOTTOM_MIDDLE:
				x = screen_width / 2 - width / 2;
				y = screen_height - height;
				break;
			case BOTTOM_RIGHT:
				x = screen_width - width;
				y = screen_height - height;
				break;
		}
		
		if(parent == null)
			setLocation(x, y);
		else
			setLocation(parent.getLocation().x + x, parent.getLocation().y + y);
	}
	
	// Static Methods
	private static void setLookAndFeel()
	{
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
		catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) { }
	}
	
	public static void main(String ... args)
	{
		Map<POSITION, BSFrame> map = new HashMap<>();
		for(POSITION position : BSFrame.POSITION.values())
		{
			BSFrame frame = new BSFrame();
			frame.setTitle(position.toString());
			if(position == POSITION.MIDDLE)
				frame.setSize(800, 600);
			else
				frame.setSize(200, 100);
			frame.setPosition(position);
			map.put(position, frame);
		}
		
		map.values().stream().forEach(e -> e.setVisible(true));
		
		for(POSITION position : BSFrame.POSITION.values())
		{
			BSFrame frame = new BSFrame(map.get(POSITION.MIDDLE));
			frame.setTitle(position.toString());
			frame.setSize(200, 100);
			frame.setPosition(position);
			frame.setVisible(true);
		}
	}
}

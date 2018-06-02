package co.bashscript.utils;

import java.util.List;

public class BSStringUtils
{
	public static String implode(String glue, List pieces)
	{
		StringBuilder builder = new StringBuilder();
		for(int i=0; i<pieces.size(); i++)
		{
			if(pieces.size()-1 == i)
				builder.append(pieces.get(i).toString());
			else
				builder.append(pieces.get(i).toString() + glue);
		}
		return builder.toString();
	}
}

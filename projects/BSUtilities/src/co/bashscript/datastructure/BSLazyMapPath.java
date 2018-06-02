package co.bashscript.datastructure;

import java.util.List;

public class BSLazyMapPath<T>
{
	private List<Object> path;
	private T data;
	
	public BSLazyMapPath(List<Object> path, T data)
	{
		this.path = path;
		this.data = data;
	}
	
	public Object[] getPath() { return path.toArray(new Object[path.size()]); }
	public T getData() { return data; }
}

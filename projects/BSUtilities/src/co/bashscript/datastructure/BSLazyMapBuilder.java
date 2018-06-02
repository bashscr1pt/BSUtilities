package co.bashscript.datastructure;

public interface BSLazyMapBuilder<T, R>
{
	public T build(T parent, R action, BSLazyMap<R> lazy_map, boolean is_leaf, int depth, int index);
}

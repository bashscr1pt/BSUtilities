package co.bashscript.datastructure.lazymap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import co.bashscript.utils.BSStringUtils;

public class BSLazyMap<T>
{
	// private
	private Object parent_key;
	private T data;
	private BSLazyMap<T> parent = null;
	private Map<Object, BSLazyMap<T>> map;
	private Comparator<Entry<Object, BSLazyMap<T>>> sort_operation = null;

	public BSLazyMap()
	{}

	public BSLazyMap(BSLazyMap<T> m)
	{
		putAll(m);
	}

	private BSLazyMap(Object key, BSLazyMap<T> parent)
	{
		this.parent_key = key;
		this.parent = parent;
		this.sort_operation = parent.sort_operation;
	}

	public boolean containsKey(Object key)
	{
		if (map != null && map.containsKey(key)) return true;
		return false;
	}

	public BSLazyMap<T> get(Object key)
	{
		if (key == null) return null;
		if (map == null || !map.containsKey(key)) return new BSLazyMap<T>(key, this);
		return map.get(key);
	}

	public BSLazyMap<T> get(Object[] keys)
	{
		BSLazyMap<T> m = this;
		for (int i = 0; i < keys.length; i++)
		{
			if (keys[i] == null) continue;
			if (m == null || !m.containsKey(keys[i]))
				m = new BSLazyMap<T>(keys[i], m);
			else
				m = m.get(keys[i]);
		}
		return m;
	}

	public BSLazyMap<T> set(T data)
	{
		return getParent().put(parent_key, data);
	}

	public BSLazyMap<T> put(Object[] path, T value)
	{
		if (path.length == 1) return put(path[0], value);
		return get(path).parent.put(path[path.length - 1], value);
	}

	public int size()
	{
		if (map == null) return 0;
		return map.size();
	}

	public BSLazyMap<T> put(Object key, T value)
	{
		Deque<BSLazyMap<T>> parents = new ArrayDeque<>();
		Deque<Object> parent_keys = new ArrayDeque<>();

		BSLazyMap<T> m = this;
		while (m != null & m.parent != null)
		{
			parents.push(m.parent);
			parent_keys.push(m.parent_key);
			m = m.parent;
		}

		// You can speed this up, by sorting circuting this, if you want to make this
		// better, do it. #Lazyness
		while (!parents.isEmpty())
		{
			BSLazyMap<T> map = parents.pop();
			BSLazyMap<T> next_map = parents.peek();
			Object k = parent_keys.pop();

			if (map.map == null) map.map = new LinkedHashMap<>();

			if (next_map == null)
			{
				if (!map.map.containsKey(k)) map.map.put(k, this);
			}
			else if (!map.map.containsKey(k)) map.map.put(k, next_map);
		}

		if (map == null) map = new LinkedHashMap<>();

		BSLazyMap<T> m1 = null;
		if (!map.containsKey(key))
			m1 = new BSLazyMap<T>(key, this);
		else
			m1 = map.get(key);
		m1.data = value;
		map.put(key, m1);

		return m1;
	}

	public T get()
	{
		return data;
	}

	public void putAll(BSLazyMap<T> map)
	{
		if (map == null) return;

		if (this.map == null) this.map = new LinkedHashMap<>();

		for (Object o : map.keySet())
		{
			if (!this.map.containsKey(o))
				this.map.put(o, map.get(o));
			else
				this.map.get(o).putAll(map.get(o));
		}

		if (map.get() != null) this.data = map.get();
	}

	public List<Object> keySet()
	{
		if (this.map == null) return new ArrayList<Object>();
		return new ArrayList<>(this.map.keySet());
	}

	public List<Entry<Object, BSLazyMap<T>>> entrySet()
	{
		if (this.map == null) return new ArrayList<Entry<Object, BSLazyMap<T>>>();
		return new ArrayList<>(this.map.entrySet());
	}

	public boolean isEmpty()
	{
		if (map == null || map.isEmpty()) return true;
		return false;
	}

	public boolean isMapNull()
	{
		if (map == null) return true;
		return false;
	}

	public List<BSLazyMapPath<T>> getAll()
	{
		if (this.map == null) return new ArrayList<>();
		return getAll(new ArrayList<>());
	}

	private List<BSLazyMapPath<T>> getAll(List<Object> path)
	{
		List<BSLazyMapPath<T>> result = new ArrayList<>();
		List<Object> a = new ArrayList<>(path);
		if (this.parent_key != null) a.add(this.parent_key);

		if (data != null) result.add(new BSLazyMapPath<T>(a, data));

		if (map != null) for (Object key : keySet())
			result.addAll(map.get(key).getAll(a));

		return result;
	}

	public <R> void build(R parent, BSLazyMapBuilder<R, T> function)
	{
		build(parent, function, -1, 0);
	}

	private <R> void build(R parent, BSLazyMapBuilder<R, T> function, int depth, int index)
	{
		if (this.parent_key != null) parent = function.build(parent, data, this, isEmpty(), depth, index);

		List<Entry<Object, BSLazyMap<T>>> keySet = entrySet();
		keySet.sort(getSortOperation());

		int i = 0;
		if (map != null) for (Entry<Object, BSLazyMap<T>> key : keySet)
			map.get(key.getKey()).build(parent, function, depth + 1, i++);
	}

	public BSLazyMap<T> getParent()
	{
		return parent;
	}

	public Object getParentObject()
	{
		return parent_key;
	}

	public void setSortOperation(Comparator<Entry<Object, BSLazyMap<T>>> sort)
	{
		this.sort_operation = sort;
	}

	public Comparator<Entry<Object, BSLazyMap<T>>> getSortOperation()
	{
		if (getParent() == null && sort_operation == null)
		{
			return new Comparator<Entry<Object, BSLazyMap<T>>>()
			{
				@Override
				public int compare(Entry<Object, BSLazyMap<T>> entry1, Entry<Object, BSLazyMap<T>> entry2)
				{
					Object o1 = entry1.getKey();
					Object o2 = entry2.getKey();
					if (o1 == null && o2 == null) return -1;
					if (o1 != null && o2 == null) return 1;
					if (o1 == null && o2 != null) return 0;

					return o1.toString().compareTo(o2.toString());
				}
			};
		}
		if (sort_operation != null) return sort_operation;
		if (getParent() != null) return getParent().getSortOperation();
		return null;
	}

	public BSLazyMap<T> remove(Object key)
	{
		if (map.containsKey(key)) return map.remove(key);
		return null;
	}

	public void clear()
	{
		map = null;
	}

	public void reset()
	{
		map = null;
		sort_operation = null;
	}

	public void toConsole()
	{
		getAll().forEach(e -> {
			System.out.println(BSStringUtils.implode("/", Arrays.asList(e.getPath())) + " : " + e.getData());
		});
	}

	public boolean pathContainsKey(Object obj)
	{
		BSLazyMap<T> parent = this;
		while (parent != null)
		{
			if (parent.getParentObject() == obj) return true;
			parent = parent.parent;
		}

		return false;
	}

	public int getDepth()
	{
		int count = 0;

		BSLazyMap<T> m = this;
		while (m.getParent() != null)
		{
			count++;
			m = m.getParent();
		}

		return count;
	}

	@Override
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		getAll().forEach(e -> {
			b.append(BSStringUtils.implode("/", Arrays.asList(e.getPath())) + " : " + e.getData() + "\n");
		});

		return b.toString();
	}

	public static void main(String[] args)
	{
		BSLazyMap<Integer> map = new BSLazyMap<>();
		BSLazyMap<Integer> map2 = new BSLazyMap<>();

		map.get("a").get("b").get("c").get("d").get("f").put("age", 30);
		map.get("a").get("b").get("c").get("d").get("f").get("age").get("t").put("c", 14);

		map.get("a").get("b").get("c").put("d", 5).get("f").get("age").set(44).get("t").get("c").set(88);
		map2.get("a").get("b").get("c").put("d", 5).get("f").get("age").put("t", 99);
		map.putAll(map2);
		
		map.toConsole();
	}
}

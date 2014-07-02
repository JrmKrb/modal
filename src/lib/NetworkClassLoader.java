package lib;

import java.util.HashMap;
import java.util.Map;

/* defineClass is protected, that's why we have to create our own custom class. */
public class NetworkClassLoader extends ClassLoader {

	private Map<String, Class<?>>	table;

	/**
	 * @param parent
	 */
	public NetworkClassLoader(ClassLoader parent) {
		super(parent);
		table = new HashMap<String, Class<?>>();
	}

	@Override
	public Class<?> findClass(String name) {
		return table.get(name);
	}

	/**
	 * @param c
	 */
	public void addClass(Class<?> c) {
		table.put(c.getName(), c);
	}

	/**
	 * @param b
	 * @return the load Class
	 */
	public Class<?> load(byte[] b) {
		/* We do not know the name of the class, hence "null" as a first argument */
		Class<?> c = defineClass(null, b, 0, b.length);
		addClass(c);
		return c;

	}

}
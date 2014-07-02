package lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;

public class ClassLoaderObjectInputStream extends ObjectInputStream {

	private final NetworkClassLoader	classLoader;

	/**
	 * @param classLoader
	 * @param inputStream
	 * @throws IOException
	 * @throws StreamCorruptedException
	 */
	public ClassLoaderObjectInputStream(NetworkClassLoader classLoader, InputStream inputStream) throws IOException, StreamCorruptedException {
		super(inputStream);
		this.classLoader = classLoader;
	}

	@Override
	public Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {

		Class<?> tempClass = classLoader.findClass(osc.getName());

		if (tempClass != null) return tempClass;
		else return super.resolveClass(osc);

	}
}
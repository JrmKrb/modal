package network;

class NetworkClassLoader extends ClassLoader {
	public Class load(byte[] b) {
		//we do not know the name of the class, hence "null" as a first argument
		return defineClass(null, b, 0, b.length);
	}
}
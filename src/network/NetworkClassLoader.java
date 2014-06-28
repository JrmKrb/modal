package network;

/* defineClass is protected, that's why we have to create our own custom class. */
class NetworkClassLoader extends ClassLoader {
	public NetworkClassLoader(ClassLoader parent){
		super(parent);
	}
	
	public Class<?> load(byte[] b) {
		//we do not know the name of the class, hence "null" as a first argument
		return defineClass("sumTask", b, 0, b.length);
		
	}
	
	public void resolve(Class<?> c){
		this.resolveClass(c);
	}
}
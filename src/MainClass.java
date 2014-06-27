import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//TODO: from a file AnnexClass.class, load this class to be used by other classes
//TODO: from a serialized (as a String) class Task (implements Task which inherits from Runnable and Serializable)

public class MainClass {
	public static void main(String[] args) throws ClassNotFoundException,
			NoSuchMethodException, SecurityException, IllegalArgumentException,
			InvocationTargetException {
		/* Try to load hidden.Hidden */
		ClassLoader classLoader = MainClass.class.getClassLoader();
		// Class<?> Hidden = classLoader.loadClass("hidden.Hidden");
		// try {
		// Object h = Hidden.newInstance();
		// System.out.println(h.getClass());
		// Class[] args1 = new Class[1];
		// args1[0] = Integer.class;
		// Method m = Hidden.getDeclaredMethod("changeFirst", args1);
		// m.invoke(h, 3);
		//
		// } catch (InstantiationException e) {
		// } catch (IllegalAccessException e) {
		// }
		// System.out.println("aClass.getName() = " + Hidden.getName());

		/* Try to load hidden.Subhidden */
		// Class Subhidden = classLoader.loadClass("hidden.Subhidden");

		/* Try to load Grid (from another project) */
		String className = args[0];
		Class<?> loadedClass = classLoader.loadClass(className);
		System.out.println("aClass.getName() = " + loadedClass.getName());
		try {
			Object l = loadedClass.newInstance();
			System.out.println("step 1");
			Method m2 = loadedClass.getDeclaredMethod("toString", null);
			String tostring = (String) m2.invoke(l, null);
			System.out.println(tostring);
		} catch (InstantiationException e) {
			System.out.println("bug1");
		} catch (IllegalAccessException e) {
			System.out.println("bug2");
		}
	}
}
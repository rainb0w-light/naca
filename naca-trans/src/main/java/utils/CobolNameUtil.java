package utils;

public class CobolNameUtil
{
	public static String fixJavaName(String name)
	{
		if (name == null || name.isEmpty()) return name;
		return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
	}
}

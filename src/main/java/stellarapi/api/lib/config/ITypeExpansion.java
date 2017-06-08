package stellarapi.api.lib.config;

import java.lang.reflect.Type;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Describes expansion of a type. <p>
 * Note that only specified generic types(array/class) without variable arguments
 *  are accepted for configuration.
 * */
public interface ITypeExpansion {
	/**
	 * Types of the values contained in this field.
	 * Should contain everything possible as sub types.
	 * If it is not specified, just return <code>null</code>.
	 * */
	public Type[] getValueTypes();

	public String[] getKeys(Object instance);

	@Nonnull
	public Object getValue(Object instance, String key);
	public Type getValueType(Object instance, String key);
	public void setValue(Object instance, String key, Object value);
	public void remove(Object instance, String key);

	public boolean hasKey(Object instance, String key);

	/**
	 * Creates instance with given properties. Don't change the contents!
	 * */
	public Object createInstance(Map<String, Object> properties);
}
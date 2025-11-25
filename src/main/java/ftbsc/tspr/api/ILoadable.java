package ftbsc.tspr.api;

/**
 * Generic loadable component, usually commands and modules
 */
public interface ILoadable {
	/**
	 * Get the unique {@link ILoadable} name
	 * @return unique name
	 */
	String getName();
}

package wuxiacraft.cultivation;

import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.HashSet;

public class Element extends ForgeRegistryEntry<Element> {

	/**
	 * The ID of this element
	 */
	private final String name;

	/**
	 * All elements that benefits from this one
	 */
	private final HashSet<String> begets;

	/**
	 * All elements that is obstructed by this one
	 */
	private final HashSet<String> obstructs;

	/**
	 * Default constructor of element
	 * @param name the id of the element
	 */
	public Element(String name) {
		this.name = name;
		begets = new HashSet<>();
		obstructs = new HashSet<>();
	}

	/**
	 * @return This element's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Adds a begetter element at the construction
	 * @param element The element to be added
	 * @return This element
	 */
	public Element addBeget(String element) {
		begets.add(element);
		return this;
	}

	/**
	 * Adds an obstructed element at the construction
	 * @param element The element to be added
	 * @return This element
	 */
	public Element addObstruct(String element) {
		obstructs.add(element);
		return this;
	}

	/**
	 * Returns whether element argument benefits from this one
	 * @param element the element to check against
	 * @return true if element is benefited from this one
	 */
	public boolean begetsElement(String element) {
		return begets.contains(element);
	}

	/**
	 * Returns whether element argument is obstructed by this one
	 * @param element the element to check against
	 * @return true if element is obstructed by this one
	 */
	public boolean obstructsElement(String element) {
		return obstructs.contains(element);
	}


}

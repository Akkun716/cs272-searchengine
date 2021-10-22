import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndexBuilder {
	private final Map<String, TreeMap<String, TreeSet<Integer>>> invertedIndex =
			new TreeMap<>();

	/**
	 * Adds  word keys, file location keys, and positions in the index
	 * (if they does not already exist in the index)
	 * 
	 * @param word stemmed word
	 * @param location file location where the word stem appeared
	 * @param position position of the word stem in respective file location
	 */
	public boolean add(String word, String location, Integer position) {
		return invertedIndex.putIfAbsent(word, new TreeMap<>())
				.putIfAbsent(location, new TreeSet<Integer>())
				.add(position);
	}
	
	/**
	 * Returns an unmodifiable invertedIndex for easy printing.
	 *
	 * @return an unmodifiable index map
	 */
	public Map<String, TreeMap<String, TreeSet<Integer>>> getIndex() {
		return Collections.unmodifiableMap(invertedIndex);
	}
	
	/**
	 * Returns an unmodifiable map of file locations of a word stem
	 *
	 * @param stem word stem that needs to be accessed
	 * @return an unmodifiable Set of positions
	 */
	public Map<String, TreeSet<Integer>> getLocations(String stem) {
		return Collections.unmodifiableMap(invertedIndex.getOrDefault(stem, new TreeMap<>()));
	}
	
	/**
	 * Returns an unmodifiable Set of word stem positions at file location
	 *
	 * @param stem word stem that needs to be accessed
	 * @param location file location that needs to be accessed
	 * @return an unmodifiable Set of positions
	 */
	public Set<Integer> getPositions(String stem, String location) {
		return hasLocation(stem, location)
				? Collections.unmodifiableSet(invertedIndex.get(stem).get(location))
				: Collections.unmodifiableSet(new TreeSet<>());
	}
	
	/**
	 * Checks if word stem exists as a key in index
	 * 
	 * @param stem word stem to be found in index
	 */
	public boolean hasStem(String stem) {
		return invertedIndex.containsKey(stem);
	}
	
	/**
	 * Checks if the word stems exists and then if location can be found
	 *  
	 * @param stem word stem to be found in index
	 * @param location file location to be found under word stem key
	 */
	public boolean hasLocation(String stem, String location) {
		return hasStem(stem) && invertedIndex.get(stem).containsKey(location);
	}
	
	/**
	 * Checks if the word and location keys exists in index and then if position
	 * can be found
	 * 
	 * @param stem word stem to be found in index
	 * @param location file location to be found under word stem key
	 * @param position position of the word stem at designated file location
	 */
	public boolean hasPosition(String stem, String location, Integer position) {
		return hasLocation(stem, location)
				&& invertedIndex.get(stem).get(location).contains(position);
	}
	
	/**
	 * Returns number of word stem keys in index
	 */
	public int stemCount() {
		return invertedIndex.size();
	}
	
	/**
	 * Returns number of file locations under word stem key in index
	 * 
	 * @param stem word stem key to be accessed
	 */
	public int locationCount(String stem) {
		return hasStem(stem)
				? invertedIndex.get(stem).size()
				: 0;
	}
	
	/**
	 * Returns number of positions under respective file location and word stem
	 * keys in index
	 * 
	 * @param stem word stem key to be accessed
	 */
	public int positionCount(String stem, String location) {
		return hasLocation(stem, location)
				? invertedIndex.get(stem).get(location).size()
				: 0;
	}
	
	@Override
	public String toString() {
		return this.invertedIndex.toString();
	}
}
package jlinq;

import java.util.Iterator;

public class Grouping<Key, Element> implements IEnumerable<Element> {
	private final IEnumerable<Element> elements;
	public final Key key;

	@Override
	public Iterator<Element> iterator() {
		return elements.iterator();
	}
	
	public Grouping(final IEnumerable<Element> elements, final Key key) {
		this.elements = elements;
		this.key = key;
	}
}

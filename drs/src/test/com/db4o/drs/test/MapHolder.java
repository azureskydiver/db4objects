package com.db4o.drs.test;

import java.util.HashMap;
import java.util.Map;

/**
 * @sharpen.ignore
 */
public class MapHolder {

	private String name;

	private Map map;

	public MapHolder() {

	}

	public MapHolder(String name) {
		this.name = name;
		this.map = new HashMap();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}

	public void put(Object key, Object value) {
		map.put(key, value);
	}

	public String toString() {
		return "name = " + name + ", map = " + map;
	}
}

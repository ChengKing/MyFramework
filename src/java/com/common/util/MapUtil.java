package com.common.util;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 
 * @since ChengKing@Nov 27, 2009
 */
public class MapUtil {

	/**
	 * 
	 * @param currentView
	 * @param newKey
	 * @return
	 * @creator ChengKing @ Nov 27, 2009
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map resort4Add(Map currentView, String newKey) {
		int total = 0;
		for (Iterator i = currentView.values().iterator(); i.hasNext();) {
			List coApps = (List) i.next();
			total += coApps.size();
		}
		//
		int nodeCount = currentView.size();
		int avgApps = total / (nodeCount + 1);
		List toRedistCoApps = new ArrayList();
		for (Iterator i = currentView.values().iterator(); i.hasNext();) {
			List coApps = (List) i.next();
			if (coApps.size() > avgApps) {
				for (int j = avgApps; j != coApps.size();) {
					toRedistCoApps.add(coApps.get(j));
					coApps.remove(j);
				}
			}
		}
		currentView.put(newKey, toRedistCoApps);
		return currentView;
	}

	/**
	 * 
	 * @return
	 * @creator ChengKing @ Nov 27, 2009
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map resort4Del(Map currentView, String delKey) {
		List toRedistCoApps = (List) currentView.get(delKey.toString());
		currentView.remove(delKey.toString());
		//
		for (int index = 0; index < toRedistCoApps.size();) {
			for (Iterator i = currentView.values().iterator(); i.hasNext(); index++) {
				if (index == toRedistCoApps.size())
					break;
				List coApps = (List) i.next();
				coApps.add(toRedistCoApps.get(index));
			}
		}
		return currentView;
	}

	/**
	 * 
	 * @param currentView
	 * @return
	 * @since v3.5
	 * @creator ChengKing @ Nov 27, 2009
	 */
	@SuppressWarnings("rawtypes")
	public static String toString(Map currentView) {
		StringBuffer buffer = new StringBuffer();
		for (Iterator i = currentView.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			buffer.append(key).append(":").append(currentView.get(key).toString()).append(";");
		}
		return buffer.toString();
	}

	/**
	 * 
	 * @param view
	 * @return
	 * @creator ChengKing @ Nov 27, 2009
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map toMap(String view) {
		Map currentView = new Hashtable();
		StringTokenizer stView = new StringTokenizer(view, ";");
		for (; stView.hasMoreTokens();) {
			String viewToken = stView.nextToken();
			int colIndex = viewToken.indexOf(":");
			String viewName = viewToken.substring(0, colIndex);
			currentView.put(viewName, parseValues(viewToken.substring(colIndex + 2, viewToken.length() - 1)));
		}
		return currentView;
	}

	/**
	 * 
	 * @param substring
	 * @return
	 * @creator ChengKing @ Nov 27, 2009
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List parseValues(String strValue) {
		List values = new ArrayList();
		StringTokenizer stValues = new StringTokenizer(strValue, ",");
		for (; stValues.hasMoreTokens();) {
			String value = stValues.nextToken();
			values.add(value.trim());
		}
		return values;
	}

}
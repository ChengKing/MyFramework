package com.common.util;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * 
 * @author chengking
 */
public class JarUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		printJarFileInfo("lib/servlet-2.3.jar");

		System.out.println("\n------------------------------------------------------------------------------");
		System.out.println(getJarInfo("lib/servlet-2.3.jar"));
	}

	public static String getJarInfo(String jarName) {
		return getJarInfo(jarName, System.getProperty("line.separator"));
	}

	@SuppressWarnings("rawtypes")
	public static String getJarInfo(String jarName, String lineStr) {
		StringBuffer sb = new StringBuffer();
		try {
			String fileName = jarName;
			File f = new File(fileName);
			if (false == f.exists()) {
				sb.append("the file not exist! ").append(fileName).append(lineStr);
			} else {
				JarFile jarfile = new JarFile(f);
				sb.append(jarfile.getName()).append('.');
				sb.append(new Timestamp(f.lastModified()));
				sb.append("\tSize:").append(jarfile.size());
				sb.append(lineStr);
				for (Enumeration enu = jarfile.entries(); enu.hasMoreElements();) {
					ZipEntry zipEntry = (ZipEntry) enu.nextElement();
					sb.append(getZipEntryInfoBuffer(zipEntry));
					sb.append(lineStr);
				}

				Manifest manifest = jarfile.getManifest();

				Map map = manifest.getEntries();
				sb.append("manifest.getEntries()=").append(map.size());
				sb.append(lineStr);

				for (Iterator it = map.keySet().iterator(); it.hasNext();) {
					String entryName = (String) it.next();

					sb.append("All attributes for the entry: ").append(entryName);
					sb.append(lineStr);

					Attributes attrs = (Attributes) map.get(entryName);
					for (Iterator it2 = attrs.keySet().iterator(); it2.hasNext();) {
						Attributes.Name attrName = (Attributes.Name) it2.next();
						sb.append(attrName).append(':').append(attrs.getValue(attrName));
						sb.append(lineStr);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			sb.append("[fail!]").append(e);
			sb.append(lineStr);
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	private static void printJarFileInfo(String jarName) {
		try {
			// Open the JAR file
			String fileName = jarName;
			File f = new File(fileName);
			if (false == f.exists()) {
				System.out.println("the file not exist! " + fileName);
				return;
			}
			JarFile jarfile = new JarFile(f);
			for (Enumeration enu = jarfile.entries(); enu.hasMoreElements();) {
				ZipEntry zipEntry = (ZipEntry) enu.nextElement();
				System.out.println(getZipEntryInfo(zipEntry));
			}

			// Get the manifest
			Manifest manifest = jarfile.getManifest();

			// Get the manifest entries
			Map map = manifest.getEntries();

			// Enumerate each entry
			for (Iterator it = map.keySet().iterator(); it.hasNext();) {
				// Get entry name
				String entryName = (String) it.next();

				// Get all attributes for the entry
				Attributes attrs = (Attributes) map.get(entryName);

				// Enumerate each attribute
				for (Iterator it2 = attrs.keySet().iterator(); it2.hasNext();) {
					// Get attribute name
					Attributes.Name attrName = (Attributes.Name) it2.next();
					// Get attribute value
					String attrValue = attrs.getValue(attrName);
					System.out.println(attrName + ": " + attrValue);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getZipEntryInfo(ZipEntry zipEntry) {
		return getZipEntryInfoBuffer(zipEntry).toString();
	}

	private static StringBuffer getZipEntryInfoBuffer(ZipEntry zipEntry) {
		StringBuffer sb = new StringBuffer(64);
		sb.append(zipEntry.getName());
		sb.append('\t').append(new Timestamp(zipEntry.getTime()));
		if (zipEntry.isDirectory()) {
			sb.append(",isDirectory!");
		} else {
			sb.append(",CompressedSize:").append(zipEntry.getCompressedSize());
			sb.append(",Size:").append(zipEntry.getSize());
		}
		sb.append(",CRC:").append(zipEntry.getCrc());
		return sb;
	}

}
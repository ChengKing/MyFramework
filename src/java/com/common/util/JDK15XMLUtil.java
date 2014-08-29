package com.common.util;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JDK15XMLUtil {

	/**
	 * 根据制定的文件路径获得代表XML文件的document对象
	 * 
	 * @param file
	 *            文件完成路径
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document loadDocument(String file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setFeature("http://xml.org/sax/features/namespaces", false);
		factory.setFeature("http://xml.org/sax/features/validation", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(file);
	}

	/**
	 * 将代表xml的document写入到执行的文件当中
	 * 
	 * @param dom
	 *            代表xml的Document对象
	 * @param file
	 *            文件完整路径
	 * @throws TransformerException
	 * @throws IOException
	 */
	public static void saveDocument(Document dom, String file) throws TransformerException, IOException {

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();

		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, dom.getDoctype().getPublicId());
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dom.getDoctype().getSystemId());

		DOMSource source = new DOMSource(dom);
		StreamResult result = new StreamResult();

		FileOutputStream outputStream = new FileOutputStream(file);
		result.setOutputStream(outputStream);
		transformer.transform(source, result);

		outputStream.flush();
		outputStream.close();
	}

	/**
	 * 删除某个节点
	 * 
	 * @param node
	 */
	public static void removeChildren(Node node) {
		NodeList childNodes = node.getChildNodes();
		int length = childNodes.getLength();
		for (int i = length - 1; i > -1; i--)
			node.removeChild(childNodes.item(i));
	}
}

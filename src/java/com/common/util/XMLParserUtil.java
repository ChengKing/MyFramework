package com.common.util;

import org.dom4j.Document;
import org.dom4j.Node;

/**
 * 
 * 解析XML数据 <BR>
 * 
 * @since chengking
 */
public class XMLParserUtil {

	/**
	 * 根据文档解析
	 * 
	 * @param document
	 *            文档元素
	 * @param value
	 *            元素
	 * @return 返回元素值
	 * 
	 * */
	public static String parseDocumentToString(Document document, String value) {
		return document.selectSingleNode(value) == null ? "" : document.selectSingleNode(value).getText();
	}

	/**
	 * 根据文档节点解析
	 * 
	 * @param node
	 *            节点
	 * @param value
	 *            元素
	 * @return 元素值
	 */
	public static String parseNodeToString(Node node, String value) {
		return node.selectSingleNode(value) == null ? "" : node.selectSingleNode("." + value).getText();
	}

	/**
	 * 该方法主要用来解析Document和Node不同类型的XML
	 * 
	 * @param obj
	 * @param value
	 * @return
	 */
	public static String parseObjToString(Object obj, String value) {
		if (obj instanceof Node) {
			Node node = (Node) obj;
			return node.selectSingleNode(value) == null ? "" : node.selectSingleNode("." + value).getText();
		}

		Document document = (Document) obj;
		return document.selectSingleNode(value) == null ? "" : document.selectSingleNode(value).getText();
	}
}

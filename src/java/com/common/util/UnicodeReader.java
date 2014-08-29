/*
 * Created: TRS信息技术股份有限公司@Aug 16, 2012 3:23:58 PM
 */
package com.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;

/**
 * 职责: 读取utf-8 BOM文件，去掉它的头内容<br>
 * 
 **/

public class UnicodeReader extends Reader {

	PushbackInputStream internalIn;
	InputStreamReader internalIn2 = null;
	String defaultEnc;

	private static final int BOM_SIZE = 4;

	/**
	 * @param in
	 *            输入的文件流
	 * @param defaultEnc
	 *            默认编码，如果输入流没有BOM标记，设为NULL就是用系统默认编码
	 */
	UnicodeReader(InputStream in, String defaultEnc) {
		internalIn = new PushbackInputStream(in, BOM_SIZE);
		this.defaultEnc = defaultEnc;
	}

	/**
	 * @return 返回默认编码
	 * @since ChengKing
	 */
	public String getDefaultEncoding() {
		return defaultEnc;
	}

	/**
	 * 获取编码
	 * 
	 * @return 文件编码
	 * @since ChengKing
	 */
	public String getEncoding() {
		if (internalIn2 == null)
			return null;
		return internalIn2.getEncoding();
	}

	/**
	 * 读取文件的头4个字节，检查是否是BOM标记。提取除BOM标记以外的文件。
	 * 
	 * @throws IOException
	 * @since ChengKing
	 */
	protected void init() throws IOException {
		if (internalIn2 != null)
			return;

		String encoding;
		byte bom[] = new byte[BOM_SIZE];
		int n, unread;
		n = internalIn.read(bom, 0, bom.length);

		if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
			encoding = "UTF-32BE";
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
			encoding = "UTF-32LE";
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
			encoding = "UTF-8";
			unread = n - 3;
		} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
			encoding = "UTF-16BE";
			unread = n - 2;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
			encoding = "UTF-16LE";
			unread = n - 2;
		} else {
			encoding = defaultEnc;
			unread = n;
		}

		if (unread > 0)
			internalIn.unread(bom, (n - unread), unread);

		if (encoding == null) {
			internalIn2 = new InputStreamReader(internalIn);
		} else {
			internalIn2 = new InputStreamReader(internalIn, encoding);
		}
	}

	/**
	 * @see java.io.Reader#close()
	 * @since ChengKing
	 */
	@Override
	public void close() throws IOException {
		init();
		internalIn2.close();
	}

	/**
	 * @see java.io.Reader#read(char[], int, int)
	 * @since ChengKing
	 */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		init();
		return internalIn2.read(cbuf, off, len);
	}

}

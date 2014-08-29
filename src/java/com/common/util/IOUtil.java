/*
 * Created: ChengKing@Mar 16, 2010 11:07:24 AM
 */
package com.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 输入输出流的读取、复制等操作. <br>
 * 
 */
public class IOUtil {

	static final int BUF_SIZE = 4096;

	/**
	 * 将输入流的内容按二进制方式复制到输出流. 由于输入输出流均为传入对象，因此本方法不关闭它们。
	 * 
	 * @param is
	 *            输入流
	 * @param os
	 *            输出流
	 * @throws IOException
	 * @since ChengKing @ Mar 16, 2010
	 */
	public static void copy(InputStream is, OutputStream os) throws IOException {
		copy(is, os, 0);
	}

	/**
	 * 
	 * @param is
	 * @param os
	 * @param listener
	 * @param totalSize
	 * @return
	 * @throws IOException
	 * @since ChengKing @ Apr 6, 2011
	 */
	public static long copy(InputStream is, OutputStream os,
			 long totalSize) throws IOException {
		return copy(is, os, BUF_SIZE, totalSize);
	}

	/**
	 * 
	 * @param is
	 * @param os
	 * @param listener
	 * @param totalSize
	 * @return
	 * @throws IOException
	 * @since ChengKing @ Apr 6, 2011
	 * @see org.apache.commons.net.io.Util#copyStream(InputStream, OutputStream,
	 *      int, long, org.apache.commons.net.io.CopyStreamListener)
	 */
	public static long copy(InputStream is, OutputStream os,
			int bufSize, long totalSize)
			throws IOException {
		AssertUtil.notNull(is, "input stream is null!");
		AssertUtil.notNull(os, "output stream is null!");
		long totalTransferred = 0;
		BufferedInputStream bis = new BufferedInputStream(is, bufSize);
		BufferedOutputStream bos = new BufferedOutputStream(os, bufSize);
		int len = 0;
		if (bufSize < 1024) {
			bufSize = 1024;
		}
		byte[] buf = new byte[bufSize];
		try {
			while ((len = bis.read(buf)) > 0) {
				bos.write(buf, 0, len);
				totalTransferred += len;
			}
		} finally {
			bos.flush();
		}
		return totalTransferred;
	}
	
	/**
	 * 将给定文件的内容按二进制方式复制到输出流. 由于输出流为传入对象, 因此本方法不关闭它.
	 * 
	 * @param file
	 * @param os
	 * @throws IOException
	 * @since ChengKing @ Mar 16, 2010
	 */
	public static void copy(File file, OutputStream os) throws IOException {
		FileUtil.assertFileCanRead(file);
		InputStream is = new FileInputStream(file);
		try {
			copy(is, os);
		} finally {
			CloseUtil.closeInputStream(is);
		}
	}

	/**
	 * 读取输入流，得到字符串构成的List. 由于输入流为传入对象, 因此本方法不关闭它.
	 * 
	 * @param is
	 * @param encoding
	 * @return
	 * @throws IOException
	 * @since ChengKing @ Sep 15, 2010
	 */
	public static List<String> loadText(InputStream is, String encoding)
			throws IOException {
		List<String> result = new ArrayList<String>();
		InputStreamReader isr = new InputStreamReader(is,
				StringHelper.avoidEmpty(encoding, "UTF-8"));
		BufferedReader br = new BufferedReader(isr);
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			result.add(line);
		}
		return result;
	}
}

/*
 * Created: liushen@Oct 14, 2010 8:35:44 PM
 */
package com.common.util;

import java.io.IOException;

import sun.org.mozilla.javascript.internal.WrappedException;
import de.schlichtherle.io.File;

/**
 * 对压缩文件常用操作的封装, 基于Truezip.
 * 
 * JDK对zip/jar/war等压缩文件的支持有很多局限，而且已经不打算再改进了，比如http://bugs.sun.com/bugdatabase/
 * view_bug.do? bug_id=4129445上就提到“the jar/zip code is already fragile enough
 * without adding a new dimension to the testing
 * matrix”；因此像ant等Toolkit自己对压缩文件的处理进行了各种增强功能的开发。
 * 
 * Truezip就是一个很好的替代者，功能强大且高效，使用说明见https://truezip.dev.java.net/manual-6.html
 */
public class TrueZipUtil {

	/**
	 * 复制文件, 不论文件是在目录还是zip格式的压缩文件中都支持; 并且会保留文件修改时间等属性; 如果目标文件已经存在,
	 * 则会覆盖(并且不论目标文件修改时间更早还是更晚). 如果文件位于压缩文件中，路径写法的例子：
	 * <ul>
	 * <li><code>masvod.war/WEB-INF/red5-web.properties</code></li>
	 * </ul>
	 * 
	 * @param srcPath
	 *            源文件的路径
	 * @param destPath
	 *            目标位置的路径
	 * @since ChengKing
	 */
	public static void copy(String srcPath, String destPath) {
		File srcFile = new File(srcPath);
		File destFile = new File(destPath);

		srcFile.setLastModified(System.currentTimeMillis());

		try {
			File.cp_p(srcFile, destFile);
			// 对于zip等压缩文件，写入时有cache，JVM退出后才会体现出来；因此加上以下处理，相当于flush的效果.
			if (destFile.isEntry()) {
				File.update(destFile.getEnclArchive());
			}
		} catch (IOException e) {
			throw new WrappedException(e);
		}
	}

//	/**
//	 * 
//	 * @param srcFile
//	 * @param destFile
//	 * @since liushen @ Nov 3, 2010
//	 */
//	public static void copy(java.io.File srcFile, java.io.File destFile) {
//		AssertUtil.notNull(srcFile, "srcFile is null");
//		AssertUtil.notNull(destFile, "destFile is null");
//		copy(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
//	}
//	Caused by: de.schlichtherle.io.ArchiveController$ArchiveFileNotFoundException: C:\Users\Administrator\AppData\Local\Temp\masvod.war (cannot read virtual root directory)
//	at de.schlichtherle.io.File.ensureNotVirtualRoot(File.java:2182)
//	at de.schlichtherle.io.Files.cp0(Files.java:403)
//	at de.schlichtherle.io.Files.cp(Files.java:385)
//	at de.schlichtherle.io.File.cp_p(File.java:4157)
//	at com.trs.dev4.jdk16.utils.TrueZipUtil.copy(TrueZipUtil.java:45)
//	... 27 more
	
	// public static void copy(InputStream is, OutputStream os) {
	// de.schlichtherle.io.Streams.cat(in, out) //
	// Streams目前仅为package可见，因此无法调用！Truezip 7中将开放
	// }

}

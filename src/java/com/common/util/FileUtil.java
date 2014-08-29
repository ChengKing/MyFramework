/*
 * Created: ChengKing@Dec 5, 2008 9:52:40 AM
 */
package com.common.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.common.constant.Const;
/**
 * 封装了文件操作（判断、创建、删除、移动、复制）相关的一些方法.<br>
 * 
 * @author TRS信息技术有限公司
 */
public class FileUtil {
	/**
	 *
	 */
	private final static Logger logger = Logger.getLogger(FileUtil.class);
	/**
	 * UTF-8编码格式
	 */
	private static final String UTF_8 = "UTF-8";

	/**
	 * 在Java中, 统一的文件(Windows/Unix)和URL的分隔符.
	 */
	public static final String UNIVERSAL_SEPARATOR = "/";

	/**
	 * 创建新文件, 如果父目录尚不存在，会先创建必要的目录结构.
	 * 
	 * @param f
	 *            文件对象
	 * @return 创建成功返回<code>true</code>；文件已存在则返回<code>false</code>.
	 * @throws Exception
	 *             出现I/O错误时(没有写入权限时也是报I/O错误：java.io.IOException: Permission
	 *             denied)
	 */
	public static boolean createNewFile(File f) {
		createFolder(f.getParent());
		try {
			return f.createNewFile();
		} catch (IOException e) {
			logger.error("create file [" + f + "] failed.", e);
			return false;
		}
	}

	/**
	 *
	 * @creator ChengKing @ Jan 23, 2010
	 * @see #createNewFile(File)
	 */
	public static boolean createNewFile(String sPath) {
		if (StringHelper.isEmpty(sPath)) {
			return false;
		}
		return createNewFile(new File(sPath));
	}

	/**
	 * 创建新目录, 如果父目录尚不存在，会先创建必要的目录结构.
	 * 
	 * @param dirPath
	 *            要创建的目录的路径
	 * @return 如果实际创建了目录, 则返回<code>true</code>; 否则返回<code>false</code>
	 *         （比如目录已经存在）.
	 * @since ChengKing @ Jan 22, 2010
	 * @see #createFolder(File)
	 */
	public static boolean createFolder(String dirPath) {
		File dir = new File(dirPath);
		return createFolder(dir);
	}

	/**
	 * 创建新目录, 如果父目录尚不存在，会先创建必要的目录结构.
	 * 
	 * @param dir
	 *            要创建的目录
	 * @return 如果实际创建了目录, 则返回<code>true</code>; 否则返回<code>false</code>
	 *         （比如目录已经存在）.
	 * @throws Exception
	 *             该路径已经被一个文件（非目录）占用，或者创建目录失败
	 * @since ChengKing @ Apr 20, 2010
	 */
	public static boolean createFolder(File dir) {
		if (dir.exists()) {
			if (dir.isDirectory()) {
				return false;
			}
			logger.error("create folder [" + dir
					+ "] failed, because it exists, but not a folder.");
			return false;
		}

		boolean ok = dir.mkdirs();
		if (ok) {
			return true;
		}
		logger.error("create folder [" + dir + "] failed.");
		return false;
	}

	/**
	 * 删除一个文件.
	 * 
	 * @param strFile
	 *            待删除文件的完整路径
	 * @see #deleteFile(File)
	 * @creator changpeng @ 2009-5-19
	 */
	public static boolean deleteFile(String strFile) {
		if (strFile == null) {
			return false;
		}
		return deleteFile(new File(strFile));
	}

	/**
	 * 删除一个文件.
	 * 
	 * @param file
	 *            待删除的文件
	 * @return 成功删除文件返回true，其他情况返回<code>false</code>(比如文件为空、不存在、是目录等情况)
	 * @since ChengKing @ Aug 6, 2010
	 */
	public static boolean deleteFile(File file) {
		if (file == null || false == file.isFile()) {
			return false;
		}
		return file.delete();
	}

	/**
	 * 断定所给路径存在，并返回完整路径.
	 * 
	 * @param path
	 *            表示所给路径的字符串
	 * @return 完整路径, 使用本操作系统表示形式；即：对于目录符，Windows上使用\而Linux和Unix上则使用/.
	 *         如果该路径是一个目录，则返回的字符串总以目录符结尾.
	 * @throws IllegalArgumentException
	 *             路径为null或者不存在时
	 */
	public static String assertExist(String path) {
		AssertUtil.notNull(path, "path is null!");
		File f = new File(path);
		if (f.exists()) {
			if (f.isDirectory()) {
				return StringHelper.smartAppendSuffix(f.getAbsolutePath(),
						File.separator);
			}
			return f.getAbsolutePath();
		}
		throw new IllegalArgumentException("the path [" + path + "] not exist!");
	}

	/**
	 * 判断目录是否存在
	 * 
	 * @param f
	 *            指定的文件
	 * @return
	 * @since ChengKing @ May 16, 2010
	 */
	public static File assertDirExists(File f) {
		if (f == null || false == f.isDirectory()) {
			logger.error("dir not exist: [" + f + "]");
		}
		return f;
	}

	/**
	 *
	 * @param path
	 * @return
	 * @since fangxiang @ May 23, 2010
	 */
	public static File assertIsDir(String path) {
		File f = new File(path);
		return assertDirExists(f);
	}

	/**
	 * 断言文件存在，否则抛出运行时异常.
	 * 
	 * @param f
	 *            文件对象
	 * @throws Exception
	 *             运行时异常; 文件不存在，或者该路径是一个目录
	 * @creator ChengKing @ Feb 20, 2010
	 */
	public static void assertFileExists(File f) {
		if (f == null || false == f.isFile()) {
			throw new IllegalArgumentException("file not exist: [" + f + "]");
		}
	}

	/**
	 * 断言文件大小大于0(隐含的断言是该文件必定存在)，否则抛出运行时异常.
	 * 
	 * @param f
	 *            文件对象
	 * @since ChengKing @ Jun 29, 2010
	 */
	public static void assertFileNotEmpty(File f) {
		assertFileExists(f);
		if (f.length() == 0) {
			logger.error("file exist but is zero size: [" + f
					+ "]");
		}
	}

	/**
	 * 断言给定目录下存在给定文件名的文件.
	 * 
	 * @param dirHome
	 * @param string
	 * @since ChengKing @ May 16, 2010
	 */
	public static void assertFileExists(File folder, String... filenames) {
		assertDirExists(folder);

		for (int i = 0; i < filenames.length; i++) {
			File file = new File(folder, filenames[i]);
			assertFileExists(file);
		}
	}

	/**
	 * 断言给定的文件可以被执行该JVM进程的用户读取(隐含该文件存在).
	 * 
	 * @param file
	 *            给定的文件
	 * @throws IOException
	 *             文件为<code>null</code>或不存在，或不能被执行该JVM进程的用户读取
	 * @since ChengKing @ Mar 16, 2010
	 */
	public static void assertFileCanRead(File file) {
		assertFileExists(file);
		if (false == file.canRead()) {
			throw new IllegalArgumentException("the file [" + file
					+ "] can not read by user [" + EnvUtil.getProcessUser()
					+ "]");
		}
	}

	/**
	 * 返回文件的扩展名(包含.), 如果不存在扩展名, 则返回<code>null</code>.
	 */
	public static String getFileExtensionWithDot(File f) {
		if (f == null) {
			throw new NullPointerException("the file object is null!");
		}
		String filename = f.getName();
		return getFileExtensionWithDot(filename);
	}

	/**
	 * 返回文件名的扩展名(包含.), 如果不存在扩展名, 则返回<code>null</code>.
	 * 
	 * @since ChengKing @ Dec 26, 2011
	 */
	public static String getFileExtensionWithDot(String fileName) {
		AssertUtil.notNullOrEmpty(fileName, "the filename is empty!");
		int lastDotPosition = fileName.lastIndexOf('.');
		if (lastDotPosition == -1) {
			return null;
		}
		return fileName.substring(lastDotPosition);
	}

	/**
	 * 返回文件的扩展名(不包含.), 如果不存在扩展名, 则返回<code>null</code>.
	 */
	public static String getFileExtension(File f) {
		AssertUtil.notNull(f, "the file object is null!");
		String filename = f.getName();
		return getFileExtension(filename);
	}

	/**
	 * 返回文件名的扩展名(不包含.), 如果不存在扩展名, 则返回<code>null</code>.
	 */
	public static String getFileExtension(String fileName) {
		AssertUtil.notNullOrEmpty(fileName, "the filename is empty!");
		int lastDotPosition = fileName.lastIndexOf('.');
		if (lastDotPosition == -1) {
			return null;
		}
		return fileName.substring(lastDotPosition + 1);
	}

	/**
	 * 返回所给文件名不包含扩展名的文件名(扩展名以最后一个.开始计算), 如果不存在扩展名, 则返回原始的文件名. 比如：对
	 * <code>"test.avi"</code>则返回<code>"test"</code>.
	 * 
	 * @param fileName
	 *            所给文件名
	 * @return 不包含扩展名的文件名;
	 */
	public static String getFileNameWithoutExt(String fileName) {
		AssertUtil.notNull(fileName, "the fileName is null.");
		fileName = fileName.trim();
		if (fileName.length() == 0) {
			throw new IllegalArgumentException("the fileName is empty.");
		}

		int lastDotPosition = fileName.lastIndexOf('.');
		if (lastDotPosition == -1) {
			return fileName;
		}
		return fileName.substring(0, lastDotPosition);
	}

	/**
	 * 返回文件的文件名(不包含扩展名, 扩展名以最后一个.开始计算), 如果不存在扩展名, 则返回原始的文件名.
	 * 
	 * @param file
	 *            所给文件
	 * @return 不包含扩展名的文件名
	 * @since ChengKing @ Jun 21, 2010
	 */
	public static String getFileNameWithoutExt(File file) {
		AssertUtil.notNull(file, "the file is null.");
		return getFileNameWithoutExt(file.getName());
	}

	/**
	 * 返回文件的文件名，其中对扩展名全部转为小写字母，其他部分则保持不变.
	 * 
	 * @param uniFile
	 *            文件对象
	 * @return 扩展名小写化处理后的文件名
	 * @since ChengKing
	 */
	public static String getFileNameWithLowerCaseExt(File uniFile) {
		AssertUtil.notNull(uniFile, "the IFile is null.");
		String fileName = uniFile.getName();
		String extWithDot = getFileExtensionWithDot(fileName);
		if (extWithDot == null) {
			return getFileNameWithoutExt(fileName);
		}
		return getFileNameWithoutExt(fileName) + extWithDot.toLowerCase();
	}

	/**
	 * 返回父目录的路径.
	 * 
	 * @return 父目录的路径, 总是以"/"结束. 如果父目录不存在, 则返回<code>null</code>.
	 */
	public static String getParentPath(File f) {
		if (f == null) {
			return null;
		}

		File parentFile = f.getParentFile();
		if (parentFile == null) {
			return null;
		}

		String parentPath = parentFile.getAbsolutePath();
		if (false == parentPath.endsWith("/")) {
			parentPath += "/";
		}
		return parentPath;
	}

	/**
	 * 以二进制模式复制文件, 适用于所有类型的文件.
	 * 
	 * @see #copyFile1(File, File)
	 * @creator fangxiang @ May 8, 2009
	 */
	public static void copyFile(String fSrc, String fDst) {
		copyFile1(new File(fSrc), new File(fDst));
	}

	/**
	 * 以二进制模式、<code>IOUtil</code>默认的缓冲区大小来复制文件, 详情参阅
	 * {@link #copyFile(File, File, int, IProgressListener)}。
	 * 
	 * @since ChengKing @ Apr 7, 2011
	 */
	public static long copyFile1(File fSrc, File fDst) {
		return copyFile(fSrc, fDst, IOUtil.BUF_SIZE);
	}

	/**
	 * 以二进制模式复制文件, 适用于所有类型的文件；目标文件存在时是否覆盖由参数 alwaysOverwrite 决定.
	 * 
	 * @since ChengKing @ Dec 19, 2012
	 * @see #copyFile(File, File, boolean, int, IProgressListener)
	 */
	public static long copyFile(File fSrc, File fDst, boolean alwaysOverwrite) {
		return copyFile(fSrc, fDst, alwaysOverwrite, IOUtil.BUF_SIZE);
	}

	/**
	 * 以二进制模式复制文件, 适用于所有类型的文件；；同时可通过{@link IProgressListener}
	 * 获取复制进度；该方法总是覆盖同名的目标文件。
	 * 
	 * @since ChengKing @ Jun 29, 2012
	 * @see #copyFile(File, File, boolean, int, IProgressListener)
	 */
	public static long copyFile(File fSrc, File fDst, int bufBytes) {
		return copyFile(fSrc, fDst, true, bufBytes);
	}

	/**
	 * 以二进制模式复制文件, 适用于所有类型的文件；目标文件存在时是否覆盖由参数 alwaysOverwrite 决定；同时可通过
	 * {@link IProgressListener} 获取复制进度.
	 * 
	 * @param fSrc
	 *            源文件
	 * @param fDst
	 *            目标; 如果fDst是一个已经存在的目录，则复制到该目录下, 文件名和源文件相同。
	 * @param alwaysOverwrite
	 *            是否总是覆盖同名文件；<code>true</code>为总是覆盖，否则当目标文件大小不为0时，不进行覆盖
	 * @param bufBytes
	 *            复制时所用的缓冲区大小，单位为字节
	 * @param listener
	 *            用于汇报进度的监听器；为<code>null</code>则不汇报进度
	 * @return 实际复制的字节数
	 * @since ChengKing @ Dec 19, 2012
	 * @see IOUtil#copy(InputStream, OutputStream, int, IProgressListener, long)
	 */
	static long copyFile(File fSrc, File fDst, boolean alwaysOverwrite, int bufBytes) {
		AssertUtil.notNull(fSrc, "src file is null!");
		AssertUtil.notNull(fDst, "dest file is null!");
		if (false == fSrc.isFile()) {
			throw new IllegalArgumentException("the fSrc [ " + fSrc
					+ " ] is NOT a file(or not exist)!");
		}
		if (false == alwaysOverwrite) {
			if (fDst.isDirectory()) {
				fDst = new File(fDst, fSrc.getName());
			}
			if (fDst.isFile() && fDst.length() > 0) {
				return 0;
			}
		}
		if (fDst.isDirectory()) {
			fDst = new File(fDst, fSrc.getName());
		}
		if (false == fDst.exists()) {
			createNewFile(fDst);
		}
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(fSrc);
			fos = new FileOutputStream(fDst);
			return IOUtil.copy(fis, fos, bufBytes, fSrc.length());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			CloseUtil.closeOutputStream(fos);
			CloseUtil.closeInputStream(fis);
		}
	}

	/**
	 * 以二进制模式复制文件, 适用于所有类型的文件； 支持从源文件到目标文件和从源文件到目标目录两种情况.
	 * 
	 * @see #copyFile(File, File, IProgressListener)
	 * @since ChengKing
	 */
	public static void copyFile(File fSrc, File fDst) {
		copyFile1(fSrc, fDst);
	}

	/**
	 * 从源目录向目标目录复制指定的文件.
	 * 
	 * @param srcDir
	 *            目录，且必须已存在
	 * @param dstDir
	 *            目录，且必须已存在
	 * @param filenames
	 * @since ChengKing @ Apr 6, 2012
	 */
	public static void copyFiles(File srcDir, File dstDir, String... filenames) {
		assertDirExists(srcDir);
		assertDirExists(dstDir);

		for (int i = 0; i < filenames.length; i++) {
			File file = new File(srcDir, filenames[i]);
			if (file.isFile()) {
				copyFile1(file, dstDir);
			}
		}
	}

	/**
	 *
	 * @see #copyFiles(File, File, String...)
	 * @since ChengKing @ Apr 6, 2012
	 */
	public static void copyFiles(String srcDir, String dstDir,
			String... filenames) {
		copyFiles(new File(srcDir), new File(dstDir), filenames);
	}

	/**
	 * 将源目录下的文件全部复制到fDst目录下(如果fDst目录不存在则先创建); 如果源目录是空目录(文件数为0)，则不创建目标目录.
	 * 
	 * @param fSrc
	 *            源目录(或源文件)
	 * @param fDst
	 *            目标目录
	 * @creator chuchanglin @ 2010-1-8
	 */
	public static void copyIntoFolder(File fSrc, File fDst) {
		AssertUtil.notNull(fSrc, "fSrc is null.");
		AssertUtil.notNull(fDst, "fDst is null.");

		if (fSrc.isFile()) {
			copyFile1(fSrc, fDst);
			return;
		}

		File[] srcFiles = fSrc.listFiles();
		if (srcFiles == null || srcFiles.length == 0) {
			return;
		}

		if (false == fDst.isDirectory()) {
			if (fDst.exists()) {
				logger.error("[" + fDst + "] 已存在，但不是目录!");
			}
			fDst.mkdirs();
		}
		String destPath = fDst.getAbsolutePath() + File.separatorChar;
		for (File srcFile : srcFiles) {
			File destFile = new File(destPath + srcFile.getName());
			if (srcFile.isDirectory()) {
				copyIntoFolder(srcFile, destFile);
				continue;
			}
			copyFile1(srcFile, destFile);
		}
	}

	/**
	 * 读取文本文件的第一行.
	 * 
	 * @param file
	 * @param charsetName
	 * @return
	 * @throws IOException
	 * @since ls@08.0820
	 */
	public static String loadFirstLine(File file, String charsetName)
			throws IOException {
		if (file == null || false == file.isFile()) {
			throw new IllegalArgumentException("the file [ " + file
					+ " ] is NOT a file!");
		}
		if (file.length() > Short.MAX_VALUE) {
			throw new IllegalArgumentException(
					"the file too large! size(in bytes): " + file.length());
		}
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			isr = new InputStreamReader(new FileInputStream(file), charsetName);
			br = new BufferedReader(isr);
			return br.readLine();
		} catch (IOException e) {
			throw e;
		} finally {
			CloseUtil.closeReader(br);
			CloseUtil.closeReader(isr);
		}
	}

	/**
	 * 移动文件位置; 此方法支持在Linux/Unix下跨分区移动文件.
	 * 
	 * @throws IllegalArgumentException
	 *             当参数为<code>null</code>, 或源文件不存在, 或目标文件名已存在
	 * @throws IOException
	 *             当对目标位置没有写入权限
	 * @creator ChengKing @ Aug 27, 2008
	 */
	public static void moveFile(File fSrc, File fDst) throws IOException {
		if (fSrc == null || fDst == null) {
			throw new IllegalArgumentException(
					"one of the two file object is null: fSrc=" + fSrc
							+ "; fDst=" + fDst);
		}
		if (false == fSrc.isFile()) {
			throw new IllegalArgumentException("the fSrc [ " + fSrc
					+ " ] is NOT a file!");
		}
		// 目标文件名(文件或目录)已存在, 不允许覆盖
		if (fDst.exists()) {
			throw new IllegalArgumentException("the fDst [ " + fSrc
					+ " ] is already exist!");
		}
		// 先尝试用File.renameTo()方法移动文件
		boolean success = fSrc.renameTo(fDst);
		if (success) {
			return;
		}
		// 可能是目标位置没有权限, 抛出异常
		File destDir = fDst.getParentFile();
		if (false == destDir.canWrite()) {
			throw new IOException("user [" + System.getProperty("user.name")
					+ "] not have write permission on dest dir [" + destDir
					+ "]!");
		}
		// 可能是在Linux/Unix上跨分区移动, File.renameTo()方法不支持此操作, 因此自行实现(先复制然后删除源文件).
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(fSrc);
			fos = new FileOutputStream(fDst);
			byte[] buf = new byte[8192];
			int i = 0;
			while ((i = fis.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
			fSrc.delete();
		} catch (IOException e) {
			throw e;
		} finally {
			CloseUtil.closeInputStream(fis);
			CloseUtil.closeOutputStream(fos);
		}
	}

	/**
	 * 移动整个文件夹
	 * 
	 * @param fSrc
	 * @param fDst
	 * @throws IOException
	 * @creator fengjianhua @ 2009-4-14
	 */
	public static void moveFolder(File fSrc, File fDst) throws IOException {
		if (fSrc == null || fDst == null) {
			throw new IllegalArgumentException(
					"one of the two file object is null!");
		}
		if (false == fSrc.isDirectory()) {
			throw new IllegalArgumentException("the fSrc [ " + fSrc
					+ " ] is NOT a directory!");
		}

		if (false == fDst.isDirectory()) {
			fDst.mkdirs();
		}

		String destPath = fDst.getAbsolutePath() + File.separatorChar;
		File[] srcFiles = fSrc.listFiles();
		for (File srcFile : srcFiles) {
			File destFile = new File(destPath + srcFile.getName());
			if (srcFile.isDirectory()) {
				moveFolder(srcFile, destFile);
				continue;
			}
			moveFile(srcFile, destFile);
		}
		fSrc.delete();
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param file
	 * @return
	 * @creator fangxiang @ May 8, 2009
	 */
	public static boolean fileExists(String fileName) {
		if (fileName == null)
			return false;
		File file = new File(fileName);
		return file.isFile();
	}

	/**
	 * 计算文件内容长度
	 * 
	 * @param file
	 *            文件
	 * @return 文件内容的长度
	 * @creator fangxiang @ May 8, 2009
	 */
	public static long fileLength(String fileName) {
		File file = new File(fileName);
		return file.length();
	}

	/**
	 * 以UTF-8编码将文本文件的内容读出到字符串中，换行符保留
	 * 
	 * @param file
	 *            文件名
	 * @return 文本文件的内容
	 * @throws Exception
	 *             文件操作出现错误时
	 * @creator changpeng @ 2009-5-19
	 * @see #readFileText(String, String)
	 */
	public static String getFileText(String file) {
		return readFileText(file, UTF_8);
	}

	/**
	 *
	 * @param file
	 * @param encoding
	 * @return
	 * @since ChengKing @ Sep 15, 2010
	 */
	public static List<String> loadText(File file, String encoding) {
		assertFileCanRead(file);
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			return IOUtil.loadText(in, encoding);
		} catch (IOException e) {
			logger.error("load text failed !", e);
			return new ArrayList<String>();
		} finally {
			CloseUtil.closeInputStream(in);
		}
	}

	/**
	 * @param filePath
	 * @param lines
	 * @creator fengjianhua @ 2009-10-22
	 * @deprecated ChengKing@Jun 29, 2010: 放错地方了，将删除!
	 */
	@Deprecated
	public static void forceWriteFile(String filePath, List<String> lines)
			throws Exception {
		FileWriter fWriter = new FileWriter(filePath);
		PrintWriter pWriter = new PrintWriter(fWriter);
		for (int i = 0; i < lines.size(); i++) {
			pWriter.println(lines.get(i));
		}
		try {
			fWriter.close();
		} catch (IOException e) {
		}
		pWriter.close();
	}

	/**
	 * 将给定的文本写入文件中; 如果该文件已存在, 则覆盖已有内容.
	 * 
	 * @param content
	 *            要写入的文本内容
	 * @param file
	 *            往哪个文件写
	 * @since ChengKing @ Oct 11, 2010
	 */
	public static void writeTextToFile(String content, File file) {
		writeTextToFile(content, file, UTF_8, false);
	}

	/**
	 * 将给定的文本写入文件中; 以新行加到最后.
	 * 
	 * @param content
	 *            要写入的文本内容
	 * @param file
	 *            往哪个文件写
	 * @since ChengKing @ Oct 14, 2010
	 */
	public static void appendTextToFile(String content, File file) {
		AssertUtil.notNull(file, "file is null");
		if (file.isDirectory()) {
			throw new IllegalArgumentException(
					"cannot write text to a directory! (" + file + ")");
		}
		if (file.length() == 0) {
			writeTextToFile(content, file, UTF_8, true);
		} else {
			writeTextToFile(Const.NEWLINE + content, file, UTF_8, true);
		}
	}

	/**
	 * 将给定的文本写入文件中; 如果该文件已存在, 则覆盖已有内容.
	 * 
	 * @param content
	 *            要写入的文本内容
	 * @param file
	 *            往哪个文件写
	 * @param encoding
	 *            文件的编码
	 * @param append
	 *            是否为追加模式
	 * @since ChengKing @ Oct 11, 2010
	 */
	static void writeTextToFile(String content, File file, String encoding,
			boolean append) {
		OutputStream fos = null;
		OutputStreamWriter osw = null;
		try {
			fos = new FileOutputStream(file, append);
			osw = new OutputStreamWriter(fos, encoding);
			osw.write(content);
		} catch (Throwable e) {
			logger.error("write text failed !",e);
		} finally {
			CloseUtil.closeWriter(osw);
			CloseUtil.closeOutputStream(fos);
		}
	}

	/**
	 * 替换文本文件中匹配的内容；假定该文件为UTF-8编码.
	 * 
	 * @see #replaceByToken(File, String, String, String)
	 */
	public static void replaceByToken(File file, String matchedToken,
			String replacement) {
		replaceByToken(file, matchedToken, replacement, UTF_8);
	}

	/**
	 * 替换文本文件中匹配的内容.
	 * 
	 * @param file
	 *            要替换内容的文本文件
	 * @param matchedToken
	 *            要替换什么内容
	 * @param replacement
	 *            替换内容
	 * @param encoding
	 *            文件的编码
	 * @since ChengKing @ Oct 19, 2010
	 */
	public static void replaceByToken(File file, String matchedToken,
			String replacement, String encoding) {
		String content = readFileText(file.getAbsolutePath(), encoding);
		String replaced = StringHelper.replaceStr(content, matchedToken,
				replacement);
		writeTextToFile(replaced, file, encoding, false);
	}

	/**
	 * 判断目录是否存在
	 * 
	 * @param path
	 *            待校验的目录
	 * @return true表示目录存在，false表示目录不存在
	 * @creator chuchanglin @ 2009-10-21
	 */
	public static boolean dirExists(String path) {
		if (StringHelper.isEmpty(path))
			return false;
		return new File(path).exists();
	}

	/**
	 * @param sourceFile
	 * @return
	 * @creator ChengKing @ Jan 27, 2010
	 */
	public static String formatPath(File sourceFile) {
		if (sourceFile == null) {
			return null;
		}
		return formatPath(sourceFile.getAbsolutePath());
	}

	/**
	 * 格式化路径：目录层级间的分割符统一由<code>/</code>表示; 目录统一以<code>/</code>结束;
	 * .或..的简略形式则转换为完整形式.
	 * 
	 * @param physicalPath
	 * @return
	 * @creator ChengKing @ Jan 23, 2010
	 */
	public static String formatPath(String physicalPath) {
		if (StringHelper.isEmpty(physicalPath)) {
			return null;
		}

		if (".".equals(physicalPath)) {
			physicalPath = getCurrentWorkingDir();
		} else if ("..".equals(physicalPath)) {
			physicalPath = getParentDir();
		}

		String formerPath = physicalPath.replace('\\', '/');
		File file = new File(formerPath);
		if (file.isDirectory()) {
			if (false == formerPath.endsWith("/")) {
				formerPath += "/";
			}
		}
		return formerPath;
	}

	/**
	 *
	 * @param physicalPath
	 * @return
	 * @creator ChengKing @ Jan 25, 2010
	 */
	public static String toNativePath(String physicalPath) {
		String formattedPath = formatPath(physicalPath);
		if (formattedPath == null) {
			return null;
		}

		formattedPath = formattedPath.replace('/', File.separatorChar);
		formattedPath = formattedPath.replace('\\', File.separatorChar);
		return formattedPath;

	}

	/**
	 *
	 * @param folder
	 * @param recursive
	 * @return
	 * @since ChengKing @ May 16, 2010
	 */
	public static List<File> listFilesInDir(File folder, boolean recursive) {
		assertDirExists(folder);
		File[] aryFiles = folder.listFiles();

		List<File> files = new ArrayList<File>();
		for (int i = 0; i < aryFiles.length; i++) {
			if (aryFiles[i].isFile()) {
				files.add(aryFiles[i]);
			} else if (aryFiles[i].isDirectory()) {
				if (recursive) {
					files.addAll(listFilesInDir(aryFiles[i], recursive));
				}
			}
		}
		return files;
	}

	/**
	 * 根据扩展名得到指定目录下的文件列表，不包含子目录下的文件.
	 * 
	 * @param folder
	 *            目录
	 * @param exts
	 *            文件扩展名(不区分大小写); 如果不指定，则列出全部文件.
	 * @return 文件列表
	 * @since lichuanjiao @ 2011-1-19
	 * @rewrite ChengKing @ Apr 6, 2011
	 */
	public static List<File> listFilesInDir(File folder, final String... exts) {
		File[] arrFile;
		if (exts.length == 0) {
			arrFile = folder.listFiles();
		} else {
			arrFile = folder.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					// ChengKing@Dec 26, 2011: 都转为小写再比较
					for (String ext : exts) {
						if (StringHelper.isEmpty(ext)) {
							continue;
						}
						String acutalExtInLowerCase = pathname.getName()
								.toLowerCase();
						if (acutalExtInLowerCase.endsWith(ext.toLowerCase())) {
							return true;
						}
					}
					return false;
				}
			});
		}

		return Arrays.asList(arrFile);
	}

	/**
	 * 根据所给的文件集，列出同名(但后缀名不同)的所有文件.
	 * 
	 * 目前只能支持一个后缀
	 * 
	 * @param dirLocation
	 * @param encoding
	 * @param refs
	 * @param exts
	 *            文件扩展名(不区分大小写); 如果不指定，则列出全部文件.
	 * @return
	 * @since ChengKing @ Apr 21, 2011
	 */
	public static List<File> listSamenameFiles(String dirLocation,
			String encoding, List<File> refs, String... exts) {
		List<File> files = listFiles(dirLocation, encoding, exts);
		if (refs == null || refs.size() == 0) {
			return files;
		}
		List<File> result = new ArrayList<File>();
		for (File iFile : refs) {
			File silimarFile = findSilimarNameFile(files, iFile);
			if (silimarFile != null) {
				result.add(silimarFile);
			}
		}
		return result;
	}

	/**
	 *
	 * @param fileNamePrefix
	 * @param ext
	 * @return
	 * @since ChengKing @ Jan 10, 2012
	 */
	public static List<File> listFilesByPrefixAndSuffix(String dir,
			final String fileNamePrefix, final String ext) {
		AssertUtil.notNull(dir, "dir is null!");
		File fDir = new File(dir);
		if (false == fDir.isDirectory()) {
			throw new RuntimeException("not exist or not a directory: ["
					+ dir + "]");
		}
		// ChengKing@Jan 10, 2012: 特别注意，这里不要做trim
		if (fileNamePrefix == null || fileNamePrefix.length() == 0) {
			return new ArrayList<File>();
		}

		File[] arrFile = fDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				String fileName = file.getName();
				String actualExtWithDot = getFileExtensionWithDot(file);
				if (false == fileName.startsWith(fileNamePrefix)) {
					return false;
				}
				return StringHelper.equalIngoreCaseAndSpace(ext,
						actualExtWithDot);
			}

		});

		return Arrays.asList(arrFile);
	}

	/**
	 * @param files
	 * @param iFile
	 * @return
	 * @since ChengKing @ Apr 21, 2011
	 */
	private static File findSilimarNameFile(List<File> files, File refFile) {
		for (File iFile : files) {
			if (sameNameExcludeSuffix(iFile, refFile)) {
				return iFile;
			}
		}
		return null;
	}

	/**
	 * 两个文件是否主文件名相同但扩展名不同.
	 * 
	 * @since ChengKing @ Apr 21, 2011
	 */
	private static boolean sameNameExcludeSuffix(File iFile, File refFile) {
		if (iFile == null || refFile == null) {
			return false;
		}
		if (ObjectUtil.equals(iFile.getName(), refFile.getName())) {
			return false;
		}
		String mainName = getFileNameWithoutExt(iFile.getName());
		String refName = getFileNameWithoutExt(refFile.getName());
		return mainName.equals(refName);
	}

	/**
	 * @param dirLocation
	 * @param exts
	 *            文件扩展名(不区分大小写); 如果不指定，则列出全部文件.
	 * @return
	 * @since ChengKing @ Apr 6, 2011
	 */
	public static List<File> listFiles(String dirLocation, String... exts) {
		List<File> files = FileUtil.listFilesInDir(new File(dirLocation), exts);
		List<File> wrappers = new ArrayList<File>(files.size());
		for (File file : files) {
			wrappers.add(file);
		}
		return wrappers;
	}
	
	/**
	 * @param dirLocation
	 * @return
	 * @since ChengKing @ Apr 6, 2011
	 */
	public static List<File> listFiles(String dirLocation, String encoding,
			String... exts) {
		AssertUtil.notNullOrEmpty(dirLocation, "dirLocation");
		if (FileUtil.dirExists(dirLocation)) {
			return listFiles(dirLocation, exts);
		}
		URL url;
		try {
			url = new URL(dirLocation);
		} catch (MalformedURLException e) {
			throw new RuntimeException("the protocol unsupprted, url: "
					+ dirLocation, e);
		}

		if (UrlUtil.isFtp(url)) {
			String host = UrlUtil.getHost(url);
			String remoteDir = UrlUtil.getRemoteDir(url);
			String userName = UrlUtil.getUserName(url);
			String passwd = UrlUtil.getPassword(url);
			int port = UrlUtil.getPort(url);
			return FTPUtil.list(host, port, userName, passwd, encoding,
					remoteDir, exts);
		}
		throw new RuntimeException("the protocol " + url.getProtocol()
				+ " unsupprted, url: " + dirLocation);
	}

	/**
	 * 列出一级子目录.
	 * 
	 * @param folder
	 * @return
	 * @since ChengKing @ May 16, 2010
	 */
	public static List<File> listSubFolder(File folder) {
		assertDirExists(folder);
		List<File> files = new ArrayList<File>();

		File[] aryFiles = folder.listFiles();
		for (int i = 0; i < aryFiles.length; i++) {
			if (aryFiles[i].isDirectory()) {
				files.add(aryFiles[i]);
			}
		}
		return files;

	}

	/**
	 * 删除文件夹；如果文件夹并不存在，则忽略.
	 * 
	 * @param dirPath
	 * @creator ChengKing @ Feb 18, 2010
	 */
	public static void deleteFolder(String dirPath) {
		deleteFolder(new File(dirPath));
	}

	/**
	 * 删除文件夹；如果文件夹并不存在，则忽略.
	 */
	public static void deleteFolder(File file) {
		if (file == null) {
			throw new IllegalArgumentException("file object is null!");
		}
		if (false == file.exists()) {
			return;
		}
		if (false == file.isDirectory()) {
			throw new IllegalArgumentException("the file [ " + file
					+ " ] is NOT a directory!");
		}
		File[] childFiles = file.listFiles();
		for (File childFile : childFiles) {
			if (childFile.isFile()) {
				childFile.delete();
			} else {
				deleteFolder(childFile);
			}
		}
		file.delete();
	}

	/**
	 * 文件重命名
	 * 
	 * @param frameFile
	 * @param dstFilePath
	 * @return
	 * @creator fengjianhua @ 2009-9-22
	 */
	public static boolean rename(File file, String newName) {
		if (!file.exists()) {
			return false;
		}
		String dstPath = file.getParent() + File.separator + newName;
		return file.renameTo(new File(dstPath));
	}

	/**
	 * 获取上层目录路径,注：返回值的最后没有"/"
	 * 
	 * @param path文件路径
	 * @param floorNum向上的层数
	 * @return
	 * @creator chuchanglin @ 2010-1-5
	 */
	public static String getParentDirPath(String path, int floorNum) {
		// 判断路径
		if (!dirExists(path) && !fileExists(path))
			logger.error("path does not exist!path is:" + path);

		// 去除空格
		try {
			path = URLDecoder.decode(path, UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < floorNum; i++) {
			path = getParentPath(new File(path));
		}

		return path;
	}

	/**
	 * 按顺序拼接路径，适用于文件系统(Windows/Linux)和URL；路径分隔符统一为<code>/</code>.
	 * 
	 * @param parts
	 *            构成路径的各部分；需要按照先后顺序.
	 * @return 字符串表示的路径
	 * @since ChengKing @ Aug 27, 2010
	 */
	public static String buildUniversalPath(String base, String... parts) {
		if (StringHelper.isEmpty(base)) {
			return null;
		}
		base = base.trim();
		StringBuilder sb = new StringBuilder(base);
		if (false == (base.endsWith("/") || base.endsWith("\\"))) {
			sb.append("/");
		}
		for (String part : parts) {
			if (StringHelper.isEmpty(part)) {
				continue;
			}
			part = part.trim();
			if (part.endsWith("/") || part.endsWith("\\")) {
				sb.append(part);
			}
			sb.append(part).append("/");
		}
		return sb.toString();
	}

	// ============ 以下为JVM通用信息获取方法 ============
	/**
	 * 获取启动程序时的工作目录.
	 * 
	 * @return 启动程序的工作目录.
	 */
	public static String getCurrentWorkingDir() {
		return System.getProperty("user.dir");
	}

	/**
	 * 获取启动程序时的工作目录的父目录.
	 */
	public static String getParentDir() {
		String currDir = System.getProperty("user.dir");
		File file = new File(currDir);
		return file.getParent();
	}

	/**
	 * 获取登录操作系统的当前用户的主目录.
	 */
	public static String getOSUserHomeDir() {
		return System.getProperty("user.home");
	}

	/**
	 * 获取JVM的临时目录.
	 */
	public static String getOSTempDir() {
		return formatPath(System.getProperty("java.io.tmpdir"));
	}

	/**
	 * 返回该文件相对于给定起始目录的子目录的路径.
	 * 
	 * @param rootPath
	 *            起始目录
	 * @param f
	 *            该文件(也可以是一个目录)
	 * @return 文件f相对于起始目录的子目录的路径, 总是以"/"结束. 如果文件或目录不存在, 或文件不在该起始目录下, 则返回
	 *         <code>null</code>.
	 */
	public static String getSubPath(String rootPath, File f) {
		if (rootPath == null || f == null) {
			return null;
		}

		File rootFile = new File(rootPath);
		if (false == rootFile.isDirectory()) {
			return null;
		}

		if (false == f.exists()) {
			return null;
		}

		String fullPath = getParentPath(f);
		// 将Windows路径符"\"替换为Java统一的路径符"/"
		fullPath = fullPath.replace('\\', '/');
		rootPath = rootPath.replace('\\', '/');
		return StringHelper.removeBeginPart(fullPath, rootPath);
	}

	/**
	 * 判断文件是否属于某个祖先目录的目录结构下.
	 * 
	 * @param file
	 *            文件
	 * @param ancestorDirPath
	 *            该祖先目录
	 * @return 文件存在并且属于该祖先目录, 则返回<code>true</code>, 否则返回<code>false</code>.
	 * @creator ChengKing @ Jan 26, 2010
	 */
	public static boolean belongsTo(File file, String ancestorDirPath) {
		if (file == null || ancestorDirPath == null) {
			return false;
		}
		File ancestorDir = new File(ancestorDirPath);
		if (false == ancestorDir.isDirectory()) {
			return false;
		}
		for (File parent = file.getParentFile(); parent != null; parent = parent
				.getParentFile()) {
			if (ancestorDir.equals(parent)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断某路径是否属于某个祖先路径下；该方法仅从路径形式上判断，和文件实际是否存在无关.
	 * 
	 * @param fullPath
	 *            路径
	 * @param ancestorDirPath
	 *            祖先路径
	 * @return 路径属于该祖先路径, 则返回<code>true</code>, 否则返回<code>false</code>.
	 * @creator ChengKing @ Jan 27, 2010
	 */
	public static boolean pathBelongsTo(String fullPath, String ancestorDirPath) {
		if (fullPath == null || ancestorDirPath == null) {
			return false;
		}
		return fullPath.indexOf(ancestorDirPath) == 0;
	}

	/**
	 * 获取路径的剩余空间
	 * 
	 * @param physicalPath
	 *            路径
	 * @return 剩余空间量
	 * @creator ChengKing @ Feb 5, 2010
	 */
	public static long getFreeSpace(String physicalPath) {
		if (physicalPath == null) {
			return 0;
		}
		File partition = new File(physicalPath);
		return partition.getFreeSpace();
	}

	/**
	 * 获取路径的总空间
	 * 
	 * @param physicalPath
	 *            物理路径
	 * @return 剩余空间量
	 * @creator ChengKing @ Feb 5, 2010
	 */
	public static long getTotalSpace(String physicalPath) {
		if (physicalPath == null) {
			return 0;
		}
		File partition = new File(physicalPath);
		return partition.getTotalSpace();
	}

	/**
	 * 按流的方式保存文件，如果目录不存在则自动创建。 传入的InputStream is在方法内不会关闭，需由调用者关闭is。
	 * 
	 * @param filePath
	 *            文件路径
	 * @param is
	 *            文件流
	 * @since fangxiang @ Oct 13, 2010
	 */
	public static void saveFile(String filePath, InputStream is) {
		FileOutputStream fos = null;
		try {
			// 创建文件
			if (FileUtil.createNewFile(filePath)) {
				fos = new FileOutputStream(filePath);
				byte[] buffer = new byte[1024 * 1024];
				int byteRead = 0;
				while ((byteRead = is.read(buffer)) != -1) {
					fos.write(buffer, 0, byteRead);
					fos.flush();
				}
			} else {
				logger.error("file (" + filePath + ") can't created.");
			}
		} catch (IOException ex) {
			logger.error(
					"file(" + filePath + ") save failed:" + ex.getMessage(), ex);
		} finally {
			CloseUtil.closeOutputStream(fos);
		}
	}

	/**
	 * 保存文本文件到指定位置，如果目录不存在则自动创建
	 * 
	 * @param filePath
	 *            文件路径
	 * @param textContent
	 *            文本文件内容
	 * @since fangxiang @ Oct 13, 2010
	 */
	public static void saveFile(String filePath, String textContent) {
		if (!FileUtil.fileExists(filePath)) { // 文件不存在时尝试创建新文件
			FileUtil.createNewFile(filePath);
		}
		// 保存到文件中
		FileWriter fw = null;
		try {
			fw = new FileWriter(filePath);
			fw.write(textContent);
			fw.flush();
		} catch (IOException ex) {
			logger.error("file save failed:" + ex.getMessage(), ex);
		} finally {
			CloseUtil.closeWriter(fw);
		}
	}

	/**
	 *
	 * @param filePath
	 * @param fileName
	 * @return
	 * @since fangxiang @ Nov 26, 2010
	 */
	public static String buildFullPath(String filePath, String fileName) {
		return StringHelper.smartAppendSuffix(filePath, File.separator)
				+ fileName;
	}

	/**
	 * 获取文件对象
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 文件对象
	 * @since fangxiang @ Oct 13, 2010
	 */
	public static File getFile(String filePath) {
		return new File(filePath);
	}

	/**
	 * 覆盖写文件
	 * 
	 * @param content
	 *            写入的内容
	 * @param encoding
	 *            文件的编码名称，如：UTF-8
	 * @exception IOException
	 */
	public static void saveFile(File file, String content, String encoding)
			throws IOException {
		if (StringHelper.isEmpty(content)) {
			content = "";
		}
		if (StringHelper.isEmpty(encoding)) {
			encoding = "UTF-8";
		}
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			file.createNewFile();
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, encoding);
			bw = new BufferedWriter(osw);
			bw.write(content);
		} finally {
			CloseUtil.closeWriter(bw);
			CloseUtil.closeWriter(osw);
			CloseUtil.closeOutputStream(fos);
		}
	}

	/**
	 * 覆盖写文件
	 * 
	 * @param filePath
	 * @param content
	 *            写入的内容
	 * @param encoding
	 *            文件的编码名称，如：UTF-8
	 * @exception IOException
	 */
	public static void saveFile(String filePath, String content, String encoding)
			throws IOException {
		saveFile(new File(filePath), content, encoding);
	}

	public static String getFileExtention(String fileName) {
		if (fileName == null)
			return null;
		int pos = fileName.lastIndexOf('.');

		if (pos >= 0) {
			return fileName.substring(pos + 1);
		}

		return null;
	}

	/**
	 *
	 * @param path
	 * @return
	 */
	public static String cleanFilePath(String path) {

		if (path == null)
			return null;

		StringBuffer sb = new StringBuffer(path.length() * 2);
		for (int i = 0; i < path.length(); i++) {
			char c = path.charAt(i);

			if (c == '\\') {
				sb.append('/');
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	/**
	 * 把指定的输入流写入目标文件。
	 * 
	 * @param inputStream
	 *            文件输入流
	 * @param targetFile
	 *            目标文件
	 * @throws IOException
	 */
	public static void writeFile(File targetFile, InputStream inputStream)
			throws IOException {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(targetFile);

			targetFile.createNewFile();

			byte buff[] = new byte[4096];
			int length;
			while ((length = inputStream.read(buff, 0, 4096)) > 0) {
				fos.write(buff, 0, length);
			}
		} finally {
			CloseUtil.closeOutputStream(fos);
			CloseUtil.closeInputStream(inputStream);
		}
	}

	/**
	 * 读取inputStream，将读取的内容按照byte[]返回。自动关闭inputStream。
	 */
	public static byte[] readBytes(InputStream inputStream) throws IOException {
		ByteArrayOutputStream bos = null;

		try {
			bos = new ByteArrayOutputStream();

			byte buff[] = new byte[4096];
			int length;
			while ((length = inputStream.read(buff, 0, 4096)) > 0) {
				bos.write(buff, 0, length);
			}

			return bos.toByteArray();

		} finally {
			CloseUtil.closeOutputStream(bos);
			CloseUtil.closeInputStream(inputStream);
		}
	}

	/**
	 * 从一个输入流中按照指定编码读取文本。
	 * 
	 * @param is
	 *            输入流，读取完毕以后将自动关闭。
	 * @param encoding
	 *            编码
	 */
	public static String readText(InputStream is, String encoding)
			throws IOException {
		StringBuffer sb = new StringBuffer(4096);
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			isr = new InputStreamReader(is, encoding);
			br = new BufferedReader(isr);
			boolean firstLine = true;
			for (String line = br.readLine(); line != null; line = br
					.readLine()) {
				if (!firstLine) {
					sb.append(Const.NEWLINE);
				} else {
					firstLine = false;
				}
				sb.append(line);
			}
			br.close();
			return sb.toString();
		} catch (Exception e) {
			return null;
		} finally {
			CloseUtil.closeReader(br);
			CloseUtil.closeReader(isr);
			CloseUtil.closeInputStream(is);
		}
	}

	/**
	 * 覆盖写文件
	 * 
	 * @param bytes
	 *            写入的内容
	 * @exception IOException
	 */
	public static void writeFile(File file, byte[] bytes) throws IOException {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		try {
			file.createNewFile();

			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);

			bos.write(bytes);
		} finally {
			CloseUtil.closeOutputStream(bos);
			CloseUtil.closeOutputStream(fos);
		}
	}

	/**
	 * 覆盖写文件
	 * 
	 * @param content
	 *            写入的内容
	 * @param encoding
	 *            文件的编码名称，如：UTF-8
	 * @exception IOException
	 */
	public static void writeFile(File file, String content, String encoding)
			throws IOException {

		if (content == null) {
			content = "";
		}

		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;

		try {
			file.createNewFile();

			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, encoding);
			bw = new BufferedWriter(osw);

			bw.write(content);

		} finally {
			CloseUtil.closeWriter(bw);
			CloseUtil.closeWriter(osw);
			CloseUtil.closeOutputStream(fos);
		}
	}

	/**
	 * 按照一个给定的编码读取文本文件。 
	 * {@link #readFileText(String, String)}，需要分析和测试确认
	 */
	public static String readTextFile(File file, String encoding)
			throws IOException {
		StringBuilder sb = new StringBuilder(4096);
		InputStreamReader isr = null;
		BufferedReader br = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, encoding);
			br = new BufferedReader(isr);
			boolean firstLine = true;
			for (String line = br.readLine(); line != null; line = br
					.readLine()) {
				if (!firstLine) {
					sb.append(Const.NEWLINE);
				} else {
					firstLine = false;
				}
				sb.append(line);
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error("fail to read file [" + file + "] use encoding [" + encoding + "]", e);
		} finally {
			CloseUtil.closeReader(br);
			CloseUtil.closeReader(isr);
			CloseUtil.closeInputStream(fis);
		}

		return null;
	}

	/**
	 * 以给定的编码将文本文件的内容读出到字符串中，换行符保留。
	 * 
	 * @param file
	 *            文件名
	 * @param encoding
	 * @return 文本文件的内容
	 * @throws Exception
	 *             文件操作出现错误时
	 * @since ChengKing @ Oct 19, 2010
	 */
	public static String readFileText(String file, String encoding) {
		StringBuilder sb = new StringBuilder();
		FileInputStream in = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			in = new FileInputStream(file);
			// isr = new InputStreamReader(in, encoding);
			br = new BufferedReader(new UnicodeReader(in, encoding));
			for (String line = br.readLine(); line != null; line = br
					.readLine()) {
				sb.append(line).append(Const.NEWLINE);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			CloseUtil.closeReader(br);
			CloseUtil.closeReader(isr);
			CloseUtil.closeInputStream(in);
		}
		return sb.toString();
	}

	/**
	 * 强制删除一个目录，包含所有子目录。<br>
	 * 如果目录不存在，直接返回。
	 */
	public static void forceDeleteDirectory(File directory) throws IOException {
		if (!directory.exists())
			return;

		File[] files = directory.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile()) {
					files[i].delete();
				} else {
					forceDeleteDirectory(files[i]);
				}
			}
		}
		// 最后删除当前的目录。
		directory.delete();
	}

	/**
	 * 获取指定文件的输入流，识别ClassPath；
	 * 
	 * @param fileName
	 * @return
	 * @since TRS @ Mar 13, 2011
	 */
	public static InputStream getInputStream(String fileUrl) {
		InputStream is = null;
		if (fileUrl.startsWith("classpath://"))
			is = FileUtil.class.getClassLoader().getResourceAsStream(
					fileUrl.substring("classpath://".length()));
		else {
			try {
				is = new FileInputStream(fileUrl);
			} catch (FileNotFoundException e) {
				logger.error("NotFound :" + fileUrl);
			}
		}
		return is;
	}

	/**
	 * 根据文件路径生成file对应的byte
	 * 
	 * @param filePath
	 * @return
	 * @since congli @ 2012-7-20
	 */
	public static byte[] getBytes(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 列出所有空的子目录
	 * 
	 * @param folder
	 * @param recursive
	 * @return
	 * @since zhuqing @ Aug 9, 2012
	 */
	public static List<File> listEmptySubDirsInDir(File folder, boolean recursive) {
		assertDirExists(folder);
		File[] aryFiles = folder.listFiles();
		List<File> files = new ArrayList<File>();
		for (int i = 0; i < aryFiles.length; i++) {
			if (aryFiles[i].isDirectory()) {
				if (recursive) {
					if (aryFiles[i].listFiles().length == 0) {
						files.add(aryFiles[i]);
					}
					files.addAll(listEmptySubDirsInDir(aryFiles[i], recursive));
				}
			}
		}
		return files;
	}

	/**
	 * 返回该目录相对于给定起始目录的子目录的路径. 如D:/TRS/repo/source/2012/09/10 相对于
	 * D:/TRS/repo/source/返回2012/09/10
	 * 
	 * @param rootPath
	 *            起始目录
	 * @param dir
	 *            目录
	 * @return 目录dir相对于起始目录的子目录的路径.如果文件或目录不存在, 或文件不在该起始目录下, 则返回
	 *         <code>null</code>.
	 */
	public static String getDirSubPath(String rootPath, File dir) {
		if (rootPath == null || dir == null) {
			return null;
		}

		File rootFile = new File(rootPath);
		if (false == rootFile.isDirectory()) {
			return null;
		}

		if (false == dir.exists()) {
			return null;
		}

		String fullPath = dir.getAbsolutePath();
		// 将Windows路径符"\"替换为Java统一的路径符"/"
		fullPath = fullPath.replace('\\', '/');
		rootPath = rootPath.replace('\\', '/');
		return StringHelper.removeBeginPart(fullPath, rootPath);
	}
}

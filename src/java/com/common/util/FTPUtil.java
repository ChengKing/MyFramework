package com.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.log4j.Logger;

/**
 * FTP工具类.<br>
 * 
 * @author TRS信息技术股份有限公司
 */
public class FTPUtil {

	private static final Logger LOG = Logger.getLogger(FTPUtil.class);

	/**
	 * 测试是否能连接成功；如果提供了帐号信息，则还将测试能否登录成功.
	 * 
	 * @param host
	 * @param port
	 * @param userName
	 *            用户名；如为空则不进行登录测试.
	 * @param passwd
	 */
	public static String tryConnectAndLogin(String host, int port,
			String userName, String passwd) {
		FTPClient ftp = makeFTPSession(host, port, userName, passwd, null);
		return ftp.getReplyString();
	}

	/**
	 * @param host
	 * @param port
	 * @return FTP服务端提示信息(一般含有软件名称和版本等)
	 */
	public static String tryConnect(String host, int port) {
		FTPClient ftp = makeFTPSession(host, port);
		return ftp.getReplyString();
	}

	/**
	 * 
	 * @param host
	 * @param port
	 * @param userName
	 * @param passwd
	 * @param encoding
	 *            用什么编码来解析文件名；如为空，则为<code>UTF-8</code>.
	 * @return
	 */
	static FTPClient makeFTPSession(String host, int port, String userName,
			String passwd, String encoding) {
		FTPClient ftp = makeFTPSession(host, port);
		ftp.setControlEncoding(StringHelper.avoidEmpty(encoding, "UTF-8"));
		if (false == StringHelper.isEmpty(userName)) {
			boolean loginSuccess;
			try {
				loginSuccess = ftp.login(userName, passwd);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			if (false == loginSuccess) {
				throw new RuntimeException("FTP登录失败: " + ftp.getReplyString());
			}
		}
		return ftp;
	}
	/**
	 * @param host
	 * @param port
	 * @return
	 */
	static FTPClient makeFTPSession(String host, int port) {
		FTPClient ftp = new FTPClient();
		// 只有日期没时间；按https://issues.apache.org/jira/browse/NET-140，也不行
		// FTPClientConfig conf = new FTPClientConfig();
		// conf.setDefaultDateFormatStr("yyyy-MM-dd HH:mm:ss");
		// conf.setRecentDateFormatStr("yyyy-MM-dd HH:mm:ss");
		// conf.setLenientFutureDates(true);
		// ftp.configure(conf);
		final int timeout = 10000;
		ftp.setDefaultTimeout(timeout);
		ftp.setDataTimeout(timeout);
		try {
			ftp.connect(host, port);
		} catch (IOException e) {
			throw new RuntimeException("connect ftp://" + host + ":" + port
                    + " fail.", e);
		}
		try {
			ftp.setSoTimeout(timeout);
		} catch (SocketException e) {
			LOG.warn("setSoTimeout failed.");
		}
		return ftp;
	}

	/**
	 * 获取FTP服务器根目录的文件列表.
	 * 
	 * @see #list(String, int, String, String, String, String, String...)
	 */
	public static List<File> list(String host, int port,
			String userName, String passwd, String encoding) {
		return list(host, port, userName, passwd, encoding, "/");
	}

	/**
	 * 获取FTP服务器指定目录的文件列表；使用FTP被动模式.
	 * 
	 * @param host
	 *            FTP服务器域名、主机名或IP地址
	 * @param port
	 *            FTP服务器端口
	 * @param userName
	 *            FTP连接帐号; 如为空，则跳过此参数和密码参数
	 * @param passwd
	 *            该连接帐号的密码
	 * @param encoding
	 *            用什么编码来解析文件名；如为空，则为<code>UTF-8</code>.
	 * @param remoteDir
	 *            要获取FTP服务器哪个目录的文件清单
	 * @param exts
	 *            只列出哪些扩展名的文件(不区分大小写); 如果不指定，则列出全部文件；指定扩展名时含不含起始的<code>.</code>
	 *            均可；
	 */
	public static List<File> list(String host, int port, String userName,
			String passwd, String encoding, String remoteDir,
			final String... exts) {
		FTPClient ftp = makeFTPSession(host, port, userName, passwd, encoding);
		ftp.setListHiddenFiles(true);
		ftp.enterLocalPassiveMode();
		FTPFile[] files;
		String encodedDir = StringHelper.getStringByEncoding(remoteDir,
				encoding);
		try {
			files = ftp.listFiles(encodedDir, new FTPFileFilter() {

				@Override
				public boolean accept(FTPFile ftpFile) {
					String fileName = ftpFile.getName();
					// 排除掉.和..(有些FTP Server如ServU会返回这两个特殊目录)
					if (fileName.equals(".") || fileName.equals("..")) {
						return false;
					}
					if (exts.length == 0) {
						return true;
					}
					for (String ext : exts) {
						if (fileName.toLowerCase().endsWith(ext.toLowerCase())) {
							return true;
						}
					}
					return false;
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		List<File> list = new ArrayList<File>(files.length);
		for (FTPFile ftpFile : files) {
		// TODO
		}
		return list;
	}

	/**
	 * @param ftp
	 * @param remoteDir
	 * @param srcFile
	 * @return
	 */
	static long getFileSize(FTPClient ftp, String remoteDir,
			final String srcFile, String encoding) {
		ftp.setListHiddenFiles(true); // 某些FTP Servr如TYP必须启用，否则列不出来
		String encodedDir = StringHelper.getStringByEncoding(remoteDir,
				encoding);
		try {
			FTPFile[] files = ftp.listFiles(encodedDir, new FTPFileFilter() {
	
				@Override
				public boolean accept(FTPFile ftpFile) {
					String name = ftpFile.getName();
					return name.equals(srcFile);
				}
			});
			if (files == null || files.length == 0) {
				throw new RuntimeException("file not exist: " + srcFile
						+ "; FTPSession encoding: " + ftp.getControlEncoding());
			}
			return files[0].getSize();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param host
	 * @param port
	 * @param userName
	 * @param passwd
	 * @param remoteDir
	 * @param remoteFile
	 * @param encoding
	 * @param localDir
	 * @param listener
	 * @return
	 * @since chengking @ Apr 7, 2011
	 */
	public static File download(String host, int port, String userName,
			String passwd, String remoteDir, final String remoteFile,
			String encoding, String localDir) {
		return download(host, port, userName, passwd, remoteDir, remoteFile,
				encoding, localDir, null);
	}

	/**
	 * 
	 * @param host
	 * @param port
	 * @param userName
	 * @param passwd
	 * @param encoding
	 * @param remoteDir
	 * @return
	 * @since chengking @ Apr 3, 2011
	 */
	@SuppressWarnings("resource")
	public static File download(String host, int port, String userName,
			String passwd, String remoteDir, final String remoteFile,
			String encoding, String localDir,
			String localFile) {
		FTPClient ftp = makeFTPSession(host, port, userName, passwd, encoding);
		ftp.enterLocalPassiveMode();
		try {
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		localDir = StringHelper.avoidEmpty(localDir,
				FileUtil.getCurrentWorkingDir());
		localFile = StringHelper.avoidEmpty(localFile, remoteFile);
		File resultFile = new File(localDir, localFile);
		OutputStream os;
		try {
			os = new FileOutputStream(resultFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		long srcSize = getFileSize(ftp, remoteDir, remoteFile, encoding);
		InputStream is;
		final String remotePath = StringHelper.smartAppendSlashToEnd(remoteDir)
				+ remoteFile;
		try {
			// chengking @ Apr 6, 2011: 从实测看，RETR可以带dir，即cwd非必须
			// ftp.changeWorkingDirectory(remoteDir);
			String fileName = StringHelper.getStringByEncoding(remotePath,
					encoding);
			is = ftp.retrieveFileStream(fileName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (is == null) {
			extracted(ftp, remotePath);
		}
		try {
			IOUtil.copy(is, os, srcSize);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			CloseUtil.closeInputStream(is);
			CloseUtil.closeOutputStream(os);
		}
		return resultFile;
	}

	private static void extracted(FTPClient ftp, final String remotePath) {
		throw new RuntimeException("the remote file not exist: "
				+ remotePath + "; FTP Code: " + ftp.getReplyString());
	}

	static void releaseSession(FTPClient ftp, String promptWhenErr) {
		try {
			if (!ftp.completePendingCommand()) {
				ftp.logout();
				ftp.disconnect();
			}
		} catch (IOException e) {
			LOG.warn("releaseSession failed: ", e);
		}
	}

	/**
	 * 测试连接
	 * 
	 * @param host
	 *            ip或者hostName
	 * @param port
	 *            端口
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param controlEncoding
	 *            ftp服务器的编码方式,如果填写务必保证正确，否则请填null。
	 * @return false:连接失败 true:连接成功
	 * @since chengking
	 */
	public static boolean testConnection(String host, int port,
			String userName, String password, String controlEncoding) {
		boolean testResult = false;
		TRSFTPClient ftpClient = new TRSFTPClient();
		try {
			testResult = ftpClient.login(host, port, userName, password,
					controlEncoding);
		} catch (RuntimeException e) {
			// 服务器连接错误，还未到账号验证阶段，此异常在此吞没，结果统一为连接错误。
		}
		ftpClient.logout();
		return testResult;
	}

	/**
	 * 默认端口上传单个文件到默认位置
	 * 
	 * @param host
	 *            ip或者hostName
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param localFilePath
	 *            本地文件名称
	 * @param controlEncoding
	 *            ftp服务器的编码方式,如果填写务必保证正确，否则请填null。
	 * @param ignoreSameFile
	 *            false表示不上传FTPServer已经有的同名文件，true代表覆盖FTPServer上已经有的同名文件
	 * @throws MAMException
	 * @creator chengking
	 */
	public static void uploadFile(String host, String userName,
			String password, String localFilePath, String controlEncoding,
			boolean ignoreSameFile) throws RuntimeException {
		uploadFile(host, 21, userName, password, localFilePath,
				controlEncoding, ignoreSameFile);
	}

	/**
	 * 指定端口上传单个文件默认位置
	 * 
	 * @param host
	 *            ip或者hostName
	 * @param port
	 *            端口
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param localFilePath
	 *            本地文件名称
	 * @param controlEncoding
	 *            ftp服务器的编码方式,如果填写务必保证正确，否则请填null。
	 * @param ignoreSameFile
	 *            false表示不上传FTPServer已经有的同名文件，true代表覆盖FTPServer上已经有的同名文件
	 * @throws MAMException
	 * @creator chengking
	 */
	public static void uploadFile(String host, int port, String userName,
			String password, String localFilePath, String controlEncoding,
			boolean ignoreSameFile) throws RuntimeException {
		TRSFTPClient ftpClient = new TRSFTPClient();
		ftpClient.login(host, port, userName, password, controlEncoding);
		ftpClient.upload(localFilePath, ignoreSameFile);
		ftpClient.logout();
	}

	/**
	 * 默认端口上传单个文件到目标位置
	 * 
	 * @param host
	 *            ip或者hostName
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param localFilePath
	 *            本地文件名称
	 * @param destPath
	 *            上传的目标位置,如：/myFile/info.txt
	 * @param controlEncoding
	 *            ftp服务器的编码方式,如果填写务必保证正确，否则请填null。
	 * @param ignoreSameFile
	 *            false表示不上传FTPServer已经有的同名文件，true代表覆盖FTPServer上已经有的同名文件
	 * @throws MAMException
	 * @creator chengking
	 */
	public static void uploadFile(String host, String userName,
			String password, String localFilePath, String destPath,String controlEncoding,
			boolean ignoreSameFile) throws RuntimeException {
		uploadFile(host, 21, userName, password, localFilePath, destPath,
				controlEncoding, ignoreSameFile);
	}

	/**
	 * 指定端口上传单个文件到目标位置
	 * 
	 * @param host
	 *            ip或者hostName
	 * @param port
	 *            端口
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param localFilePath
	 *            本地文件名称
	 * @param destPath
	 *            上传的目标位置,如：/myFile/info.txt
	 * @param controlEncoding
	 *            ftp服务器的编码方式,如果填写务必保证正确，否则请填null。
	 * @param ignoreSameFile
	 *            false表示不上传FTPServer已经有的同名文件，true代表覆盖FTPServer上已经有的同名文件
	 * @throws MAMException
	 * @creator chengking
	 */
	public static void uploadFile(String host, int port, String userName,
			String password, String localFilePath, String destPath,
			String controlEncoding, boolean ignoreSameFile)
			throws RuntimeException {
		TRSFTPClient ftpClient = new TRSFTPClient();
		ftpClient.login(host, port, userName, password, controlEncoding);
		ftpClient.upload(localFilePath, destPath, ignoreSameFile);
		ftpClient.logout();
	}

	/**
	 * 指定端口上传单个文件到目标位置
	 * 
	 * @param host
	 *            ip或者hostName
	 * @param port
	 *            端口
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param content
	 *            要写入的文件内容
	 * @param fileName
	 *            要写入的文件名
	 * @param destPath
	 *            上传的目标位置,如：/myFile
	 * @param controlEncoding
	 *            ftp服务器的编码方式,如果填写务必保证正确，否则请填null。
	 * @param ignoreSameFile
	 *            false表示不上传FTPServer已经有的同名文件，true代表覆盖FTPServer上已经有的同名文件
	 * @throws MAMException
	 * @creator chengking
	 */
	public static void uploadFile(String host, int port, String userName, String password, StringBuffer content, String fileName, String destPath,
			String controlEncoding, boolean ignoreSameFile) throws RuntimeException {
		TRSFTPClient ftpClient = new TRSFTPClient();
		boolean result = ftpClient.login(host, port, userName, password, controlEncoding);
		if (!result) {
			LOG.error("failed to connect ftp by userName [" + userName + "] ,password [" + Base64Util.encode(password) + "] for host[" + host + "] by port ["
					+ port + "]");
			throw new RuntimeException(FailInfoUtil.getLoginFailInfo(userName, password));
		}
		ftpClient.upload(content, fileName, destPath, ignoreSameFile);
		ftpClient.logout();
	}

	/**
	 * 默认端口删除默认位置上的指定文件
	 * 
	 * @param host
	 *            ip或者hostName
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param fileName
	 *            文件名称，含后缀名
	 * @param controlEncoding
	 *            ftp服务器的编码方式,如果填写务必保证正确，否则请填null。
	 * @throws MAMException
	 * @creator chengking
	 */
	public static void deleteFile(String host, String userName,
			String password, String fileName, String controlEncoding)
			throws RuntimeException {
		deleteFile(host, 21, userName, password, fileName, controlEncoding);
	}

	/**
	 * 指定端口删除默认位置上的指定文件
	 * 
	 * @param host
	 *            ip或者hostName
	 * @param port
	 *            端口
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param fileName
	 * @param controlEncoding
	 *            ftp服务器的编码方式,如果填写务必保证正确，否则请填null。
	 * @throws MAMException
	 * @creator chengking
	 */
	public static void deleteFile(String host, int port, String userName,
			String password, String fileName, String controlEncoding)
			throws RuntimeException {
		TRSFTPClient ftpClient = new TRSFTPClient();
		ftpClient.login(host, port, userName, password,controlEncoding);
		ftpClient.delete(fileName);
		ftpClient.logout();
	}

	/**
	 * 默认端口删除指定位置上的指定文件
	 * 
	 * @param host
	 *            ip或者hostName
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param path
	 * @param fileName
	 * @param controlEncoding
	 *            ftp服务器的编码方式,如果填写务必保证正确，否则请填null。
	 * @throws MAMException
	 * @creator chengking
	 */
	public static void deleteFile(String host, String userName,
			String password, String destPath, String fileName,
			String controlEncoding) throws RuntimeException {
		deleteFile(host, 21, userName, password, destPath, fileName,controlEncoding);
	}

	/**
	 * 指定端口删除指定位置上的指定文件
	 * 
	 * @param host
	 *            ip或者hostName
	 * @param port
	 *            端口
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param path
	 * @param fileName
	 * @param controlEncoding
	 *            ftp服务器的编码方式,如果填写务必保证正确，否则请填null。
	 * @throws MAMException
	 * @creator chengking
	 */
	public static void deleteFile(String host, int port, String userName,
			String password, String destPath, String fileName,
			String controlEncoding) throws RuntimeException {
		TRSFTPClient ftpClient = new TRSFTPClient();
		ftpClient.login(host, port, userName, password, controlEncoding);
		ftpClient.delete(destPath, fileName);
		ftpClient.logout();
	}

}

/**
 * Created: lichuanjiao@2010-1-15 下午02:08:31
 */
package com.common.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * 职责: FTP的上传、下载、查看等核心方法实现，为FTPUtil服务.<br>
 * 
 * @author TRS信息技术股份有限公司
 */
class TRSFTPClient {

	// 声明一个FTPClient
	private FTPClient ftp;

	/**
	 * 登录FTPServer
	 * 
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @return
	 * @throws RuntimeException
	 * @creator lichuanjiao @ 2010-1-15
	 */
	boolean login(String host, int port, String userName, String password,
			String controlEncoding) throws RuntimeException {
		boolean loginResult = false;
		ftp = new FTPClient();
		try {
			if (null != controlEncoding && 0 != controlEncoding.trim().length())
				ftp.setControlEncoding(controlEncoding);
			ftp.connect(host, port);
		} catch (IOException e) {
			String errorMessage = FailInfoUtil.getConnetFailInfo(host, port,
					controlEncoding);
			throw new RuntimeException(errorMessage, e);
		}
		try {
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException e) {
			// 在设置传输模式出现异常时，断开已经连接上的FTP
			if (ftp.isConnected()) {
				this.logout();
			}
			String errorMessage = FailInfoUtil
					.getSetFileTypeFailInfo("BINARY_FILE_TYPE");
			throw new RuntimeException(errorMessage, e);
		}
		try {
			loginResult = ftp.login(userName, password);
		} catch (Exception e) {
			// 在登录出现异常时，断开已经连接上的FTP
			if (ftp.isConnected()) {
				this.logout();
			}
			String errorMessage = FailInfoUtil.getLoginFailInfo(userName,
					password);
			throw new RuntimeException(errorMessage, e);
		}
		return loginResult;
	}

	/**
	 * 注销FTPServer
	 * 
	 * @creator lichuanjiao @ 2010-1-21
	 */
	void logout() {
		this.closeFTPConnection(ftp);
	}

	/**
	 * 上传文件到服务器的方法（统一调用此方法完成各种方式的上传）
	 * 
	 * @param localFilePath
	 * @param remoteFileName
	 * @param ignoreSameFile
	 *            false表示不上传FTPServer已经有的同名文件，true代表覆盖FTPServer上已经有的同名文件
	 * @throws RuntimeException
	 * @creator lichuanjiao @ 2010-1-18
	 */
	private void uploadFile(StringBuffer content, String remoteFileName,
			boolean ignoreSameFile) throws RuntimeException {
		// 由于FTPUtil可以作为单独的工具类，因此不引入FileUitl而是引入java.io.file。
		InputStream input = null;
		input = new ByteArrayInputStream(content.toString().getBytes());
		try {
			if (false == isFileExist(remoteFileName))
				ftp.storeFile(remoteFileName, input);
			else {
				if (false == ignoreSameFile)
					ftp.storeFile(remoteFileName, input);
			}
		} catch (IOException e) {
			String errorMessage = FailInfoUtil.getUploadFailInfo(remoteFileName);
			throw new RuntimeException(errorMessage, e);
		}
	}

	/**
	 * 上传文件到服务器的方法（统一调用此方法完成各种方式的上传）
	 * 
	 * @param localFilePath
	 * @param remoteFileName
	 * @param ignoreSameFile
	 *            false表示不上传FTPServer已经有的同名文件，true代表覆盖FTPServer上已经有的同名文件
	 * @throws RuntimeException
	 * @creator lichuanjiao @ 2010-1-18
	 */
	private void uploadFile(String localFilePath, String remoteFileName,
			boolean ignoreSameFile) throws RuntimeException {
		// 由于FTPUtil可以作为单独的工具类，因此不引入FileUitl而是引入java.io.file。
		InputStream input = null;
		try {
			input = new FileInputStream(localFilePath);
		} catch (FileNotFoundException f) {
			String errorMessage = FailInfoUtil
					.getFileNoExistInfo(localFilePath);
			throw new RuntimeException(errorMessage, f);
		}
		try {
			if (false == isFileExist(remoteFileName))
				ftp.storeFile(remoteFileName, input);
			else {
				if (false == ignoreSameFile)
					ftp.storeFile(remoteFileName, input);
			}
		} catch (IOException e) {
			String errorMessage = FailInfoUtil.getUploadFailInfo(localFilePath);
			throw new RuntimeException(errorMessage, e);
		}
	}

	/**
	 * 上传单个文件到默认位置
	 * 
	 * @param localFilePath
	 * @param ignoreSameFile
	 *            false表示不上传FTPServer已经有的同名文件，true代表覆盖FTPServer上已经有的同名文件
	 * @throws RuntimeException
	 * @creator lichuanjiao @ 2010-1-18
	 */
	void upload(String localFilePath, boolean ignoreSameFile)
			throws RuntimeException {
		String remoteFileNameSplit[] = StringHelper
				.split(localFilePath, "\\\\");
		String remoteFileName = remoteFileNameSplit[remoteFileNameSplit.length - 1];
		uploadFile(localFilePath, remoteFileName, ignoreSameFile);
	}

	/**
	 * 上传文件到指定位置
	 * 
	 * @param localFilePath
	 * @param destPath
	 * @param ignoreSameFile
	 *            false表示不上传FTPServer已经有的同名文件，true代表覆盖FTPServer上已经有的同名文件
	 * @throws RuntimeException
	 * @creator lichuanjiao @ 2010-1-18
	 */
	void upload(String localFilePath, String destPath, boolean ignoreSameFile)
			throws RuntimeException {
		createDirectoryOnFTPServer(destPath);
		changeFTPServerWorkingDirectory(destPath);
		String remoteFileNameSplit[] = StringHelper
				.split(localFilePath, "\\\\");
		String remoteFileName = remoteFileNameSplit[remoteFileNameSplit.length - 1];
		uploadFile(localFilePath, remoteFileName, ignoreSameFile);
	}

	void upload(StringBuffer content, String fileName, String destPath, boolean ignoreSameFile) {
		createDirectoryOnFTPServer(destPath);
		changeFTPServerWorkingDirectory(destPath);
		uploadFile(content, fileName, ignoreSameFile);
	}

	/**
	 * 删除默认路径下的指定文件
	 * 
	 * @param fileName
	 * @throws RuntimeException
	 * @creator lichuanjiao @ 2010-1-15
	 */
	void delete(String fileName) throws RuntimeException {
		try {
			// 删除指定的文件
			ftp.deleteFile(fileName);
		} catch (IOException e) {
			String errorMessage = FailInfoUtil.getDeleteFailInfo(fileName);
			throw new RuntimeException(errorMessage, e);
		}
	}

	/**
	 * 删除FTPServer上的空文件夹
	 * 
	 * @param folderName
	 * @creator lichuanjiao @ 2010-1-17
	 */
	boolean deleteEmptyFolder(String folderName) throws RuntimeException {
		boolean removeDirectoryResult = false;
		try {
			removeDirectoryResult = ftp.removeDirectory(folderName);
		} catch (IOException e) {
			String errorMessage = FailInfoUtil.getDeleteEmptyFolder(folderName);
			throw new RuntimeException(errorMessage, e);
		}
		return removeDirectoryResult;
	}

	/**
	 * 删除指定路径下的指定文件
	 * 
	 * @param path
	 * @param fileName
	 * @throws RuntimeException
	 * @creator lichuanjiao @ 2010-1-15
	 */
	void delete(String destPath, String fileName) throws RuntimeException {
		this.changeFTPServerWorkingDirectory(destPath);
		delete(fileName);
	}

	/**
	 * 获取默认路径下的文件列表
	 * 
	 * @return
	 * @throws RuntimeException
	 * @creator lichuanjiao @ 2010-1-15
	 * 
	 *          Deprecated:用String[] getFileNames(String controlEncoding) throws
	 *          RuntimeException替代
	 */
	@Deprecated
	List<String> list() throws RuntimeException {
		List<String> listFileName = new ArrayList<String>();
		FTPFile[] listFiles = null;
		try {
			ftp.setControlEncoding("utf-8");
			listFiles = ftp.listFiles();
		} catch (IOException e) {
			String errorMessage = FailInfoUtil.getListFilesFailInfo();
			throw new RuntimeException(errorMessage, e);
		}
		// 获取的文件列表的头两个为“.”和“..”无意义的专有符号，忽略掉从第三个开始取文件名
		for (int i = 2; i < listFiles.length; i++) {
			listFileName.add(listFiles[i].getName());
		}
		return listFileName;
	}

	/**
	 * 获取默认路径下的文件名集合
	 * 
	 * @param controlEncoding
	 * @return
	 * @throws RuntimeException
	 * @since lichuanjiao @ 2010-12-1
	 */
	String[] getFileNames(String[] exts) throws RuntimeException {
		String[] fileNames = null;
		try {
			fileNames = ftp.listNames();
		} catch (IOException e) {
			String errorMessage = FailInfoUtil.getListFilesFailInfo();
			throw new RuntimeException(errorMessage, e);
		}
		if (null != fileNames && null != exts) {
			fileNames = getFileNamesByExts(fileNames, exts);
		}
		return fileNames;
	}

	/**
	 * 
	 * @param exts
	 * @return
	 * @throws RuntimeException
	 * @since lichuanjiao @ 2011-1-20
	 */
	FTPFile[] getFiles(String[] exts) throws RuntimeException {
		FTPFile[] ftpfiles;
		try {
			ftpfiles = ftp.listFiles();
		} catch (IOException e) {
			String errorMessage = FailInfoUtil.getListFilesFailInfo();
			throw new RuntimeException(errorMessage, e);
		}
		if (null != ftpfiles && null != exts) {
			// TODO lichuanjiao 20100120:待实现，会影响根据后缀名得到FTPFile的方法。如果不用后缀名则不影响。
		}
		return ftpfiles;
	}

	/**
	 * @param fileNames
	 * @param exts
	 * @return
	 * @since lichuanjiao @ 2011-1-20
	 */
	private String[] getFileNamesByExts(String[] fileNames, String[] exts) {
		List<String> fileNameList = new ArrayList<String>();
		for (int i = 0; i < fileNames.length; i++) {
			fileNameList.add(fileNames[i]);
		}
		for (int i = 0; i < fileNameList.size(); i++) {
			int flag = 0;
			String fileName = fileNameList.get(i);
			for (String ext : exts) {
				if (ext.equals(getFileNameExtension(fileName))) {
					flag = 1;
				}
			}
			if (flag == 0) {
				fileNameList.remove(fileNameList.get(i));
				i--;
			}
		}
		int len = fileNameList.size();
		String[] newFileNames = new String[len];
		for (int i = 0; i < len; i++) {
			newFileNames[i] = fileNameList.get(i);
		}
		return newFileNames;
	}

	/**
	 * 根据文件名得到其扩展名（后缀名）
	 * 
	 * @param fileName
	 * @return
	 * @since lichuanjiao @ 2011-1-20
	 */
	private String getFileNameExtension(String fileName) {
		return FileUtil.getFileExtension(new File(fileName));
	}

	/**
	 * 获取指定路径下的文件列表
	 * 
	 * @param path
	 * @return
	 * @throws RuntimeException
	 * @creator lichuanjiao @ 2010-1-15
	 * 
	 *          Deprecated:用String[] getFileNames(String destPath, String
	 *          controlEncoding)替代
	 */
	@Deprecated 
	List<String> list(String destPath) throws RuntimeException {
		this.changeFTPServerWorkingDirectory(destPath);
		return this.list();
	}

	/**
	 * 获取指定路径下的文件列表
	 * 
	 * @param path
	 * @return
	 * @throws RuntimeException
	 * @creator lichuanjiao @ 2010-1-15
	 */
	String[] getFileNames(String destPath, String[] exts)
			throws RuntimeException {
		changeFTPServerWorkingDirectory(destPath);
		return getFileNames(exts);
	}

	/**
	 * 
	 * @param destPath
	 * @param exts
	 * @return
	 * @since lichuanjiao @ 2011-1-20
	 */
	FTPFile[] getfiles(String destPath, String[] exts) {
		changeFTPServerWorkingDirectory(destPath);
		return getFiles(exts);
	}

	/**
	 * 获取文件上传的时间
	 * 
	 * @return
	 * @throws RuntimeException
	 * @creator lichuanjiao @ 2010-1-15
	 */
	String getUploadloadTime() throws RuntimeException {
		// TODO 目前仅为空实现；需要实现文件的上传时间。
		return "";
	}

	/**
	 * 获取上传目录中的文件数
	 * 
	 * @return
	 * @throws RuntimeException
	 * @creator lichuanjiao @ 2010-1-15
	 */
	int getUploadAmount(String srcDir) throws RuntimeException {
		File fileDir = new File(srcDir);
		String[] uploadFiles = fileDir.list();
		return uploadFiles.length;
	}

	/**
	 * 在FTPServer上创建新的文件夹
	 * 
	 * @param WorkingDirectory
	 * @throws RuntimeException
	 * @creator lichuanjiao @ 2010-1-18
	 */
	private void createDirectoryOnFTPServer(String WorkingDirectory)
			throws RuntimeException {
		try {
			ftp.makeDirectory(WorkingDirectory);
		} catch (IOException e) {
			String errorMessage = FailInfoUtil
					.getCreateDirectoryFailInfo(WorkingDirectory);
			throw new RuntimeException(errorMessage, e);
		}
	}

	/**
	 * 切换FTPServer上的工作目录到指定目录
	 * 
	 * @param WorkingDirectory
	 * @creator lichuanjiao @ 2010-1-17
	 */
	void changeFTPServerWorkingDirectory(String WorkingDirectory)
			throws RuntimeException {
		try {
			ftp.changeWorkingDirectory(WorkingDirectory);
		} catch (IOException e) {
			String errorMessage = FailInfoUtil
					.getChangeWorkingDirectoryFailInfo(WorkingDirectory);
			throw new RuntimeException(errorMessage, e);
		}
	}

	/**
	 * 判断文件于FTP服务器上是否已经存在
	 * 
	 * @param fileName
	 * @param hostName
	 * @param port
	 * @param userName
	 * @param password
	 * @return
	 * @creator lichuanjiao @ 2010-1-8
	 */
	private boolean isFileExist(String fileName) throws RuntimeException {
		List<String> listFiles = list();
		boolean isFileExist = false;
		for (int i = 0; i < listFiles.size(); i++) {
			if (listFiles.get(i).equals(fileName)) {
				isFileExist = true;
				return isFileExist;
			}
		}
		return isFileExist;
	}

	/**
	 * 断开给定的ftp连接
	 * 
	 * @param ftp
	 * @creator lichuanjiao @ 2010-1-15
	 */
	private void closeFTPConnection(FTPClient ftp) {
		try {
			ftp.disconnect();
		} catch (IOException e) {
			String errorMessage = "error on close FTPClient, ignored!";
			throw new RuntimeException(errorMessage, e);
		}
	}

	/**
	 * 下载远程文件到本地指定路径
	 * 
	 * @param srcFile
	 *            远程文件地址
	 * @param destFile
	 *            下载到的本地目标路径
	 * @since lichuanjiao @ 2010-6-28
	 */
	void copyFile(String srcFile, String destFile) {
		FileOutputStream fos = null;
		try {
			File file = new File(destFile);
			fos = new FileOutputStream(file);
			ftp.retrieveFile(srcFile, fos);
		} catch (FileNotFoundException fnfex) {
			String errorMessage = FailInfoUtil.getFileNotFoundInfo(destFile);
			throw new RuntimeException(errorMessage, fnfex);
		} catch (IOException ioe) {
			String errorMessage = FailInfoUtil.getIOExceptionInfo(ioe
					.getMessage());
			throw new RuntimeException(errorMessage, ioe);
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				String errorMessage = "error on close FileOutputStream, ignored!";
				throw new RuntimeException(errorMessage, e);
			}
		}
	}
}

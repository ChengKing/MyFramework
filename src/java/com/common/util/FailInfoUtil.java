/**
 * Created: lichuanjiao @2010-1-22 上午10:15:06
 */
package com.common.util;

/**
 * 职责: 构造记录到日志的错误信息.<br>
 * 
 * @author TRS信息技术股份有限公司
 */
public class FailInfoUtil {

	// FTPUtl start

	/**
	 * 记录连接错误的信息
	 * 
	 * @param host
	 * @param port
	 * @return
	 * @creator lichuanjiao @ 2010-1-15
	 */
	static String getConnetFailInfo(String host, int port,
			String controlEncoding) {
		return "Connet fail!" + "hostName:" + host + ",port:" + port
				+ ",controlEncoding:" + controlEncoding;
	}

	/**
	 * 记录设置传输模式的错误的信息
	 * 
	 * @param fileType
	 * @return
	 * @creator lichuanjiao @ 2010-1-18
	 */
	static String getSetFileTypeFailInfo(String fileType) {
		return "The setFileType is:" + fileType;
	}

	/**
	 * 记录登录错误的信息
	 * 
	 * @param userName
	 * @param password
	 * @return
	 * @creator lichuanjiao @ 2010-1-15
	 */
	static String getLoginFailInfo(String userName, String password) {
		return "Login fail!" + "userName:" + userName + ",password:"
				+ password;
	}

	/**
	 * 记录文件不存在的错误信息
	 * 
	 * @param localFilePath
	 * @return
	 * @creator lichuanjiao @ 2010-1-15
	 */
	static String getFileNoExistInfo(String localFilePath) {
		return "Error on localFilePath, no exist!" + "localFilePath:"
				+ localFilePath;
	}

	/**
	 * 记录获取根目录下的文件列表失败的信息
	 * 
	 * @return
	 * @creator lichuanjiao @ 2010-1-17
	 */
	static String getListFilesFailInfo() {
		return "获取FTPSever根目录下的文件列表失败";
	}

	/**
	 * 记录切换路径的错误信息
	 * 
	 * @param destPath
	 * @return
	 * @creator lichuanjiao @ 2010-1-17
	 */
	static String getChangeWorkingDirectoryFailInfo(String destPath) {
		return "ChangeWorkingDirectory Fail!," + "destPath:" + destPath;
	}

	/**
	 * 记录创建目录的错误信息
	 * 
	 * @param destPath
	 * @return
	 * @creator lichuanjiao @ 2010-1-17
	 */
	static String getCreateDirectoryFailInfo(String destPath) {
		return "CreateDirectory Fail!," + "destPath:" + destPath;
	}

	/**
	 * 记录删除空文件夹的错误信息
	 * 
	 * @param folderName
	 * @return
	 * @creator lichuanjiao @ 2010-1-17
	 */
	static String getDeleteEmptyFolder(String folderName) {
		return "DeleteEmptyFolder Fail," + "folderName:" + folderName;
	}

	// FTPUtil end

	// SFTPUtil start

	/**
	 * 记录getSession失败的信息
	 * 
	 * @param userName
	 * @param host
	 * @param port
	 * @return
	 * @creator lichuanjiao @ 2010-1-21
	 */
	static String getSessionFailInfo(String userName, String host, int port) {
		return "Session Fail!," + "userName:" + userName + ",host:" + host
				+ ",port:" + port;
	}

	/**
	 * 记录Session连接失败的信息
	 * 
	 * @param userName
	 * @param host
	 * @param port
	 * @param password
	 * @return
	 * @creator lichuanjiao @ 2010-1-21
	 */
	static String getConnectFailInfo(String userName, String host, int port,
			String password) {
		return getSessionFailInfo(userName, host, port) + ",password"
				+ password;
	}

	/**
	 * 记录打开特定类型的连接失败的信息
	 * 
	 * @param connetType
	 * @return
	 * @creator lichuanjiao @ 2010-1-21
	 */
	static String getOpenChannelFailInfo(String connetType) {
		return "OpenChannel Fail!," + "openChannel:" + connetType;
	}

	/**
	 * 记录建立连接失败的信息
	 * 
	 * @param userName
	 * @param host
	 * @param port
	 * @param password
	 * @param connetType
	 * @return
	 * @creator lichuanjiao @ 2010-1-21
	 */
	static String getChannelConnectFailInfo(String userName, String host,
			int port, String password, String connetType) {
		return getConnectFailInfo(userName, host, port, password)
				+ ",connetType" + connetType;
	}

	/**
	 * 记录更改上传目标目录错误的信息
	 * 
	 * @param destPath
	 *            上传目标
	 * @return
	 * @since lichuanjiao @ 2010-6-7
	 */
	static String getChangeDestPathFailInfo(String destPath) {
		return "ChangeDestPathFail:" + destPath;
	}

	/**
	 * 记录创建文件失败的信息
	 * 
	 * @param directory
	 * @return
	 * @since lichuanjiao @ 2010-6-7
	 */
	static String getMkdirFailInfo(String directory) {
		return "mkdirFail:" + directory;
	}

	// SFTPUtil end

	// same use

	/**
	 * 记录上传文件失败的信息
	 * 
	 * @param localFilePath
	 * @return
	 * @creator lichuanjiao @ 2010-1-15
	 */
	static String getUploadFailInfo(String uploadFile) {
		return "Error on uploadFile!" + "uploadFile:" + uploadFile;
	}

	/**
	 * 记录删除文件失败的信息
	 * 
	 * @param localFilePath
	 * @return
	 * @creator lichuanjiao @ 2010-1-15
	 */
	static String getDeleteFailInfo(String deleteFile) {
		return "Error on deleteFile!" + "deleteFile:" + deleteFile;
	}

	/**
	 * 记录构造文件输入输出流时文件不存在的错误
	 * 
	 * @param filePath
	 * @return
	 * @creator lichuanjiao @ 2010-1-23
	 */
	static String getFileNotFoundInfo(String filePath) {
		return "Error on initFile!" + "filePath:" + filePath;
	}

	/**
	 * 记录远程文件不存在时的错误信息
	 * 
	 * @param directory
	 * @return
	 * @since lichuanjiao @ 2010-6-7
	 */
	static String getRemoteFileNoExistInfo(String directory) {
		return "FileNoExist:" + directory;
	}

	/**
	 * 获取IO异常信息
	 * 
	 * @param ex
	 * @return
	 * @since lichuanjiao @ 2010-6-28
	 */
	static String getIOExceptionInfo(String ex) {
		return "IOException is:" + ex;
	}
}

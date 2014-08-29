package com.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * 配置文件修改类. 该类不适合于写过大的文件(如10MB以上). <BR>
 * [08.12.12]增加void modifyProperties(Map modifiedProperties)，用于批量修改属性
 * 
 * @author chengking
 */
public class ConfigFileModifier {

	private static final String NEWLINE = System.getProperty("line.separator");

	private static final Logger LOG = Logger.getLogger(ConfigFileModifier.class);

	private File file; // 该对象所要修改的文件

	private StringBuffer sb; // 缓冲新文件的内容

	private Properties modifiedProps; // 用于存放所有修改项
	@SuppressWarnings("rawtypes")
	private Map toDelProps; // 用于存放所有要删除的项
	@SuppressWarnings("rawtypes")
	private Map toRemProps; // 存放要整行注释的项

	/**
	 * 根据给定的文件名构造对象
	 * 
	 * @param fileName
	 *            给定的文件名
	 * @see #ConfigFileModifier(File)
	 */
	public ConfigFileModifier(String fileName) {
		this(new File(fileName));
	}

	/**
	 * 根据给定的文件构造对像
	 * 
	 * @param f
	 *            给定的文件
	 */
	@SuppressWarnings("rawtypes")
	public ConfigFileModifier(File f) {
		this.file = f;
		modifiedProps = new Properties();
		toDelProps = new HashMap();
		toRemProps = new HashMap();
	}

	/**
	 * 根据给定的URL构�?对象.
	 * 
	 * @param fileUrl
	 *            给定的URL
	 */
	public ConfigFileModifier(URL fileUrl) {
		this(UrlUtil.decode(fileUrl.getFile()));
	}

	/**
	 * 注释掉某个key对应的整行
	 * 
	 * @param key
	 */
	@SuppressWarnings("unchecked")
	public void remPropertyAsNewLine(String key) {
		if (key == null || key.trim().length() == 0) {
			return;
		}
		toRemProps.put(key, key);
	}

	/**
	 * 删除某个配置项
	 * 
	 * @param key 配置项名
	 * @since chengking
	 */
	@SuppressWarnings("unchecked")
	public void removeProperty(String key) {
		if (key == null || key.trim().length() == 0) {
			return;
		}
		toDelProps.put(key, key);
	}

	/**
	 * 添加一个修改项. <BR>
	 * 修改项和取值均只能使用ASCII字符, 不支持中文等字符.
	 * 
	 * @param key
	 *            要修改的项.
	 * @param newValue
	 *            该项的新取值. null值不会被添加.
	 */
	public void modifyProperty(String key, String newValue) {
		if (newValue != null) {
			try {
				modifiedProps.put(key, new String(newValue.getBytes(), "ISO-8859-1"));
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e.toString(), e);
			}
		}
	}

	/**
	 * 添加一个取值为int类型的修改项.
	 * 
	 * @param key
	 *            要修改的项
	 * @param newValue
	 *            该项的int类型的新取值.
	 */
	public void modifyProperty(String key, int newValue) {
		modifiedProps.put(key, String.valueOf(newValue));
	}

	/**
	 * 批量修改属性
	 * 
	 * @param modifiedProperties
	 *            待修改的属性
	 */
	@SuppressWarnings("rawtypes")
	public void modifyProperties(Map modifiedProperties) {
		if (modifiedProperties == null)
			return;
		//
		for (Iterator i = modifiedProperties.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			modifyProperty(key, (String) modifiedProperties.get(key));
		}
	}

	public void modifyProperty(String key, boolean bool) {
		modifiedProps.put(key, String.valueOf(bool));
	}

	/**
	 * 将所有的修改内容写入配置文件.<BR>
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public void saveModifies() throws IOException {
		// 1. 判断文件是否存在.
		if (false == file.isFile()) {
			throw new FileNotFoundException("The file: " + file.getAbsolutePath() + " not exist or not a file!");
		}
		// 不需要更改则直接返回
		if (modifiedProps.size() == 0 && toDelProps.size() == 0 && toRemProps.size() == 0) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Modified properties size = 0, so return directly!");
			}
			return;
		}
		// 2. 准备相应变量.
		InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "ISO-8859-1");
		BufferedReader br = new BufferedReader(isr);
		int bufferSize = (file.length() > Short.MAX_VALUE) ? Short.MAX_VALUE : (int) file.length();
		sb = new StringBuffer(bufferSize);
		String key = null;
		String newValue = null;
		boolean lineDone = false;
		boolean canReadFile = false;
		// 3. 更新文件内容(一些Properties key的新取值)到buffer中.
		try {
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if (false == canReadFile) {
					canReadFile = true;
				}

				// 3.0 保留空行和注�?
				String trimedLine = line.trim();
				if (trimedLine.length() == 0 || trimedLine.startsWith("#")) {
					sb.append(line);
					sb.append(NEWLINE);
					continue;
				}
				int indexEqualChar = trimedLine.indexOf('=');
				int indexSpaceChar = trimedLine.indexOf(' ');
				if (indexEqualChar <= 0 && indexSpaceChar <= 0) {
					sb.append(line);
					sb.append(NEWLINE);
					continue;
				}
				// 3.0 保留空行和注释
				if (indexEqualChar > 0) {
					String curKey = trimedLine.substring(0, indexEqualChar).trim();
					// 应被注释
					if (toRemProps.containsKey(curKey)) {
						trimedLine = "#" + trimedLine;
						sb.append(trimedLine);
						sb.append(NEWLINE);

						if (LOG.isDebugEnabled()) {
							LOG.debug("Rem property key[" + curKey + "], value[" + trimedLine + "]");
						}
					}
					// 应被删除
					if (toDelProps.containsKey(curKey)) {

						if (LOG.isDebugEnabled()) {
							LOG.debug("Delete property key[" + curKey + "]");
						}
						continue;
					}

				}

				// 3.1 将标志量置为未写入该行.
				lineDone = false;
				// 3.2 检查该行是否符合一个更新项, 符合则完成更新, 并将标志量置为已写入该行.
				for (Iterator iter = modifiedProps.keySet().iterator(); iter.hasNext();) {
					key = (String) iter.next();
					newValue = modifiedProps.getProperty(key);
					if (trimedLine.startsWith(key) && false == trimedLine.startsWith(key + ".")) {
						sb.append(key).append('=').append(newValue);
						sb.append(NEWLINE);
						lineDone = true;
						modifiedProps.remove(key);
						if (LOG.isDebugEnabled()) {
							LOG.debug("Modify property key[" + key + "], value[" + newValue + "]");
						}
						break;
					}
				}
				// 3.3 该行不符合任一个更新项, 则写入原行.
				if (lineDone == false) {
					sb.append(line);
					sb.append(NEWLINE);
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("Trimed Line[" + trimedLine + "]");
				}
			}
			// 4. 将没找到匹配的配置项和内容写入buffer.(新配置项或被注释掉的配置项)
			for (Iterator iter = modifiedProps.keySet().iterator(); iter.hasNext();) {
				key = (String) iter.next();
				newValue = modifiedProps.getProperty(key);

				sb.append(key).append('=').append(newValue);
				sb.append(NEWLINE);
			}

		} catch (IOException e) {
			LOG.error("fail! key=" + key + ", value=" + newValue, e);
			throw e;
		} finally {
			br.close();
		}

		if (sb.length() > 0 && canReadFile) {
			try {
				// 5. 将buffer�?��内容写入实际文件.
				Writer writer = new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1");
				writer.write(sb.toString());
				writer.close();

				if (LOG.isDebugEnabled()) {
					LOG.debug("Update config file[" + file.getAbsolutePath() + "], sb.length=" + sb.length()
							+ ", content=" + sb.toString());
				} else {
					LOG.info("Update config file[" + file.getAbsolutePath() + "], sb.length=" + sb.length());
				}
				// 如果写入成功，则清除modifiedProps
				modifiedProps.clear();
			} catch (Exception e) {
				LOG.error("Can't write config file[" + file.getAbsolutePath() + "]:" + e.getMessage(), e);
			}
		} else {
			LOG.error("Can't update config file[" + file.getAbsolutePath() + "]: content length[" + sb.length()
					+ "], canReadFile[" + canReadFile + "]!");
		}
	}
}
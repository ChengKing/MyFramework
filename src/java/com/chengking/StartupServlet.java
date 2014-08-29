package com.chengking;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.log4j.Logger;
public class StartupServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 启动线程
	 */
	private StartupThread startupThread = null;
	
	/**
	 * 上下文
	 */
	private MkContext context = null;

	/**
	 * 日志
	 */
	private static final Logger logger = Logger.getLogger(StartupServlet.class);

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		context = new MkContext();
		context.setGlobalCfg(config.getInitParameter("projectCfg"));
		if(logger.isDebugEnabled()){
			logger.debug("init StartServlet config is ["+config+"] ! MkContext has create !");
		}
		startupThread = new StartupThread(context);
		startupThread.setName("MK-StartThread");
		startupThread.start();
	}

	/**
	 * 
	 */
	@Override
	public void destroy() {
		if(startupThread != null){
			startupThread = null;
		}
		if(context != null){
			context.stop();
			context = null;
		}
		super.destroy();
	}
}

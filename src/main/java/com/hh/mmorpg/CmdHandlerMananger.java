package com.hh.mmorpg;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.ServiceHandler;
import com.hh.mmorpg.domain.User;

public class CmdHandlerMananger {

	public static final CmdHandlerMananger INSATANCE = new CmdHandlerMananger();

	private Map<Integer, ServiceHandler> methodMap;

	private CmdHandlerMananger() {
		methodMap = new HashMap<Integer, ServiceHandler>();

		init();

	}

	public void invokeHandler(String cmd, User user, CMDdomain cmdDomain) {
		int serviceId = getServiceId(cmd);
		ServiceHandler handler = methodMap.get(serviceId);
		if (handler == null) {
			return;
		}
		handler.invodeMethod(cmd, user, cmdDomain);
	}

	private void init() {
		String packageName = CmdHandlerMananger.class.getPackage().getName();

		List<String> classNames = getClassName(packageName, true);

		for (String name : classNames) {
			Class<?> c = null;
			try {
				c = Class.forName(name);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Extension extension = (Extension) c.getAnnotation(Extension.class);
			if (extension == null) {
				continue;
			}

			ServiceHandler handler = null;
			if (methodMap.get(extension.id()) == null) {
				try {
					handler = new ServiceHandler(extension.id(), c.newInstance());
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				methodMap.put(extension.id(), handler);
				for (Method method : c.getMethods()) {
					CmdService annotation = method.getAnnotation(CmdService.class);
					if (annotation == null) {
						continue;
					}

					String cmdKey = annotation.cmd();
					handler.addMethod(cmdKey, method);
				}
			}

		}
	}

	private List<String> getClassName(String packageName, boolean childPackage) {
		List<String> fileNames = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String packagePath = packageName.replace(".", "/");
		URL url = loader.getResource(packagePath);
		if (url != null) {
			String type = url.getProtocol();
			if (type.equals("file")) {
				fileNames = getClassNameByFile(url.getPath(), null, childPackage);
			}
		}
		return fileNames;
	}

	private List<String> getClassNameByFile(String filePath, List<String> className, boolean childPackage) {
		List<String> myClassName = new ArrayList<String>();
		File file = new File(filePath);
		File[] childFiles = file.listFiles();
		for (File childFile : childFiles) {
			if (childFile.isDirectory()) {
				if (childPackage) {
					myClassName.addAll(getClassNameByFile(childFile.getPath(), myClassName, childPackage));
				}
			} else {
				String childFilePath = childFile.getPath();
				if (childFilePath.endsWith(".class")) {
					childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9,
							childFilePath.lastIndexOf("."));
					childFilePath = childFilePath.replace("\\", ".");
					myClassName.add(childFilePath);
				}
			}
		}
		return myClassName;
	}

	private int getServiceId(String cmd) {
		return Integer.parseInt(cmd.split("_")[0]);
	}
}

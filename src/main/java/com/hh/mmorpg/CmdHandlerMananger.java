package com.hh.mmorpg;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CMDdomain;
import com.hh.mmorpg.domain.ServiceHandler;
import com.hh.mmorpg.domain.User;

public class CmdHandlerMananger {

	private Map<Integer, ServiceHandler> methodMap;

	public void init() throws ClassNotFoundException {
		String packageName = CmdHandlerMananger.class.getPackage().getName();

		List<String> classNames = getClassName(packageName, true);

		for (String name : classNames) {
			Class<?> c = Class.forName(name);

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
			}

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

	public List<String> getClassName(String packageName, boolean childPackage) {
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

//	public void beforInvoke() {
//
//	}

	public void invokeHandler(String cmd, User user, CMDdomain cmdDomain) {
		int serviceId = getServiceId(cmd);
		ServiceHandler handler = methodMap.get(serviceId);
		handler.invodeMethod(cmd, user, cmdDomain);
	}

//	public void afterInvoke() {
//
//	}

	private int getServiceId(String cmd) {
		return Integer.parseInt(cmd.split("_")[0]);
	}
}

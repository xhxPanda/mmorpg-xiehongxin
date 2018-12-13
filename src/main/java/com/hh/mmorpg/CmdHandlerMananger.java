package com.hh.mmorpg;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hh.mmorpg.annotation.CmdService;
import com.hh.mmorpg.annotation.Extension;
import com.hh.mmorpg.domain.CmdDomain;
import com.hh.mmorpg.domain.ServiceHandler;
import com.hh.mmorpg.domain.User;

public class CmdHandlerMananger {

	public static final CmdHandlerMananger INSATANCE = new CmdHandlerMananger();

	private Map<String, ServiceHandler> methodMap;

	private CmdHandlerMananger() {
		methodMap = new HashMap<String, ServiceHandler>();

		init();

	}

	public void invokeHandler(User user, CmdDomain cmdDomain) {

		ServiceHandler handler = methodMap.get(cmdDomain.getStringParam(0));
		if (handler == null) {
			return;
		}
		try {
			handler.getMethod().invoke(handler.getClassInstance(), user, cmdDomain);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void init() {
		String packageName = CmdHandlerMananger.class.getPackage().getName();

		List<String> classNames = getClassName(packageName, true);

		for (String name : classNames) {
			Class<?> c = null;
			try {
				c = Class.forName(name);
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}

			Extension extension = (Extension) c.getAnnotation(Extension.class);
			if (extension == null) {
				continue;
			}

			ServiceHandler handler = null;

			for (Method method : c.getMethods()) {
				CmdService annotation = method.getAnnotation(CmdService.class);
				if (annotation == null) {
					continue;
				}

				try {
					handler = new ServiceHandler(extension.id(), c.newInstance(), method);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				methodMap.put(annotation.cmd(), handler);
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

}

package com.hh.mmorpg.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author xhx
 * 这个类注解的方法表示该方法是处理前端请求的方法
 *
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CmdService {
	public String cmd();
	
}

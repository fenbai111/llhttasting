package com.jlt.vote.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ValidateFiled {
	
	/**
	 * 参数索引位置
	 */
    int index() default -1 ;
	
	/**
	 * 参数名称
	 */
    String desc() default "";
	
	/**
	 * 正则验证,指定的参数只能是String类型
	 */
    String regStr() default "";
	
	/**
	 * 是否能为空  ， 为true表示不能为空 ， false表示能够为空
	 */
    boolean notNull() default false;
	
	/**
	 * 是否能为空  ， 为true表示不能为空 ， false表示能够为空
	 */
    boolean notEmpty() default false;
	
	/**
	 * 是否能为空  ， 为true表示不能为空 ， false表示能够为空
	 */
    int maxLen() default -1 ;
	
	/**
	 * 最小长度 ， 用户验证字符串
	 */
    int minLen() default -1 ;
	
	/**
	 *最大值 ，用于验证数字类型数据
	 */
    int maxVal() default -1 ;
	
	/**
	 *最小值 ，用于验证数值类型数据
	 */
    int minVal() default -1 ;
	
}

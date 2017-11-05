package com.jlt.vote.validation;

import com.jlt.vote.util.ResponseUtils;
import com.xcrm.common.util.ListUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 验证注解处理类
 */
@Component
@Aspect
public class ValidateAspectHandle {

	@Pointcut("@annotation(com.jlt.vote.validation.ValidateGroup)")
	public void validatePointCut(){}
	
	@SuppressWarnings("finally")
	@Around("validatePointCut()")  
	public Object validateAround(ProceedingJoinPoint joinPoint) throws Throwable  {
		ValidateEntity validateEntity = null;
		ValidateGroup an = null;
		Object[] args =  null ;
		Method method = null;
		Object target = null ;
		String methodName = null;
		try{
			methodName = joinPoint.getSignature().getName();
			target = joinPoint.getTarget();
			method = getMethodByClassAndName(target.getClass(), methodName);	//得到拦截的方法
			args = joinPoint.getArgs();		//方法的参数	
			an = (ValidateGroup)getAnnotationByMethod(method ,ValidateGroup.class );
			validateEntity = validateFiled(an.fileds() , args);
		}catch(Exception e){
			validateEntity = new ValidateEntity(false);
		}finally{
			if(validateEntity.isResult()){
				return joinPoint.proceed();
			}else{
				//是否需要打印到model中
				if(an.isModel()){
					ModelAndView modelAndView = null;
					//返回错误页面
					if(!"".equals(an.urlPath())){
						modelAndView = new ModelAndView(an.urlPath());
					}else{
						modelAndView = new ModelAndView("/error");
					}
					modelAndView.addObject("errors", validateEntity.getErrorMessage());
					//返回错误页面
					return modelAndView;
				}else{
					//返回json错误消息
					for(Object tmpArgs:args){
						if(tmpArgs instanceof HttpServletResponse){
							HttpServletResponse response = (HttpServletResponse) tmpArgs;
							ResponseUtils.createBadResponse(response,validateEntity.getErrorMessage());
						}
					}
					return "";

				}
			}
		}
	}

	/**
	 * 验证参数是否合法
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public ValidateEntity validateFiled( ValidateFiled[] valiedatefiles , Object[] args) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
		StringBuilder sb = new StringBuilder();
		boolean result = true;
		for (ValidateFiled validateFiled : valiedatefiles) {
			Object arg = null;
			arg = args[validateFiled.index()];

			if(validateFiled.notNull()){		
				//判断参数是否为空
				if(arg == null ){
					sb.append("参数 " + validateFiled.desc() + "为空  ");
				    result =  false;
				    continue;
				}
			}else{		
				//如果该参数能够为空，并且当参数为空时，就不用判断后面的了 ，直接返回true
				if(arg == null )
					continue;
			}
			
			if(validateFiled.notEmpty()){		
				//判断参数是否为空
				if(arg == null ){
					sb.append("参数 " + validateFiled.desc() + "为空  ");
				    result =  false;
				    continue;
				}else{
					if(arg instanceof List){
						List check = (List) arg;
						if(ListUtil.isEmpty(check)){
							sb.append("参数 " + validateFiled.desc() + "为空  ");
						    result =  false;
						    continue;
						}
					}else if(arg.getClass().isArray()){
						Object[] check = (Object[]) arg;
						if(check.length==0){
							sb.append("参数 " + validateFiled.desc() + "为空  ");
						    result =  false;
						    continue;
						}
					}else if(arg instanceof String){
						String check = (String) arg;
						if(check.isEmpty()){
							sb.append("参数 " + validateFiled.desc() + "为空  ");
						    result =  false;
						    continue;
						}
					}
				}
			}else{		
				//如果该参数能够为空，并且当参数为空时，就不用判断后面的了 ，直接返回true
				if(arg == null )
					continue;
			}

			if((validateFiled.maxLen() > 0)
					&&(((String)arg).length() > validateFiled.maxLen())){		
				//判断字符串最大长度
				sb.append("参数 " + validateFiled.desc() + "长度大于最大长度  ");
			    result =  false;
			    continue;
			}

			if((validateFiled.minLen() > 0)
					&&(((String)arg).length() < validateFiled.minLen())){		
				//判断字符串最小长度
				sb.append("参数 " + validateFiled.desc() + "小于最小长度  ");
			    result =  false;
			    continue;
			}

			if((validateFiled.maxVal() != -1)
				&&((Integer)arg > validateFiled.maxVal())){	
				//判断数值最大值
				sb.append("参数 " + validateFiled.desc() + "大于最大值  ");
		        result =  false;
		        continue;
			}

			if((validateFiled.minVal() != -1 )
					&& ((Integer)arg < validateFiled.minVal())){	
				//判断数值最小值
				sb.append("参数 " + validateFiled.desc() + "小于最小值  ");
		        result =  false;
		        continue;
			}

			if((!"".equals(validateFiled.regStr()))
					&&(!((String)arg).matches(validateFiled.regStr()))){	//判断正则
				sb.append("参数 " + validateFiled.desc() + "不符合规则  ");
		        result =  false;
		        continue;
			}
		}
		return new ValidateEntity(result,sb.toString());
	}

	/**
	 * 根据对象和属性名得到 属性
	 */
	public Object getFieldByObjectAndFileName(Object targetObj , String fileName) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		String tmp[] = fileName.split("\\.");
		Object arg = targetObj ;
		for (int i = 0; i < tmp.length; i++) {
			Method methdo = arg.getClass().
					getMethod(getGetterNameByFiledName(tmp[i]));
			arg = methdo.invoke(arg);
		}
		return arg ;
	}

	/**
	 * 根据属性名 得到该属性的getter方法名
	 */
	public String getGetterNameByFiledName(String fieldName){
		return "get" + fieldName.substring(0 ,1).toUpperCase() + fieldName.substring(1) ;
	}

	/**
	 * 根据目标方法和注解类型  得到该目标方法的指定注解
	 */
	public Annotation getAnnotationByMethod(Method method , Class annoClass){
		Annotation all[] = method.getAnnotations();
		for (Annotation annotation : all) {
			if (annotation.annotationType() == annoClass) {
				return annotation;
			}
		}
		return null;
	}

	/**
	 * 根据类和方法名得到方法
	 */
	public Method getMethodByClassAndName(Class c , String methodName){
		Method[] methods = c.getDeclaredMethods();
		for (Method method : methods) {
			if(method.getName().equals(methodName)){
				return method ;
			}
		}
		return null;
	}
	
}

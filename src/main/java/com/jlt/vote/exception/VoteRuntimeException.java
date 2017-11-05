package com.jlt.vote.exception;

/**
 * 核心业务错误，模块业务异常基类
 * 继承RuntimeException 抛出时 事务回滚
 * @Author gaoyan
 * @Date: 2017/7/30
 */
public class VoteRuntimeException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private Object extraInformation;
	
	private Object[] errorContents;
	
	private String errorCode;

	public VoteRuntimeException() {

	}

	public VoteRuntimeException(String errorCode) {
		super("errorCode=" +errorCode);
		this.errorCode = errorCode;
	}
	
	public VoteRuntimeException(String errorCode, Object...errorContents) {
		super("errorCode=" +errorCode);
		this.errorCode = errorCode;
		this.errorContents = errorContents;
	}

	public VoteRuntimeException(String errorCode, String msg) {
		super(msg);
		this.errorCode = errorCode;
		Object[] o = new Object[1];
		o[0] = msg;
		this.errorContents = o;
	}
	
	public VoteRuntimeException(String errorCode, String msg, Object extraInformation) {
		super(msg);
		this.errorCode = errorCode;
		this.extraInformation = extraInformation;
	}

	/**
	 * Any additional information about the exception. Generally a
	 * <code>UserDetails</code> object.
	 * 
	 * @return extra information or <code>null</code>
	 */
	public Object getExtraInformation() {
		return extraInformation;
	}

	public void clearExtraInformation() {
		this.extraInformation = null;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public Object[] getErrorContents() {
		return errorContents;
	}

}

package main.mmwork.com.mmworklib.http;

/***
 * Http请求单元
 */
public abstract class BaseTask<REQ>{
	protected REQ req;


	public BaseTask(REQ req){
		this.req = req;
	} 
	
	/***
	 * 整个请求业务都放置在这个地方执行
	 */
	public abstract void doTask();

	/***
	 * 内部引用的资源要进行释放掉
	 */
	public abstract void recyle();

	/****
	 * 获取请求序列号
	 * @return
	 */
	public abstract int getReqSeqNo();

	/***
	 * 断开连接
	 */
	public abstract void disConnect();
}

package github.light.TimerExecute.plugin;

/**
 * 简易计时器类,单独开辟一个线程
 * @author Light
 *
 */
public abstract class TestingTimer{
	
	/**
	 * 同步资源计时器创建
	 * @param resource 同步的资源
	 */
	public TestingTimer(Object resource) {
		
		this.append = true;
		
		this.resource = resource;
		
	}
	
	/**
	 * 默认计时器创建
	 */
	public TestingTimer() {}

	//计时器是否停止
	private boolean flag = true;
	
	//此线程同步还是异步
	private boolean append = false;
	
	//同步的资源
	private Object resource;
	
	/**
	 * 计时器运行主体
	 */
	protected abstract void runMethod() throws Exception;
	
	/**
	 * 调用时让此计时器停止运行(安全)
	 */
	public void stop() {
		
		flag = false;
		
	}
	
	/**
	 * 获得这个计时器是不是被暂停
	 * @return 
	 */
	public boolean isStop() {
		
		return !flag;
		
	}
	
	/**
	 * 运行重写的runMethod方法
	 * @param delay 计时器开启之前的延时
	 * @param num runMethod需要执行多少遍,传入-1会进入死循环
	 * @param interval 运行一次之后的间隔(隔多久再执行一次)
	 */
	public void start(long delay,int num,long interval){
		
		new Thread(new Thread01("计时器") {
			
			public void run() {
				
				int num1 = num;
				
				try {
				
					Thread.sleep(delay);
				
					if(num == -1)
						
						while(flag) {
							
							if(append && (resource != null)) {
							
								synchronized (resource) {
								
									runMethod();
									
								}
							
							}else
								
								runMethod();
						
							Thread.sleep(interval);
						
						}
					
					while((num1 > 0) && flag){
						
						runMethod();
					
						num1--;
					
						Thread.sleep(interval);
						
					}
				
				} catch (Exception e) {
					
					e.printStackTrace();
				
				}
				
			}
			
		}).start();
		
	}
	
	//单独开辟的线程内部类
	private class Thread01 extends Thread{
		
		private Thread01(String name){
			
			super(name);
			
		}
		
	}
	
}

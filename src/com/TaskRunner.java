/*
 * @author - Jeremy Trifilo (Digistr).
*/

package com;

import java.lang.InterruptedException;

public class TaskRunner extends Thread
{
	private final BotScript SCRIPT;

	public TaskRunner(final BotScript SCRIPT)
	{
		this.SCRIPT = SCRIPT;
		Thread executor = new Thread(this);
		executor.setPriority(Thread.MAX_PRIORITY);
		executor.start();
	}

	public void run() {
		int sleep = 0;
		long start = System.nanoTime();
		while (!SCRIPT.isShutdown()) 
		{
			try 
			{
				OnDemandLoader.handleRequests();
			} catch (Exception e) { 

			}
			long end = System.nanoTime();
			sleep = (int)((end - start) * 0.000001) - (500 - sleep);
			start = System.nanoTime();
			if (sleep > 500)
				sleep = 500;
			try 
			{
				Thread.sleep(500 - sleep);
			} catch (InterruptedException ie){ 

			}
		}
	}
}
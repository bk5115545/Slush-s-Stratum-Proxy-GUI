package crypto.currency;

import java.io.BufferedInputStream;
import java.io.IOException;

import crypto.currency.workers.Worker;
import crypto.currency.workers.WorkerRegistry;

public class Main {

	public static boolean running = true;
	static long submitted = 0;
	static long accepted = 0;
	static long rejected = 0;
	static int exactTime = 0;
	static int lastLine = 0;
	
	static Thread killThread = null;
	static InputProcessor inProc = null;

	public static void main(String[] args) {
		String argString = "";
		System.out.println(argString);
		for(String a : args) argString += " " + a;
		String cmd = "mining_proxy.exe" + argString;
		new WorkerRegistry();
		try {
			final Process p = Runtime.getRuntime().exec(cmd);
			try {
				killThread = new Thread(new Runnable() {
					@Override
					public void run() {
						p.destroy();
						try {
							p.waitFor();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						try {
							System.out.println(p.exitValue());
						} catch (Exception e) {
							//kill another way then
							try {
								Runtime.getRuntime().exec("taskkill /F /IM mining_proxy.exe");
							} catch (IOException e1) {
								System.err.println("The mining proxy could not be forceably closed.  You are on windows right?\nIf you read this then I really need a better way of doing this.  LET ME KNOW!");
							}
						}
					}
				});
				Runtime.getRuntime().addShutdownHook(killThread);
			} catch (Exception e2) { 
				System.err.println("The proxy has started but it may not be closed when this program closes.");
			}
			inProc = new InputProcessor(new BufferedInputStream(p.getErrorStream()));
			inProc.start();
		} catch (IOException e) {
			System.err.println("The mining proxy could not be started because it was not found.");
		} finally {}

		while(running) {
			try {
				Thread.currentThread();
				Thread.sleep(1000);
			} catch (Exception e) {}
			
			try {
				Runtime.getRuntime().exec(new String[]{"cmd","/c","cls"});
				//wtf why does this not work?
				for(int i=0; i<16; i++) System.out.print("\n\n\n\n\n\n\n\n"); //128 lines
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Runtime.getRuntime().exec("clear");
			} catch (Exception e) {}
			
			
			System.out.println("-------------------------------------------------------------------");
			System.out.print("S: "+ submitted + "\t"); System.out.print("A: " + accepted + "\t"); System.out.println("R: " + rejected);
			System.out.println("Submit Time: " + exactTime);
			System.out.println("-------------------------------------------------------------------");
			
			String[] names = WorkerRegistry.getNames();
			for(String name : names) {
				Worker w = WorkerRegistry.pollWorker(name);
				
				System.out.print("U: " + w.name + "\t");
				System.out.print("A: " + w.accepted + "   ");
				System.out.print("R: " + w.rejected + "   ");
				System.out.print("S: " + w.submitted + "   ");
				//System.out.print("Q: " + w.downloaded + "   "); //something is off here
				System.out.print("Diff: " + w.currentDiff);
				System.out.println();
			}
			
			System.out.println(); System.out.println();
			
			
			for(int i=0; i<16; i++) {
				System.out.println(inProc.readCache(i));
			}
			
		}
		 
	}

}

package crypto.currency;

import java.io.InputStream;
import java.util.Scanner;

import crypto.currency.workers.Worker;
import crypto.currency.workers.WorkerRegistry;

public class InputProcessor extends Thread {
	
	private InputStream source;
	private Object cacheLock = new Object();
	private String[] cache = new String[16];
	private int lastLine = 0;
	private int cacheIndex = 0;
	
	public InputProcessor(InputStream in) {
		source = in;
	}
	
	@Override
	public void run() {
		Scanner sc = new Scanner(source);
		
		while(Main.running) {
			try {
				Thread.currentThread();
				Thread.sleep(1);
			} catch (Exception e) {}
			
			while(sc.hasNextLine()) {
				String line = null;
				try {
					line = sc.nextLine();
					synchronized(cacheLock) { cache[cacheIndex] = line; }
					if(Integer.parseInt(line.charAt(0)+"")>1000) throw new Exception();
					if(line.equals("ENDING")) break;
					
					String[] elements = line.split(" ");
					
					
					if(elements.length > 5 && elements[2].equals("INFO") && elements[4].equals("jobs.submit")) {
						Main.submitted++;
						Main.submitted %= Long.MAX_VALUE;
					}

					if(elements.length > 7 && elements[6].equals("Worker")) {
						String name = elements[7].replaceAll("'", "");
						Worker w = WorkerRegistry.pollWorker(name);
						if(w == null) w = WorkerRegistry.addWorker(name);
						w.downloaded++;
						w.downloaded %= Long.MAX_VALUE;
					}
					else if(elements.length > 10 && elements[6].startsWith("[")) {
						elements[6] = elements[6].substring(1, elements[6].length()-3);
						Main.exactTime = Integer.parseInt(elements[6]);

						String name = elements[9].replaceAll("'", "");
						Worker w = WorkerRegistry.pollWorker(name);
						w.currentDiff = Integer.parseInt(elements[12]);
						if(elements[10].equals("accepted,")) {
							Main.accepted++;
							Main.accepted %= Long.MAX_VALUE;
							w.accepted += w.currentDiff;
							w.accepted %= Long.MAX_VALUE;
						}
						else {
							Main.rejected++;
							Main.rejected %= Long.MAX_VALUE;
							w.rejected += w.currentDiff;
							w.rejected %= Long.MAX_VALUE;
						}
						w.submitted++;
						w.submitted %= Long.MAX_VALUE;
					}
					cacheIndex = (cacheIndex+1)%cache.length;
					
				} catch(Exception e) {
					//e.printStackTrace();
					System.err.println("ERROR PARSING LINE: " + line);
					
				}
			}
		}
		sc.close();
	}
	
	public int prepCacheRead() {
		lastLine = cacheIndex;
		return lastLine;
	}
	
	public String readCache(int line) {
		synchronized (cacheLock) {
			return cache[Math.abs(line)%16];
		}
	}
}

package crypto.currency.workers;

import java.util.Hashtable;

public class WorkerRegistry {

	public static Hashtable<String, Worker> table;

	private static Object tableLock = new Object();

	public WorkerRegistry() {
		if(table == null) {
			table = new Hashtable<String, Worker>();
		}
		else {
			System.err.println("ALREADY HAVE WORKER REGISTRY!");
		}
	}

	public static Worker pollWorker(String name) {
		synchronized (tableLock) {
			if(name == null) return null;
			return table.get(name);
		}
	}

	public static Worker addWorker(String name) {
		synchronized (tableLock) {
			Worker w;
			if((w=pollWorker(name))!=null) return w;
			w = new Worker(name);
			table.put(name, new Worker(name));
			return w;
		}
	}

	public static boolean removeWorker(String name) {
		synchronized (tableLock) {
			return table.remove(name)==null;
		}
	}

	public static String[] getNames() {
		synchronized (tableLock) {
			String[] names = new String[table.size()];
			table.keySet().toArray(names);
			return names;
		}
	}

}

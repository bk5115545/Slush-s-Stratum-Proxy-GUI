package crypto.currency.workers;

public class Worker {
	
	public String name = "default";
	public long downloaded = 0;
	public long submitted = 0;
	public long accepted = 0;
	public long rejected = 0;
	public long currentDiff = 0;
	public long startTime = 0;
	
	protected Worker(String name) {
		this.name = name;
		startTime = System.currentTimeMillis();
	}
}

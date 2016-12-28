package apjp2016 ;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class HW53 {	

	/** 
	 * IntPair is used to store a pair of ints. 
	 * @author chencc
	 *
	 */
	static class IntPair {
      public  int fst, snd ;
	  IntPair(int f, int s) {  fst = f; snd = s; }
	  public String toString(){
		  return "(" + fst + "," + snd + ")" ; 
	  }
	}
	
	

 public static void main(String[] args) {

	    // this queue is used to hold at most 2 pairs put by producers. 
		BlockingQueue<IntPair> queue = new ArrayBlockingQueue<>(2);
		

		Producer[] ps = new Producer[4];
		for (int k = 0; k < 4; k++) {
			ps[k] = new Producer(queue, k);
		}

		Consumer[] cs = new Consumer[4];
		for (int k = 0; k < 4; k++) {
			cs[k] = new Consumer(queue, k);
		}

		// Using fixed-sized ThreadPool with size(3) < number of producers (4)
		// will result in deadlock. The available threads(3) will be hold by producers which
		// are blocked in the blockingQueue and cannot terminate.

		// ExecutorService es = Executors.newFixedThreadPool(3);

		ExecutorService es = Executors.newCachedThreadPool();

		long startTime = System.currentTimeMillis();

		for (Producer p : ps) {
			es.execute(p);
		}

		for (Consumer csumer : cs) {
			es.execute(csumer);
		}

		es.shutdown();

		while (!es.isTerminated()) {
			try {
				es.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			} catch (InterruptedException e) {
			}		}

		long endTime = System.currentTimeMillis();
		System.out.println("The total execution time is "
				+ (endTime - startTime) + " milliseconds");

	}
	
	public static class Producer implements Runnable {
		private BlockingQueue<IntPair> q;
		private int id;

		public Producer(BlockingQueue<IntPair> q, int id) {
			this.q = q;
			this.id = id;
		}

		public void run() {
			for (int i = 0; i < 5; i++)
				for (int j = 0; j < 5; j++) {
					try{
						q.put(new IntPair(i, j));
						System.out.println("Producer #" + this.id + " put: (" + i + "," + j + ").");
						Thread.sleep((int) (Math.random() * 100));
					} catch (InterruptedException e) {
					}
				}

		}
	}

	public static class Consumer implements Runnable {
		private BlockingQueue<IntPair> q;
		private int id;

		public Consumer(BlockingQueue<IntPair> q, int id) {
			this.q = q;
			this.id = id;
		}

		public void run() {
			for (int i = 0; i < 25; i++) {
			List<IntPair> c = new CopyOnWriteArrayList<IntPair>();

				q.drainTo(c,2);
				try {
					for(IntPair a:c) {
						System.out.println("Consumer #" + this.id + " got: " + a.fst + a.snd);
						c.remove(a);
					}
					Thread.sleep((int) (Math.random() * 100));

				} catch (InterruptedException e) {
				}
			}

		}
	}

	

}

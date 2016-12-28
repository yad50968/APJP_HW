package apjp2016;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HW52 {
	

	/**
	 * CubbyHole2 can hold at most 2 pairs of numbers
	 * Implement this class to satisfy the requirement 
	 * 
	 * @author chencc
	 * 
	 */
	public static class CubbyHole2 {
		


      // the following code need to be modified
		private int x, y;

        private int pairCount = 0;
		public synchronized int get() {
			while ( pairCount == 0) {
				try {
					this.wait();
				} catch (InterruptedException e) {
				}
			}
			pairCount = 0;
			notifyAll(); // notify all producer/consumer to compete for
							// execution!
			// use notify() if just wanting to wakeup one thread!
			return x + y;
		}

		public synchronized void put(int a, int b) {
			while ( pairCount == 2) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
			pairCount++;
			x = a;
			y = b;
			notifyAll(); // or notify(); }}
		}
	}

	public static class Producer implements Runnable {
		private CubbyHole2 cubbyhole;
		private int id;

		public Producer(CubbyHole2 c, int id) {
			cubbyhole = c;
			this.id = id;
		}

		public void run() {
			for (int i = 0; i < 5; i++)
				for (int j = 0; j < 5; j++) {
					cubbyhole.put(i, j);
					System.out.println("Producer #" + this.id + " put: (" + i
							+ "," + j + ").");
					try {
						Thread.sleep((int) (Math.random() * 100));
					} catch (InterruptedException e) {
					}
				}

		}
	}

	public static class Consumer implements Runnable {
		private CubbyHole2 cubbyhole;
		private int id;

		public Consumer(CubbyHole2 c, int id) {
			cubbyhole = c;
			this.id = id;
		}

		public void run() {
			int value = 0;
			for (int i = 0; i < 25; i++) {
				value = cubbyhole.get();
				System.out.println("Consumer #" + this.id + " got: " + value);
				try {
					Thread.sleep((int) (Math.random() * 100));
				} catch (InterruptedException e) {
				}
			}
			
		}
	}

	public static void main(String[] args) {

		CubbyHole2 c = new CubbyHole2();
		

		Producer[] ps = new Producer[4];
		for (int k = 0; k < 4; k++) {
			ps[k] = new Producer(c, k);
		}

		Consumer[] cs = new Consumer[4];
		for (int k = 0; k < 4; k++) {
			cs[k] = new Consumer(c, k);
		}

		// Using fixed-sized ThreadPool with size(3) < number of producers (5)
		// will result in deadlock. The available threads(3) will be hold by producers which
		// are blocked in the blockingQueue and cannot terminate.
		
		//ExecutorService es = Executors.newFixedThreadPool(4);
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
			}
		}

		long endTime = System.currentTimeMillis();
		System.out.println("The total execution time is "
				+ (endTime - startTime) + " milliseconds");

	}

}

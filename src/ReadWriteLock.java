
/**
 * A simple custom lock that allows simultaneously read operations, but
 * disallows simultaneously write and read/write operations.
 *
 * Does not implement any form or priority to read or write operations. The
 * first thread that acquires the appropriate lock should be allowed to
 * continue.
 */
public class ReadWriteLock {
	private int readers;
	private int writers;

	/**
	 * Initializes a multi-reader single-writer lock.
	 */
	public ReadWriteLock() {
		readers = 0;
		writers = 0;
	}

	/**
	 * Will wait until there are no active writers in the system, and then will
	 * increase the number of active readers.
	 */
	public synchronized void lockReadOnly() {
		try {
			while (writers > 0) {
				this.wait();
			}
		} catch (InterruptedException e) {
			System.out.println("InterruptedException"); // TODO Fix
		}
		readers++;
	}

	/**
	 * Will decrease the number of active readers, and notify any waiting
	 * threads if necessary.
	 */
	public synchronized void unlockReadOnly() {
		readers--;
		if (readers <= 0) {
			this.notifyAll();
		}
		
		/*
		 * TODO Efficency issue here---you are over-notifying. Assume there are
		 * 10 active threads reading currently, i.e. readers == 10. If a writer
		 * thread tries to lockReadWrite(), it is forced to wait for those readers.
		 * 
		 * Now suppose one of those readers calls unlockReadOnly. The number of
		 * readers goes down to 9, and then the waiting writer is woken up. But,
		 * it still can't write yet so it goes back to sleep.
		 * 
		 * And then suppose another reader calls unlockReadOnly. The number of 
		 * readers goes down to 8, and the same thing happens again.
		 * 
		 * When should you really call notifyAll() so that you aren't unecessarily
		 * waking up the writer thread?
		 */
	}

	/**
	 * Will wait until there are no active readers or writers in the system, and
	 * then will increase the number of active writers.
	 */
	public synchronized void lockReadWrite() {
		try {
			while (readers > 0 || writers > 0) {
				this.wait();
			}
		} catch (InterruptedException e) {
			System.out.println("InterruptedException"); // TODO Fix
		} finally {
			writers++;
		}
	}

	/**
	 * Will decrease the number of active writers, and notify any waiting
	 * threads if necessary.
	 */
	public synchronized void unlockReadWrite() {
		writers--;
		this.notifyAll();
	}
}

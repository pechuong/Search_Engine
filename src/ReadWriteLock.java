
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
			System.out.println("Thread " + this.toString() + " woke up in lockReadOnly()");
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
			System.out.println("Thread " + this.toString() + " woke up in lockReadWrite()");
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

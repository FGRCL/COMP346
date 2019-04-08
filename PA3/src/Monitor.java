import java.util.ArrayList;

/**
 * Class Monitor To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor {
	public static enum State { hungry, eating, thinking, talking, sleeping }

	public ArrayList<State> states;
	
	private int sleepingCount;
	private boolean isTalking;
	private int talkPendingCount;
	public int nbAvailablePepperShakers = 2;
	private static double percentChancePhilosopherChange = 0.5;

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers) {
		states = new ArrayList<State>(piNumberOfPhilosophers);
		for (int i = 0; i < piNumberOfPhilosophers; i++) {
			states.add(State.thinking);
		}
		sleepingCount = 0;
		isTalking = false;
		talkPendingCount  = 0;
	}

	/*
	 * ------------------------------- User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final Philosopher phil) throws InterruptedException{
		states.set(phil.getIndexTID(), State.hungry);
		while (!canEat(phil.getIndexTID()))
		{
			wait();
		}
		states.set(phil.getIndexTID(), State.eating);
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down and
	 * let others know they are available.
	 */
	public synchronized void putDown(final Philosopher phil) {
		states.set(phil.getIndexTID(), State.thinking);
		notifyAll();
	}

	/**
	 * Only one philopher at a time is allowed to philosophy (while she is not
	 * eating).
	 */
	public synchronized void requestTalk(final Philosopher phil) throws InterruptedException{
		talkPendingCount++;
		while(isSomeoneTalking() || sleepingCount > 0 || isTalking) {
			wait();
		}
		isTalking = true;
		states.set(phil.getIndexTID(), State.talking);
	}

	/**
	 * When one philosopher is done talking stuff, others can feel free to start
	 * talking.
	 */
	public synchronized void endTalk(final Philosopher phil) {
		talkPendingCount--;
		isTalking = false;
		states.set(phil.getIndexTID(), State.thinking);
		notifyAll();
	}
	
	public synchronized void requestSleep(final Philosopher phil) throws InterruptedException{
		while(isSomeoneTalking() || talkPendingCount > 0) {
			wait();
		}
		sleepingCount++;
		states.set(phil.getIndexTID(), State.sleeping);
	}

	public synchronized void endSleep(final Philosopher phil) {
		sleepingCount--;
		states.set(phil.getIndexTID(), State.thinking);
		if(sleepingCount<0) {
			notifyAll();
		}
	}
	

	public synchronized void requestPepperShaker(final Philosopher phil) throws InterruptedException {
		while(nbAvailablePepperShakers<1) {
			wait();
		}
		nbAvailablePepperShakers--;
	}

	public synchronized void endPepperShaker(final int piTID) {
		nbAvailablePepperShakers++;
		notify();
	}
	
	private synchronized int leftPhilosopher(final int i) {
		return (i+states.size()-1)%states.size();
	}
	
	private synchronized int rightPhilosopher(final int i) {
		return (i+1)%states.size();
	}
	
	private synchronized boolean canEat(final int piTID) {
		boolean leftChopsitckAvailable = states.get(leftPhilosopher(piTID)) != State.eating && 
				(leftPhilosopher(piTID)% states.size()>=piTID || (leftPhilosopher(piTID)% states.size()<piTID && states.get(leftPhilosopher(piTID)) != State.hungry));
		boolean rightChopstickAvailable = states.get(rightPhilosopher(piTID)) != State.eating && 
				(rightPhilosopher(piTID)% states.size()>=piTID || (rightPhilosopher(piTID)% states.size()<piTID && states.get(rightPhilosopher(piTID)) != State.hungry));
		return leftChopsitckAvailable && rightChopstickAvailable;
	}
	
	private synchronized boolean isSomeoneTalking() {
		boolean isSomeoneTalking = false;
		for (State state : states) {
			if (state == State.talking) {
				isSomeoneTalking = true;
			}
		}
		return isSomeoneTalking;
	}
	
	public synchronized boolean addOrRemovePhilospshers(final int id) throws InterruptedException {
		boolean removedPhilosopher = false;
		double rand = Math.random();
		if(safeToChangeArray() && rand < percentChancePhilosopherChange) {
			rand = Math.random();
			if(rand <0.5) {
				addPhilosopher(id);
			} else {
				removePhilosopher(id);
				removedPhilosopher = true;
			}
		}
		return removedPhilosopher;

	}
	
	private synchronized void removePhilosopher(final int id) throws InterruptedException {
		System.out.println(id + " bids farewell to his fellow philosophers");
		states.remove(id-1);
		DiningPhilosophers.removePhilosopher(id);
	}

	private synchronized void addPhilosopher(final int id) {
		System.out.println(id + " invites a new philosopher to the table");
		states.add(Monitor.State.thinking);
		DiningPhilosophers.addPhilosopher(id);
	}
	
	private synchronized boolean safeToChangeArray() {
		boolean allThinking = true;
		for(State state: states) {
			if(state != State.thinking) {
				allThinking = false;
			}
		}
		return allThinking;
	}
}

// EOF

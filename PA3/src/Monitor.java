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
	public synchronized void pickUp(final int piTID) throws InterruptedException{
		states.set(piTID, State.hungry);
		while (!canEat(piTID))
		{
			wait();
		}
		states.set(piTID, State.eating);
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down and
	 * let others know they are available.
	 */
	public synchronized void putDown(final int piTID) {
		states.set(piTID, State.thinking);
		notifyAll();
	}

	/**
	 * Only one philopher at a time is allowed to philosophy (while she is not
	 * eating).
	 */
	public synchronized void requestTalk(final int piTID) throws InterruptedException{
		talkPendingCount++;
		while(isSomeoneTalking() || sleepingCount > 0 || isTalking) {
			wait();
		}
		isTalking = true;
		states.set(piTID, State.talking);
	}

	/**
	 * When one philosopher is done talking stuff, others can feel free to start
	 * talking.
	 */
	public synchronized void endTalk(final int piTID) {
		talkPendingCount--;
		isTalking = false;
		states.set(piTID, State.thinking);
		notifyAll();
	}
	
	public synchronized void requestSleep(final int piTID) throws InterruptedException{
		while(isSomeoneTalking() || talkPendingCount > 0) {
			wait();
		}
		sleepingCount++;
		states.set(piTID, State.sleeping);
	}

	public synchronized void endSleep(final int piTID) {
		sleepingCount--;
		states.set(piTID, State.thinking);
		if(sleepingCount<0) {
			notifyAll();
		}
	}
	

	public synchronized void requestPepperShaker(final int piTID) throws InterruptedException {
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

	public synchronized void addPhilosopher(final int piTID) {
		System.out.println((piTID+1)+" invites "+states.size());
		states.add(State.thinking);
	}

	public synchronized void removePhilosopher(final int piTID) {
		System.out.println((piTID+1)+" bids farewell to his fellow philosophers");
		states.remove(piTID);
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
}

// EOF

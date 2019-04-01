import java.util.Arrays;
import java.util.concurrent.locks.Condition;

/**
 * Class Monitor To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor {
	public static enum State { hungry, eating, thinking, talking, sleeping }

	public State[] states;
	
	private int sleepingCount;
	private int talkingCount;
	Condition sleeping;

	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers) {
		// TODO: set appropriate number of chopsticks based on the # of philosophers
		states = new State[piNumberOfPhilosophers];
		for (int i = 0; i < states.length; i++) {
			states[i] = State.thinking;
		}
		sleepingCount = 0;
		talkingCount  = 0;
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
		states[piTID] = State.hungry;
		while (!canEat(piTID))
		{
			wait();
		}
		states[piTID] = State.eating;
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down and
	 * let others know they are available.
	 */
	public synchronized void putDown(final int piTID) {
		states[piTID] = State.thinking;
		notify();
	}

	/**
	 * Only one philopher at a time is allowed to philosophy (while she is not
	 * eating).
	 */
	public synchronized void requestTalk(final int piTID) throws InterruptedException{
		boolean isSomeoneTalking = false;
		for (State state : states) {
			if (state == State.talking) {
				isSomeoneTalking = true;
			}
		}

		if (isSomeoneTalking) {
			wait();
		}
		states[piTID] = State.talking;
	}

	/**
	 * When one philosopher is done talking stuff, others can feel free to start
	 * talking.
	 */
	public synchronized void endTalk(final int piTID) {
		states[piTID] = State.thinking;
		notify();
	}
	
	private synchronized int leftPhilosopher(int i) {
		return (i+states.length-1)%states.length;
	}
	
	private synchronized int rightPhilosopher(int i) {
		return (i+1)%states.length;
	}
	
	private synchronized boolean canEat(int piTID) {
		return (states[leftPhilosopher(piTID)] != State.eating && 
				states[rightPhilosopher(piTID)] != State.eating && 
					((leftPhilosopher(piTID)% states.length>piTID && states[leftPhilosopher(piTID)] != State.hungry) 
					|| 
					(rightPhilosopher(piTID)% states.length>piTID && states[rightPhilosopher(piTID)] != State.hungry)) );
	}
}

// EOF

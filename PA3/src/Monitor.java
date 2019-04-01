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
	public synchronized void pickUp(final int piTID) {
		states[piTID - 1] = State.hungry;
		test(piTID);

		if (states[piTID - 1] == State.hungry) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down and
	 * let others know they are available.
	 */
	public synchronized void putDown(final int piTID) {
		states[piTID - 1] = State.thinking;
		test(((piTID - 1) + (states.length - 1)) % states.length + 1);
		test((piTID) % states.length + 1);
	}

	private synchronized void test(int piTID) {
		if (states[((piTID - 1) + (states.length - 1)) % states.length] != State.eating && 
			states[(piTID) % states.length] != State.eating && 
			states[piTID - 1] == State.hungry &&
			((((piTID - 1) + (states.length - 1)) % states.length>piTID-1) && (states[((piTID - 1) + (states.length - 1)) % states.length] != State.hungry)) && 
			((((piTID - 1) + (states.length - 1)) % states.length>piTID-1) && states[(piTID) % states.length] != State.hungry) ){
			states[piTID - 1] = State.eating;
			notify();
		}
	}

	/**
	 * Only one philopher at a time is allowed to philosophy (while she is not
	 * eating).
	 */
	public synchronized void requestTalk(final int piTID) {
		boolean isSomeoneTalking = false;
		for (State state : states) {
			if (state == State.talking) {
				isSomeoneTalking = true;
			}
		}

		if (isSomeoneTalking) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		states[piTID - 1] = State.talking;
	}

	/**
	 * When one philosopher is done talking stuff, others can feel free to start
	 * talking.
	 */
	public synchronized void endTalk(final int piTID) {
		states[piTID - 1] = State.thinking;
		notify();
	}
}

// EOF

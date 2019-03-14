/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
	private enum State{
		hungry, eating, thinking, talking, sleeping;
	}
	
	State[] states;


	/**
	 * Constructor
	 */
	public Monitor(int piNumberOfPhilosophers)
	{
		// TODO: set appropriate number of chopsticks based on the # of philosophers
		states = new State[piNumberOfPhilosophers];
		for(State state: states) {
			state = State.thinking;
		}
	}

	/*
	 * -------------------------------
	 * User-defined monitor procedures
	 * -------------------------------
	 */

	/**
	 * Grants request (returns) to eat when both chopsticks/forks are available.
	 * Else forces the philosopher to wait()
	 */
	public synchronized void pickUp(final int piTID)
	{
		if(states[(piTID-1)%states.length] != State.eating && states[(piTID-1)%states.length] != State.eating && states[piTID] == State.hungry) {
			states[piTID] = State.eating;
		}
	}

	/**
	 * When a given philosopher's done eating, they put the chopstiks/forks down
	 * and let others know they are available.
	 */
	public synchronized void putDown(final int piTID)
	{
		states[piTID] = State.thinking;
	}

	/**
	 * Only one philopher at a time is allowed to philosophy
	 * (while she is not eating).
	 */
	public synchronized void requestTalk(final int piTID)
	{
		boolean isSomeoneTalking = false;
		for(State state: states) {
			if(state == State.talking) {
				isSomeoneTalking = true;
			}
		}
		
		if(!isSomeoneTalking) {
			states[piTID] = State.talking;
		}
	}

	/**
	 * When one philosopher is done talking stuff, others
	 * can feel free to start talking.
	 */
	public synchronized void endTalk(final int piTID)
	{
		states[piTID] = State.thinking;
	}
}

// EOF

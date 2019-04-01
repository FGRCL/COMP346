import java.io.PrintWriter;
import java.util.Arrays;

import common.BaseThread;

/**
 * Class Philosopher. Outlines main subrutines of our virtual philosopher.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Philosopher extends BaseThread {
	/**
	 * Max time an action can take (in milliseconds)
	 */
	public static final long TIME_TO_WASTE = 1000;
	
	private static double percentChanceTalking = 1;
	
	private PrintWriter debugLog;

	/**
	 * The act of eating. - Print the fact that a given phil (their TID) has started
	 * eating. - yield - Then sleep() for a random interval. - yield - The print
	 * that they are done eating.
	 */
	public void eat() {
		try {
			System.out.println(iTID + " has started eating");
			logArray();
			yield();
			sleep((long) (Math.random() * TIME_TO_WASTE));
			yield();
			System.out.println(iTID + " is done eating");
		} catch (InterruptedException e) {
			System.err.println("Philosopher.eat():");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
	}

	/**
	 * The act of thinking. - Print the fact that a given phil (their TID) has
	 * started thinking. - yield - Then sleep() for a random interval. - yield - The
	 * print that they are done thinking.
	 */
	public void think() {
		try {
			System.out.println(iTID + " has started thinking");
			logArray();
			yield();
			sleep((long) (Math.random() * TIME_TO_WASTE));
			yield();
			System.out.println(iTID + " is done thinking");
		} catch (InterruptedException e) {
			System.err.println("Philosopher.think():");
			DiningPhilosophers.reportException(e);
			System.exit(1);
		}
	}

	/**
	 * The act of talking. - Print the fact that a given phil (their TID) has
	 * started talking. - yield - Say something brilliant at random - yield - The
	 * print that they are done talking.
	 */
	public void talk() {
		System.out.println(iTID + " has started talking");
		logArray();
		yield();
		saySomething();
		yield();
		System.out.println(iTID + " is done talking");
	}

	/**
	 * No, this is not the act of running, just the overridden Thread.run()
	 */
	public void run() {
		try {
			debugLog  = new PrintWriter("debug.log", "UTF-8");
			for (int i = 0; i < DiningPhilosophers.DINING_STEPS; i++) {
				DiningPhilosophers.soMonitor.pickUp(getTID()-1);
				eat();
				DiningPhilosophers.soMonitor.putDown(getTID()-1);

				think();

				/*
				 * TODO: A decision is made at random whether this particular philosopher is
				 * about to say something terribly useful.
				 * 
				 */
				double rand = Math.random();
				if (rand < percentChanceTalking) {
					DiningPhilosophers.soMonitor.requestTalk(getTID()-1);
					talk();
					DiningPhilosophers.soMonitor.endTalk(getTID()-1);
				}

				yield();
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("It didn't work");
		} finally {
			debugLog.close();
		}
	} // run()

	/**
	 * Prints out a phrase from the array of phrases at random. Feel free to add
	 * your own phrases.
	 */
	public void saySomething() {
		String[] astrPhrases = { "Eh, it's not easy to be a philosopher: eat, think, talk, eat...",
				"You know, true is false and false is true if you think of it",
				"2 + 2 = 5 for extremely large values of 2...", "If thee cannot speak, thee must be silent",
				"My number is " + getTID() + "",
				"To live is to suffer, to survive is to find some meaning in the suffering.",
				"When you look into an abyss, the abyss also looks into you.",
				"God is dead. God remains dead. And we have killed him.",
				"Love is a serious mental disease.",
				"Man - a being in search of meaning.",
				"One of the penalties for refusing to participate in politics is that you end up being governed by your inferiors."};

		System.out.println(
				"Philosopher " + getTID() + " says: " + astrPhrases[(int) (Math.random() * astrPhrases.length)]);
	}
	
	private synchronized void logArray() {
		debugLog.println(Arrays.toString(DiningPhilosophers.soMonitor.states));
		if(DiningPhilosophers.DEV_MODE) {
			System.out.println("\t\t"+Arrays.toString(DiningPhilosophers.soMonitor.states));
		}
	}
}

// EOF

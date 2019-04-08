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
	
	public Philosopher(int id) {
		super(id);
	}

	public Philosopher() {
		super();
	}

	/**
	 * The act of eating. - Print the fact that a given phil (their TID) has started
	 * eating. - yield - Then sleep() for a random interval. - yield - The print
	 * that they are done eating.
	 */
	public void eat() {
		try {
			System.out.println(iTID + " has started eating");
			DiningPhilosophers.logArray();
			addPepper();
			yield();
			sleep();
			yield();
			System.out.println(iTID + " is done eating");
			DiningPhilosophers.logArray();
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
			DiningPhilosophers.logArray();
			yield();
			sleep();
			yield();
			System.out.println(iTID + " is done thinking");
			DiningPhilosophers.logArray();
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
	 * @throws InterruptedException 
	 */
	public void talk() throws InterruptedException {
		System.out.println(iTID + " has started talking");
		DiningPhilosophers.logArray();
		yield();
		saySomething();
		yield();
		System.out.println(iTID + " is done talking");
		DiningPhilosophers.logArray();
	}
	
	/**
	 * The act of a philospher sleeping
	 * essentially wraps Thread.sleep()
	 */
	public void sleep() throws InterruptedException {
		DiningPhilosophers.soMonitor.requestSleep(this);
		System.out.println(iTID + " has started sleeping");
		DiningPhilosophers.logArray();
		sleep((long) (Math.random() * TIME_TO_WASTE));
		System.out.println(iTID + " is done sleeping");
		DiningPhilosophers.logArray();
		DiningPhilosophers.soMonitor.endSleep(this);
	}
	
	public void addPepper() throws InterruptedException {
		DiningPhilosophers.soMonitor.requestPepperShaker();
		System.out.println(iTID + " takes a pepper shaker");
		DiningPhilosophers.logArray();
		sleep((long) (Math.random() * TIME_TO_WASTE));
		System.out.println(iTID + " puts the pepper shaker back");
		DiningPhilosophers.logArray();
		DiningPhilosophers.soMonitor.endPepperShaker();
	}
	
	public synchronized void incrementTID() {
		this.iTID++;
	}
	
	public synchronized void decrementTID() {
		this.iTID--;
	}
	
	public synchronized int getIndexTID() {
		return this.iTID-1;
	}

	/**
	 * No, this is not the act of running, just the overridden Thread.run()
	 */
	public void run() {
		try {
			for (int i = 0; i < DiningPhilosophers.DINING_STEPS; i++) {
				DiningPhilosophers.soMonitor.pickUp(this);
				eat();
				DiningPhilosophers.soMonitor.putDown(this);

				think();

				double rand = Math.random();
				if (rand < percentChanceTalking) {
					DiningPhilosophers.soMonitor.requestTalk(this);
					talk();
					DiningPhilosophers.soMonitor.endTalk(this);
				}
				
				if(DiningPhilosophers.soMonitor.addOrRemovePhilospshers(getTID())) {
					break;
				}
				yield();	
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.err.println("It didn't work");
		}
	} // run()

	/**
	 * Prints out a phrase from the array of phrases at random. Feel free to add
	 * your own phrases.
	 * @throws InterruptedException 
	 */
	public void saySomething() throws InterruptedException {
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
		sleep((long) (Math.random() * TIME_TO_WASTE));
		System.out.println(
				"Philosopher " + getTID() + " says: " + astrPhrases[(int) (Math.random() * astrPhrases.length)]);
	}
}

// EOF

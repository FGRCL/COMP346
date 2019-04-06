import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Class DiningPhilosophers
 * The main starter.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class DiningPhilosophers
{
	/*
	 * ------------
	 * Data members
	 * ------------
	 */

	/**
	 * This default may be overridden from the command line
	 */
	public static final int DEFAULT_NUMBER_OF_PHILOSOPHERS = 4;
	
	public static final boolean DEV_MODE = true;

	/**
	 * Dining "iterations" per philosopher thread
	 * while they are socializing there
	 */
	public static final int DINING_STEPS = 10;

	/**
	 * Our shared monitor for the philosphers to consult
	 */
	public static Monitor soMonitor = null;
	
	private static PrintWriter debugLog;
	
	private static ArrayList<String> debugQueue = new ArrayList<String>();
	
	private static double percentChancePhilosopherChange = 0.5;
	
	private static ArrayList<Philosopher> aoPhilosophers;


	/*
	 * -------
	 * Methods
	 * -------
	 */

	/**
	 * Main system starts up right here
	 */
	public static void main(String[] argv)
	{
		try
		{
			int iPhilosophers = DEFAULT_NUMBER_OF_PHILOSOPHERS;
			if(argv.length>0 && Integer.parseInt(argv[0])>=0) {
				iPhilosophers = Integer.parseInt(argv[0]);
			}
			
			// Make the monitor aware of how many philosophers there are
			soMonitor = new Monitor(iPhilosophers);

			// Space for all the philosophers
			aoPhilosophers = new ArrayList<Philosopher>();

			// Let 'em sit down
			for(int j = 0; j < iPhilosophers; j++)
			{
				aoPhilosophers.add(new Philosopher());
				aoPhilosophers.get(j).start();
			}

			System.out.println
			(
				iPhilosophers +
				" philosopher(s) came in for a dinner."
			);

			// Main waits for all its children to die...
			// I mean, philosophers to finish their dinner.
			for(int j = 0; j < iPhilosophers; j++)
				aoPhilosophers.get(j).join();

			System.out.println("All philosophers have left. System terminates normally.");
			writeLog();
		}
		catch(InterruptedException e)
		{
			System.err.println("main():");
			reportException(e);
			System.exit(1);
		}
	} // main()

	/**
	 * Outputs exception information to STDERR
	 * @param poException Exception object to dump to STDERR
	 */
	public static void reportException(Exception poException)
	{
		System.err.println("Caught exception : " + poException.getClass().getName());
		System.err.println("Message          : " + poException.getMessage());
		System.err.println("Stack Trace      : ");
		poException.printStackTrace(System.err);
	}
	
	public static synchronized void addOrRemovePhilospshers(final int id) {
		double rand = Math.random();
		if(rand < percentChancePhilosopherChange) {
			addPhilosopher(id);
		}else {
			rand = Math.random();
			if(rand < percentChancePhilosopherChange) {
				removePhilosopher(id);
			}
		}
	}
	
	private static synchronized void removePhilosopher(final int id) {
		System.out.println(id + " bids farewell to the other philososphers.");
		aoPhilosophers.remove(id-1);
		for(int i=(id-1); i<aoPhilosophers.size(); i++) {
			aoPhilosophers.get(i).decrementTID();
		}
		soMonitor.states.remove(id-1);
	}

	private static synchronized void addPhilosopher(final int id) {
		System.out.println(id + " invites a new philosopher to the table.");
		soMonitor.states.add(id-1, Monitor.State.thinking);
		aoPhilosophers.add(id-1, new Philosopher(id));
		for(int i=id; i<aoPhilosophers.size(); i++) {
			aoPhilosophers.get(i).incrementTID();
		}
		aoPhilosophers.get(id-1).start();
	}

	public static synchronized void logArray() {
		debugQueue.add(DiningPhilosophers.soMonitor.states.toString()+" : "+DiningPhilosophers.soMonitor.nbAvailablePepperShakers);
		if(DiningPhilosophers.DEV_MODE) {
			System.out.println("\t\t"+DiningPhilosophers.soMonitor.states+" : "+DiningPhilosophers.soMonitor.nbAvailablePepperShakers);
		}
		validateArray();
	}
	
	private static void validateArray() {
		int sleepingCount = 0;
		int talkingCount = 0;
		
		for(int i=0; i<DiningPhilosophers.soMonitor.states.size(); i++) {
			if(DiningPhilosophers.soMonitor.states.get(i) == Monitor.State.eating && ( DiningPhilosophers.soMonitor.states.get((i+DiningPhilosophers.soMonitor.states.size()-1)%DiningPhilosophers.soMonitor.states.size()) == Monitor.State.eating || DiningPhilosophers.soMonitor.states.get((i+1)%DiningPhilosophers.soMonitor.states.size()) == Monitor.State.eating)) {
				System.err.println((i+1)+" has neighbours eating");
			}else if(DiningPhilosophers.soMonitor.states.get(i) == Monitor.State.sleeping) {
				sleepingCount++;
			}else if(DiningPhilosophers.soMonitor.states.get(i) == Monitor.State.talking) {
				talkingCount++;
			}
			
			if(talkingCount>1) {
				System.err.println("More than one talker");
			}else if(talkingCount>0 && sleepingCount>0) {
				System.err.println("Talker and sleeper at the same time");
			}
		}
	}
	
	private static synchronized void writeLog() {
		try {
			debugLog  = new PrintWriter("debug.log", "UTF-8");
			for(String line: debugQueue) {
				debugLog.println(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			debugLog.close();
		}
	}
}

// EOF

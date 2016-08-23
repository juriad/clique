package cz.artique.vad.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cz.artique.vad.network.vertex.Behavior;
import cz.artique.vad.network.vertex.Subject;
import cz.artique.vad.scenario.Scenario;
import cz.artique.vad.scenario.action.Action;
import cz.artique.vad.scenario.action.PeriodicAction;
import cz.artique.vad.scenario.action.TerminatingAction;
import cz.artique.vad.scenario.graph.Graph;

public class Simulator {
	private static final int TIMESTEP = 100;

	private final Logger logger;
	private final Scenario scenario;
	private final Behavior<?> behavior;
	private final Network<?> network;

	public Simulator(Logger logger, Scenario scenario) {
		this.logger = logger;
		this.scenario = scenario;
		behavior = createBehavior();
		network = createNetwork(behavior);
	}

	private Behavior<?> createBehavior() {
		try {
			return (Behavior<?>) Class.forName(scenario.getBehavior()).newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Failed to instaniate behavior " + scenario.getBehavior(), e);
		}
	}

	private <S extends Subject> Network<S> createNetwork(Behavior<S> behavior) {
		Network<S> network = new Network<S>(logger, behavior, TIMESTEP);
		for (Graph g : scenario.getGraphs()) {
			network.registerGraph(g);
		}
		return network;
	}

	public ExecutorService schedule() {
		final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable arg0) {
				Thread thread = new Thread(arg0);
				thread.setPriority(8);
				return thread;
			}
		});

		AtomicInteger lives = new AtomicInteger(0);
		for (final Action a : scenario.getActions()) {
			boolean periodic = a instanceof PeriodicAction && ((PeriodicAction) a).getLimit() != 1;

			Runnable run = new Runnable() {
				@Override
				public void run() {
					logger.log("Performing action " + a);
					boolean terminate = a.perform(network, logger);

					if (terminate) {
						if (a instanceof TerminatingAction) {
							if (lives.decrementAndGet() <= 0) {
								logger.log("Shutting down.");
								executor.shutdownNow();
							} else {
								logger.log("Lives remaining: " + lives);
							}
						} else {
							logger.log("Terminating action " + a);
							throw new RuntimeException();
						}
					}
				}
			};

			if (a instanceof TerminatingAction) {
				lives.incrementAndGet();
			}

			if (periodic) {
				logger.log("Sheduling periodic action " + a);
				executor.scheduleWithFixedDelay(run, (long) (a.getDelay() * TIMESTEP),
						(long) (((PeriodicAction) a).getPeriod() * TIMESTEP), TimeUnit.MILLISECONDS);
			} else {
				logger.log("Sheduling action " + a);
				executor.schedule(run, (long) (a.getDelay() * TIMESTEP), TimeUnit.MILLISECONDS);
			}
		}
		return executor;
	}

	public static void main(String[] args) {
		try (Logger logger = new Logger(System.out)) {
			if (args.length != 1) {
				logger.log("The program wants exactly one argument: the file with a scenario.");
				return;
			}

			File file = new File(args[0]);
			if (!file.canRead()) {
				logger.log("The file provided does not exist or it is not readable.");
				return;
			}

			logger.log("Loading scenario from file: " + file);
			Scenario scenario;
			try {
				scenario = Scenario.fromFile(new FileReader(file));
			} catch (FileNotFoundException e) {
				logger.log("The file provided does not exist or it is not readable.");
				e.printStackTrace();
				return;
			}

			logger.log(scenario.toString());

			Simulator simulator = new Simulator(logger, scenario);
			logger.log("The network has been created.");

			ExecutorService schedule = simulator.schedule();
			logger.log("Actions has been scheduled.");

			logger.log("Waiting for the network to stabilize.");
			simulator.awaitTermination(schedule);
			logger.log("Ending.");
		}
	}

	private void awaitTermination(ExecutorService executorService) {
		boolean terminated = false;
		do {
			try {
				terminated = executorService.awaitTermination(TIMESTEP * 10, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
			}
		} while (!terminated);
	}
}

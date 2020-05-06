package comp34120.ex2;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A very simple leader implementation that only generates random prices
 *
 * @author Xin
 */
final class Group5Leader extends PlayerImpl {

    private ArrayList<Record> platformHistory;
    private Solver linearRegSolver;

    private Group5Leader() throws RemoteException, NotBoundException {
        super(PlayerType.LEADER, "Group5Leader");
    }

    public static void main(final String[] p_args) throws RemoteException, NotBoundException {
        new Group5Leader();
    }

    @Override
    public void goodbye() throws RemoteException {
        ExitTask.exit(500);
    }

    /**
     * To inform this instance to proceed to a new simulation day
     *
     * @param p_date The date of the new day
     * @throws RemoteException
     */
    @Override
    public void proceedNewDay(int p_date) throws RemoteException {
    	// Get last record
		Record lastDay = m_platformStub.query(m_type, p_date);
        platformHistory.add(lastDay);
		// Train
        // Calculate parameters for followers reaction function
		linearRegSolver.fit(platformHistory);

		// Predict
        // Find the best strategy for the leader by solving maximisation problem
        // Use the predicted follower function we found above to help calculate our response
        // linearRegSolver.predict(lastDay.m_followerPrice)
        float a=linearRegSolver.getA();
        float b=linearRegSolver.getB();
        float leadOptimal= (float) ((0.3*a+0.3*b-3)/(0.6*b-2));

		// Publish
        m_platformStub.publishPrice(m_type, leadOptimal);
    }

    @Override
    public void startSimulation(int p_steps) throws RemoteException {
        platformHistory = new ArrayList<Record>();
        platformHistory = PlatformHelper.getRecordHistory(m_platformStub);
		linearRegSolver = new LinearRegressionSolver();
    }

    /**
     * The task used to automatically exit the leader process
     *
     * @author Xin
     */
    private static class ExitTask extends TimerTask {
        static void exit(final long p_delay) {
            (new Timer()).schedule(new ExitTask(), p_delay);
        }

        @Override
        public void run() {
            System.exit(0);
        }
    }
}

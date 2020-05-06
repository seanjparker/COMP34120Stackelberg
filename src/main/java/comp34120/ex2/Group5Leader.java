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
		Record lastDay = m_platformStub.query(m_type, p_date - 1);
        platformHistory.add(lastDay);
		float lastDayProfit = PlatformHelper.calculateProfit(lastDay);

		// Train
        // Calculate parameters for followers reaction function
		linearRegSolver.fit(platformHistory);

		// Predict
        // Find the best strategy for the leader by solving maximisation problem
        // Use the predicted follower function we found above to help calculate our response
        float a = linearRegSolver.getA();
        float b = linearRegSolver.getB();
        float leadOptimal = (0.3f * a + 0.3f * b - 3) / (0.6f * b - 2);

        if (b > 3.33) {
            m_platformStub.log(PlayerType.LEADER,"Constraints Broken");
        }

		// Publish
        m_platformStub.publishPrice(m_type, leadOptimal);


        // Log info
        m_platformStub.log(PlayerType.LEADER, "Last days profit = " + lastDayProfit);

        if (p_date == 130) {
            // Log total profit
            float total = 0.0f;
            for (Record day : platformHistory) {
                // It only matters about the profit for the last 30 days in the simulation
                if (day.m_date > 100) {
                    total += PlatformHelper.calculateProfit(day);
                }
            }

            m_platformStub.log(PlayerType.LEADER, "Total profit = " + total);
        }
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

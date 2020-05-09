import comp34120.ex2.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

final class Group5Leader extends PlayerImpl {
    class LinearRegressionSolver {
        private float a, b;

        public LinearRegressionSolver() {
            a = 1.0f;
            b = 1.0f;
        }

        public float getA() {
            return this.a;
        }

        public float getB() {
            return this.b;
        }

        // Implements the equations in Lecture 4, slide 25
        public void fit(ArrayList<Record> history) {
            float leader_squared_sum = 0, follower_reaction_sum = 0, leader_sum = 0, leader_prod_follower_sum = 0;
            int T = history.size(), window = 60;
            Record day;

            double forgetting_factor = 0.95;
            // Single loop where we calculate the terms of the equations
            for (int i = T - window - 1; i < T; i++) {
                day = history.get(i);

                leader_squared_sum += Math.pow(forgetting_factor, T-i)*(day.m_leaderPrice * day.m_leaderPrice);
                follower_reaction_sum += Math.pow(forgetting_factor, T-i)*day.m_followerPrice;
                leader_sum += Math.pow(forgetting_factor, T-i)*day.m_leaderPrice;
                leader_prod_follower_sum += Math.pow(forgetting_factor, T-i)*(day.m_leaderPrice * day.m_followerPrice);
            }

            // Calculate the numerator for each of the equations
            float a_numerator = (leader_squared_sum * follower_reaction_sum) - (leader_sum * leader_prod_follower_sum);
            float b_numerator = (T * leader_prod_follower_sum) - (leader_sum * follower_reaction_sum);

            // The denominator is the same in both of the equations
            float denominator = (T * leader_squared_sum) - (leader_sum * leader_sum);

            a = a_numerator / denominator;
            b = b_numerator / denominator;
        }

        // Predict using the learnt parameters and the linear function
        public float predict(float u) {
            return a + u * b;
        }
    }

    private ArrayList<Record> platformHistory;
    private LinearRegressionSolver linearRegSolver;

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
		float lastDayProfit = calculateProfit(lastDay);

		// Train
        // Calculate parameters for followers reaction function
		linearRegSolver.fit(platformHistory);

		// Predict
        // Find the best strategy for the leader by solving maximisation problem
        // Use the predicted follower function we found above to help calculate our response
        float a = linearRegSolver.getA();
        float b = linearRegSolver.getB();
        float leadOptimal = (0.3f * a + 0.3f * b - 3) / (0.6f * b - 2);

        // Log what we think the followers price will be for this day
        m_platformStub.log(PlayerType.LEADER, "We predict followers price: " + linearRegSolver.predict(leadOptimal));

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
            for (int i = 100; i <= 129; i++) {
                // It only matters about the profit for the last 30 days in the simulation
                total += calculateProfit(platformHistory.get(i));
            }

            m_platformStub.log(PlayerType.LEADER, "Total profit = " + total);
        }
    }

    @Override
    public void startSimulation(int p_steps) throws RemoteException {
        platformHistory = new ArrayList<Record>();
        platformHistory = getRecordHistory(m_platformStub);
		linearRegSolver = new LinearRegressionSolver();
    }

    public static ArrayList<Record> getRecordHistory(Platform plt) throws RemoteException {
        ArrayList<Record> history = new ArrayList<Record>();
        for (int i = 1; i <= 100; i++) {
            history.add(plt.query(PlayerType.LEADER, i));
        }
        return history;
    }

    public static float calculateProfit(Record day) {
        // (u_L - c_L) * S_L(u_L, u_F)
        return (day.m_leaderPrice - day.m_cost) * (2 - day.m_leaderPrice + (0.3f * day.m_followerPrice));
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

package comp34120.ex2;

public class LinearRegressionSolver implements Solver {
    private float a, b;

    public LinearRegressionSolver() {
        a = 1.0f;
        b = 1.0f;
    }

    @Override
    public float getA() {
        return this.a;
    }

    @Override
    public float getB() {
        return this.b;
    }

    @Override
    // Implements the equations in Lecture 4, slide 25
    public void fit(Record[] history) {
        float leader_squared_sum = 0, follower_reaction_sum = 0, leader_sum = 0, leader_prod_follower_sum = 0;
        int T = history.length;
        Record day;

        // Single loop where we calculate the terms of the equations
        for (Record record : history) {
            day = record;

            leader_squared_sum += (day.m_leaderPrice * day.m_leaderPrice);
            follower_reaction_sum += day.m_followerPrice;
            leader_sum += day.m_leaderPrice;
            leader_prod_follower_sum += (day.m_leaderPrice * day.m_followerPrice);
        }

        // Calculate the numerator for each of the equations
        float a_numerator = (leader_squared_sum * follower_reaction_sum) - (leader_sum * leader_prod_follower_sum);
        float b_numerator = (T * leader_prod_follower_sum) - (leader_sum * follower_reaction_sum);

        // The denominator is the same in both of the equations
        float denominator = (T * leader_squared_sum) - (leader_sum * leader_sum);

        a = a_numerator / denominator;
        b = b_numerator / denominator;
    }

    @Override
    // Predict using the learnt parameters and the linear function
    public float predict(float u) {
        return a + u * b;
    }
}

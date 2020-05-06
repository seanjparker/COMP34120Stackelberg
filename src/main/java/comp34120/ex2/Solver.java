package comp34120.ex2;

import java.util.ArrayList;

public interface Solver {
    float getA();
    float getB();
    void fit(ArrayList<Record> history);
    float predict(float u);
}

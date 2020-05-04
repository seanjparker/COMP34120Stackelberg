package comp34120.ex2;

public interface Solver {
    float getA();
    float getB();
    void fit(Record[] history);
    float predict(float u);
}

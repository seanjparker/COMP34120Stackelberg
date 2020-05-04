package comp34120.ex2;

import java.rmi.RemoteException;

public class PlatformHelper {

    public static Record[] getRecordHistory(Platform plt) throws RemoteException {
        Record[] history = new Record[100];
        for (int i = 1; i <= 100; i++) {
            history[i - 1] = plt.query(PlayerType.LEADER, i);
        }
        return history;
    }

    public static float calculateProfit(Record day) {
        // We need to calculate the profit
        return 1.0f;
    }
}

package comp34120.ex2;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class PlatformHelper {

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
}

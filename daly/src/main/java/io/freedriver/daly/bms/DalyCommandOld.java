package io.freedriver.daly.bms;

public class DalyCommandOld {
    static {
        ReadRequest rr = new ReadRequest(QueryId.CELL_VOLTAGE);
        rr.setQueryId(QueryId.SOC);

    }
}

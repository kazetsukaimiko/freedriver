package io.freedriver.daly.bms;

public class DalyCommandOld {
    static {
        Request rr = new ReadRequest(QueryId.CELL_VOLTAGE)
                .toBuilder()
                .queryId(QueryId.SOC)
                .build();

    }
}

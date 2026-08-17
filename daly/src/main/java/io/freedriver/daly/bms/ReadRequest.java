package io.freedriver.daly.bms;

public class ReadRequest extends Request {
    public ReadRequest(QueryId t) {
        this(t, Address.UPPER);
    }

    public ReadRequest(QueryId queryId, Address address) {
        super(address, queryId, (byte) 0, DalyCommand.READ, new byte[8]);
    }
}

package io.freedriver.daly.bms;

public class ReadRequest extends Request {
    public ReadRequest(QueryId t) {
        this(t, Address.UPPER);
    }

    public ReadRequest(QueryId queryId, Address address) {
        super(DalyCommand.READ, new byte[8]);
        setQueryId(queryId);
        setAddress(address);
    }
}

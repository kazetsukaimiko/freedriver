package io.freedriver.daly.bms.hwtest;

public class BMSConnectionHWTest {
    /*
    String device = "/dev/ttyUSB0";
    SerialParams params = new SerialParams().setBaudRate(BaudRates.BAUDRATE_9600);
    JSSCPort port = JSSCPort.get(PortReference.auto(Paths.get(device)), params);

    @Test
    public void testGetCellVoltages() throws IOException {
        Request request = new ReadRequest(QueryId.CELL_VOLTAGE);
        testRequestRepliesAccordingly(request);
    }

    public void testRequestRepliesAccordingly(Request request) throws IOException {
        SerialEntityStream stream = new JSSCSerialStream(port);
        stream.getOutputStream()
                .write(request.asByteArray());
        stream.getOutputStream().flush();

        ResponseAccumlator responseAccumlator = new ResponseAccumlator();
        Response response = responseAccumlator.apply(stream);

        assertEquals(request.getQueryId(), response.getQueryId());
        
        System.out.println("Finish");
    }

     */

}

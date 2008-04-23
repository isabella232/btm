package bitronix.tm.mock;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.mock.resource.jms.MockXAConnectionFactory;
import bitronix.tm.resource.jms.PoolingConnectionFactory;
import junit.framework.TestCase;

import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.Queue;
import javax.jms.MessageProducer;

public class JmsPoolTest extends TestCase {

    private PoolingConnectionFactory pcf;

    protected void setUp() throws Exception {
        pcf = new PoolingConnectionFactory();
        pcf.setMinPoolSize(1);
        pcf.setMaxPoolSize(2);
        pcf.setMaxIdleTime(1);
        pcf.setClassName(MockXAConnectionFactory.class.getName());
        pcf.setUniqueName("pcf");
        pcf.setAllowLocalTransactions(true);
        pcf.setAcquisitionTimeout(1);
        pcf.init();
    }


    protected void tearDown() throws Exception {
        pcf.close();
    }
    
    public void testPoolNotStartingTransactionManager() throws Exception {
        // make sure TM is not running
        TransactionManagerServices.getTransactionManager().shutdown();

        PoolingConnectionFactory pcf = new PoolingConnectionFactory();
        pcf.setMinPoolSize(1);
        pcf.setMaxPoolSize(2);
        pcf.setMaxIdleTime(1);
        pcf.setClassName(MockXAConnectionFactory.class.getName());
        pcf.setUniqueName("pcf2");
        pcf.setAllowLocalTransactions(true);
        pcf.setAcquisitionTimeout(1);
        pcf.init();

        assertFalse(TransactionManagerServices.isTransactionManagerRunning());

        Connection c = pcf.createConnection();
        Session s = c.createSession(false, 0);
        Queue q = s.createQueue("q");
        MessageProducer mp = s.createProducer(q);
        mp.send(s.createTextMessage("test123"));
        mp.close();
        s.close();
        c.close();

        assertFalse(TransactionManagerServices.isTransactionManagerRunning());

        pcf.close();

        assertFalse(TransactionManagerServices.isTransactionManagerRunning());
    }

}
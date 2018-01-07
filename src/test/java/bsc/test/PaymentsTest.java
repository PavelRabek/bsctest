package bsc.test;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.bsc.Payments;
import com.bsc.PaymentsImpl;

public class PaymentsTest {
	
	@Test
	public void testAddPayment() {
		Payments payments = new PaymentsImpl();
		
		payments.addPayment("USD 100");
		List<String> allCurrencies = payments.getAllCurrencies();
		Assert.assertEquals(1, allCurrencies.size());
		Assert.assertEquals("USD", allCurrencies.get(0));
		Assert.assertEquals(100l, payments.getValue("USD").longValue());
		
		payments.addPayment("USD 30");
		Assert.assertEquals(130l, payments.getValue("USD").longValue());
		
		payments.addPayment("USD -80");
		Assert.assertEquals(50l, payments.getValue("USD").longValue());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddPaymentIncorrentFormat1() {
		Payments payments = new PaymentsImpl();
		payments.addPayment("123");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddPaymentIncorrentFormat2() {
		Payments payments = new PaymentsImpl();
		payments.addPayment("USD XXX");
	}
	
	@Test
	public void testAddPayments() {
		Payments payments = new PaymentsImpl();
		
		List<String> list = new LinkedList<>();
		list.add("USD 1000");
		list.add("HKD 100");
		list.add("USD -100");
		list.add("RMB 2000");
		list.add("HKD 200");
		
		payments.addPayments(list.stream());
		List<String> allCurrencies = payments.getAllCurrencies();
		Assert.assertEquals(3, allCurrencies.size());
		Assert.assertEquals(900l, payments.getValue("USD").longValue());
		Assert.assertEquals(300l, payments.getValue("HKD").longValue());
		Assert.assertEquals(2000l, payments.getValue("RMB").longValue());
	}
	
	@Test
	public void testSetConversion() {
		Payments payments = new PaymentsImpl();
		
		payments.setConversion("HKD", 20d);
		
		Assert.assertEquals(20d, payments.getConversion("HKD").doubleValue(), 0d);
	}
	
	@Test
	public void testGetPaymentReport() {
		Payments payments = new PaymentsImpl();
		
		payments.addPayment("HKD", 100l);
		payments.setConversion("HKD", 20d);
		
		Assert.assertEquals("HKD 100 (USD 5)", payments.getPaymentsReport());
	}
	
}

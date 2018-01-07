package com.bsc;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Interface for handling payments, exchange rates and reports.
 * @author prabek
 */
public interface Payments {

	/**
	 * Adds payment.
	 * @param currency
	 * @param value
	 */
	void addPayment(String currency, Long value);
	
	/**
	 * Adds payment in form of input command: CURRENCY_CODE INT_VALUE (e.g. USD 25).
	 * @param payment
	 */
	void addPayment(String payment);
	
	/**
	 * Adds several payments as a stream of input commands.
	 * @param payments
	 */
	void addPayments(Stream<String> payments);
	
	/**
	 * Sets a new conversion rate between given currency and USD.
	 * @param currency
	 * @param usdConversionRate
	 */
	void setConversion(String currency, Double usdConversionRate);
	
	/**
	 * Sets several conversion rates between given currencies and USD.
	 * @param conversions
	 */
	void setConversions(Map<String, Double> conversions);
	
	/**
	 * Returns currency codes of all used currencies.
	 * @return currency codes.
	 */
	List<String> getAllCurrencies();
	
	/**
	 * Returns actual amount for given currency. 
	 * @param currency
	 * @return actual amount
	 */
	Long getValue(String currency);
	
	/**
	 * Returns actual conversion rate for given currency.
	 * @param currency
	 * @return actual conversion rate
	 */
	Double getConversion(String currency);
	
	/**
	 * Returns report of current amounts for each currency.
	 * @return report
	 */
	String getPaymentsReport();
	
}

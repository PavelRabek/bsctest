package com.bsc;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of Payments interface which stores payments in memory in form of thread safe map.
 * @author prabek
 */
public class PaymentsImpl implements Payments {
	
	private static final Pattern PAYMENT_PATTERN = Pattern.compile("([a-zA-Z]{3})\\s+(\\-?[0-9]+)");
	
	private final static Logger LOGGER = Logger.getLogger(PaymentsImpl.class.getName());
	
	private static final NumberFormat DECIMAL_FORMATTER = DecimalFormat.getInstance(Locale.ENGLISH);
	
	private ConcurrentHashMap<String, Long> paymentMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Double> conversionMap = new ConcurrentHashMap<>();
	
	public PaymentsImpl() {
		DECIMAL_FORMATTER.setMaximumFractionDigits(2);
	}
	
	@Override
	public void addPayment(String currency, Long value) {
		// thread safe and thread consistent value update
		paymentMap.merge(currency, value, (oldVal, newVal) -> oldVal + newVal);
	}
	
	@Override
	public void addPayment(String payment) {
		Matcher matcher = PAYMENT_PATTERN.matcher(payment);
		if (matcher.matches()) {
			String currency = matcher.group(1);
			Long value = Long.valueOf(matcher.group(2));
			addPayment(currency, value);
		} else {
			throw new IllegalArgumentException("Invalid payment: " + payment + ". Correct format is: CURRENCY_CODE INT_VALUE, e.g. USD 25");
		}
	}
	
	@Override
	public void addPayments(Stream<String> payments) {
		payments.forEach(payment -> {
			try {
				addPayment(payment);
			} catch (IllegalArgumentException e) {
				LOGGER.warning(e.getMessage());
			}
		});
	}
	
	@Override
	public void setConversion(String currency, Double usdConversionRate) {
		// thread safe and thread consistent value update
		conversionMap.merge(currency, usdConversionRate, (oldVal, newVal) -> newVal);
	}

	@Override
	public void setConversions(Map<String, Double> conversions) {
		conversions.forEach((String currency, Double rate) -> {
			setConversion(currency, rate);
		});
	}
	
	@Override
	public List<String> getAllCurrencies() {
		return Collections.list(paymentMap.keys());
	}
	
	@Override
	public Long getValue(String currency) {
		return paymentMap.get(currency);
	}
	
	@Override
	public Double getConversion(String currency) {
		return conversionMap.get(currency);
	}
	
	@Override
	public String getPaymentsReport() {
		return paymentMap.entrySet().stream()
			.filter(entry -> entry.getValue() > 0)
			.map(entry -> {
				StringBuilder sb = new StringBuilder();
				sb.append(entry.getKey()).append(" ").append(entry.getValue());
				Double conversionRate = conversionMap.get(entry.getKey());
				if (conversionRate != null) {
					Double conversion = entry.getValue().doubleValue() / conversionRate;
					sb.append(" (USD ").append(DECIMAL_FORMATTER.format(conversion)).append(")");
				}
				return sb.toString();
			})
			.collect(Collectors.joining("\n"));
	}

}

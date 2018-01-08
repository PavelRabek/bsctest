package com.bsc;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Entry point of this demo application.
 * @author prabek
 */
public class App {
	
	private static final String FILE_PARAM = "-f";
	private static final long INTERVAL = 60l;
	
	public static void main(String[] args) {
		Payments payments = initPayments(args);
		
		schedulePeriodicPaymentReport(payments);
		
		handleUserInput(payments);
	}
	
	private static Payments initPayments(String[] args) {
		Payments payments = new PaymentsImpl();
		
		Optional<String> fileName = getFileName(args);
		if (fileName.isPresent()) {
			try (Stream<String> fileStream = Files.lines(Paths.get(fileName.get()))) {
				payments.addPayments(fileStream);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		Map<String, Double> conversionRates = loadConversionRates();
		payments.setConversions(conversionRates);
		
		return payments;
	}

	private static void schedulePeriodicPaymentReport(Payments payments) {
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
			String report = payments.getPaymentsReport();
			if (!report.isEmpty()) {
				System.out.println("*************************");
				System.out.println(report);
				System.out.println("*************************");
				System.out.println("Enter command: ");
			}
		}, 0, INTERVAL, TimeUnit.SECONDS);
	}

	private static void handleUserInput(Payments payments) {
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				System.out.println("Enter command: ");
				String command = scanner.nextLine();
				
				switch (command) {
				case "help":
					System.out.println("*************************");
					System.out.println("Available commands:");
					System.out.println(" quit (terminates program)");
					System.out.println(" CURRENCY_CODE INT_VALUE (to enter payment, e.g. USD 25)");
					System.out.println("*************************");
					break;
					
				case "quit":
					System.exit(0);
	
				default:
					try {
						payments.addPayment(command);
					} catch (IllegalArgumentException e) {
						System.err.println("Invalid command, type help");
					}
				}
			}
		}
	}

	private static Optional<String> getFileName(String[] args) {
		if (args.length > 0) {
			String firstArg = args[0];
			if (FILE_PARAM.equals(firstArg)) {
				if (args.length > 1) {
					String fileName = args[1];
					return Optional.of(fileName);
				} else {
					System.err.println("File name required.");
					System.exit(1);
				}
			}
		}
		return Optional.empty();
	}
	
	private static Map<String, Double> loadConversionRates() {
		String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		String propertiesPath = rootPath + "usdConversionRates.properties";
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(propertiesPath));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		Map<String, Double> conversionRates = properties.entrySet().stream().collect(
			    Collectors.toMap(
			        e -> (String) e.getKey(),
			        e -> Double.valueOf((String)e.getValue())
			    ));
		return conversionRates;
	}
}

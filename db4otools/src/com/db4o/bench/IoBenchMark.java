/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.bench;

import java.io.*;
import java.util.*;

import com.db4o.bench.crud.*;
import com.db4o.bench.logging.*;
import com.db4o.bench.logging.replay.*;
import com.db4o.foundation.*;
import com.db4o.io.*;

public class IoBenchMark {
	
	private static final int SMALL 	= 1000;		//1'000
	
	private static final int MEDIUM	= 30000;	//30'000
	
	private static final int LARGE 	= 1000000;	//1'000'000
	
	private static final String DB_FILE_NAME = "ioBenchMark.db4o";
	
	
	private static final String _doubleLine = "=============================================================";

	private static final String _singleLine = "-------------------------------------------------------------";
	
	
	public static void main(String[] args) throws IOException {
		printBenchmarkHeader();

		IoBenchMark ioBenchMark = new IoBenchMark();
		ioBenchMark.run(SMALL);
		ioBenchMark.run(MEDIUM);
//		ioBenchMark.run(LARGE);
	}

	
	private void run(int itemCount) throws FileNotFoundException, IOException {
		runTargetApplication(itemCount);
		prepareDbFile(itemCount);
		
		removeExistingLog(itemCount);
		PrintStream out = new PrintStream(new FileOutputStream(logFileName(itemCount), true));
		
		printRunHeader(itemCount, out);
		
		for (int i = 0; i < LogConstants.ALL_ENTRIES.length; i++) {
			String currentCommand = LogConstants.ALL_ENTRIES[i];
			benchmarkCommand(currentCommand, itemCount, out);	
		}
		
		removeDbFile();
	}



	/**
	 * This runs a "real world" application to generate an I/O-access log.
	 *  
	 * @param itemCount	The number of items to be stored
	 */
	private void runTargetApplication(int itemCount) {
		new CrudApplication().run(itemCount);
	}
	
	/**
	 * Replays an I/O-access log to prepare a DB-file for benchmarking
	 *  
	 * @param itemCount	The number of items to be stored
	 * @throws IOException
	 */
	private void prepareDbFile(int itemCount) throws IOException {
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter raf = rafFactory.open(DB_FILE_NAME, false, 0, false);
		LogReplayer replayer = new LogReplayer(CrudApplication.logFileName(itemCount), raf);
		replayer.replayLog();
	}


	private void benchmarkCommand(String command, int itemCount, PrintStream out) throws IOException {
		StopWatch watch = new StopWatch();
		long timeElapsed, operationCount;
		
		HashSet commands = new HashSet();
		commands.add(command);
		
		IoAdapter rafFactory = new RandomAccessFileAdapter();
		IoAdapter raf = rafFactory.open(DB_FILE_NAME, false, 0, false);
		LogReplayer replayer = new LogReplayer(CrudApplication.logFileName(itemCount), raf, commands);
		
		watch.start();
		replayer.replayLog();
		watch.stop();
		
		timeElapsed = watch.elapsed();
		operationCount = ((Long)replayer.operationCounts().get(command)).longValue();
		
		printStatisticsForCommand(out, command, timeElapsed, operationCount);
	}
	
	
	private static String logFileName(int itemCount){
		return "db4o-io-benchmark-results-" + itemCount + ".log";
	}

	private void removeExistingLog(int itemCount) {
		new File(logFileName(itemCount)).delete();
	}
	
	private void removeDbFile() {
		new File(DB_FILE_NAME).delete();
	}
	
	private static void printBenchmarkHeader() {
		printDoubleLine();
		System.out.println("Running db4o IoBenchMark");
		printSingleLine();
		System.out.println("Be aware: running the LARGE benchmark\n" +
							"1) May take a very long time, depending on your machine speed\n" +
							"2) May require to increase the heap size of your JVM (eg with '-Xmx512m')" +
							"3) Will need about 1.5GB of disk space");
		printDoubleLine();
	}
	
	private void printRunHeader(int itemCount, PrintStream out) {
		output(out, _singleLine);
		output(out, "db4o IoBenchmark results with " + itemCount + " items");
		System.out.println("Statistics written to " + logFileName(itemCount));
		output(out, _singleLine);
		output(out, "");
	}
	
	private void printStatisticsForCommand(PrintStream out, String currentCommand, long timeElapsed, long operationCount) {
		
		double avgTimePerOp = (double)timeElapsed/(double)operationCount;
		double opsPerMs = (double)operationCount/(double)timeElapsed;
		double nanosPerMilli = Math.pow(10, 6);
		
		String output = "Results for " + currentCommand + "\r\n" +
						"> time elapsed: " + timeElapsed + " ms" + "\r\n" + 
						"> operations executed: " + operationCount + "\r\n" +
						"> operations per millisecond: " + opsPerMs + "\r\n" +
						"> average duration per operation: " + avgTimePerOp + " ms" + "\r\n" +
						currentCommand + (avgTimePerOp*nanosPerMilli) + " (I/O Benchmark value. Smaller numbers are better)\r\n";
		
		output(out, output);
	}

	private void output(PrintStream out, String text) {
		out.println(text);
		System.out.println(text);
	}
		
	private static void printSingleLine() {
		System.out.println(_singleLine);
	}
	
	private static void printDoubleLine() {
		System.out.println(_doubleLine);
	}
	
}

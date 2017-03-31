package com.robotcontrol.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {

	private static ExecutorService executorService = Executors
			.newCachedThreadPool();

	// 线程池执行方法
	public static void execute(Runnable run) {
		executorService.submit(run);
	}
}

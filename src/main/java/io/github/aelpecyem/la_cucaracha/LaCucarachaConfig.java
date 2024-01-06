package io.github.aelpecyem.la_cucaracha;

import eu.midnightdust.lib.config.MidnightConfig;

public class LaCucarachaConfig extends MidnightConfig {
	@Entry
	public static boolean roachSpawnEnabledCarriers = true;

	@Entry
	public static boolean roachSpawnEnabledStructures = true;

	@Entry(min = 1, max = Integer.MAX_VALUE)
	public static int roachSpawnIntervalStructures = 100;

	@Entry
	public static boolean roachSpawnEnabledFood = true;

	@Entry(min = 1, max = Integer.MAX_VALUE)
	public static int roachSpawnIntervalFood = 80;

	@Entry(min = 1, max = 15)
	public static int roachMaxGroupSize = 3;

	@Entry(min = 1, max = 20)
	public static int roachAggressionGroupSize = 2;
}

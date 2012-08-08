package com.greatmancode.mctospout;

public class World {

	private int spawnX, spawnY, spawnZ, gamemode;
	private String name, generator;
	private long seed = 0;
	public World (String name, String generator, int gamemode, long seed, int spawnX, int spawnY, int spawnZ) {
		this.spawnX = spawnX;
		this.spawnY = spawnY;
		this.spawnZ = spawnZ;
		this.name = name;
		this.generator = generator;
		this.seed = seed;
	}
	public int getSpawnX() {
		return spawnX;
	}

	public int getSpawnY() {
		return spawnY;
	}

	public int getSpawnZ() {
		return spawnZ;
	}
	public String getName() {
		return name;
	}
	public String getGenerator() {
		return generator;
	}
	public long getSeed() {
		return seed;
	}
	
	public boolean isValid() {
		if (name != null && !name.isEmpty() && generator != null && !generator.isEmpty() && seed != 0) {
			return true;
		}
		return false;
	}
	public String getGamemode() {
		String result = "";
		if (gamemode == 0) {
			result = "SURVIVAL";
		} else if (gamemode == 1) {
			result = "CREATIVE";
		} else if (gamemode == 2) {
			result = "ADVENTURE";
			
		} else {
			result = "SURVIVAL";
		}
		return result;
	}
	
}

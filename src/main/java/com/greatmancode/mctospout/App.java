package com.greatmancode.mctospout;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.collections.map.DefaultedMap;
import org.spout.api.datatable.DataMap;
import org.spout.api.datatable.GenericDatatableMap;
import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.FloatTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.Tag;
import org.spout.nbt.StringTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

/**
 * Hello world!
 * 
 */
public class App {
	private static World world;
	private static File worldFolder;
	private static File spoutFolder;
	public static void main(String[] args) {
		worldFolder = new File(args[0]);
		if (worldFolder.exists() && worldFolder.isDirectory()) {

			// START level.dat checkup & World initialization
			System.out.println("Reading level.dat file....");
			if (readLevelDatMc()) {
				if (world != null) { // Just a check to be sure
					System.out.println("World " + world.getName() + " loaded!");
					createWorldDatSpout();
					
				}
			}
		} else {
			System.out.println("This is not a world folder!");
		}
	}

	//STEP 1
	public static boolean readLevelDatMc() {
		boolean result = false;
		File worldInformation = new File(worldFolder, "world.dat");
		if (worldInformation.exists() && worldInformation.isFile()) {
			CompoundTag data = null;
			try {
				NBTInputStream in = new NBTInputStream(new FileInputStream(worldInformation), false);
				data = (CompoundTag) in.readTag();
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HashMap<String, Tag> level = new HashMap<String, Tag>();
			level.putAll(data.getValue());
			System.out.println(level);
			if (level.containsKey("Data")) {
				sendUpdate();
				CompoundMap dataTag = (CompoundMap) level.get("Data").getValue();
				if (((IntTag) dataTag.get("version")).getValue().equals(19133)) {
					sendUpdate();
					world = new World(((StringTag) dataTag.get("LevelName")).getValue(), ((StringTag) dataTag.get("generatorName")).getValue(), ((IntTag)dataTag.get("GameType")).getValue(), ((LongTag) dataTag.get("RandomSeed")).getValue(), ((IntTag) dataTag.get("SpawnX")).getValue(), ((IntTag) dataTag.get("SpawnY")).getValue(), ((IntTag) dataTag.get("SpawnZ")).getValue());
					if (world.isValid()) {
						sendUpdate();
						result = true;
					} else {
						System.out.println("Invalid world");
					}
				} else {
					System.out.println("This system is only compatible with 1.3 maps");
				}

			} else {
				System.out.println("Invalid level.dat file");
			}
		} else {
			System.out.println("The level.dat file doesn't exist!");
		}
		return result;
	}
	
	//STEP 2
	public static boolean createWorldDatSpout() {
		boolean result = false;
		CompoundMap worldTags = new CompoundMap();
		//World Version 1
		worldTags.put(new ByteTag("version", (byte)2));
		worldTags.put(new LongTag("seed", world.getSeed()));
		worldTags.put(new StringTag("generator","VanillaNormal"));
		worldTags.put(new LongTag("UUID_lsb", new Random().nextLong()));
		worldTags.put(new LongTag("UUID_msb", new Random().nextLong()));

		DataMap map = new DataMap(new GenericDatatableMap());
		map.put("weather", "CLEAR");
		map.put("game_mode", world.getGamemode());
		map.put("difficulty", "NORMAL");
		map.put("dimension", "NORMAL");
		worldTags.put(new ByteArrayTag("extra_data", map.getRawMap().compress()));
		worldTags.put(new LongTag("age", 0));
		//World version 2
		ArrayList<FloatTag> spawn = new ArrayList<FloatTag>();
		spawn.add(new FloatTag("px", world.getSpawnX()));
		spawn.add(new FloatTag("py", world.getSpawnY()));
		spawn.add(new FloatTag("pz", world.getSpawnZ()));
		
		//TODO: Actually do something about those?
		spawn.add(new FloatTag("rw", 0.0f));
		spawn.add(new FloatTag("rx", 0.0f));
		spawn.add(new FloatTag("ry", 0.0f));
		spawn.add(new FloatTag("rz", 0.0f));
		spawn.add(new FloatTag("sx", 1.0f));
		spawn.add(new FloatTag("sy", 1.0f));
		spawn.add(new FloatTag("sz", 1.0f));
		worldTags.put(new ListTag<FloatTag>("spawn_position", FloatTag.class, spawn));
		CompoundTag worldTag = new CompoundTag(world.getName(), worldTags);
		sendUpdate();
		
		//Let's create the new folder
		NBTOutputStream os = null;
		try {
			spoutFolder = new File(worldFolder.getParent(), world.getName());
			spoutFolder.mkdirs();
			sendUpdate();
			os = new NBTOutputStream(new DataOutputStream(new FileOutputStream(new File(spoutFolder, "world.dat"))), false);
			os.writeTag(worldTag);
			result = true;
		} catch (IOException e) {
			System.out.println("Error saving world data for " + world.toString() + ". Reason:" + e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ignore) {
				}
			}
		}

		return result;
	}
	
	public static void sendUpdate() {
		System.out.println(".");
	}
}

package com.dre.dungeonsxl.signs;

import java.util.concurrent.CopyOnWriteArrayList;

public class DSignRoot {
	private static CopyOnWriteArrayList<DSign> signTypes = new CopyOnWriteArrayList<DSign>();
	
	//SignTypes
	private static SIGNCheckpoint signCheckpoint = new SIGNCheckpoint();
	private static SIGNChest signChest = new SIGNChest();
	private static SIGNChunkUpdater signChunkUpdater = new SIGNChunkUpdater();
	private static SIGNClasses signClasses = new SIGNClasses();
	private static SIGNEnd signEnd = new SIGNEnd();
	private static SIGNLeave signLeave = new SIGNLeave();
	private static SIGNLobby signLobby = new SIGNLobby();
	private static SIGNMob signMob = new SIGNMob();
	private static SIGNMsg signMsg = new SIGNMsg();
	private static SIGNPlace signPlace = new SIGNPlace();
	private static SIGNReady signReady = new SIGNReady();
	private static SIGNSoundMsg signSoundMsg = new SIGNSoundMsg();
	private static SIGNStart signStart = new SIGNStart();
	
	//Methods
	public static void init(){
		signTypes.add(signCheckpoint);
		signTypes.add(signChest);
		signTypes.add(signChunkUpdater);
		signTypes.add(signClasses);
		signTypes.add(signEnd);
		signTypes.add(signLeave);
		signTypes.add(signLobby);
		signTypes.add(signMob);
		signTypes.add(signMsg);
		signTypes.add(signPlace);
		signTypes.add(signReady);
		signTypes.add(signSoundMsg);
		signTypes.add(signStart);
	}
	
	public static CopyOnWriteArrayList<DSign> get(){
		return signTypes;
	}
}

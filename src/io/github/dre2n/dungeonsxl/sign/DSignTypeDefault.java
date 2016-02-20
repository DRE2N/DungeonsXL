package io.github.dre2n.dungeonsxl.sign;

public enum DSignTypeDefault implements DSignType {
	
	BLOCK("Block", "dxl.sign.block", false, BlockSign.class),
	CHECKPOINT("Checkpoint", "dxl.sign.checkpoint", false, CheckpointSign.class),
	CHEST("Chest", "dxl.sign.chest", false, ChestSign.class),
	CHUNK_UPDATER("ChunkUpdater", "dxl.sign.chunkupdater", true, ChunkUpdaterSign.class),
	CLASSES("Classes", "dxl.sign.classes", true, ClassesSign.class),
	COMMAND("CMD", "dxl.sign.cmd", false, CommandSign.class),
	END("End", "dxl.sign.end", false, EndSign.class),
	FLOOR("Floor", "dxl.sign.floor", false, FloorSign.class),
	INTERACT("Interact", "dxl.sign.interact", true, InteractSign.class),
	LEAVE("Leave", "dxl.sign.leave", true, LeaveSign.class),
	LOBBY("Lobby", "dxl.sign.lobby", true, LobbySign.class),
	MOB("Mob", "dxl.sign.mob", false, MobSign.class),
	MESSAGE("MSG", "dxl.sign.msg", false, MessageSign.class),
	MYTHIC_MOBS("MythicMobs", "dxl.sign.mob", false, MythicMobsSign.class),
	PLACE("Place", "dxl.sign.place", false, PlaceSign.class),
	READY("Ready", "dxl.sign.ready", true, ReadySign.class),
	REDSTONE("Redstone", "dxl.sign.redstone", false, RedstoneSign.class),
	SOUND_MESSAGE("SoundMSG", "dxl.sign.soundmsg", false, SoundMessageSign.class),
	START("Start", "dxl.sign.start", true, StartSign.class),
	TRIGGER("Trigger", "dxl.sign.trigger", true, TriggerSign.class),
	WAVE("Wave", "dxl.sign.wave", false, WaveSign.class);
	
	private String name;
	private String buildPermission;
	private boolean onDungeonInit;
	private Class<? extends DSign> handler;
	
	DSignTypeDefault(String name, String buildPermission, boolean onDungeonInit, Class<? extends DSign> handler) {
		this.name = name;
		this.buildPermission = buildPermission;
		this.onDungeonInit = onDungeonInit;
		this.handler = handler;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getBuildPermission() {
		return buildPermission;
	}
	
	@Override
	public boolean isOnDungeonInit() {
		return onDungeonInit;
	}
	
	@Override
	public Class<? extends DSign> getHandler() {
		return handler;
	}
	
}

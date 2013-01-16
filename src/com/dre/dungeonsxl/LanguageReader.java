package com.dre.dungeonsxl;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class LanguageReader {
	private Map<String,String> entries = new TreeMap<String,String>();
	private Map<String,String> defaults = new TreeMap<String,String>();

	private File file;
	private boolean changed;

	public LanguageReader(File file){
		this.setDefaults();

		/* Load */
		this.file = file;

		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);

		Set<String> keySet = configFile.getKeys(false);
		for(String key:keySet){
			entries.put(key, configFile.getString(key));
		}

		/* Check */
		this.check();
	}

	private void setDefaults(){

		/* Log */
		defaults.put("Log_NewDungeon","New Dungeon: ");
		defaults.put("Log_GenerateNewWorld","Generate new world...");
		defaults.put("Log_WorldGenerationFinished","World generation finished!");
		defaults.put("Log_Error_MobEnchantment","Error at loading mob.yml: Enchantmet &v1 doesn't exist!");
		defaults.put("Log_Error_MobType","Error at loading mob.yml: Mob &v1 doesn't exist!");
		defaults.put("Log_Error_Spout","Spout wurde nicht gefunden!");
		defaults.put("Log_Error_Spout_Skin","Der Spout Skin von &v1 ist kein png!");
		
		/* Player */
		defaults.put("Player_CheckpointReached","&6Checkpoint erreicht!");
		defaults.put("Player_LootAdded","&6Deinem Belohnungsinventar sind&4&v1&6 hinzugefügt worden!");
		defaults.put("Player_Ready","&6Du bist nun bereit für den Dungeon!");
		defaults.put("Player_FinishedDungeon","&6Du hast den Dungeon erfolgreich beendet!");
		defaults.put("Player_WaitForOtherPlayers","&6Noch auf Mitspieler warten...");
		defaults.put("Player_LeaveGroup","&6Du hast deine Gruppe erfolgreich verlassen!");

		/* Cmds */
		defaults.put("Cmd_Chat_DungeonChat","&6Du bist nun im Dungeon-Chat");
		defaults.put("Cmd_Chat_NormalChat","&6Du bist nun im öffentlichen Chat");
		defaults.put("Cmd_Chatspy_Stopped","&6Du hast aufgehört den DXL-Chat auszuspähen!");
		defaults.put("Cmd_Chatspy_Start","&Du hast begonnen den DXL-Chat auszuspähen!");
		defaults.put("Cmd_Invite_Success","&6Spieler &4&v1&6 wurde erfolgreich eingeladen am Dungeon &4&v2&6 zu arbeiten!");
		defaults.put("Cmd_Leave_Success","&6Du hast deine Gruppe erfolgreich verlassen!");
		defaults.put("Cmd_Msg_Added","&6Neue Nachricht (&4&v1&6) hinzugefügt!");
		defaults.put("Cmd_Msg_Updated","&6Nachricht (&4&v1&6) aktualisiert!");
		defaults.put("Cmd_Reload_Start","&6DungeonsXL wird neu geladen");
		defaults.put("Cmd_Reload_Done","&6DungeonsXL erfolgreich neu geladen!");
		defaults.put("Cmd_Save_Success","&6Dungeon erfolgreich gespeichert!");
		defaults.put("Cmd_Uninvite_Success","&4&v1&6 wurde erfolgreich ausgeladen an &4&v1&6 zu arbeiten!");

		/* Errors */
		defaults.put("Error_Enderchest","&4Du kannst keine Enderchest in einem Dungeon verwenden!");
		defaults.put("Error_Dispenser","&4Du kannst nicht auf diesen Dispenser zugreifen!");
		defaults.put("Error_Ready","&4Wähle zuerst eine Klasse aus!");
		defaults.put("Error_Cooldown","&4Du kannst den Dungeon nur alle &6&v1&4 Stunden betreten!");
		defaults.put("Error_Leftklick","&4Du musst das Schild mit Links-klick berühren!");
		defaults.put("Error_Drop","&4Du kannst keine sicheren Objekte droppen");
		defaults.put("Error_Cmd","&4Befehle sind während des Dungeons nicht erlaubt!");
		defaults.put("Error_NotInGroup","&4Du musst zuerst einer Gruppe beitreten!");
		defaults.put("Error_NoPermissions","&4Du hast keine Erlaubnis dies zu tun!");
		defaults.put("Error_CmdNotExist1","&4Befehl &6&v1&4 existiert nicht!");
		defaults.put("Error_CmdNotExist2","&4Bitte gib &6/dxl help&4 für Hilfe ein!");
		defaults.put("Error_NotInDungeon","&4Du bist in keinem Dungeon!");
		defaults.put("Error_DungeonNotExist","&4Dungeon &6&v1&4 existiert nicht!");
		defaults.put("Error_LeaveDungeon","&4Du musst zuerst den aktuellen Dungeon verlassen!");
		defaults.put("Error_NameToLong","&4Der Name darf nicht länger sein als 15 Zeichen!");
		defaults.put("Error_LeaveGroup","&4Du musst zuerst deine Gruppe verlassen!");
		defaults.put("Error_NoLeaveInTutorial","&4Du kannst diesen Befehl nicht in einem Tutorial benutzen!");
		defaults.put("Error_MsgIdNotExist","&4Nachricht mit der Id &6&v1&4 existiert nicht!");
		defaults.put("Error_MsgFormat","&4Die Nachricht muss zwischen '' liegen!");
		defaults.put("Error_MsgNoInt","&4Parameter <id> muss eine Zahl beinhalten!");
		defaults.put("Error_TutorialNotExist","&4Tutorial Dungeon existiert nicht!");
		defaults.put("Error_SpoutCraftOnly","&4Du brauchst SpoutCraft um diesen Dungeon spielen zu können!");
		
		
		/* Help */
		defaults.put("Help_Cmd_Chat","/dxl chat - Ändert den Chat-Modus");
		defaults.put("Help_Cmd_Chatspy","/dxl chatspy - De/Aktiviert den Spioniermodus");
		defaults.put("Help_Cmd_Create","/dxl create <name> - Erstellt einen neuen Dungeon");
		defaults.put("Help_Cmd_Edit","/dxl edit <name> - Editiere einen existierenden Dungeon");
		defaults.put("Help_Cmd_Help","/dxl help - Zeigt die Hilfeseite an");
		defaults.put("Help_Cmd_Invite","/dxl invite <player> <dungeon> - Ladet einen Spieler dazu ein den Dungeon zu editieren");
		defaults.put("Help_Cmd_Leave","/dxl leave - Verlässt den aktuellen Dungeon");
		defaults.put("Help_Cmd_List","/dxl list - Zeigt alle Dungeons an");
		defaults.put("Help_Cmd_Msg","/dxl msg <id> '[msg]' - Zeigt oder editiert eine Nachricht");
		defaults.put("Help_Cmd_Portal","/dxl portal - Erstellt ein Portal welches in Dungeons führt");
		defaults.put("Help_Cmd_Reload","/dxl reload - Ladet das Plugin neu");
		defaults.put("Help_Cmd_Save","/dxl save - Speichert den aktuellen Dungeon");
		defaults.put("Help_Cmd_Test","/dxl test [dungeon] - Testet einen Dungeon");
		defaults.put("Help_Cmd_Uninvite","/dxl uninvite <player> <dungeon> - Lädt einen Spieler aus den Dungeon zu editieren");
	}

	private void check(){
		for(String defaultEntry:defaults.keySet()){
			if(!entries.containsKey(defaultEntry)){
				entries.put(defaultEntry,defaults.get(defaultEntry));
				changed = true;
			}
		}
	}

	public void save(){
		if(changed){
			/* Copy old File */
			File source = new File(file.getPath());
			String filePath = file.getPath();
			File temp = new File(filePath.substring(0,filePath.length()-4)+"_old.yml");

	        if(temp.exists())
	            temp.delete();

	        source.renameTo(temp);

			/* Save */
			FileConfiguration configFile = new YamlConfiguration();

			for(String key:entries.keySet()){
				configFile.set(key, entries.get(key));
			}

			try {
				configFile.save(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String get(String key, String... args){
		String entry = entries.get(key);

		if(entry!=null){
			int i=0;
			for(String arg:args){
				i++;
				entry = entry.replace("&v"+i, arg);
			}
		}

		return entry;
	}
}

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
		
		P.p.log("FilePath:"+file.getPath());
		
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
		
		Set<String> keySet = configFile.getKeys(false);
		for(String key:keySet){
			entries.put(key, configFile.getString(key));
		}
		
		/* Check */
		this.check();
	}
	
	private void setDefaults(){
		defaults.put("error_tutorialnotexist","&4Tutorial Dungeon existiert nicht!");
		
		/* Log */
		defaults.put("log_newdungeon","New Dungeon: ");
		defaults.put("log_generatenewworld","Generate new world...");
		defaults.put("log_worldgenerationfinished","World generation finished!");
		defaults.put("log_error_mobenchantment","Error at loading mob.yml: Enchantmet &v1 doesn't exist!");
		defaults.put("log_error_mobtype","Error at loading mob.yml: Mob &v1 doesn't exist!");
		
		/* Player */
		defaults.put("player_checkpoint_reached","&6Checkpoint erreicht!");
		defaults.put("player_lootadded","&6Deinem Belohnungsinventar sind&4&v1&6 hinzugefügt worden!");
		defaults.put("player_enderchest_error","&4Du kannst keine Enderchest in einem Dungeon verwenden!");
		defaults.put("player_ready","&6Du bist nun bereit für den Dungeon!");
		defaults.put("player_ready_error","&4Wähle zuerst eine Klasse aus!");
		defaults.put("player_leftklick_error","&4Du musst das Schild mit Links-klick berühren!");
		defaults.put("player_drop_error","&4Du kannst keine sicheren Objekte droppen");
		defaults.put("player_cmd_error","&4Befehle sind während des Dungeons nicht erlaubt!");
		defaults.put("player_join_error","&4Du kannst den Dungeon nur alle &6&v1&4 Stunden betreten!");
		defaults.put("player_finisheddungeon","&6Du hast den Dungeon erfolgreich beendet!");
		defaults.put("player_waitforotherplayers","&6Noch auf Mitspieler warten...");
		defaults.put("player_dungeon_error","&4Dungeon &6&v1&4 existiert nicht!");
		defaults.put("player_group_error","&4Du musst zuerst einer Gruppe beitreten!");
		defaults.put("player_leavegroup","&6Du hast deine Gruppe erfolgreich verlassen!");
		
		/* Cmds */
		defaults.put("cmd_chat_dungeonchat","&6Du bist nun im Dungeon-Chat");
		defaults.put("cmd_chat_error1","&4Du bist in keinem Dungeon!");
		defaults.put("cmd_chat_normalchat","&6Du bist nun im öffentlichen Chat");
		defaults.put("cmd_chatspy_stopped","&6Du hast aufgehört den DXL-Chat auszuspähen!");
		defaults.put("cmd_chatspy_start","&Du hast begonnen den DXL-Chat auszuspähen!");
		defaults.put("cmd_create_error1","&4Der Name darf nicht länger sein als 15 Zeichen!");
		defaults.put("cmd_create_error2","&4Du musst zuerst aus dem aktuellen Dungeon raus!");
		defaults.put("cmd_edit_error1","&4Dungeon &6&v1&4 existiert nicht!");
		defaults.put("cmd_edit_error2","&4Du musst zuerst deine Gruppe verlassen!");
		defaults.put("cmd_edit_error3","&4Du musst zuerst den aktuellen Dungeon verlassen!");
		defaults.put("cmd_invite_success","&6Spieler &4&v1&6 wurde erfolgreich eingeladen am Dungeon &4&v2&6 zu arbeiten!");
		defaults.put("cmd_invite_error1","&4Dungeon &6&v1&4 existiert nicht!");
		defaults.put("cmd_leave_error1","&4You aren't in a dungeon!");
		defaults.put("cmd_leave_error2","&4Du kannst diesen Befehl nicht in einem Tutorial benutzen!");
		defaults.put("cmd_leave_success","&6Du hast deine Gruppe erfolgreich verlassen!");
		defaults.put("cmd_msg_error1","&4Nachricht mit der Id &6&v1&4 existiert nicht!");
		defaults.put("cmd_msg_error2","&4Die Nachricht muss zwischen '' liegen!");
		defaults.put("cmd_msg_error3","&4Parameter <id> muss eine Zahl beinhalten!");
		defaults.put("cmd_msg_error4","&4Du musst einen Dungeon bearbeiten um diesen Befehl zu benutzen!");
		defaults.put("cmd_msg_added","&6Neue Nachricht (&4&v1&6) hinzugefügt!");
		defaults.put("cmd_msg_updated","&6Nachricht (&4&v1&6) aktualisiert!");
		defaults.put("cmd_reload_start","&6DungeonsXL wird neu geladen");
		defaults.put("cmd_reload_done","&6DungeonsXL erfolgreich neu geladen!");
		defaults.put("cmd_save_success","&6Dungeon erfolgreich gespeichert!");
		defaults.put("cmd_save_error1","&4Du musst einen Dungeon editieren, um ihn zu speichern!");
		defaults.put("cmd_test_error1","&4Dungeon &6&v1&4 existiert nicht!");
		defaults.put("cmd_test_error2","&4Du must zuerst den aktuellen Dungeon verlassen!");
		defaults.put("cmd_uninvite_success","&4&v1&6 wurde erfolgreich ausgeladen an &4&v1&6 zu arbeiten!");
		defaults.put("cmd_uninvite_error1","&4Dungeon &6&v1&4 existiert nicht!");
		defaults.put("cmd_nopermissions","&4Du hast keine Erlaubnis dies zu tun!");
		defaults.put("cmd_notexist1","&4Befehl &6&v1&4 existiert nicht!");
		defaults.put("cmd_notexist2","&4Bitte gib &6/dxl help&4 für Hilfe ein!");
		
		/* Help */
		defaults.put("help_cmd_chat","/dxl chat - Ändert den Chat-Modus");
		defaults.put("help_cmd_chatspy","/dxl chatspy - De/Aktiviert den Spioniermodus");
		defaults.put("help_cmd_create","/dxl create <name> - Erstellt einen neuen Dungeon");
		defaults.put("help_cmd_edit","/dxl edit <name> - Editiere einen existierenden Dungeon");
		defaults.put("help_cmd_help","/dxl help - Zeigt die Hilfeseite an");
		defaults.put("help_cmd_invite","/dxl invite <player> <dungeon> - Ladet einen Spieler dazu ein den Dungeon zu editieren");
		defaults.put("help_cmd_leave","/dxl leave - Verlässt den aktuellen Dungeon");
		defaults.put("help_cmd_list","/dxl list - Zeigt alle Dungeons an");
		defaults.put("help_cmd_msg","/dxl msg <id> '[msg]' - Zeigt oder editiert eine Nachricht");
		defaults.put("help_cmd_portal","/dxl portal - Erstellt ein Portal welches in Dungeons führt");
		defaults.put("help_cmd_reload","/dxl reload - Ladet das Plugin neu");
		defaults.put("help_cmd_save","/dxl save - Speichert den aktuellen Dungeon");
		defaults.put("help_cmd_test","/dxl test [dungeon] - Testet einen Dungeon");
		defaults.put("help_cmd_uninvite","/dxl uninvite <player> <dungeon> - Lädt einen Spieler aus den Dungeon zu editieren");
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

package com.dre.dungeonsxl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class DConfig {
	public File file;
	
	private CopyOnWriteArrayList<DClass> dClasses = new CopyOnWriteArrayList<DClass>();
	public Map<Integer,String> msgs = new HashMap<Integer,String>();
	
	public CopyOnWriteArrayList<String> invitedPlayers = new CopyOnWriteArrayList<String>();
	public CopyOnWriteArrayList<Material> secureObjects = new CopyOnWriteArrayList<Material>();
	
	public boolean isLobbyDisabled = false;
	public int timeToNextPlay = 0;
	public int timeToNextLoot = 0;
	
	//Spout
	public boolean spoutCraftOnly = false;
	public String spoutTexturepackURL;
	
	public DConfig(File file){
		this.file=file;
		
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
		
		load(configFile);
	}
	
	public DConfig(ConfigurationSection configFile){
		load(configFile);
	}
	
	//Load & Save
	public void load(ConfigurationSection configFile){
		
		/* Classes */
		ConfigurationSection configSetionClasses = configFile.getConfigurationSection("classes");
		if(configSetionClasses!=null){
			Set<String> list = configSetionClasses.getKeys(false);
			for (String className:list) {
				String name = className;
				boolean hasDog = configSetionClasses.getBoolean(className+".hasdog");
				
				/* Items */
				List<String> items = configSetionClasses.getStringList(className+".items");
				CopyOnWriteArrayList<ItemStack> istacks=new CopyOnWriteArrayList<ItemStack>();
				
				for(String item:items){
					String[] itemsplit=item.split(",");
					if(itemsplit.length>0){
						int itemId=0,itemData=0,itemSize=1,itemLvlEnchantment=1;
						Enchantment itemEnchantment=null;
						
						//Check Id & Data
						String[] idAndData=itemsplit[0].split("/");
						itemId=P.p.parseInt(idAndData[0]);
						
						if(idAndData.length>1){
							itemData=P.p.parseInt(idAndData[1]);
						}
						
						//Size
						if(itemsplit.length>1){
							itemSize=P.p.parseInt(itemsplit[1]);
						}
						
						//Enchantment
						if(itemsplit.length>2){
							String[] enchantmentSplit=itemsplit[2].split("/");
							
							itemEnchantment=Enchantment.getByName(enchantmentSplit[0]);
							
							if(enchantmentSplit.length>1){
								itemLvlEnchantment=P.p.parseInt(enchantmentSplit[1]);
							}
						}
						
						//Add Item to Stacks
						ItemStack istack=new ItemStack(itemId,itemSize,(short) itemData);
						if(itemEnchantment!=null){
							istack.addEnchantment(itemEnchantment, itemLvlEnchantment);
						}
						
						istacks.add(istack);
					}
				}
				
				/* Spout */
				String spoutSkinURL = null;
				if(P.p.isSpoutEnabled){
					if(configSetionClasses.contains(className+".spoutSkinURL")){
						spoutSkinURL = configSetionClasses.getString(className+".spoutSkinURL");
					}
				}
				
				/* Create Class */
				this.dClasses.add(new DClass(name,istacks,hasDog,spoutSkinURL));
			}
		}
		
		/* Messages */
		ConfigurationSection configSetionMessages = configFile.getConfigurationSection("message");
		if (configSetionMessages != null) {
			Set<String> list = configSetionMessages.getKeys(false);
			for (String messagePath:list) {
				int messageId = P.p.parseInt(messagePath);
				this.msgs.put(messageId,configSetionMessages.getString(messagePath));
			}
		}
		
		/* Secure Objects */
		if(configFile.contains("secureObjects")){						
			List<Integer> secureobjectlist = configFile.getIntegerList("secureObjects");
			for(int i:secureobjectlist){
				this.secureObjects.add(Material.getMaterial(i));
			}
		}
		
		/* Invited Players */
		if(configFile.contains("invitedplayers")){
			List<String> invitedplayers = configFile.getStringList("invitedplayers");
			for(String i:invitedplayers){
				this.invitedPlayers.add(i);
			}
		}
		
		/* Lobby */
		if(configFile.contains("islobbydisabled")){
			isLobbyDisabled = configFile.getBoolean("islobbydisabled");
		}
		
		/* Times */
		if(configFile.contains("timetonextplay")){
			timeToNextPlay = configFile.getInt("timetonextplay");
		}
		
		if(configFile.contains("timetonextloot")){
			timeToNextLoot = configFile.getInt("timetonextloot");
		}
		
		/* Spout */
		if(configFile.contains("spout.spoutCraftOnly")){
			spoutCraftOnly = configFile.getBoolean("spout.spoutCraftOnly");
		}
		
		if(configFile.contains("spout.spoutTexturepackURL")){
			spoutTexturepackURL = configFile.getString("spout.spoutTexturepackURL");
		}
	}

	public void save(){
		if(this.file!=null){
			FileConfiguration configFile = YamlConfiguration.loadConfiguration(this.file);
			
			//Messages
			for(Integer msgs:this.msgs.keySet()){
				configFile.set("message."+msgs, this.msgs.get(msgs));
			}
			
			//Secure Objects
			CopyOnWriteArrayList<Integer> secureObjectsids=new CopyOnWriteArrayList<Integer>();
			
			for(Material mat:this.secureObjects){
				secureObjectsids.add(mat.getId());
			}
			
			configFile.set("secureObjects", secureObjectsids);
			
			//Invited Players
			configFile.set("invitedplayers", this.invitedPlayers);
			
			try {
				configFile.save(this.file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//Get
	public CopyOnWriteArrayList<DClass> getClasses(){
		return dClasses;
	}
	
	public DClass getClass(String name){
		for(DClass dClass:dClasses){
			if(dClass.name.equals(name)){
				return dClass;
			}
		}
		return null;
	}
	
	public String getMsg(int id){
		return this.msgs.get(id);
	}
}

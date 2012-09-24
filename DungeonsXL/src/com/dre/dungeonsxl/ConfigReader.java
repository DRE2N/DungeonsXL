package com.dre.dungeonsxl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ConfigReader {
	public static DungeonsXL p=DungeonsXL.p;
	
	public File file;
	
	private CopyOnWriteArrayList<DClass> dclasses=new CopyOnWriteArrayList<DClass>();
	public CopyOnWriteArrayList<String> invitedPlayer = new CopyOnWriteArrayList<String>();
	public Map<Integer,String> msgs=new HashMap<Integer,String>();
	public CopyOnWriteArrayList<Material> secureobjects=new CopyOnWriteArrayList<Material>();
	
	public boolean isLobbyDisabled=false;
	public int timeToNextPlay=0;
	
	public ConfigReader(File file){
		this.file=file;
		
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(file);
		
		//Read Classes
		int id=0;
		String preString;
		do{
			id++;
			preString="classes."+id+".";
			if(configFile.contains(preString)){						
				String name=configFile.getString(preString+".name");
				boolean hasDog=configFile.getBoolean(preString+".hasdog");
				@SuppressWarnings("unchecked")
				List<String> items=(List<String>) configFile.getList(preString+".items");

				CopyOnWriteArrayList<ItemStack> istacks=new CopyOnWriteArrayList<ItemStack>();
				
				for(String item:items){
					String[] itemsplit=item.split(",");
					if(itemsplit.length>0){
						int itemId=0,itemData=0,itemSize=1,itemLvlEnchantment=1;
						Enchantment itemEnchantment=null;
						
						//Check Id & Data
						String[] idAndData=itemsplit[0].split("/");
						
						itemId=Integer.parseInt(idAndData[0]);
						
						if(idAndData.length>1){
							itemData=Integer.parseInt(idAndData[1]);
						}
						
						//Size
						if(itemsplit.length>1){
							itemSize=Integer.parseInt(itemsplit[1]);
						}
						
						//Enchantment
						if(itemsplit.length>2){
							String[] enchantmentSplit=itemsplit[2].split("/");
							
							itemEnchantment=Enchantment.getByName(enchantmentSplit[0]);
							
							if(enchantmentSplit.length>1){
								itemLvlEnchantment=Integer.parseInt(enchantmentSplit[1]);
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
				dclasses.add(new DClass(name,istacks,hasDog));
			}
		}while(configFile.contains(preString));
		
		//Read Messages
		id=0;
		do{
			id++;
			preString="message."+id;
			if(configFile.contains(preString)){
				this.msgs.put(id,configFile.getString(preString));
			}
		}while(configFile.contains(preString));
		
		//Read Secure Objects
		if(configFile.contains("secureobjects")){						
			@SuppressWarnings("unchecked")
			List<Integer> secureobjectlist=(List<Integer>) configFile.getList("secureobjects");
			for(int i:secureobjectlist){
				this.secureobjects.add(Material.getMaterial(i));
			}
		}
		
		//Read Invited Player
		if(configFile.contains("invitedplayers")){
			@SuppressWarnings("unchecked")
			List<String> invitedplayers=(List<String>) configFile.getList("invitedplayers");
			for(String i:invitedplayers){
				this.invitedPlayer.add(i);
			}
		}
		
		//Read Tutorial-Mode
		if(configFile.contains("tutorialdungeon")){
			p.tutorialDungeon=configFile.getString("tutorialdungeon");
			p.tutorialStartGroup=configFile.getString("tutorialstartgroup");
			p.tutorialEndGroup=configFile.getString("tutorialendgroup");
		}
		
		//Read Lobby disabled
		if(configFile.contains("islobbydisabled")){
			isLobbyDisabled=configFile.getBoolean("islobbydisabled");
		}
		
		if(configFile.contains("timetonextplay")){
			timeToNextPlay=configFile.getInt("timetonextplay");
		}
		
	}
	
	public String getMsg(int id){
		return this.msgs.get(id);
	}
	
	
	//Save
	public void save(){
		FileConfiguration configFile = YamlConfiguration.loadConfiguration(this.file);
		
		//Classes don't save
		
		//Messages
		for(Integer msgs:this.msgs.keySet()){
			configFile.set("message."+msgs, this.msgs.get(msgs));
		}
		
		//Secure Objects
		CopyOnWriteArrayList<Integer> secureobjectsids=new CopyOnWriteArrayList<Integer>();
		
		for(Material mat:this.secureobjects){
			secureobjectsids.add(mat.getId());
		}
		
		configFile.set("secureobjects", secureobjectsids);
		
		//Invited Players
		configFile.set("invitedplayers", this.invitedPlayer);
		
		try {
			p.log("SAVE"+this.file);
			configFile.save(this.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Classes
	
	public CopyOnWriteArrayList<DClass> getClasses(){
		return dclasses;
	}
	
	public DClass getClass(String name){
		for(DClass dclass:dclasses){
			if(dclass.name.equals(name)){
				return dclass;
			}
		}
		return null;
	}
	
}

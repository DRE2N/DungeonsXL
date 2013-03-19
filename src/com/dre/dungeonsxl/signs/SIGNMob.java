package com.dre.dungeonsxl.signs;

import org.bukkit.block.Sign;
import com.dre.dungeonsxl.game.GameWorld;
import com.dre.dungeonsxl.game.MobSpawner;

public class SIGNMob extends DSignType{

	@Override
	public boolean check(Sign sign) {
		// TODO Auto-generated method stub
		
		return false;
	}

	@Override
	public void onDungeonInit(Sign sign, GameWorld gworld) {
		String lines[] = sign.getLines();
		if(lines[2]!=""&&lines[3]!=""){
			String mob=lines[2];
			if(mob!=null){
				String[] atributes=lines[3].split(",");
				if(atributes.length==3){
					new MobSpawner(sign.getBlock(), mob, p.parseInt(atributes[0]), p.parseInt(atributes[1]), p.parseInt(atributes[2]),0);
				}
				if(atributes.length==4){
					new MobSpawner(sign.getBlock(), mob, p.parseInt(atributes[0]), p.parseInt(atributes[1]), p.parseInt(atributes[2]),p.parseInt(atributes[3]));
				}
			}
		}
		sign.setTypeId(0);
	}

	@Override
	public void onTrigger(Sign sign, GameWorld gworld) {
		// TODO Auto-generated method stub
		
	}
}

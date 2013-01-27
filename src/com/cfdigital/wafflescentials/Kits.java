package com.cfdigital.wafflescentials;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Kits {
	public Kits(int cooldown, String itemList) {
		String[] items = itemList.split(" ");
		setCoolDown(cooldown);
		int x = 0;
		
		while (x != items.length) {
			int itemID = Integer.valueOf(items[x]);
			ItemStack is = new ItemStack(Material.getMaterial(itemID));
			this.itemStack.add(is);
			x++;
		}		
	}
	
	private void setCoolDown(int coolDown) {
		this.coolDown = coolDown;
	}
	public int getCoolDown() {
		return coolDown;
	}

	public List<ItemStack> getItemStack() {
		return itemStack;
	}

	private int coolDown;
	private List<ItemStack> itemStack = new ArrayList<ItemStack>();
	
}
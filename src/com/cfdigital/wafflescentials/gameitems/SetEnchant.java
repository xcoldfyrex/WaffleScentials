package com.cfdigital.wafflescentials.gameitems;

import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetEnchant {
	

	public static boolean addEnchant(Player player, String enchantment, int level) {
		Enchantment en = Enchantment.getByName(enchantment);
		ItemStack is = player.getInventory().getItemInHand();
		is.addUnsafeEnchantment(en, level);
		return true;
	}
	
	public static boolean clearEnchantments(Player player) {
		ItemStack is = player.getInventory().getItemInHand();
		Map<Enchantment, Integer> em = is.getEnchantments();
		for (Enchantment toRemove : em.keySet()) {
			is.removeEnchantment(toRemove);
		}
		return true;
	}
	
}
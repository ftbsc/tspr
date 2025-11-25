package ftbsc.tspr.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ftbsc.tspr.Tiramisuper;
import static ftbsc.tspr.Tiramisuper.mc;

/**
 * Utility methods to scan, process and manage player inventory
 */
public class Inventory {

	/** number of hotbar slots */
	public static final int HOTBAR_SIZE = 9;
	/** start index of hotbar, in inventory slots */
	public static final int HOTBAR_START = 36;

	/**
	 * Returns only {@link Slot}s in player hotbar
	 * @param player local player to get hotbar of
	 */
	public static List<Slot> hotbar(LocalPlayer player) {
		return player.inventoryMenu.slots.subList(HOTBAR_START, HOTBAR_START + HOTBAR_SIZE);
	}

	/**
	 * Calculates attach damage for given {@link ItemStack}
	 * @param item get damage for this item
	 */
	public static double itemAttachDamage(ItemStack item) {
		List<Double> attackDamage = new ArrayList<>();
		item.getAttributeModifiers().forEach(EquipmentSlotGroup.MAINHAND, (attr, mod) -> {
			if (attr.equals(Attributes.ATTACK_DAMAGE)) {
				attackDamage.add(mod.amount());
			}

		});
		if (attackDamage.size() > 1) {
			Tiramisuper.LOGGER.warn("item has multiple attack speeds? {}", item.getItem().toString());
		} else if (attackDamage.isEmpty()) {
			attackDamage.add(0.);
		}

		return 2. + attackDamage.getFirst();
	}

	/**
	 * Calculates attach speed for given {@link ItemStack}
	 * @param item get speed for this item
	 */
	public static double itemAttackSpeed(ItemStack item) {
		List<Double> attackSpeed = new ArrayList<>();
		item.getAttributeModifiers().forEach(EquipmentSlotGroup.MAINHAND, (attr, mod) -> {
			if (attr.equals(Attributes.ATTACK_SPEED)) {
				attackSpeed.add(mod.amount());
			}

		});
		if (attackSpeed.size() > 1) {
			Tiramisuper.LOGGER.warn("item has multiple attack speeds? {}", item.getItem().toString());
		} else if (attackSpeed.isEmpty()) {
			attackSpeed.add(0.);
		}

		return 4.0 + attackSpeed.getFirst();
	}

	/**
	 * Calculates Damage-Per-Second for given {@link ItemStack}
	 * @param item get dps for this item
	 */
	public static double itemDPS(ItemStack item) {
		double damage = Inventory.itemAttachDamage(item);
		double speed  = Inventory.itemAttackSpeed(item);

		// TODO wtf is this disaster .......
		var lookup = net.neoforged.neoforge.common.CommonHooks.resolveLookup(net.minecraft.core.registries.Registries.ENCHANTMENT);
		var enchs = item.getAllEnchantments(lookup);
		var sharp = enchs.keySet().stream().filter(e -> e.getKey().equals(Enchantments.SHARPNESS)).findAny();

		if (sharp.isPresent()) {
			int sharpness = item.getEnchantmentLevel(sharp.get());
			if (sharpness > 0) {
				damage += (0.5 * sharpness) + 0.5;
			}
		}

		return damage * speed;
	}

	public static void clickSlot(int slotIndex, ClickType click) { clickSlot(0, slotIndex, 0, click); }
	public static void clickSlot(Slot slot, int button, ClickType click) { clickSlot(0, slot.index, button, click); }
	public static void clickSlot(int container, int slot_index, ClickType click) { clickSlot(container, slot_index, 0, click); }

	public static void clickSlot(int container, int slot_index, int button, ClickType click) {
		mc().gameMode.handleInventoryMouseClick(container, slot_index, button, click, Minecraft.getInstance().player);
	}

	public static boolean matchItem(Pattern pattern, ItemStack stack) {
		if (stack.isEmpty()) return false;

		String displayName = stack.getDisplayName().getString();
		if (pattern.matcher(displayName).find()) return true;

		if (Items.ENCHANTED_BOOK.equals(stack.getItem()) || stack.isEnchanted()) {
			var lookup = net.neoforged.neoforge.common.CommonHooks.resolveLookup(net.minecraft.core.registries.Registries.ENCHANTMENT);
			var enchs = stack.getAllEnchantments(lookup);
			for (Holder<Enchantment> ench : enchs.keySet()) {
				if (pattern.matcher(ench.getRegisteredName()).find()) return true;
			}
		}

		return false;
	}
}

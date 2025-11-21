package ftbsc.tspr.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import ftbsc.tspr.Tiramisuper;

public class Inventory {

	public static final int HOTBAR_SIZE = 9;
	public static final int HOTBAR_START = 36;

	public static List<Slot> hotbar(LocalPlayer player) {
		return player.inventoryMenu.slots.subList(HOTBAR_START, HOTBAR_START + HOTBAR_SIZE);
	}

	public static double itemAttachDamage(ItemStack item) {
		List<Double> attackDamage = new ArrayList<>();
		item.getAttributeModifiers().forEach(EquipmentSlotGroup.MAINHAND, (attr, mod) -> {
			if (attr.equals(Attributes.ATTACK_DAMAGE)) {
				attackDamage.add(mod.amount());
			}

		});
		if (attackDamage.size() > 1) {
			Tiramisuper.LOGGER.warn("item has multiple attack speeds?");
		} else if (attackDamage.isEmpty()) {
			Tiramisuper.LOGGER.error("could not find attack speed attribute for item");
			return 0.1;
		}
		return attackDamage.getFirst();
	}

	public static double itemAttackSpeed(ItemStack item) {
		List<Double> attackSpeed = new ArrayList<>();
		item.getAttributeModifiers().forEach(EquipmentSlotGroup.MAINHAND, (attr, mod) -> {
			if (attr.equals(Attributes.ATTACK_SPEED)) {
				attackSpeed.add(mod.amount());
			}

		});
		if (attackSpeed.size() > 1) {
			Tiramisuper.LOGGER.warn("item has multiple attack speeds?");
		} else if (attackSpeed.isEmpty()) {
			Tiramisuper.LOGGER.error("could not find attack speed attribute for item");
			return 0.1;
		}
		return attackSpeed.getFirst();
	}

	public static double itemDPS(ItemStack item) {
		double damage = (double) item.getDamageValue();
		double speed  = Inventory.itemAttackSpeed(item);

		// int sharpness = item.getEnchantmentLevel(Enchantments.SHARPNESS);
		// if (sharpness > 0) {
		// 	damage += 0.5 * Math.max(0, sharpness - 1) + 1.;
		// }

		return damage / (1. + speed);
	}

	public static void clickSlot(int slotIndex, ClickType click) { clickSlot(0, slotIndex, 0, click); }
	public static void clickSlot(Slot slot, int button, ClickType click) { clickSlot(0, slot.index, button, click); }
	public static void clickSlot(int container, int slot_index, ClickType click) { clickSlot(container, slot_index, 0, click); }

	public static void clickSlot(int container, int slot_index, int button, ClickType click) {
		Minecraft.getInstance().gameMode.handleInventoryMouseClick(container, slot_index, button, click, Minecraft.getInstance().player);
	}

	public static boolean matchItem(Pattern pattern, ItemStack stack) {
		if (stack.isEmpty()) return false;

		String displayName = stack.getDisplayName().getString();
		if (pattern.matcher(displayName).find()) return true;

		// if (Items.ENCHANTED_BOOK.equals(stack.getItem()) || stack.isEnchanted()) {
		// 	for (String ench : itemEnchantments(stack)) {
		// 		if (pattern.matcher(ench).find()) return true;
		// 	}
		// }

		return false;
	}
}

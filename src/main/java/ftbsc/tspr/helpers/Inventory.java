package ftbsc.tspr.helpers;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.regex.Pattern;

import ftbsc.tspr.api.IGlobals;

public class Inventory implements IGlobals {

	public static final int HOTBAR_SIZE = 9;
	public static final int HOTBAR_START = 36;

	public static List<Slot> hotbar(LocalPlayer player) {
		return player.inventoryMenu.slots.subList(HOTBAR_START, HOTBAR_START + HOTBAR_SIZE);
	}

	public static double itemAttackSpeed(ItemStack item) {
		// Collection<AttributeModifier> speed_attrs =
		// 	item.getAttributeModifiers(EquipmentSlotGroup.MAINHAND)
		// 		.get(Attributes.ATTACK_SPEED);
		// if (speed_attrs.isEmpty()) return 0.;
		// return Math.abs(speed_attrs.iterator().next().getAmount());
		return 0.;
	}

	public static double itemDPS(ItemStack item) {
		double damage = (double) item.getDamageValue();
		double speed  = Inventory.itemAttackSpeed(item);

		// int sharpness = getEnchLevel(item, Enchantments.SHARPNESS);
		// if (sharpness > 0) {
		// 	damage += 0.5 * Math.max(0, sharpness - 1) + 1.;
		// }

		return damage / (1. + speed);
	}

	public static void clickSlot(int slotIndex, ClickType click) { clickSlot(0, slotIndex, 0, click); }
	public static void clickSlot(Slot slot, int button, ClickType click) { clickSlot(0, slot.index, button, click); }
	public static void clickSlot(int container, int slot_index, ClickType click) { clickSlot(container, slot_index, 0, click); }

	public static void clickSlot(int container, int slot_index, int button, ClickType click) {
		MC.gameMode.handleInventoryMouseClick(container, slot_index, button, click, MC.player);
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

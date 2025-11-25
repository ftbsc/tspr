package ftbsc.tspr.modules.client;

import com.google.auto.service.AutoService;
import com.mojang.blaze3d.platform.InputConstants;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.TogglableModule;
import ftbsc.tspr.helpers.Inventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import static ftbsc.tspr.Tiramisuper.mc;

@AutoService(ILoadable.class)
public class PickTool extends TogglableModule {

	// private ModConfigSpec.IntValue limit;
	// private ModConfigSpec.BooleanValue prefer_looting;

	@Override
	public void config(ModConfigSpec.Builder builder) {
		// this.limit = builder
		// 	.comment("durability limit for tools, set to 0 to destroy them")
		// 	.defineInRange("limit", 1, 0, Integer.MAX_VALUE);

		// this.prefer_looting = builder
		// 	.comment("when picking best weapon, prefer looting over slight more DPS")
		// 	.define("prefer-looting", true);
	}

	public static boolean itemIsTooDamaged(ItemStack item) {
		return item.getMaxDamage() > 0
			&& item.getMaxDamage() - item.getDamageValue() <= 3;
	}

	public static boolean selectBestWeapon() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return false;
		LocalPlayer player = mc.player;
		List<Slot> hotbar = Inventory.hotbar(player);
		int current_slot = player.getInventory().getSelectedSlot();
		double current_damage = Inventory.itemDPS(hotbar.get(current_slot).getItem());
		for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
			ItemStack item = hotbar.get(i).getItem();
			if (PickTool.itemIsTooDamaged(item)) {
				continue;
			}

			double damage = Inventory.itemDPS(item);

			// int looting = Inventory.getEnchLevel(item, Enchantments.MOB_LOOTING);
			// if (this.prefer_looting.get() && looting > 0) {
			// 	damage += 0.1 * looting;
			// }

			if (damage > current_damage) {
				current_slot = i;
				current_damage = damage;
			}
		}
		if (current_slot != player.getInventory().getSelectedSlot()) {
			player.getInventory().setSelectedSlot(current_slot);
      mc.getConnection().send(new ServerboundSetCarriedItemPacket(current_slot));
			return true;
		}
		return false;
	}

	public static boolean selectBestTool(BlockPos pos) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return false;
		LocalPlayer player = mc.player;
		List<Slot> hotbar = Inventory.hotbar(player);
		int current_slot = player.getInventory().getSelectedSlot();
		BlockState state = mc.level.getBlockState(pos);
		float current_speed = hotbar.get(current_slot).getItem().getDestroySpeed(state);
		for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
			ItemStack item = hotbar.get(i).getItem();
			if (PickTool.itemIsTooDamaged(item)) {
				continue;
			}
			float speed = item.getDestroySpeed(state);
			if (speed > current_speed) {
				current_slot = i;
				current_speed = speed;
			}
		}
		if (current_slot != player.getInventory().getSelectedSlot()) {
			player.getInventory().setSelectedSlot(current_slot);
      mc.getConnection().send(new ServerboundSetCarriedItemPacket(current_slot));
			return true;
		}
		return false;
	}

	@SubscribeEvent
	public void onClick(InputEvent.MouseButton.Pre event) {
		if (mc().player == null) return;
		// TODO this is fired many times consecutively, can we filter out 
		//  some without putting a dumb time cooldown?;
		if (event.getAction() == InputConstants.RELEASE) return;
		if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) return;
		switch (mc().hitResult) {
			case null:
				break;
			case BlockHitResult block:
				PickTool.selectBestTool(block.getBlockPos());
				break;
			case EntityHitResult entity:
				PickTool.selectBestWeapon();
			default:
				break;
		}
	}
}

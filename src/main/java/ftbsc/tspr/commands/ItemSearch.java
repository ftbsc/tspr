package ftbsc.tspr.commands;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.command.BaseCommand;
import ftbsc.tspr.helpers.Chat;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static ftbsc.tspr.Tiramisuper.mc;

@AutoService(ILoadable.class)
public class ItemSearch extends BaseCommand {

	@Override
	public String getName() { return "item"; }

	public LiteralArgumentBuilder<CommandSourceStack> command(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
		return builder
			// .then(
			// 	Commands.literal("damage")
			// 		.executes(ctx -> {
			// 			Slot slot = Inventory.hotbar(mc().player).get(mc().player.getInventory().getSelectedSlot());
			// 			if (!slot.hasItem()) return 0;
			// 			Chat.message(
			// 				"A %.1f | S %.1f | DPS %.2f",
			// 				Inventory.itemAttackDamage(slot.getItem()),
			// 				Inventory.itemAttackSpeed(slot.getItem()),
			// 				Inventory.itemDPS(slot.getItem())
			// 			);
			// 			return 1;
			// 		})
			// )
			.then(
				Commands.literal("search")
					.then(
						Commands.argument("id", IntegerArgumentType.integer(0))
							.executes(ctx -> {
								int item_id = ctx.getArgument("id", Integer.class);
								Chat.message("item #[%d] >> %s", item_id, Item.byId(item_id).toString());
								return 1;
							})
					)
			)
			.then(
				Commands.literal("id")
					.then(
						Commands.argument("name", ItemArgument.item(context))
							.executes( ctx -> {
								ItemInput arg = ctx.getArgument("name", ItemInput.class);
								Item item = arg.getItem();
								Chat.message("item #[%d] >> %s", Item.getId(item), item.toString());
								return 1;
							})
					)
			)
			.executes(ctx -> {
				ItemStack item = mc().player.getInventory().getSelectedItem();
				Chat.message(item.toString());
				return 1;
			});
	}
}

package ftbsc.tspr.modules.client;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.TogglableModule;
import ftbsc.tspr.helpers.Inventory;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.regex.Pattern;

import org.joml.Matrix3x2fStack;

import com.google.auto.service.AutoService;

// @AutoService(ILoadable.class)
public class Highlighter extends TogglableModule {

	private ModConfigSpec.ConfigValue<String> query;
	private Pattern pattern;
	private int counter = 0;

	public void config(ModConfigSpec.Builder builder) {
		this.pattern = Pattern.compile("");
		this.query = builder
			.comment("search query")
			.define("query", "mending");
	}

	@SubscribeEvent
	void onTick(ClientTickEvent.Post event) {
		// TODO a bit jank just recompiling it each second, can't we do it whenever the congig changes?
		if (this.counter == 20) {
			this.pattern = Pattern.compile(this.query.get());
			this.counter = 0;
		} else {
			this.counter += 1;
		}
	}

	@SubscribeEvent
	public void onGuiContainerDraw(ScreenEvent.Render.Post event) {
		if (!this.isEnabled()) return;
		if (!(event.getScreen() instanceof AbstractContainerScreen)) return;
		AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) event.getScreen();
		Matrix3x2fStack matrix = event.getGuiGraphics().pose();

		matrix.pushMatrix();
		matrix.translate((float) screen.getGuiLeft(), (float) screen.getGuiTop());

		for (Slot slot : screen.getMenu().slots) {
			ItemStack stack = slot.getItem();
			if (Inventory.matchItem(this.pattern, stack)) {
				// GuiUtils.drawGradientRect(
				// 	matrix, 0,
				// 	slot.x, slot.y, slot.x + 16, slot.y + 16,
				// 	// TODO de-hardcode these colors by creating our util!
				// 	-925194976,  // R218 G165 B 32 A200
				// 	-927090837   // R189 G183 B107 A200
				// );
			}
		}

		matrix.popMatrix();
	}
}

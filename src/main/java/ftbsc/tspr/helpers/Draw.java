package ftbsc.tspr.helpers;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import static ftbsc.tspr.Tiramisuper.mc;

import java.util.OptionalDouble;

/**
 * Draw 3D shapes in client world
 */
public class Draw {

	private PoseStack poseStack;
	private Vec3 camera;

	/**
	 * Prepare for drawing from a {@link RenderLevelStageEvent}
	 * @param event RenderLevelStageEvent
	 */
	public static Draw prepare(RenderLevelStageEvent event) {
		Draw draw = new Draw();
		draw.poseStack = event.getPoseStack();
		draw.camera = event.getLevelRenderState().cameraRenderState.pos;
		return draw;
	}

	/**
	 * Draws an outline box, without faces filling
	 * @param pos location to draw
	 * @param color packed int RBG color
	 * @param alpha transparency value
	 */
	public void drawOutlineBox(BlockPos pos, int color, float alpha) {
		Vec3 offset = Vec3.atLowerCornerOf(pos).subtract(this.camera);
		this.poseStack.pushPose();
		this.poseStack.translate(offset.x, offset.y, offset.z);
		Vec3 color_f = Vec3.fromRGB24(color);
		VertexConsumer consumer = mc().renderBuffers().bufferSource().getBuffer(Draw.OVERLAY_LINES);
		ShapeRenderer.renderLineBox(
			this.poseStack.last(), consumer, new AABB(0, 0, 0, 1, 1, 1),
			(float) color_f.x, (float) color_f.y, (float) color_f.z, alpha
		);
		this.poseStack.popPose();
	}

	/**
	 * Draws a transparent box
	 * @param pos location to draw
	 * @param color packed int RBG color
	 * @param alpha transparency value
	 */
	public void drawFilledBox(BlockPos pos, int color, float alpha) {
		Vec3 offset = Vec3.atLowerCornerOf(pos).subtract(this.camera);
		this.poseStack.pushPose();
		this.poseStack.translate(offset.x, offset.y, offset.z);
		Vec3 color_f = Vec3.fromRGB24(color);
		VertexConsumer consumer = mc().renderBuffers().bufferSource().getBuffer(Draw.OVERLAY_DEBUG_SECTION_QUADS);
		for (Direction dir : Direction.values()) {
			ShapeRenderer.renderFace(
				this.poseStack.last().pose(), consumer, dir, 0.f, 0.f, 0.f, 1.f, 1.f, 1.f,
				(float) color_f.x, (float) color_f.y, (float) color_f.z, alpha
			);
		}
		this.poseStack.popPose();
	}

	/**
	 * Draws a transparent box with outlines
	 * @param pos location to draw
	 * @param color packed int RBG color
	 * @param alpha_line transparency value for outlines
	 * @param alpha_face transparency value for faces
	 */
	public void drawOutlineFilledBox(BlockPos pos, int color, float alpha_line, float alpha_face) {
		Vec3 offset = Vec3.atLowerCornerOf(pos).subtract(this.camera);
		this.poseStack.pushPose();
		this.poseStack.translate(offset.x, offset.y, offset.z);
		Vec3 color_f = Vec3.fromRGB24(color);
		VertexConsumer consumer = mc().renderBuffers().bufferSource().getBuffer(Draw.OVERLAY_LINES);
		ShapeRenderer.renderLineBox(
			this.poseStack.last(), consumer, new AABB(0, 0, 0, 1, 1, 1),
			(float) color_f.x, (float) color_f.y, (float) color_f.z, alpha_line
		);
		consumer = mc().renderBuffers().bufferSource().getBuffer(Draw.OVERLAY_DEBUG_SECTION_QUADS);
		for (Direction dir : Direction.values()) {
			ShapeRenderer.renderFace(
				this.poseStack.last().pose(), consumer, dir, 0.f, 0.f, 0.f, 1.f, 1.f, 1.f,
				(float) color_f.x, (float) color_f.y, (float) color_f.z, alpha_face
			);
		}
		this.poseStack.popPose();
	}

	/** render pipeline for lines ignoring depth culling */
	public static final RenderPipeline LINES_NO_DEPTH = RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
		.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
		.withCull(false)
		.withLocation(ResourceLocation.parse("ftbsc:pipelines/lines_no_depth"))
		.build();

	/** render pipeline for quads ignoring depth culling */
	public static final RenderPipeline DEBUG_SECTION_QUADS_NO_DEPTH = RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
		.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
		.withCull(false)
		.withLocation(ResourceLocation.parse("ftbsc:pipeline/debug_section_quads_no_depth"))
		.build();

	private static final RenderType OVERLAY_LINES = RenderType.create(
		"overlay_lines",
		1536,
		Draw.LINES_NO_DEPTH,
		RenderType.CompositeState.builder()
			.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
			.setLayeringState(RenderStateShard.LayeringStateShard.VIEW_OFFSET_Z_LAYERING)
			.setOutputState(RenderStateShard.LayeringStateShard.ITEM_ENTITY_TARGET)
			.createCompositeState(false)
	);

	private static final RenderType OVERLAY_DEBUG_SECTION_QUADS = RenderType.create(
		"overlay_debug_section_quads",
		1536,
		false,
		true,
		Draw.DEBUG_SECTION_QUADS_NO_DEPTH,
		RenderType.CompositeState.builder()
			.setLayeringState(RenderStateShard.LayeringStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false)
	);
}

package dev.homestead.animations.mixin;

import dev.homestead.animations.AnimationSettings;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> extends BipedEntityModel<T> {
    @Shadow public ModelPart leftSleeve;
    @Shadow public ModelPart rightSleeve;
    @Shadow public ModelPart leftPants;
    @Shadow public ModelPart rightPants;
    @Shadow public ModelPart jacket;
    protected PlayerModelMixin(ModelPart root) {super(root);}
    @Inject(method="setAngles", at=@At("TAIL"))
    private void homestead$animate(T entity,float limbAngle,float limbDistance,float animationProgress,float headYaw,float headPitch,CallbackInfo ci) {
        AnimationSettings s = AnimationSettings.INSTANCE;
        if (!s.enabled || entity.isSpectator()) return;
        // Additive poses keep vanilla/other-mod animation as the starting point.
        float motion = MathHelper.clamp(limbDistance, 0f, 1f);
        float cycle = limbAngle * 0.6662f;
        float scale = s.intensity;
        float breath = s.breathing ? MathHelper.sin(animationProgress * 0.085f) * 0.025f : 0f;
        boolean swimming = entity.isSwimming();
        boolean flying = entity.isFallFlying();
        boolean crouching = entity.isSneaking();
        boolean sprinting = entity.isSprinting() && !swimming && !flying;
        // Avoid modifying the torso pivot: armor/backpacks follow vanilla anchors.
        if (!swimming && !flying && s.movementAnimations) {
            float stride = sprinting ? 1.0f : 0.65f;
            float sway = MathHelper.sin(cycle) * motion * 0.028f * scale * stride;
            rightArm.roll += sway;
            leftArm.roll -= sway;
            if (s.jumping && !entity.isOnGround() && !entity.isClimbing()) {
                // Small additive airborne pose, no torso or leg contortion.
                rightLeg.pitch -= 0.08f * scale;
                leftLeg.pitch += 0.06f * scale;
            }
            if (s.breathing && motion < 0.08f && !crouching) {
                rightArm.roll += breath * 0.25f * scale;
                leftArm.roll -= breath * 0.25f * scale;
            }
        }
        if (s.toolAnimations && entity instanceof PlayerEntity player) {
            homestead$tools(player, animationProgress, s);
        }
        leftSleeve.copyTransform(leftArm);
        rightSleeve.copyTransform(rightArm);
        leftPants.copyTransform(leftLeg);
        rightPants.copyTransform(rightLeg);
        jacket.copyTransform(body);
    }
    private void homestead$tools(PlayerEntity player, float ticks, AnimationSettings s) {
        ItemStack main = player.getMainHandStack();
        Item item = main.getItem();
        float k = s.toolIntensity;
        if (k <= 0f || main.isEmpty()) return;
        boolean pick = item instanceof PickaxeItem;
        boolean axe = item instanceof AxeItem;
        boolean shovel = item instanceof ShovelItem;
        boolean hoe = item instanceof HoeItem;
        boolean sword = item instanceof SwordItem;
        boolean heavy = pick || axe || shovel || hoe;
        boolean freeOffhand = player.getOffHandStack().isEmpty();
        boolean leftMain = player.getMainArm() == net.minecraft.util.Arm.LEFT;
        ModelPart primary = leftMain ? leftArm : rightArm;
        ModelPart support = leftMain ? rightArm : leftArm;
        float sign = leftMain ? -1f : 1f;
        // Actual swing-progress-driven motion rather than a permanent holding pose.
        // Smooth envelope: zero at beginning/end, maximum at mid-swing.
        float progress = MathHelper.clamp(player.handSwingProgress, 0f, 1f);
        float envelope = player.handSwinging ? MathHelper.sin(progress * MathHelper.PI) : 0f;
        if (heavy && s.toolSwings && envelope > 0f) {
            float strength = (axe ? 0.70f : pick ? 0.62f : shovel ? 0.48f : 0.40f) * k;
            primary.pitch -= strength * envelope;
            primary.yaw += sign * 0.18f * envelope * k;
            primary.roll += sign * 0.12f * envelope * k;
            if (s.twoHandedTools && freeOffhand) {
                support.pitch -= strength * 0.58f * envelope;
                support.yaw -= sign * 0.14f * envelope;
            }
        } else if (sword && s.toolSwings && envelope > 0f) {
            primary.pitch -= 0.26f * envelope * k;
            primary.yaw -= sign * 0.20f * envelope * k;
        }
        // Never override bow/crossbow use, shield blocking, eating or Better Combat's
        // attack animation. Existing mods are responsible for those poses.
        if (player.isUsingItem()) return;
        if (s.holdingPoses && heavy && envelope == 0f && !player.isSwimming()) {
            primary.pitch -= 0.065f * k;
        }
    }
}

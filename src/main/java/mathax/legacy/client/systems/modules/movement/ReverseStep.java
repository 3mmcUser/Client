package mathax.legacy.client.systems.modules.movement;

import mathax.legacy.client.events.world.TickEvent;
import mathax.legacy.client.mixininterface.IVec3d;
import mathax.legacy.client.settings.DoubleSetting;
import mathax.legacy.client.settings.Setting;
import mathax.legacy.client.settings.SettingGroup;
import mathax.legacy.client.systems.modules.Categories;
import mathax.legacy.client.systems.modules.Module;
import mathax.legacy.client.bus.EventHandler;
import net.minecraft.block.BedBlock;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public class ReverseStep extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> fallSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("fall-speed")
        .description("How fast to fall in blocks per second.")
        .defaultValue(3)
        .min(0)
        .sliderMax(10)
        .build()
    );

    private final Setting<Double> fallDistance = sgGeneral.add(new DoubleSetting.Builder()
        .name("fall-distance")
        .description("The maximum fall distance this setting will activate at.")
        .defaultValue(3)
        .min(0)
        .sliderMax(10)
        .build()
    );

    public ReverseStep() {
        super(Categories.Movement, Items.DIAMOND_BOOTS, "reverse-step");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!mc.player.isOnGround() || mc.player.isHoldingOntoLadder() || mc.player.isSubmergedInWater() || mc.player.isInLava() ||mc.options.keyJump.isPressed() || mc.player.noClip || mc.player.forwardSpeed == 0 && mc.player.sidewaysSpeed == 0) return;

        if (!isOnBed() && !mc.world.isSpaceEmpty(mc.player.getBoundingBox().offset(0.0, (float) -(fallDistance.get() + 0.01), 0.0))) ((IVec3d) mc.player.getVelocity()).setY(-fallSpeed.get());
    }

    private boolean isOnBed() {
        BlockPos.Mutable blockPos = mc.player.getBlockPos().mutableCopy();

        if (check(blockPos, 0, 0)) return true;

        double xa = mc.player.getX() - blockPos.getX();
        double za = mc.player.getZ() - blockPos.getZ();

        if (xa >= 0 && xa <= 0.3 && check(blockPos, -1, 0)) return true;
        if (xa >= 0.7 && check(blockPos, 1, 0)) return true;
        if (za >= 0 && za <= 0.3 && check(blockPos, 0, -1)) return true;
        if (za >= 0.7 && check(blockPos, 0, 1)) return true;

        if (xa >= 0 && xa <= 0.3 && za >= 0 && za <= 0.3 && check(blockPos, -1, -1)) return true;
        if (xa >= 0 && xa <= 0.3 && za >= 0.7 && check(blockPos, -1, 1)) return true;
        if (xa >= 0.7 && za >= 0 && za <= 0.3 && check(blockPos, 1, -1)) return true;
        return xa >= 0.7 && za >= 0.7 && check(blockPos, 1, 1);
    }

    private boolean check(BlockPos.Mutable blockPos, int x, int z) {
        blockPos.move(x, 0, z);
        boolean is = mc.world.getBlockState(blockPos).getBlock() instanceof BedBlock;
        blockPos.move(-x, 0, -z);

        return is;
    }
}

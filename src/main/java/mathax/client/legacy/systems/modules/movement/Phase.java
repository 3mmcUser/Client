package mathax.client.legacy.systems.modules.movement;

import mathax.client.legacy.bus.EventHandler;
import mathax.client.legacy.systems.modules.Categories;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

import mathax.client.legacy.events.world.CollisionShapeEvent;
import mathax.client.legacy.events.world.TickEvent;
import mathax.client.legacy.settings.DoubleSetting;
import mathax.client.legacy.settings.EnumSetting;
import mathax.client.legacy.settings.Setting;
import mathax.client.legacy.settings.SettingGroup;
import mathax.client.legacy.systems.modules.Module;

public class Phase extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("The phase mode used.")
        .defaultValue(Mode.NRNB)
        .onChanged(v -> { setPos(); })
        .build()
    );

    private final Setting<Double> distance = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed")
        .description("The X and Z distance per clip.")
        .defaultValue(0.1)
        .min(0.0)
        .sliderMin(0.0)
        .sliderMax(10.0)
        .visible(() -> (mode.get() != Mode.CollisionShape))
        .build()
    );


    private double prevX = Double.NaN;
    private double prevZ = Double.NaN;

    public Phase() {
        super(Categories.Movement, Items.ELYTRA, "phase", "Lets you clip through ground sometimes.");
    }

    @Override
    public void onActivate() {
        if (mc.player == null) return;
        setPos();
    }

    @Override
    public void onDeactivate() {
        prevX = Double.NaN;
        prevZ = Double.NaN;
    }

    @EventHandler
    private void onCollisionShape(CollisionShapeEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (mode.get() != Mode.CollisionShape) return;
        if (event == null || event.pos == null) return;
        if (event.type != CollisionShapeEvent.CollisionType.BLOCK) return;
        if (event.pos.getY() < mc.player.getY()) {
            if (mc.player.isSneaking()) {
                event.shape = VoxelShapes.empty();
            }
        } else {
            event.shape = VoxelShapes.empty();
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post post) {
        if (mode.get() == Mode.CollisionShape) return;
        if (mc.player == null) return;

        if (Double.isNaN(prevX) || Double.isNaN(prevZ)) setPos();

        Vec3d yawForward = Vec3d.fromPolar((float)0.0f, (float)mc.player.getYaw());
        Vec3d yawBack = Vec3d.fromPolar((float)0.0f, (float)mc.player.getYaw() - 180f);
        Vec3d yawLeft = Vec3d.fromPolar((float)0.0f, (float)mc.player.getYaw() - 90f);
        Vec3d yawRight = Vec3d.fromPolar((float)0.0f, (float)mc.player.getYaw() - 270f);

        if (mode.get() == Mode.Normal) {

            if (mc.options.keyForward.isPressed()) {
                mc.player.setPos(
                    mc.player.getX() + yawForward.x * distance.get(),
                    mc.player.getY(),
                    mc.player.getZ() + yawForward.z * distance.get()
                );
            }

            if (mc.options.keyBack.isPressed()) {
                mc.player.setPos(
                    mc.player.getX() + yawBack.x * distance.get(),
                    mc.player.getY(),
                    mc.player.getZ() + yawBack.z * distance.get()
                );
            }

            if (mc.options.keyLeft.isPressed()) {
                mc.player.setPos(
                    mc.player.getX() + yawLeft.x * distance.get(),
                    mc.player.getY(),
                    mc.player.getZ() + yawLeft.z * distance.get()
                );
            }

            if (mc.options.keyRight.isPressed()) {
                mc.player.setPos(
                    mc.player.getX() + yawRight.x * distance.get(),
                    mc.player.getY(),
                    mc.player.getZ() + yawRight.z * distance.get()
                );
            }
        }

        else if (mode.get() == Mode.NRNB) {
            if (mc.options.keyForward.isPressed()) {
                prevX += yawForward.x * distance.get();
                prevZ += yawForward.z * distance.get();
                mc.player.setPos(prevX, mc.player.getY(), prevZ);
            }

            if (mc.options.keyBack.isPressed()) {
                prevX += yawBack.x * distance.get();
                prevZ += yawBack.z * distance.get();
                mc.player.setPos(prevX, mc.player.getY(), prevZ);
            }

            if (mc.options.keyLeft.isPressed()) {
                prevX += yawLeft.x * distance.get();
                prevZ += yawLeft.z * distance.get();
                mc.player.setPos(prevX, mc.player.getY(), prevZ);
            }

            if (mc.options.keyRight.isPressed()) {
                prevX += yawRight.x * distance.get();
                prevZ += yawRight.z * distance.get();
                mc.player.setPos(prevX, mc.player.getY(), prevZ);
            }
        }
    }

    private void setPos() {
        if (mc.player == null) return;
        prevX = mc.player.getX();
        prevZ = mc.player.getZ();
    }

    public static enum Mode {
        NRNB,
        Normal,
        CollisionShape
    }
}

package mathax.legacy.client.mixin;

import mathax.legacy.client.MatHaxLegacy;
import mathax.legacy.client.systems.config.Config;
import mathax.legacy.client.systems.modules.Modules;
import mathax.legacy.client.systems.modules.misc.NameProtect;
import mathax.legacy.client.utils.Utils;
import mathax.legacy.client.Version;
import mathax.legacy.client.utils.network.Http;
import mathax.legacy.client.utils.network.MatHaxExecutor;
import mathax.legacy.client.utils.render.PromptBuilder;
import mathax.legacy.client.utils.render.color.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    private final int WHITE = Color.fromRGBA(255, 255, 255, 255);
    private final int GRAY = Color.fromRGBA(175, 175, 175, 255);

    private String textLeftUp;
    private int textLeftUpLength;

    private String textRightUp1;
    private int textRightUp1Length;

    private String textRightUp2;
    private int textRightUp2Length;

    private String textRightUp3;

    private int fullLengthRightUp;
    private int prevWidthRightUp;

    private String textRightDown1;
    private int textRightDown1Length;

    private String textRightDown2;
    private int textRightDown2Length;

    private String textRightDown3;

    private int fullLengthRightDown;
    private int prevWidthRightDown;

    private String textRightDownButtonDiscord;

    private String textRightDownButtonWebsite;

    public TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {

        textLeftUp = "Logged in as ";

        textRightUp1 = "MatHax Legacy";
        textRightUp2 = " ";
        textRightUp3 = Version.getStylized();

        textRightDown1 = "By";
        textRightDown2 = " ";
        textRightDown3 = "Matejko06";

        textRightDownButtonDiscord = "MatHax Discord";

        textRightDownButtonWebsite = "MatHax Website";

        textLeftUpLength = textRenderer.getWidth(textLeftUp);

        textRightUp1Length = textRenderer.getWidth(textRightUp1);
        textRightUp2Length = textRenderer.getWidth(textRightUp2);
        int textRightUp1Length = textRenderer.getWidth(textRightUp1);
        int textRightUp2Length = textRenderer.getWidth(textRightUp2);
        int textRightUp3Length = textRenderer.getWidth(textRightUp3);

        textRightDown1Length = textRenderer.getWidth(textRightDown1);
        textRightDown2Length = textRenderer.getWidth(textRightDown2);
        int textRightDown1Length = textRenderer.getWidth(textRightDown1);
        int textRightDown2Length = textRenderer.getWidth(textRightDown2);
        int textRightDown3Length = textRenderer.getWidth(textRightDown3);

        fullLengthRightUp =  textRightUp1Length + textRightUp2Length + textRightUp3Length;
        prevWidthRightUp = 0;
        fullLengthRightDown = textRightDown1Length + textRightDown2Length + textRightDown3Length;
        prevWidthRightDown = 0;
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (Utils.didntCheckForLatestVersion) {
            Utils.didntCheckForLatestVersion = false;
            MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "Checking for latest version of MatHax Legacy!");

            MatHaxExecutor.execute(() -> {
                String apiLatestVer = Http.get(MatHaxLegacy.URL + "Version/Legacy/1-17-1").sendString();
                String processedApiLatestVer = apiLatestVer.replace("\n", "");
                if (processedApiLatestVer == null) return;

                Version latestVer = new Version(processedApiLatestVer);

                if (latestVer.isHigherThan(Config.get().version)) {
                    MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "There is a new version of MatHax Legacy, v" + latestVer + "! You are using v" + Config.get().version.toString() + "!");
                    String DOWNLOAD_URL = MatHaxLegacy.URL + "/Download";
                    new PromptBuilder()
                        .title("New Update")
                        .message("A new version of MatHax Legacy has been released.")
                        .message("\n")
                        .message("Your version: v" + Config.get().version)
                        .message("Latest version: v" +  latestVer)
                        .message("\n")
                        .message("Do you want to update?")
                        .onYes(() -> {
                            Util.getOperatingSystem().open(DOWNLOAD_URL);
                        })
                        .promptId("new-update")
                        .show();
                } else {
                    MatHaxLegacy.LOG.info(MatHaxLegacy.logprefix + "You are using the latest version of MatHax Legacy, v" + Config.get().version.toString() + "!");
                }
            });
        }

        textRenderer.drawWithShadow(matrices, textLeftUp, 3, 3, WHITE);
        textRenderer.drawWithShadow(matrices, Modules.get().get(NameProtect.class).getName(client.getSession().getUsername()) + getDeveloper(), 3 + textLeftUpLength, 3, GRAY);

        prevWidthRightUp = 0;
        textRenderer.drawWithShadow(matrices, textRightUp1, width - fullLengthRightUp - 3, 3, MatHaxLegacy.INSTANCE.MATHAX_COLOR_INT);
        prevWidthRightUp += textRightUp1Length;
        textRenderer.drawWithShadow(matrices, textRightUp2, width - fullLengthRightUp + prevWidthRightUp - 3, 3, WHITE);
        prevWidthRightUp += textRightUp2Length;
        textRenderer.drawWithShadow(matrices, textRightUp3, width - fullLengthRightUp + prevWidthRightUp - 3, 3, GRAY);

        prevWidthRightDown = 0;
        textRenderer.drawWithShadow(matrices, textRightDown1, width - fullLengthRightDown - 3, 15, WHITE);
        prevWidthRightDown += textRightDown1Length;
        textRenderer.drawWithShadow(matrices, textRightDown2, width - fullLengthRightDown + prevWidthRightDown - 3, 15, WHITE);
        prevWidthRightDown += textRightDown2Length;
        textRenderer.drawWithShadow(matrices, textRightDown3, width - fullLengthRightDown + prevWidthRightDown - 3, 15, GRAY);

        addDrawableChild(new ButtonWidget(width - 103, height - 35, 100, 20, new LiteralText(textRightDownButtonDiscord), button -> {
            Util.getOperatingSystem().open("https://mathaxclient.xyz/Discord");
        }));

        addDrawableChild(new ButtonWidget(width - 103, height - 58, 100, 20, new LiteralText(textRightDownButtonWebsite), button -> {
            Util.getOperatingSystem().open("https://mathaxclient.xyz/");
        }));
    }

    private String getDeveloper() {
        if (Modules.get() == null) return "";
        if (Modules.get().isActive(NameProtect.class)) return "";
        if ((Utils.mc.getSession().getUuid().equals(MatHaxLegacy.devUUID.replace("-", "")) || Utils.mc.getSession().getUuid().equals(MatHaxLegacy.devOfflineUUID.replace("-", "")))) return Formatting.WHITE + " [Developer]";
        else return "";
    }
}
package dev.homestead.animations;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class HomesteadSettingsScreen extends Screen {
    private final Screen parent;
    private int page = 0;
    public HomesteadSettingsScreen(Screen parent) { super(Text.literal("Homestead Player Animations")); this.parent=parent; }
    private static String flag(boolean v) { return v ? "ON" : "OFF"; }
    private void toggle(String label, int x, int y, boolean value, Runnable action) {
        addDrawableChild(ButtonWidget.builder(Text.literal(label + ": " + flag(value)), b -> { action.run(); AnimationSettings.INSTANCE.save(); clearAndInit(); }).dimensions(x,y,240,20).build());
    }
    @Override protected void init() {
        AnimationSettings s = AnimationSettings.INSTANCE;
        int x = width/2-120, y = Math.max(38,height/2-95);
        toggle("Enable all animations",x,y,s.enabled,()->s.enabled=!s.enabled);
        if (page==0) {
            toggle("Movement adjustments",x,y+25,s.movementAnimations,()->s.movementAnimations=!s.movementAnimations);
            toggle("Idle breathing",x,y+50,s.breathing,()->s.breathing=!s.breathing);
            toggle("Airborne pose",x,y+75,s.jumping,()->s.jumping=!s.jumping);
            addDrawableChild(ButtonWidget.builder(Text.literal("Movement strength: " + String.format(java.util.Locale.ROOT,"%.1fx",s.intensity)), b -> {s.intensity = s.intensity >= 1.5f ? 0.5f : s.intensity+0.25f;s.save();clearAndInit();}).dimensions(x,y+100,240,20).build());
        } else {
            toggle("Tool animation system",x,y+25,s.toolAnimations,()->s.toolAnimations=!s.toolAnimations);
            toggle("Animated tool swings",x,y+50,s.toolSwings,()->s.toolSwings=!s.toolSwings);
            toggle("Two-handed follow-through",x,y+75,s.twoHandedTools,()->s.twoHandedTools=!s.twoHandedTools);
            toggle("Tool holding poses",x,y+100,s.holdingPoses,()->s.holdingPoses=!s.holdingPoses);
            addDrawableChild(ButtonWidget.builder(Text.literal("Swing strength: " + String.format(java.util.Locale.ROOT,"%.1fx",s.toolIntensity)), b -> {s.toolIntensity=s.toolIntensity>=1.5f?0.5f:s.toolIntensity+0.25f;s.save();clearAndInit();}).dimensions(x,y+125,240,20).build());
        }
        int footer = Math.min(height-50,y+158);
        addDrawableChild(ButtonWidget.builder(Text.literal(page==0?"Tools & Swing Settings →":"← Movement Settings"),b->{page=1-page;clearAndInit();}).dimensions(x,footer,240,20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset Defaults"),b->{AnimationSettings.INSTANCE=new AnimationSettings();AnimationSettings.INSTANCE.save();clearAndInit();}).dimensions(x,footer+24,116,20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Done"),b->close()).dimensions(x+124,footer+24,116,20).build());
    }
    @Override public void close() { client.setScreen(parent); }
}

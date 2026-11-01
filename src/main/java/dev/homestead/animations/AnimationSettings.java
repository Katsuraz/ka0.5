package dev.homestead.animations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AnimationSettings {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("homestead-player-animations.json");
    public static AnimationSettings INSTANCE = load();
    public boolean enabled = true;
    public boolean breathing = true;
    public boolean movementAnimations = true;
    public boolean toolSwings = true;
    public boolean holdingPoses = false;
    public boolean jumping = true;
    public boolean toolAnimations = true;
    public boolean twoHandedTools = true;
    public float toolIntensity = 1.0f;
    public float intensity = 1.0f;
    public static AnimationSettings load() {
        try { if (Files.exists(PATH)) { AnimationSettings s = GSON.fromJson(Files.readString(PATH), AnimationSettings.class); if (s != null) {s.intensity = Math.max(0, Math.min(3, s.intensity)); s.toolIntensity = Math.max(0, Math.min(2, s.toolIntensity)); return s;} } } catch (Exception e) {System.err.println("[Homestead Animations] Could not load settings: " + e);}
        return new AnimationSettings();
    }
    public void save() {
        try { Files.createDirectories(PATH.getParent()); Files.writeString(PATH, GSON.toJson(this)); }
        catch (Exception e) {System.err.println("[Homestead Animations] Could not save settings: " + e);}
    }
}

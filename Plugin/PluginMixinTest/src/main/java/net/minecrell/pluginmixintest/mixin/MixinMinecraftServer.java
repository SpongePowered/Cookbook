package net.minecrell.pluginmixintest.mixin;

import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {

    @Shadow @Final private static Logger LOG;

    @Inject(method = "run", at = @At("HEAD"))
    private void onRunHead(CallbackInfo ci) {
        LOG.info("Mixin was successfully applied!");
    }

}

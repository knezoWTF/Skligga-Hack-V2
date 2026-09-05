package net.skliggahack.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.skliggahack.SkliggaHack;
import net.skliggahack.event.EventManager;
import net.skliggahack.event.events.SendChatMessageListener;
import net.skliggahack.module.modules.misc.NoLoadingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.skliggahack.SkliggaHack.MC;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{

	@Unique
	private boolean positionLookSetup = false;
	@Unique
	private boolean forwardingChat = false;

	@Inject(at = @At("TAIL"), method = "onPlayerPositionLook")
	private void onPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci)
	{
		if (!positionLookSetup)
		{
			positionLookSetup = true;
			if (SkliggaHack.INSTANCE.getModuleManager().getModule(NoLoadingScreen.class).isEnabled())
				MC.setScreen(null);
		}
	}

	@Inject(at = @At("HEAD"), method = "onPlayerRespawn")
	private void reset(PlayerRespawnS2CPacket packet, CallbackInfo ci)
	{
		positionLookSetup = false;
	}

	@Inject(at = @At("HEAD"), method = "sendChatMessage(Ljava/lang/String;)V", cancellable = true)
	private void onSendChatMessage(String message, CallbackInfo ci)
	{
		if (forwardingChat)
			return;
		SendChatMessageListener.SendChatMessageEvent event = new SendChatMessageListener.SendChatMessageEvent(message);
		EventManager.fire(event);
		if (event.isCancelled())
		{
			ci.cancel();
			return;
		}
		if (event.isModified())
		{
			ci.cancel();
			forwardingChat = true;
			try
			{
				MC.getNetworkHandler().sendChatMessage(event.getMessage());
			}
			finally
			{
				forwardingChat = false;
			}
		}
	}

}

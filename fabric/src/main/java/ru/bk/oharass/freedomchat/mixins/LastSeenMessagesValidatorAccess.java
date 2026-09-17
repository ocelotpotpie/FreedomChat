package ru.bk.oharass.freedomchat.mixins;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.network.chat.LastSeenMessagesValidator;
import net.minecraft.network.chat.LastSeenTrackedEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LastSeenMessagesValidator.class)
public interface LastSeenMessagesValidatorAccess {
    @Accessor("trackedMessages")
    ObjectList<LastSeenTrackedEntry> freedomChat$getTrackedMessages();
}

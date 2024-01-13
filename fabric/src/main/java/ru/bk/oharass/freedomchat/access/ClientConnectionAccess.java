package ru.bk.oharass.freedomchat.access;

import io.netty.channel.Channel;

public interface ClientConnectionAccess {
    Channel getChannel();
}

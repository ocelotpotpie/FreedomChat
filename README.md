# FreedomChat - Disable Chat Reporting
## The Definitive Chat Report Disabler
FreedomChat is a very simple plugin that makes player chat unreportable. FreedomChat completely disables chat reporting for 1.19+ without other negative consequences and maximum compatibility.

> **Note**
> CraftBukkit (and thus both Spigot and Paper) currently do exactly what FreedomChat does for player public chat messages. This is not the case for vanilla commands (`/msg`, `/say`) which will currently be signed. This plugin protects you from reporting of both.

## Installation
Installing this plugin is very simple. Just download it and put it in your plugins folder. There is no configuration, it is always active.

If you get an error when the plugin is enabling, you have likely downloaded the wrong version! Each Minecraft version has its own plugin version.

## Comparison

Unlike NoEncryption, FreedomChat does not create invalid chat packets. NoEncryption will cause a large and visible warning to players on 1.19.1. FreedomChat will not do this, and instead only show a small grey bar.
This is because while NoEncryption strips the signature from player chat packets, FreedomChat reconstructs player chat as if it were coming from the server itself, which is never signed (and thus not reportable).

<details>
  <summary>NoEncryption Warning</summary>

![NoEncryption Warning](https://i.imgur.com/5FYjMLl.png)

</details>

Unlike NoChatReports (bukkit plugin, **not** mod), FreedomChat will not break chat plugins. NoChatReports will completely break any chat plugin, by design. It must manually add compatibility for every plugin modifying chat. FreedomChat requires no such work, and will work out of the box with almost every chat plugin.

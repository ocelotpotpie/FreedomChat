# FreedomChat - Disable Chat Reporting

| **NOTE**: If you wish to allow players using client mods disabling chat signing to join your server, you must also disable `enforce-secure-profiles` in `server.properties` in addition to installing this plugin.<br><br>Out of the box, FreedomChat only rewrites **outgoing** messages and does not modify the signature checking messages for incoming messages.<br>Doing this mitigates compatibility issues caused by chat signing, and in the process prevents players from reporting each other, but does **not** allow players using client-side mods disabling chat signing to join. |
|-|

## The Definitive Chat Report Disabler
FreedomChat is a very simple plugin that makes player chat unreportable. FreedomChat completely disables chat reporting for 1.19+ without other negative consequences and maximum compatibility.

## Installation
Installing this plugin is very simple. Just download it and put it in your plugins folder. There is no configuration, it is always active.

If you get an error when the plugin is enabling or when the players are joining, you have likely downloaded the wrong version! Each Minecraft version has its own plugin version.

## Comparison

Unlike NoEncryption, FreedomChat does not create invalid chat packets. NoEncryption will cause a large and visible warning to players on 1.19.1. FreedomChat will not do this, and instead only show a small grey bar.
This is because while NoEncryption strips the signature from player chat packets, FreedomChat reconstructs player chat as if it were coming from the server itself, which is never signed (and thus not reportable).

<details>
  <summary>NoEncryption Warning</summary>

![NoEncryption Warning](https://i.imgur.com/5FYjMLl.png)

</details>

Unlike NoChatReports (bukkit plugin, **not** mod), FreedomChat will not break chat plugins. NoChatReports will completely break any chat plugin, by design. It must manually add compatibility for every plugin modifying chat. FreedomChat requires no such work, and will work out of the box with almost every chat plugin.

Unlike NoPopup, this plugin has no chance to cause your players to automatically disconnect themselves under certain conditions!

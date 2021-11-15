version = "1.1.0"
description = "BetterStatus replaces default discord status icons"

aliucord {
    changelog.set("""
        # 1.1.0 Known Issues:
        * Resizing bug
        * ChannelMembersList showing wrong status icons and ring sometimes
        # 1.1.0
        * Fixed Themer compatibilty (probably)
        # 1.0.9 Known Issues:
        * Resizing bug
        * ChannelMembersList showing wrong status icons and ring sometimes
        # 1.0.9
        * Fixed own user had random status
        * Fixed status icons not rendering correctly on ChannelMembersList
        # 1.0.8 Known Issues:
        * Own avatar still gets radial status
        * Resizing bug still exists
        * ChannelMembersList sometimes shows "wrong" status icon(s) and the icons dont rendering correctly. Just scroll down a few and then back up or vice-versa and it will show the icons.
        # 1.0.8
        * Optimizations and bug fixes. (Huge thanks for mantikafasi#4444, ID: 287555395151593473)
        * Fixed offline users had radial status (mantikafasi#4444)
        * Added a new option toggle, Chat Status: Displays a little status circle next to usernames.
        * Added a new option toggle, RadialStatus (Chat): Shows a status ring around the user avatar in the Chat.
        # 1.0.7 Known Issues:
        * I know there is resizing bug. Please dont post screenshots about it in support channel or anywhere and dont DM me about it. Im still working on it to fix it.
        * The icons dont rendering on the channel member list. Just scroll down a few and then back up and it will show the icons. I will push an update for this if I figured out how to reredner.
        * Invisible or Offline users have radial status (sometimes). I know. Fix coming soon.
        # 1.0.7
        * Added compatibilty to work with Square Avatars plugin from Juby210#0577
        # 1.0.6
        * A little fix
        # 1.0.5
        * Added RadialStatus with 3 options (DM's, ChannelsMemeberList and UserProfile)
        # 1.0.4
        * Improved icons displaying positions
        # 1.0.3
        * Added an option to use filled color status icons
        # 1.0.2
        * Fixed some codes
        # 1.0.1
        * Fixed error spamming in debug
        * Changed status icons from png to svg
        # 1.0.0
        * Released
    """.trimIndent())
}

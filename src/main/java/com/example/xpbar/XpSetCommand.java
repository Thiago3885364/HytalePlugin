package com.example.xpbar;

import com.example.xpbar.XpBarPlugin;
import com.example.xpbar.XpHud;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class XpSetCommand extends AbstractPlayerCommand {

    private final XpBarPlugin plugin;

    // /xpset <level>
    private final RequiredArg<Integer> levelArg =
            this.withRequiredArg("level", "xpbar.commands.xpset.level", ArgTypes.INTEGER); // [5](https://www.hytale-dev.com/plugin-development/commands)[6](https://hytale-france.github.io/en/commands/argument-types/)

    public XpSetCommand(XpBarPlugin plugin) {
        super("xpset", "Seta o nível da barra de XP (0..100)", false);
        this.plugin = plugin;
    }

    @Override
    protected void execute(
            @Nonnull CommandContext ctx,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        int level = ctx.get(levelArg); // padrão oficial de leitura de args [5](https://www.hytale-dev.com/plugin-development/commands)[6](https://hytale-france.github.io/en/commands/argument-types/)

        XpHud hud = plugin.getHud(playerRef.getUuid());
        if (hud != null) {
            hud.setLevel(level);
        }
    }
}
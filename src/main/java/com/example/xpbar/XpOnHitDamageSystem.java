package com.example.xpbar;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class XpOnHitDamageSystem extends EntityEventSystem<EntityStore, Damage> {

    private final XpService xpService;

    public XpOnHitDamageSystem(XpService xpService) {
        super(Damage.class); // Damage é ECS event [1](https://hytale-docs.com/docs/modding/plugins/events/damage/damage-event)
        this.xpService = xpService;
    }

    @Override
    public void handle(int index,
                       @Nonnull com.hypixel.hytale.component.ArchetypeChunk<EntityStore> chunk,
                       @Nonnull Store<EntityStore> store,
                       @Nonnull CommandBuffer<EntityStore> commandBuffer,
                       @Nonnull Damage damageEvent) {

        // Se o dano foi cancelado, não premiar
        if (damageEvent.isCancelled()) {
            return;
        }

        // Descobrir o atacante (entity hit ou projectile)
        Damage.Source src = damageEvent.getSource();
        if (src == null) return;

        // EntitySource ou ProjectileSource (ambos existem e carregam ref do atacante) [2](https://hytale.limetta.dev/reference/events/damage.html)[1](https://hytale-docs.com/docs/modding/plugins/events/damage/damage-event)
        com.hypixel.hytale.component.Ref<EntityStore> attackerRef = null;

        if (src instanceof Damage.EntitySource es) {
            attackerRef = es.getRef();
        } else if (src instanceof Damage.ProjectileSource ps) {
            attackerRef = ps.getRef(); // shooter
        } else {
            // dano ambiental, comando etc.
            return;
        }

        if (attackerRef == null) return;

        // Verifica se atacante é Player
        if (!store.hasComponent(attackerRef, Player.getComponentType())) {
            return;
        }

        Player attacker = store.getComponent(attackerRef, Player.getComponentType());
        PlayerRef attackerPlayerRef = store.getComponent(attackerRef, PlayerRef.getComponentType());

        if (attackerPlayerRef == null) return;

        // +10 níveis por acerto, travando em 100
        xpService.addLevels(attackerPlayerRef, XpService.LEVELS_PER_HIT);
    }

    @Override
    public Query<EntityStore> getQuery() {
        // Esse system é um event listener, não precisa filtrar entidades específicas
        return Archetype.empty();
    }
}
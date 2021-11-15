package com.discord.stores;

import com.discord.models.presence.Presence;
import com.discord.utilities.collections.SnowflakePartitionMap;

import kotlin.jvm.internal.DefaultConstructorMarker;

public class StoreUserPresence extends StoreV2 {

    private final SnowflakePartitionMap.CopiablePartitionMap<Presence> presences = new SnowflakePartitionMap.CopiablePartitionMap<>(0, 1, (DefaultConstructorMarker) null);
    private Presence localPresence;

    public SnowflakePartitionMap.CopiablePartitionMap<Presence> getPresences() {
        return this.presences;
    }

    public final Presence getLocalPresence$app_productionCanaryRelease() {
        return this.localPresence;
    }
}

package io.github.darkkronicle.advancedchatbox.suggester;

import com.mojang.brigadier.context.StringRange;
import io.github.darkkronicle.advancedchatbox.chat.AdvancedSuggestion;
import io.github.darkkronicle.advancedchatbox.config.ChatBoxConfigStorage;
import io.github.darkkronicle.advancedchatbox.interfaces.IMessageSuggestor;
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage;
import io.github.darkkronicle.advancedchatcore.util.FindType;
import io.github.darkkronicle.advancedchatcore.util.SearchUtils;
import io.github.darkkronicle.advancedchatcore.util.StringMatch;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class PlayerSuggestor implements IMessageSuggestor {

    @Override
    public Optional<List<AdvancedSuggestion>> suggestCurrentWord(String text, StringRange range) {
        List<AdvancedSuggestion> newSuggestions = new ArrayList<>();
        Collection<String> names = getPlayerNames();
        for (String name : names) {
            if (text.equals("") || name.toLowerCase().startsWith(text.toLowerCase())) {
                newSuggestions.add(new AdvancedSuggestion(range, name));
            }
        }
        return Optional.of(newSuggestions);
    }

    private Collection<String> getPlayerNames() {
        List<String> list = new ArrayList<>();

        for (PlayerListEntry playerListEntry : MinecraftClient.getInstance().player.networkHandler.getPlayerList()) {
            if (ChatBoxConfigStorage.General.PRUNE_PLAYER_SUGGESTIONS.config.getBooleanValue() && playerListEntry.getDisplayName() != null) {
                // Try to get their actual name (without prefix)
                StringMatch match = SearchUtils.getMatch(playerListEntry.getDisplayName().getString(), ConfigStorage.General.MESSAGE_OWNER_REGEX.config.getStringValue(), FindType.REGEX).orElse(null);
                if (match != null) {
                    // Check to make sure it isn't blank
                    if (!match.match.equals("")) {
                        list.add(match.match);
                    }
                } else {
                    // Check to make sure it isn't blank
                    if (!playerListEntry.getDisplayName().getString().equals("")) {
                        list.add(playerListEntry.getDisplayName().getString());
                    }
                }
            } else {
                // Player name is never null. But on servers it can be populated with fake players.
                list.add(playerListEntry.getProfile().getName());
            }
        }

        return list;
    }

}

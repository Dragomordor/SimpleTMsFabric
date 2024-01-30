package git.dragomordor.simpletms.fabric.event;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import git.dragomordor.simpletms.fabric.config.SimpleTMsConfig;
import git.dragomordor.simpletms.fabric.item.SimpleTMsItems;
import kotlin.Unit;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ModEvents {

    public static void registerEvents() {
        CobblemonEvents.BATTLE_FAINTED.subscribe(com.cobblemon.mod.common.api.Priority.NORMAL, ModEvents::onBattleFainted);
    }

    private static Unit onBattleFainted(com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent event) {
        // Get all players involved in the battle
        List<ServerPlayerEntity> players = event.getBattle().getPlayers();
        // If only one player is involved, proceed (only for 1 player PvE)
        if (players.size() == 1) {
            ServerPlayerEntity playerEntity = players.get(0);

            BattlePokemon killed = event.getKilled();
            Pokemon pokemon = killed.getEffectedPokemon();

            if (!(pokemon.isPlayerOwned())) { // only wild Pokémon drop TMs
                BlockPos pos;
                World world = playerEntity.getEntityWorld();

                if (pokemon.getEntity()==null) { // spawn item on player
                    pos = playerEntity.getBlockPos();
                } else { // spawn item on pokemon
                    pos = pokemon.getEntity().getBlockPos();
                }

                ItemStack droppedTMitem = SimpleTMsItems.getRandomTMItemStack(pokemon);
                ItemStack droppedTRitem = SimpleTMsItems.getRandomTRItemStack(pokemon);

                float randomTMChance = world.getRandom().nextFloat() * 100;
                float dropChanceTMPercentage = SimpleTMsConfig.getTMDropChance();

                // Spawn the chosen TM item
                if (randomTMChance <= dropChanceTMPercentage && !droppedTMitem.isEmpty()) {
                    spawnTMItem(world, playerEntity, pos, droppedTMitem, event);
                } else {
                    float randomTRChance = world.getRandom().nextFloat() * 100;
                    System.out.println("Random TR Chance: "+randomTRChance);
                    float dropChanceTRPercentage = SimpleTMsConfig.getTRDropChance();
                    System.out.println("Random TR config: "+dropChanceTRPercentage);
                    if (randomTRChance <= dropChanceTRPercentage && !droppedTRitem.isEmpty()) {
                        spawnTMItem(world, playerEntity, pos, droppedTMitem, event);
                    }
                }
            }
        }

        return Unit.INSTANCE;
    }


    // Helper method to spawn a TM item in the world
    private static void spawnTMItem(World world,PlayerEntity player, BlockPos pos, ItemStack tmItemStack, BattleFaintedEvent event) {
        String tmName = tmItemStack.getName().getString();

        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), tmItemStack);
        // Add the TM item to the player's inventory
        PlayerInventory playerInventory = player.getInventory();
        if (!playerInventory.insertStack(tmItemStack)) { // If the inventory is full, drop the item in the world
            world.spawnEntity(itemEntity);
        }
        player.sendMessage(Text.of("Received "+tmName+" from "+event.getKilled().getEffectedPokemon().getDisplayName().getString()),true);

    }

}




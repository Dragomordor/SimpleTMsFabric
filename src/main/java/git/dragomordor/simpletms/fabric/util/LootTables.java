package git.dragomordor.simpletms.fabric.util;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.Pokemon;
import git.dragomordor.simpletms.fabric.config.SimpleTMsConfig;
import git.dragomordor.simpletms.fabric.item.SimpleTMsItems;
import kotlin.Unit;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class LootTables {

    public static void registerEvents() {
        CobblemonEvents.BATTLE_FAINTED.subscribe(com.cobblemon.mod.common.api.Priority.NORMAL, LootTables::onBattleFainted);
    }

    private static Unit onBattleFainted(BattleFaintedEvent event) {
        // Get all players involved in the battle
        List<ServerPlayerEntity> players = event.getBattle().getPlayers();
        // If only one player is involved, proceed
        if (players.size() == 1) {
            BattlePokemon killed = event.getKilled();
            Pokemon pokemon = killed.getEntity().getPokemon();

            if (!(pokemon.isPlayerOwned())) { // only wild pokemon drop TMs
                // Get the world and position where the Pokémon fainted
                World world = pokemon.getEntity().getEntityWorld();
                BlockPos pos = pokemon.getEntity().getBlockPos();

                ItemStack droppedTMitem = SimpleTMsItems.getRandomTMItemStack(pokemon);
                ItemStack droppedTRitem = SimpleTMsItems.getRandomTRItemStack(pokemon);

                // Spawn the chosen TM item
                float randomTMChance = world.getRandom().nextFloat() * 100;
                System.out.println("Random TM Chance: "+randomTMChance);
                float dropChanceTMPercentage = SimpleTMsConfig.getTMDropChance();
                System.out.println("Random TM config: "+dropChanceTMPercentage);


                if (randomTMChance <= dropChanceTMPercentage && !droppedTMitem.isEmpty()) {
                    spawnTMItem(world, pos, droppedTMitem, event);
                } else {
                    float randomTRChance = world.getRandom().nextFloat() * 100;
                    System.out.println("Random TR Chance: "+randomTRChance);
                    float dropChanceTRPercentage = SimpleTMsConfig.getTRDropChance();
                    System.out.println("Random TR config: "+dropChanceTRPercentage);
                    if (randomTRChance <= dropChanceTRPercentage && !droppedTRitem.isEmpty()) {
                        spawnTMItem(world, pos, droppedTRitem, event);
                    }
                }
            }
        }

        return Unit.INSTANCE;
    }


    // Helper method to spawn a TM item in the world
    private static void spawnTMItem(World world, BlockPos pos, ItemStack tmItemStack, BattleFaintedEvent event) {
        ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), tmItemStack);

        String tmName = tmItemStack.getName().getString();
        // Get all players involved in the battle
        List<ServerPlayerEntity> players = event.getBattle().getPlayers();

        if (players.size() == 1) {
            ServerPlayerEntity playerEntity = players.get(0);

            // Add the TM item to the player's inventory
            PlayerInventory playerInventory = playerEntity.getInventory();
            if (!playerInventory.insertStack(tmItemStack)) {
                // If the inventory is full, drop the item in the world
                playerEntity.sendMessage(Text.of("Received "+tmName+" from "+event.getKilled().getEntity().getPokemon().getDisplayName().getString()),true);
                world.spawnEntity(itemEntity);
            } else {
                playerEntity.sendMessage(Text.of("Received "+tmName+" from "+event.getKilled().getEntity().getPokemon().getDisplayName().getString()),true);
            }
        } else {
            // If there is no player or more than one player, simply spawn the item in the world
            world.spawnEntity(itemEntity);
        }
    }

}




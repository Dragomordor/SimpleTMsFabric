package git.dragomordor.simpletms.fabric.item.custom;

import com.cobblemon.mod.common.api.moves.*;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import git.dragomordor.simpletms.fabric.config.SimpleTMsConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MoveTutorItem extends PokemonUseItem {
    private final String moveName;
    private final String moveType;
    private final boolean SingleUse;
    private final int cooldownTicks = SimpleTMsConfig.getTMCooldownTicks();

    public MoveTutorItem(String moveName, String moveType, boolean singleUse) {
        super(new FabricItemSettings().maxCount(1));
        this.moveType = moveType;
        this.moveName = moveName;
        SingleUse = singleUse;
    }


    @Override
    public ActionResult processInteraction(ItemStack itemStack, PlayerEntity player, PokemonEntity target, Pokemon pokemon) {
        MoveSet currentmoves = pokemon.getMoveSet(); // moves Pokémon currently has equipped
        BenchedMoves benchedMoves = pokemon.getBenchedMoves(); // moves Pokémon currently has benched
        MoveTemplate taughtMove = Moves.INSTANCE.getByName(moveName);

        final int cooldownTicks = SimpleTMsConfig.getTMCooldownTicks(); // Define the cooldown in ticks


        if (player.getItemCooldownManager().isCoolingDown(this)) {
            player.sendMessage(Text.of("TM is on cooldown."), true);
            return ActionResult.FAIL;

        }
        if (taughtMove==null) {
            player.sendMessage(Text.of("Invalid move!"),true);
            return ActionResult.FAIL;
        }
        if (currentmoves.getMoves().stream().anyMatch(move -> move.getTemplate().equals(taughtMove))
            || benchedMovesContainsMove(benchedMoves, taughtMove)) {
            player.sendMessage(Text.of(pokemon.getSpecies().getName() +" already knows " + taughtMove.getDisplayName().getString()+"!"),true);
            return ActionResult.FAIL;
        }
        // can Pokémon learn move?
        boolean canLearnMove = canLearnMove(itemStack, player, target, pokemon, taughtMove);

        // if Pokémon can't learn move, return fail
        if (!canLearnMove) {
            player.sendMessage(Text.of( pokemon.getSpecies().getName() + " cannot be taught " + taughtMove.getDisplayName().getString()),true);
            return ActionResult.FAIL;
        }
        // if Pokémon can learn move, teach move
        if (currentmoves.hasSpace()) {
            currentmoves.add(taughtMove.create());
        } else {
            benchedMoves.add(new BenchedMove(taughtMove,0));
        }
        player.sendMessage(Text.of("Taught " + pokemon.getSpecies().getName() + " " + taughtMove.getDisplayName().getString()+"!"),true);
        // removes item if it is single use
        if (SingleUse) {
            itemStack.decrement(1); // remove item after use
        }
        // Puts item on cooldown if not singleUse
        if (!SingleUse && (cooldownTicks>0)){
            player.getItemCooldownManager().set(this, cooldownTicks);
        }

        // play level up sound if Pokémon is taught move
        player.getWorld().playSound(null,player.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS,1.0f,1.0f);
        return ActionResult.SUCCESS;
    }


    // Functions
    //checks if benchedMoves contains taughtMove
    private boolean benchedMovesContainsMove(BenchedMoves benchedMoves, MoveTemplate taughtMove) {
        for (BenchedMove benchedMove : benchedMoves) {
            if (benchedMove.getMoveTemplate().equals(taughtMove)) {
                return true;
            }
        }
        return false;
    }
    private boolean canLearnMove(ItemStack itemStack, PlayerEntity player, PokemonEntity target, Pokemon pokemon, MoveTemplate taughtMove) {
        boolean canLearnMove = SimpleTMsConfig.getAreAllMovesLearnable(); // Default value for canLearnMove
        if (canLearnMove) {
            return true;
        }
        if (pokemon.getForm().getMoves().getTmMoves().contains(taughtMove)) {
            return true;
        }
        if (pokemon.getForm().getMoves().getTutorMoves().contains(taughtMove) && SimpleTMsConfig.getAreTutorMovesLearnable()) {
            return true;
        }
        if (pokemon.getForm().getMoves().getEggMoves().contains(taughtMove) && SimpleTMsConfig.getAreEggMovesLearnable()) {
            return true;
        }
        return false;
    }


    // Tooltip
    private static final Map<String, Integer> moveTypeColors = new HashMap<>();
    static {  // Initialize moveTypeColors map
        moveTypeColors.put("Bug", 0xB6E881);
        moveTypeColors.put("Dark", 0x68A0A9);
        moveTypeColors.put("Dragon", 0xA9B0D1);
        moveTypeColors.put("Electric", 0xF3BF00);
        moveTypeColors.put("Fairy", 0xEE9F96);
        moveTypeColors.put("Fighting", 0xD87000);
        moveTypeColors.put("Fire", 0xD72200);
        moveTypeColors.put("Flying", 0xAED8E9);
        moveTypeColors.put("Ghost", 0xAB62DC);
        moveTypeColors.put("Grass", 0x3EB556);
        moveTypeColors.put("Ground", 0xEFE843);
        moveTypeColors.put("Ice", 0x94E0F2);
        moveTypeColors.put("Normal", 0xFFFFFF);
        moveTypeColors.put("Poison", 0xC379BA);
        moveTypeColors.put("Psychic", 0xF3B8AF);
        moveTypeColors.put("Rock", 0xCDB02E);
        moveTypeColors.put("Steel", 0xD0D0D0);
        moveTypeColors.put("Water", 0x63A8EB);
    }
    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        PlayerEntity player = MinecraftClient.getInstance().player;
        // Check if the item has a cooldown and add the cooldown information to the tooltip
       if (player!= null) {
           if (player.getItemCooldownManager().isCoolingDown(this)) {
               // get remaining cooldown
               int maxCooldownTicks = SimpleTMsConfig.getTMCooldownTicks();
               int ticksLeft = Math.round(player.getItemCooldownManager().getCooldownProgress(this,0.0F)*maxCooldownTicks);
               //convert cooldown to seconds, minutes, hours
               int hoursLeft = ticksLeft / 72000; // 72000 ticks = 1 hour
               int minutesLeft = (ticksLeft % 72000) / 1200; // 1200 ticks = 1 minute
               int secondsLeft = (ticksLeft % 1200) / 20; // 20 ticks = 1 second
                // display cooldown
               StringBuilder cooldownText = new StringBuilder("Cooldown: ");
               boolean hasContent = false;
               if (hoursLeft > 0) {
                   cooldownText.append(hoursLeft).append(" hour").append(hoursLeft > 1 ? "s" : "");
                   hasContent = true;
               }
               if (minutesLeft > 0) {
                   if (hasContent) {
                       cooldownText.append(", ");
                   }
                   cooldownText.append(minutesLeft).append(" minute").append(minutesLeft > 1 ? "s" : "");
                   hasContent = true;
               }
               if (secondsLeft > 0 || (!hasContent && ticksLeft == 0)) {
                   if (hasContent) {
                       cooldownText.append(", ");
                   }
                   cooldownText.append(secondsLeft).append(" second").append(secondsLeft > 1 ? "s" : "");
               }
                String cooldownTextString = cooldownText.toString();
               LiteralTextContent cooldownLiteralTextContent = new LiteralTextContent(cooldownTextString);
               Text cooldownDisplayTextContent = MutableText.of(cooldownLiteralTextContent).setStyle(Style.EMPTY.withColor(Formatting.RED));
               tooltip.add(cooldownDisplayTextContent);
           }
       }

        // Adds warning that item is single use on tooltip
        if (SingleUse) {
            String singleUseString = ("Consumed after use");
            LiteralTextContent singleUseLiteralTextContent = new LiteralTextContent(singleUseString);
            Text singleUseDisplayTextContent = MutableText.of(singleUseLiteralTextContent).setStyle(Style.EMPTY.withColor(Formatting.DARK_RED));
            tooltip.add(singleUseDisplayTextContent);
        }

        // Adds move type to tooltip
        String moveType = this.moveType;
        // Check if the move type exists in the map
        if (moveTypeColors.containsKey(moveType)) {
            int color = moveTypeColors.get(moveType);

            // Create the text with the move type and apply the specified color
            LiteralTextContent moveContent = new LiteralTextContent(moveType);
            Text moveTypeText = MutableText.of(moveContent).setStyle(Style.EMPTY.withColor(color));
            tooltip.add(moveTypeText); // Add the colored move type to the tooltip
        }
    }



    public String getMoveType() {
        return moveType;
    }

    public String getMoveName() {
        return moveName;
    }

}
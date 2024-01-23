package git.dragomordor.simpletms.fabric.item.custom;

import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import git.dragomordor.simpletms.fabric.config.SimpleTMsConfig;
import git.dragomordor.simpletms.fabric.item.SimpleTMsItems;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.Objects;

public class BlankTMItem extends PokemonUseItem {
    private final boolean IsTM;
    String prefix;
    String blankTitle;


    public BlankTMItem(boolean isTM) {
        super(new FabricItemSettings());
        this.IsTM = isTM;
    }

    @Override
    public ActionResult processInteraction(ItemStack itemStack, PlayerEntity player, PokemonEntity target, Pokemon pokemon) {

        if (!SimpleTMsConfig.getImprintableBlankTMs()) {
            player.sendMessage(Text.of("Blank TM and TR imprinting disabled!"));
            return ActionResult.FAIL;
        }

        Move FirstMoveInMovesetMove = pokemon.getMoveSet().get(0);
        String FirstMoveInMoveset = Objects.requireNonNull(FirstMoveInMovesetMove).getName();
        // add custom mod item with name "FirstMoveInMoveset"
        if (IsTM) {
            prefix = "tm_";
            blankTitle = "TM";
        } else {
            prefix = "tr_";
            blankTitle = "TR";
        }

        ItemStack newTMItem = SimpleTMsItems.getItemStackByName(prefix+FirstMoveInMoveset);
        PlayerInventory inventory = player.getInventory(); // get player inventory
        // get current item inventory slot


        // TODO add item to main hand if possible?
        if (!inventory.insertStack(newTMItem)) { // add item to inventory
            player.dropItem(newTMItem,false); // drop item on ground if inventory full
        }
        itemStack.decrement(1); // delete blank tm

        player.sendMessage(Text.of("Imprinted "+FirstMoveInMovesetMove.getDisplayName().getString()+" onto "+blankTitle+"!"),true);

        player.getWorld().playSound(null,player.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.PLAYERS,1.0f,2.0f);
        return ActionResult.SUCCESS;
    }
}


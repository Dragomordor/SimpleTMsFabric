package git.dragomordor.simpletms.fabric;

import com.google.gson.JsonObject;
import git.dragomordor.simpletms.fabric.config.SimpleTMsConfig;
import git.dragomordor.simpletms.fabric.item.SimpleTMsItemGroups;
import git.dragomordor.simpletms.fabric.item.SimpleTMsItems;
import git.dragomordor.simpletms.fabric.util.LootTables;
import git.dragomordor.simpletms.fabric.util.TMsTRsList;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleTMsMod implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(SimpleTMsMod.class); // create logger
    public static final String MODID = "simpletms"; // mod ID

    @Override
    public void onInitialize() {
        // Load config file
        SimpleTMsConfig.loadConfig();
        // Register all items
        SimpleTMsItems.registerModItems(); // register generic items
        // Register creative tabs
        SimpleTMsItemGroups.registerItemGroups(); //register TM item tab

        // Register TM list
        TMsTRsList.registerTMList();
        // Register events
        LootTables.registerEvents();
    }
}


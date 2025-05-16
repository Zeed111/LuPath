package com.example.lupath.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lupath.data.Converters
import com.example.lupath.data.database.dao.*
import com.example.lupath.data.database.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

@Database(
    entities = [
        MountainEntity::class, CampsiteEntity::class, TrailEntity::class,
        GuidelineEntity::class,
        ChecklistItemEntity::class, // Only this for the general checklist
        HikePlanEntity::class // Remove if not used elsewhere
        // HikePlanChecklistLinkEntity::class // Remove if not used elsewhere
    ],
    version = 1 /* Your current version, increment if schema changed */,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mountainDao(): MountainDao
    abstract fun campsiteDao(): CampsiteDao
    abstract fun trailDao(): TrailDao
    abstract fun guidelineDao(): GuidelineDao
    abstract fun hikePlanDao(): HikePlanDao
    abstract fun checklistItemDao(): ChecklistItemDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lupath_app_database"
                )
                    .addCallback(AppDatabaseCallback(context)) // Add callback for pre-population
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database)
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val mountainDao = database.mountainDao()
            val campsiteDao = database.campsiteDao()
            val trailDao = database.trailDao()
            val guidelineDao = database.guidelineDao()
            val checklistItemDao = database.checklistItemDao()

            // --- Mt. Batulao Data ---
            val mtBatulaoId = "mtbtl001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtBatulaoId,
                    mountainName = "Mt. Batulao",
                    pictureReference = "mt_batulao_main",
                    location = "Nasugbu, Batangas (bordering Cavite)",
                    masl = 811,
                    difficultyText = "Easy to moderate (4/9)",
                    hoursToSummit = "4–6 hours round trip",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Inactive stratovolcano",
                    trekDurationDetails = "4–6 hours round trip (via New Trail or Old Trail; longer if traversing both)",
                    trailTypeDescription = "Combination of rolling ridges, grassy slopes, short rock scrambles; Old Trail is more forested, New Trail is more open and scenic",
                    sceneryDescription = "Rolling golden hills during dry season, lush green slopes during wet season; panoramic views of Taal Lake, Pico de Loro, and Nasugbu coastline",
                    viewsDescription = "360-degree summit view; neighboring ridgelines, Batangas lowlands, and even Tagaytay on clear days",
                    wildlifeDescription = "Grassland birds, butterflies, native grasses, some flowering shrubs and small mammals",
                    featuresDescription = "Iconic gorilla-shaped ridgeline, multiple minor peaks, and a picturesque summit ridge",
                    hikingSeasonDetails = "Best during November to February (dry and cool); avoid summer midday heat and rainy season for safety",
                    introduction = "Mount Batulao is an inactive stratovolcano located in Nasugbu, Batangas... referencing how sunlight hits its ridges at dawn and dusk." // Shortened
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, name = "Station 7 (Old Trail)", description = "Sheltered and flat"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, name = "Station 10 (Old Trail)", description = "Near summit, ideal for early ascents"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, name = "New Trail ridge points", description = "Good for day hikes and short rests")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, name = "Old Trail", description = "Gradual inclines, more trees and shade, wider paths"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, name = "New Trail", description = "Open ridges, steeper in parts, more dramatic scenery")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, category = "Registration Fee", description = "₱30–₱40 (separate for Old and New Trail entrances)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, category = "Guide Fee", description = "₱500–₱800 per group (optional but recommended for first-timers or overnighters)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, category = "Camping Fee", description = "₱30–₱50 depending on the site"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, category = "Safety Tip", description = "Avoid ridges during storms (lightning risk)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, category = "Safety Tip", description = "Wear sturdy shoes—trails can be slippery when wet"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, category = "Safety Tip", description = "Bring sun protection (New Trail is very exposed)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, category = "Leave No Trace", description = "Always take your trash back down"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, category = "Hiking Season", description = "Best during November to February (dry and cool); avoid summer midday heat and rainy season for safety")
            ))

            // --- Mt. Maculot (Rockies) Data ---
            val mtMaculotId = "mtmcl001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtMaculotId,
                    mountainName = "Mt. Maculot (Rockies)",
                    pictureReference = "mt_maculot_main",
                    location = "Cuenca, Batangas",
                    masl = 930,
                    difficultyText = "Moderate (3/9 to the Rockies; 4/9 for full traverse)",
                    hoursToSummit = "1-2 hours to the Rockies; 2–4 hours to the summit", // Trek Duration from research
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Volcanic mountain",
                    trekDurationDetails = "1-2 hours to the Rockies; 2–4 hours to the summit",
                    trailTypeDescription = "Steep ascents with rocky paths; forested sections leading to the summit",
                    sceneryDescription = "Panoramic vistas of Taal Lake and Taal Volcano.",
                    viewsDescription = "Panoramic views of Taal Lake, surrounding mountains, and lush forests",
                    wildlifeDescription = "Typical lowland forest species; occasional sightings of birds and small mammals",
                    featuresDescription = "The \"Rockies\" viewpoint, summit, and a grotto frequented by pilgrims",
                    hikingSeasonDetails = "November to February for cooler temperatures and clearer views",
                    introduction = "Mount Maculot is a prominent peak located in Cuenca, Batangas...breathtaking scenery it offers." // Shortened
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaculotId, name = "Near Rockies and summit areas", description = "Camping is permitted near the \"Rockies\" area...several rest stops along the way.")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMaculotId, name = "Rockies Trail", description = "Direct ascent to the Rockies viewpoint"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMaculotId, name = "Summit Trail", description = "Continues from the Rockies to the summit and grotto")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaculotId, category = "Registration Fee", description = "Approximately ₱20–₱30, payable at the barangay hall"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaculotId, category = "Guide Fee", description = "Optional but recommended for first-time hikers"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaculotId, category = "Camping Fee", description = "May apply; inquire at the registration area"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaculotId, category = "Safety Tip", description = "Wear appropriate hiking footwear; trails can be slippery, especially after rain"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaculotId, category = "Water Source", description = "Limited; hikers are advised to bring sufficient water"), // From Campsite section in research
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaculotId, category = "Best Season", description = "November to February for cooler temperatures and clearer views")
            ))

            // TODO: Add initial ChecklistItemEntity data (predefined items)

            checklistItemDao.insertAllItems(listOf(
                ChecklistItemEntity(itemId = "predef_001", name = "Sleeping Bags and Sleeping Mat", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_002", name = "Water Bottles (at least 2-3L)", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_003", name = "Headlamp or Flashlight (extra batteries)", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_004", name = "First Aid Kit", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_005", name = "Backpack with Rain Cover", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_006", name = "Proper Hiking Shoes", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_007", name = "Sun Protection (Sunscreen, Hat, Sunglasses)", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_008", name = "Rain Gear (Poncho/Rain Jacket)", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_009", name = "Trail Food/Snacks", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_010", name = "Trash Bag (Leave No Trace)", isPreMade = true)
                // Add more common items
            ))
        }
    }
}
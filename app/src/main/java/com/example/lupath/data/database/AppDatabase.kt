package com.example.lupath.data.database

import android.content.Context
import android.util.Log
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
    version = 9 /* Your current version, increment if schema changed */,
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
                    .fallbackToDestructiveMigration() // If you increment version and don't want to write migrations during dev
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

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            Log.d("AppDatabaseCallback", "onDestructiveMigration called. Re-populating data.")
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
                    difficultySummary = "Easy to moderate",
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
                    introduction = "Mount Batulao is an inactive stratovolcano located in Nasugbu, " +
                            "Batangas, on the northwestern edge of the Taal Caldera. It’s one of the " +
                            "most frequented hiking destinations in Southern Luzon due to its " +
                            "accessibility, scenic grassy ridges, and its suitability for beginner " +
                            "to intermediate hikers. The name \"Batulao\" comes from the Tagalog " +
                            "phrase bato sa ilaw, meaning \"rock in the light,\" referencing how " +
                            "sunlight hits its ridges at dawn and dusk.",
                    tagline = "Let's Hike to Mt. Batulao",
                    mountainImageRef1 = "mt_batulao_1",
                    mountainImageRef2 = "mt_batulao_2",
                    mountainImageRef3 = "mt_batulao_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Butterflies",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, description = "Mount Batulao features several established campsites, " +
                        "particularly on the Old Trail (e.g., Station 7 and 10), with flatter and more shaded areas compared to the New Trail. These are commonly used for overnight stays " +
                        "and sunrise viewing. The New Trail has scenic, wind-exposed ridges great for short rest stops or photo breaks."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, name = "Station 7 (Old Trail)", description = "Sheltered and flat"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, name = "Station 10 (Old Trail)", description = "Near summit, ideal for early ascents"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, name = "New Trail ridge points", description = "Good for day hikes and short rests"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, name = "Trek Time to Campsite", description = "1.5–3 hours depending on pace and starting point"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBatulaoId, name = "Water Source", description = "Available on Old Trail, but not always reliable—bring at least 2–3 liters")
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
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate (4/9)",
                    hoursToSummit = "2–4 hours to the summit",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Volcanic mountain",
                    trekDurationDetails = "1-2 hours to the Rockies; 2–4 hours to the summit",
                    trailTypeDescription = "Steep ascents with rocky paths; forested sections leading to the summit",
                    sceneryDescription = "Panoramic vistas of Taal Lake and Taal Volcano.",
                    viewsDescription = "Panoramic views of Taal Lake, surrounding mountains, and lush forests",
                    wildlifeDescription = "Typical lowland forest species; occasional sightings of birds and small mammals",
                    featuresDescription = "The \"Rockies\" viewpoint, summit, and a grotto frequented by pilgrims",
                    hikingSeasonDetails = "November to February for cooler temperatures and clearer views",
                    introduction = "Mount Maculot is a prominent peak located in Cuenca, Batangas, " +
                            "standing at approximately 930 meters above sea level (MASL). It's renowned " +
                            "for its \"Rockies\"—a cliffside viewpoint offering panoramic vistas of " +
                            "Taal Lake and Taal Volcano. The mountain is a favorite among hikers for " +
                            "its accessibility and the breathtaking scenery it offers.\n",
                    tagline = "Let's Hike to Mt. Maculot",
                    mountainImageRef1 = "mt_maculot_1",
                    mountainImageRef2 = "mt_maculot_2",
                    mountainImageRef3 = "mt_maculot_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Small Mammals",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
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

            // --- Mt. Talamitam Data ---
            val mtTalamitamId = "mttalamitm001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtTalamitamId,
                    mountainName = "Mt. Talamitam",
                    pictureReference = "mt_talamitam_main",
                    location = "Nasugbu, Batangas",
                    masl = 630,
                    difficultySummary = "Easy to moderate",
                    difficultyText = "Easy to moderate (3/9)",
                    hoursToSummit = "1.5–2.5 hours to the summit",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Grassland mountain",
                    trekDurationDetails = " ",
                    trailTypeDescription = "Open trails with minimal tree cover; some steep sections\n",
                    sceneryDescription = "Panoramic views of Batangas landscapes and nearby mountains",
                    viewsDescription = "360-degree summit view; neighboring ridgelines, Batangas lowlands, and even Tagaytay on clear days",
                    wildlifeDescription = "Grassland species and occasional sightings of local fauna",
                    featuresDescription = "Rolling hills and open trails",
                    hikingSeasonDetails = "Best during November to February (dry and cool); avoid summer midday heat and rainy season for safety",
                    introduction = "Mount Talamitam is situated in Nasugbu, Batangas, with an elevation " +
                            "of approximately 630 MASL. The mountain is characterized by open grasslands " +
                            "and offers a relatively straightforward ascent, making it suitable for beginners.",
                    tagline = "Let's Hike to Mt. Talamitam",
                    mountainImageRef1 = "mt_talamitam_1",
                    mountainImageRef2 = "mt_talamitam_2",
                    mountainImageRef3 = "mt_talamitam_3",

                    hasSteepSections = true,
                    notableWildlife = "fauna",
                    isRocky = false,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTalamitamId, description = "Camping areas are available near the summit, " +
                        "providing hikers with expansive views of the surrounding countryside. The trail is mostly exposed, so it's advisable to start early to avoid the midday sun."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTalamitamId, name = "Trail Options:", description = " "),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTalamitamId, name = "Main Trail:", description = "Near the summit area"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTalamitamId, name = "Water Sources:", description = "Scarce; hikers should bring ample water\n"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTalamitamId, name = "Best For:", description = "Beginner hikers, overnight camping, and sunrise viewing"),

            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTalamitamId, category = "Registration Fee", description = "Approximately ₱40, including barangay and environmental fees"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTalamitamId, category = "Guide Fee", description = "Optional; local guides are available at the jump-off point"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTalamitamId, category = "Camping Fee", description = "May apply; confirm at the registration area\n"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTalamitamId, category = "Safety Tip", description = "Wear sun protection due to limited shade; be cautious of slippery trails during the rainy season\n"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTalamitamId, category = "Safety Tip", description = "Bring sun protection (New Trail is very exposed)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTalamitamId, category = "Leave No Trace", description = "Always take your trash back down"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTalamitamId, category = "Hiking Season", description = "November to February for cooler weather")
            ))

            // --- Mt. Manabu Data ---
            val mtManabuId = "mtmanab001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtManabuId,
                    mountainName = "Mt. Manabu",
                    pictureReference = "mt_manabu_main",
                    location = "Sto. Tomas, Batangas",
                    masl = 760,
                    difficultySummary = "Easy",
                    difficultyText = "Easy (2/9)",
                    hoursToSummit = "2–3 hours to the summit",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Forested mountain",
                    trekDurationDetails = "2–3 hours to the summit",
                    trailTypeDescription = "Forested paths with occasional open areas; loop trail",
                    sceneryDescription = "Lush greenery and views of nearby peaks",
                    viewsDescription = "Views of nearby peaks",
                    wildlifeDescription = "Rich biodiversity, including various bird species and endemic plants",
                    featuresDescription = "Forest trails, a prominent white cross at the summit, and a grotto",
                    hikingSeasonDetails = "November to February for cooler temperatures and clearer trails",
                    introduction = "Mount Manabu, part of the Malipunyo Range, is located in Sto. Tomas, " +
                            "Batangas, and rises to about 760 MASL. The name \"Manabu\" is " +
                            "derived from \"Mataas na Bundok,\" meaning \"High Mountain.\" It's a " +
                            "favored destination for beginners due to its manageable trails and " +
                            "serene environment.",
                    tagline = "Let's Hike to Mt. Manabu",
                    mountainImageRef1 = "mt_manabu_1",
                    mountainImageRef2 = "mt_manabu_2",
                    mountainImageRef3 = "mt_manabu_3",

                    hasSteepSections = false,
                    notableWildlife = "Birds, Endemic Plants",
                    isRocky = false,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtManabuId, description = "Several clearings along the trail serve as campsites, offering a peaceful " +
                        "setting amidst nature. [cite: 38] The trail is well-shaded, making it comfortable for hikers even during warmer days."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtManabuId, name = "Campsite Locations", description = "Along the trail and near the summit area"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtManabuId, name = "Water Source", description = "Available; a spring is located near one of the campsites"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtManabuId, name = "Best For", description = "Beginner hikers, overnight camping, and nature appreciation")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtManabuId, name = "Main Trail", description = "Loop trail leading to the summit")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtManabuId, category = "Registration Fee", description = "Approximately ₱20–₱30"), // [cite: 41]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtManabuId, category = "Guide Fee", description = "Optional; local guides are familiar with the trail and can enhance the hiking experience"), // [cite: 41]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtManabuId, category = "Camping Fee", description = "May apply; check with local authorities"), // [cite: 42]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtManabuId, category = "Safety Tip", description = "Be prepared for muddy trails during the rainy season; bring insect repellent"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtManabuId, category = "Best Season", description = "November to February for cooler temperatures and clearer trails")
            ))

            // --- Mt. Gulugod Baboy Data ---
            val mtGulugodBaboyId = "mtglgdbby001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtGulugodBaboyId,
                    mountainName = "Mt. Gulugod Baboy",
                    pictureReference = "mt_gulugod_baboy_main",
                    location = "Mabini, Batangas",
                    masl = 525,
                    difficultySummary = "Easy",
                    difficultyText = "Easy (2/9)",
                    hoursToSummit = "1.5–2 hours to the summit",
                    bestMonthsToHike = "November to March",
                    typeVolcano = "Coastal hill / rolling ridge",
                    trekDurationDetails = "1.5–2 hours to the summit",
                    trailTypeDescription = "Open grassy slopes with gradual inclines; established paths",
                    sceneryDescription = "Overlooks Balayan Bay, Batangas Bay, and several islands including Sombrero and Maricaban",
                    viewsDescription = "360-degree views of sea, coastline, and Batangas countryside",
                    wildlifeDescription = "Grassland birds, grazing livestock, butterflies, and native shrubs",
                    featuresDescription = "Gentle ridges, sea views, and proximity to diving resorts for post-hike swims",
                    hikingSeasonDetails = "November to March (cooler, drier months)",
                    introduction = "Mount Gulugod Baboy, located in Anilao, Mabini, Batangas, is a " +
                            "coastal mountain standing at around 525 MASL. The name translates to " +
                            "\"pig's spine,\" referencing the mountain's rolling, ridge-like terrain. " +
                            "It's known for its short, friendly trails and stunning views of the ocean " +
                            "and nearby islands, making it a favorite for casual hikers and campers.",
                    tagline = "Let's Hike to Mt. Gulugod Baboy",
                    mountainImageRef1 = "mt_gulugod_baboy_1",
                    mountainImageRef2 = "mt_gulugod_baboy_2",
                    mountainImageRef3 = "mt_gulugod_baboy_3",

                    hasSteepSections = false,
                    notableWildlife = "Birds, Butterflies, Livestock",
                    isRocky = false,
                    isSlippery = false,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtGulugodBaboyId, description = "The mountain features open grassland suitable for pitching tents, especially near the summit. " +
                        "Overnight campers are rewarded with colorful sunsets and cool breezes from the nearby sea. The trail is mostly open, so early hikes are recommended to avoid direct sun exposure."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtGulugodBaboyId, name = "Campsite Locations", description = "Summit ridge: Wide and grassy with panoramic views"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtGulugodBaboyId, name = "Water Sources", description = "None on the trail; bring at least 2–3 liters per person"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtGulugodBaboyId, name = "Best For", description = "Day hikes, first-time hikers, overnight campers, and sea+mountain combos")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtGulugodBaboyId, name = "Main Trail from Philpan Dive Resort", description = "Most popular route"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtGulugodBaboyId, name = "Alternate trail from Barangay San Teodoro", description = "Less crowded")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtGulugodBaboyId, category = "Registration Fee", description = "₱40–₱50 (depending on the trailhead)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtGulugodBaboyId, category = "Guide Fee", description = "Optional (₱300–₱500 per group), trail is straightforward"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtGulugodBaboyId, category = "Camping Fee", description = "₱30–₱50, payable at the jump-off"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtGulugodBaboyId, category = "Safety Tip", description = "Trail is very exposed—wear sun protection"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtGulugodBaboyId, category = "Safety Tip", description = "Monitor the weather to avoid sudden changes in wind/rain"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtGulugodBaboyId, category = "Leave No Trace", description = "Bring your trash down, respect nearby residential areas"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtGulugodBaboyId, category = "Best Season", description = "November to March (cooler, drier months)")
            ))

            // --- Mt. Pico de Loro (Mt. Palay-Palay) Data ---
            val mtPicoDeLoroId = "mtpdl001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtPicoDeLoroId,
                    mountainName = "Mt. Pico de Loro (Mt. Palay-Palay)",
                    pictureReference = "mt_pico_de_loro_main",
                    location = "Ternate, Cavite / Nasugbu, Batangas",
                    masl = 688,
                    difficultySummary = "Moderate to Challenging",
                    difficultyText = "Moderate to Challenging (4/9 to summit; 5/9 to monolith)",
                    hoursToSummit = "3–4 hours to the summit; 5-6 hours total for out-and-back",
                    bestMonthsToHike = "November to March",
                    typeVolcano = "Dormant volcanic cone",
                    trekDurationDetails = "3–4 hours to the summit; 5–6 hours total for out-and-back",
                    trailTypeDescription = "Forested trail with rocky sections, ridge walk, and optional monolith climb",
                    sceneryDescription = "Forest canopy, coastal views, and the famous monolith",
                    viewsDescription = "Panoramic sights of Batangas coastline, South China Sea, and nearby ranges",
                    wildlifeDescription = "Forest birds, monkeys, butterflies, and lush flora",
                    featuresDescription = "The summit rock (monolith), summit ridge, and reforested areas",
                    hikingSeasonDetails = "November to March (dry and cool weather)",
                    introduction = "Mount Pico de Loro, also known as Mount Palay-Palay, is one of the " +
                            "most iconic hiking destinations in Southern Luzon. It straddles the " +
                            "provinces of Cavite and Batangas and stands at 688 meters above sea " +
                            "level (MASL). Named \"Parrot's Beak\" for its distinct summit rock " +
                            "formation, Pico de Loro offers a scenic hike through forest trails, " +
                            "culminating in panoramic views from its monolithic peak.",
                    tagline = "Let's Hike to Mt. Pico de Loro",
                    mountainImageRef1 = "mt_pico_de_loro_1",
                    mountainImageRef2 = "mt_pico_de_loro_2",
                    mountainImageRef3 = "mt_pico_de_loro_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Monkeys, Butterflies",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPicoDeLoroId, description = "There is an official campsite below the summit, providing ample " +
                        "space and excellent scenery. Camping is regulated to protect the surrounding forest."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPicoDeLoroId, name = "Summit base campsite", description = "Spacious and cleared with views of the monolith"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPicoDeLoroId, name = "Water Sources", description = "Limited; a water source may exist midway through the trail (subject to availability)"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPicoDeLoroId, name = "Best For", description = "Adventurous hikers, photography enthusiasts, overnight climbs")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtPicoDeLoroId, name = "New Trail (DENR route)", description = "Main regulated trail via DENR station in Ternate"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtPicoDeLoroId, name = "Old Trail (Nasugbu side)", description = "Previously used, now mostly off-limits or discouraged")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPicoDeLoroId, category = "Registration Fee", description = "₱25–₱50 at the DENR station"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPicoDeLoroId, category = "Guide Fee", description = "Required (₱500–₱1,000 depending on group size)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPicoDeLoroId, category = "Camping Fee", description = "₱30–₱50 per person"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPicoDeLoroId, category = "Restriction", description = "Monolith climb may be off-limits due to erosion and safety risks"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPicoDeLoroId, category = "Leave No Trace", description = "Strict \"Leave No Trace\" policy enforced"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPicoDeLoroId, category = "Safety Tip", description = "Bring gloves and wear proper shoes, especially if ridge or monolith climb is open"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPicoDeLoroId, category = "Safety Tip", description = "Prepare for slippery trails during wet season"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPicoDeLoroId, category = "Best Season", description = "November to March (dry and cool weather)\n")
            ))

            // --- Mt. Balagbag Data ---
            val mtBalagbagId = "mtblgbg001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtBalagbagId,
                    mountainName = "Mt. Balagbag",
                    pictureReference = "mt_balagbag_main",
                    location = "Rodriguez, Rizal / San Jose del Monte, Bulacan",
                    masl = 777,
                    difficultySummary = "Easy to moderate",
                    difficultyText = "Easy to moderate (2/9 to 3/9)",
                    hoursToSummit = "1.5–3 hours to the summit depending on trail and pace",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Part of the Sierra Madre foothills (upland grassland ridge)",
                    trekDurationDetails = "1.5–3 hours to the summit depending on trail and pace",
                    trailTypeDescription = "Dirt road, open ridges, and gradual ascents; bike-friendly",
                    sceneryDescription = "Expansive ridges, grasslands, views of nearby dams, cityscape, and mountains",
                    viewsDescription = "Metro Manila skyline, La Mesa Dam, Sierra Madre range",
                    wildlifeDescription = "Grassland birds, butterflies, and seasonal wildflowers",
                    featuresDescription = "Wide ridges, perfect for stargazing, off-road biking, and group camping",
                    hikingSeasonDetails = "November to February; good stargazing conditions in dry season",
                    introduction = "Mount Balagbag is a popular hiking and biking destination located " +
                            "at the boundary of Rodriguez (Montalban), Rizal, and San Jose del Monte, " +
                            "Bulacan. It rises to about 777 meters above sea level (MASL). Known for " +
                            "its rolling grassland trails and accessibility from Metro Manila, the " +
                            "mountain offers wide, open ridges with scenic views of the Sierra Madre, " +
                            "La Mesa Dam, and parts of the Metro skyline.",
                    tagline = "Let's Hike to Mt. Balagbag",
                    mountainImageRef1 = "mt_balagbag_1",
                    mountainImageRef2 = "mt_balagbag_2",
                    mountainImageRef3 = "mt_balagbag_3",

                    hasSteepSections = false,
                    notableWildlife = "Birds, Butterflies, Wildflowers",
                    isRocky = false,
                    isSlippery = false,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBalagbagId, description = "Mount Balagbag is well-known for its broad ridges that double as excellent camping areas. " +
                        "It's ideal for beginners, family hikers, and large groups. There are multiple areas to pitch tents along the route and near the summit."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBalagbagId, name = "Summit ridge", description = "Open grassland with space for many tents"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBalagbagId, name = "Along the trail", description = "Several rest stops and cleared areas suitable for camping"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBalagbagId, name = "Water Sources", description = "Few; better to bring enough water (2-3L recommended)"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBalagbagId, name = "Best For", description = "Beginners, mountain bikers, overnighters, and quick day hikes")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtBalagbagId, name = "Licao-Licao Trail", description = "Most common trail from Barangay Licao-Licao"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtBalagbagId, name = "Karahume Trail", description = "Longer but more scenic route")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalagbagId, category = "Registration Fee", description = "₱20–₱50, depending on the barangay or route"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalagbagId, category = "Guide Fee", description = "Optional; not required but local guides are available"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalagbagId, category = "Camping Fee", description = "₱20–₱50 per person"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalagbagId, category = "Accessibility", description = "Can be reached by jeep/trike from Tungko or Fairview, then hike or ride"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalagbagId, category = "Safety Tip", description = "Very exposed trail—bring sun protection and plenty of water"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalagbagId, category = "Safety Tip", description = "Avoid hiking during thunderstorms due to wide, open terrain"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalagbagId, category = "Best Season", description = "November to February; good stargazing conditions in dry season")

            ))

            // --- Mt. Binacayan Data ---
            val mtBinacayanId = "mtbncyn001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtBinacayanId,
                    mountainName = "Mt. Binacayan",
                    pictureReference = "mt_binacayan_main",
                    location = "Rodriguez (Montalban), Rizal",
                    masl = 424,
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate (3/9)",
                    hoursToSummit = "2–3 hours to the summit",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Rocky hill, part of the Sierra Madre range",
                    trekDurationDetails = "2–3 hours to the summit",
                    trailTypeDescription = "Rocky, somewhat steep with ridges",
                    sceneryDescription = "Gorgeous river views, mountains, and a scenic ridge walk",
                    viewsDescription = "Panoramic views of Wawa River, neighboring mountains, and the valleys below",
                    wildlifeDescription = "Birds, small mammals, and plant life in the surrounding forest",
                    featuresDescription = "Steep ridges, a rocky summit, and panoramic views of Wawa River",
                    hikingSeasonDetails = "November to February (dry and cooler months)",
                    introduction = "Mount Binacayan is a relatively short but scenic hike located in " +
                            "Rodriguez (Montalban), Rizal, with an elevation of around 424 meters above sea level (MASL). " +
                            "It is one of the lesser-known mountains in the area but offers stunning " +
                            "views of the nearby Wawa River and Sierra Madre Mountains. The mountain " +
                            "is popular for its rocky terrain and interesting ridges, making it a " +
                            "great spot for day hikes and rock climbing enthusiasts.",
                    tagline = "Let's Hike to Mt. Binacayan",
                    mountainImageRef1 = "mt_binacayan_1",
                    mountainImageRef2 = "mt_binacayan_2",
                    mountainImageRef3 = "mt_binacayan_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Small Mammals",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBinacayanId, description = "Camping on Mount Binacayan is possible but limited due to its rocky terrain. " +
                        "It's better for those looking for a day hike or short overnight trip with a quick return. The area near the summit provides some flat spaces for camping."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBinacayanId, name = "Near the summit", description = "Small open areas for tents, with views of the surrounding peaks"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBinacayanId, name = "Water Sources", description = "Limited; hikers are advised to bring sufficient water"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBinacayanId, name = "Best For", description = "Day hikers, adventurous beginners, rock climbing enthusiasts")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtBinacayanId, name = "Binacayan Trail", description = "The main trail leading to the summit from the jump-off point"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtBinacayanId, name = "Side Ridges", description = "Some hikers choose to hike along the side ridges for a more challenging experience")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBinacayanId, category = "Registration Fee", description = "₱20–₱50, payable at the barangay hall"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBinacayanId, category = "Guide Fee", description = "Optional, recommended for first-time hikers (₱300–₱500 per group)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBinacayanId, category = "Camping Fee", description = "₱20–₱50, depending on the area"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBinacayanId, category = "Safety Tip", description = "Trail is rocky and can be slippery during the rainy season"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBinacayanId, category = "Safety Tip", description = "Be cautious of loose rocks and sharp inclines") ,
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBinacayanId, category = "Best Season", description = "November to February (dry and cooler months)")
            ))

            // --- Mt. Pamitinan Data ---
            val mtPamitinanId = "mtpmtnn001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtPamitinanId,
                    mountainName = "Mt. Pamitinan",
                    pictureReference = "mt_pamitinan_main",
                    location = "Rodriguez (Montalban), Rizal",
                    masl = 426,
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate (3/9)",
                    hoursToSummit = "2–3 hours to the summit",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Limestone mountain with rocky terrain",
                    trekDurationDetails = "2–3 hours to the summit",
                    trailTypeDescription = "Rocky, steep, and sometimes exposed",
                    sceneryDescription = "Scenic views of the river, valleys, and the nearby mountains",
                    viewsDescription = "Panoramic views of the Wawa River, Sierra Madre, and the neighboring mountains",
                    wildlifeDescription = "Various bird species, insects, and occasional wild animals in the forested areas",
                    featuresDescription = "Limestone cliffs, rock formations, and an expansive view from the summit",
                    hikingSeasonDetails = "November to February (best weather conditions, dry season)",
                    introduction = "Mount Pamitinan is a relatively short but exciting hike located " +
                            "in Rodriguez (Montalban), Rizal, standing at 426 meters above sea level " +
                            "(MASL). Known for its limestone formations, it offers hikers a unique " +
                            "experience, with rugged rock faces and panoramic views of the surrounding " +
                            "Sierra Madre mountains and Wawa River. It's a great spot for both " +
                            "beginner and intermediate hikers seeking a short but rewarding trek.",
                    tagline = "Let's Hike to Mt. Pamitinan",
                    mountainImageRef1 = "mt_pamitinan_1",
                    mountainImageRef2 = "mt_pamitinan_2",
                    mountainImageRef3 = "mt_pamitinan_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Insects, Wild Animals",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPamitinanId, description = "Mount Pamitinan is typically a day hike destination, but camping is possible in some areas near the base or " +
                        "on the trail. [cite: 81] Due to the rocky and steep terrain, camping near the summit isn't recommended. However, there are some flat areas near the jump-off point or along the trail for an overnight experience."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPamitinanId, name = "Near the base", description = "Open spaces suitable for tents"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPamitinanId, name = "Water Sources", description = "Wawa River (accessed early in the hike), but carry enough water (at least 2 liters)"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPamitinanId, name = "Best For", description = "Day hikers, rock climbing enthusiasts, first-time hikers looking for a bit of a challenge")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtPamitinanId, name = "Pamitinan Trail", description = "Main route to the summit from the jump-off in Wawa")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPamitinanId, category = "Registration Fee", description = "₱20–₱30 at the barangay hall in Wawa"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPamitinanId, category = "Guide Fee", description = "Recommended (₱300–₱500 per group), as the trail can be tricky for first-timers"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPamitinanId, category = "Camping Fee", description = "₱20–₱50, payable at the jump-off or campsite"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPamitinanId, category = "Safety Tip", description = "Take extra care on the rocky sections, especially when wet"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPamitinanId, category = "Safety Tip", description = "Bring a flashlight or headlamp if planning for an overnight hike"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPamitinanId, category = "Best Season", description = "November to February (best weather conditions, dry season)")
            ))

            // --- Mt. Hapunang Banoi Data ---
            val mtHapunangBanoiId = "mthpngbni001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtHapunangBanoiId,
                    mountainName = "Mt. Hapunang Banoi",
                    pictureReference = "mt_hapunang_banoi_main",
                    location = "Rodriguez (Montalban), Rizal",
                    masl = 723,
                    difficultySummary = "Moderate to Challenging",
                    difficultyText = "Moderate to Challenging (4/9)",
                    hoursToSummit = "3–4 hours to the summit",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Limestone mountain with steep ascents and rocky paths",
                    trekDurationDetails = "3–4 hours to the summit",
                    trailTypeDescription = "Steep, rocky paths, with scrambling sections",
                    sceneryDescription = "Dense forests, exposed ridges, and views of nearby peaks and valleys",
                    viewsDescription = "Views of the Sierra Madre, Montalban valley, and nearby mountains",
                    wildlifeDescription = "Birds, insects, and occasional wildlife in the forested areas",
                    featuresDescription = "Limestone formations, exposed rock faces, and ridges",
                    hikingSeasonDetails = "November to February (best for dry weather and clear views)",
                    introduction = "Mount Hapunang Banoi is a prominent peak located in Rodriguez " +
                            "(Montalban), Rizal, with an elevation of 723 meters above sea level " +
                            "(MASL). It is known for its rugged terrain, steep trails, and striking " +
                            "limestone rock formations. The mountain is often combined with nearby " +
                            "peaks such as Mount Pamitinan and Mount Binacayan for a more challenging " +
                            "hike, but it can also be tackled on its own. It offers a great blend of " +
                            "forest, rock climbing, and expansive views.",
                    tagline = "Let's Hike to Mt. Hapunang Banoi",
                    mountainImageRef1 = "mt_hapunang_banoi_1",
                    mountainImageRef2 = "mt_hapunang_banoi_2",
                    mountainImageRef3 = "mt_hapunang_banoi_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Insects, Wildlife",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtHapunangBanoiId, description = "Camping on Mount Hapunang Banoi is limited, given the steep nature of the terrain. It's " +
                        "better suited for day hikes, but there are campsites near the trailhead and lower sections of the trail where hikers can stop for rest and short stays."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtHapunangBanoiId, name = "Campsite Location", description = "Near the base and initial parts of the trail for rest"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtHapunangBanoiId, name = "Campsite Location", description = "Limited flat ground near the trailhead"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtHapunangBanoiId, name = "Water Sources", description = "Wawa River is the main water source for the first section of the hike"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtHapunangBanoiId, name = "Best For", description = "Day hikers, experienced hikers combining multiple mountains, rock climbing enthusiasts")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtHapunangBanoiId, name = "Main Trail", description = "The standard route starting from the Wawa River"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtHapunangBanoiId, name = "Combo Trail", description = "A longer route that connects with Mount Pamitinan and Mount Binacayan")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtHapunangBanoiId, category = "Registration Fee", description = "₱20–₱30 at the barangay hall or jump-off point"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtHapunangBanoiId, category = "Guide Fee", description = "Highly recommended (₱300–₱500 per group) for navigation and safety"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtHapunangBanoiId, category = "Camping Fee", description = "₱20–₱50, depending on the location"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtHapunangBanoiId, category = "Safety Tip", description = "The trail is steep and rocky—wear proper hiking shoes"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtHapunangBanoiId, category = "Safety Tip", description = "Be cautious around the limestone rock formations to avoid slipping"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtHapunangBanoiId, category = "Best Season", description = "November to February (best for dry weather and clear views)")
            ))

// --- Mt. Maynoba Data ---
            val mtMaynobaId = "mtmynba001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtMaynobaId,
                    mountainName = "Mt. Maynoba",
                    pictureReference = "mt_maynoba_main",
                    location = "Rodriguez (Montalban), Rizal", // [cite: 95]
                    masl = 723, // [cite: 93, 95] (Document has conflicting info, 728 MASL on page 12, 723 on page 13. Using 723 from details)
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate (3/9)", // [cite: 95]
                    hoursToSummit = "3–4 hours to the summit", // [cite: 95]
                    bestMonthsToHike = "November to February", // [cite: 98]
                    typeVolcano = "Mountain ridge with some rocky sections", // [cite: 95]
                    trekDurationDetails = "3–4 hours to the summit", // [cite: 95]
                    trailTypeDescription = "Forest trail with rocky sections and gradual inclines", // [cite: 95]
                    sceneryDescription = "Dense forests, ridges, and mountain landscapes", // [cite: 95]
                    viewsDescription = "Panoramic views of the Sierra Madre mountains and Wawa River", // [cite: 95]
                    wildlifeDescription = "Birds, insects, and native plants", // [cite: 95]
                    featuresDescription = "Ridges, forest trails, and stunning summit views", // [cite: 95]
                    hikingSeasonDetails = "November to February (cooler and drier conditions)", // [cite: 98]
                    introduction = "Mount Maynoba is located in Rodriguez (Montalban), Rizal, and stands at an elevation of about 723 meters above sea level (MASL). [cite: 93, 95] It's a popular mountain for both novice and intermediate hikers due to its manageable trails and beautiful views. [cite: 94] The hike offers a mix of forested sections, open ridges, and rocky terrain, providing panoramic views of the Wawa River, Sierra Madre mountains, and nearby peaks. [cite: 94]",
                    tagline = "Let's Hike to Mt. Maynoba",
                    mountainImageRef1 = "mt_maynoba_1",
                    mountainImageRef2 = "mt_maynoba_2",
                    mountainImageRef3 = "mt_maynoba_3",

                    hasSteepSections = false, // Described with "gradual inclines" [cite: 95]
                    notableWildlife = "Birds, Insects, Native Plants", // [cite: 95]
                    isRocky = true, // [cite: 94, 95]
                    isSlippery = true, // "The trail can be slippery after rainfall" [cite: 98]
                    isEstablishedTrail = true // Implied by "Main Trail" [cite: 97]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, description = "There are no formal campsites on Mount Maynoba, but the mountain has some flat areas near the summit and along the lower sections of the trail where hikers can pitch tents for a peaceful night under the stars. [cite: 96]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, name = "Near the summit", description = "Flat spots perfect for tents and offering scenic views"), // [cite: 97]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, name = "Water Sources", description = "Limited; carry at least 2–3 liters of water for the hike"), // [cite: 98]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, name = "Best For", description = "Day hikers, beginner hikers, first-timers, overnight trips") // [cite: 98]
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, name = "Main Trail", description = "The most common route from the jump-off point near Wawa River"), // [cite: 97]
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, name = "Alternative Trail", description = "A longer route from the nearby towns of San Isidro or Dulong Bayan") // [cite: 97]
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, category = "Registration Fee", description = "₱20–₱50, payable at the barangay hall or jump-off"), // [cite: 98]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, category = "Guide Fee", description = "Optional (₱300–₱500), helpful for first-time hikers"), // [cite: 98]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, category = "Camping Fee", description = "₱20–₱50, depending on campsite availability"), // [cite: 98]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, category = "Safety Tip", description = "The trail can be slippery after rainfall ensure proper footwear"), // [cite: 98]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, category = "Safety Tip", description = "If camping, pack sufficient food and water, as supplies are limited") // [cite: 98]
            ))

// --- Mt. Daraitan Data ---
            val mtDaraitanId = "mtdrtn001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtDaraitanId,
                    mountainName = "Mt. Daraitan",
                    pictureReference = "mt_daraitan_main",
                    location = "Tanay, Rizal", // [cite: 101]
                    masl = 1130, // [cite: 99, 101] (Document has conflicting info, 739 MASL on page 13, 1130 on page 14. Using 1130 from details.)
                    difficultySummary = "Moderate to Challenging",
                    difficultyText = "Moderate to Challenging (5/9)", // [cite: 101]
                    hoursToSummit = "3–5 hours to the summit", // [cite: 101]
                    bestMonthsToHike = "Not explicitly stated, but generally dry season (Nov-Feb) is preferred for most PH mountains.", // General knowledge
                    typeVolcano = "Limestone mountain with rich biodiversity", // [cite: 101]
                    trekDurationDetails = "3–5 hours to the summit", // [cite: 101]
                    trailTypeDescription = "Rocky and forested with sections of river crossing", // [cite: 101]
                    sceneryDescription = "Lush forests, limestone rock formations, valleys, and rivers", // [cite: 101]
                    viewsDescription = "Panoramic views of the Tanay plains, Sierra Madre mountains, and Daraitan River", // [cite: 101]
                    wildlifeDescription = "Rich biodiversity, including birds, butterflies, and native plants", // [cite: 101]
                    featuresDescription = "Limestone formations, river crossing, and panoramic views from the summit", // [cite: 102]
                    hikingSeasonDetails = "Not explicitly stated, assume dry season (November to February/March) for better conditions.", // General inference
                    introduction = "Mount Daraitan is a majestic peak located in Tanay, Rizal, rising to an elevation of about 1,130 meters above sea level (MASL). [cite: 99, 101] Known for its stunning limestone formations, lush forests, and the famous Daraitan River, this mountain has become a sought-after destination for hikers and nature lovers. [cite: 100] The trail provides scenic views of the surrounding valleys and rivers, making it a great adventure for both novice and experienced hikers. [cite: 100]",
                    tagline = "Let's Hike to Mt. Daraitan",
                    mountainImageRef1 = "mt_daraitan_1",
                    mountainImageRef2 = "mt_daraitan_2",
                    mountainImageRef3 = "mt_daraitan_3",

                    hasSteepSections = true, // Implied by "rocky and steep sections" [cite: 101, 105]
                    notableWildlife = "Birds, Butterflies, Native Plants", // [cite: 101]
                    isRocky = true, // [cite: 101]
                    isSlippery = true, // "it can get slippery" (river crossing), "rocky and steep sections" [cite: 105]
                    isEstablishedTrail = true // Implied by "Main Trail" [cite: 104]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, description = "Mount Daraitan offers some campsites near the base and lower sections of the trail, particularly near the Daraitan River. [cite: 103] This river is a common spot for overnight stays, as it's both beautiful and tranquil, ideal for setting up camp. [cite: 103]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, name = "Near Daraitan River", description = "Perfect for overnight stays with beautiful views of the river and mountains"), // [cite: 104]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, name = "Near the summit", description = "Limited camping space due to steep terrain"), // [cite: 104]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, name = "Water Sources", description = "Daraitan River, available at the jump-off point"), // [cite: 104]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, name = "Best For", description = "Nature lovers, overnight campers, first-timers, and photography enthusiasts") // [cite: 104]
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, name = "Main Trail", description = "The primary trail leading from the Daraitan Barangay Hall"), // [cite: 104]
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, name = "River Crossing", description = "Part of the trail involves crossing the Daraitan River") // [cite: 104]
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, category = "Registration Fee", description = "₱20–₱50 at the Barangay Hall"), // [cite: 104]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, category = "Guide Fee", description = "Required (₱500–₱800 depending on group size)"), // [cite: 104]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, category = "Camping Fee", description = "₱20–₱50 at river campsites"), // [cite: 104]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, category = "Safety Tip", description = "Bring extra socks for crossing the river, as it can get slippery"), // [cite: 105]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, category = "Safety Tip", description = "Ensure proper footwear for the rocky and steep sections") // [cite: 105]
            ))

// --- Mt. Mapalad Data ---
            val mtMapaladId = "mtmpld001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtMapaladId,
                    mountainName = "Mt. Mapalad",
                    pictureReference = "mt_mapalad_main",
                    location = "Rodriguez (Montalban), Rizal", // [cite: 108]
                    masl = 500, // [cite: 106, 108]
                    difficultySummary = "Easy to Moderate",
                    difficultyText = "Easy to Moderate (2/9 to 3/9)", // [cite: 108]
                    hoursToSummit = "2–3 hours to the summit", // [cite: 108]
                    bestMonthsToHike = "November to February", // [cite: 112]
                    typeVolcano = "Forested mountain with grassy ridges", // [cite: 108]
                    trekDurationDetails = "2–3 hours to the summit", // [cite: 108]
                    trailTypeDescription = "Forested paths and grassland ridges", // [cite: 108]
                    sceneryDescription = "Lush forests, ridges with expansive views, and nearby mountains", // [cite: 108]
                    viewsDescription = "Sierra Madre, Montalban valley, and nearby peaks", // [cite: 109]
                    wildlifeDescription = "Birds, insects, and various plant species", // [cite: 109]
                    featuresDescription = "Forest paths, grassy ridges, and sweeping views from the summit", // [cite: 109]
                    hikingSeasonDetails = "November to February (cooler and drier conditions)", // [cite: 112]
                    introduction = "Mount Mapalad is a relatively lesser-known mountain located in Rodriguez (Montalban), Rizal, with an elevation of around 500 meters above sea level (MASL). [cite: 106] Known for its picturesque views of the Sierra Madre and Montalban Valley, it offers a refreshing escape for those looking to enjoy nature without the typical crowds. [cite: 107] The trail features a mix of forests, grassy ridges, and small rocky sections, making it perfect for beginner to moderate hikers. [cite: 107]",
                    tagline = "Let's Hike to Mt. Mapalad",
                    mountainImageRef1 = "mt_mapalad_1",
                    mountainImageRef2 = "mt_mapalad_2",
                    mountainImageRef3 = "mt_mapalad_3",

                    hasSteepSections = false, // Not explicitly stated as having significant steep sections, "small rocky sections" [cite: 107]
                    notableWildlife = "Birds, Insects, Plant Species", // [cite: 109]
                    isRocky = true, // "small rocky sections" [cite: 107]
                    isSlippery = true, // "occasionally slippery sections" [cite: 112]
                    isEstablishedTrail = true // Implied by "Main Trail" [cite: 111]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, description = "Camping on Mount Mapalad is possible, but it's best suited for those who prefer simple overnight stays with minimal facilities. [cite: 110] The mountain offers some camping spots along the trail, particularly near the ridges and lower slopes. [cite: 110]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, name = "Near the summit", description = "A few flat spots for tents with great views"), // [cite: 111]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, name = "Lower sections", description = "Open grassy areas suitable for a basic camping setup"), // [cite: 111]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, name = "Water Sources", description = "Wawa River and a few springs along the trail"), // [cite: 111]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, name = "Best For", description = "Beginner hikers, family outings, and overnight campers") // [cite: 111]
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, name = "Main Trail", description = "Starts from the Wawa River area or nearby barangays"), // [cite: 111]
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, name = "Alternative Trail", description = "From San Isidro, providing a longer route with more scenic views") // [cite: 111]
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, category = "Registration Fee", description = "₱20–₱30 at the Barangay Hall"), // [cite: 112]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, category = "Guide Fee", description = "Optional (₱300–₱500 per group), recommended for first-timers"), // [cite: 112]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, category = "Camping Fee", description = "₱20–₱50, depending on the area"), // [cite: 112]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, category = "Safety Tip", description = "Be aware of sudden weather changes—bring a raincoat or jacket"), // [cite: 112]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, category = "Safety Tip", description = "Wear sturdy shoes for the grassy and occasionally slippery sections") // [cite: 112]
            ))

            // --- Mt. Ayaas Data ---
            val mtAyaasId = "mtayaas001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtAyaasId,
                    mountainName = "Mt. Ayaas",
                    pictureReference = "mt_ayaas_main",
                    location = "Rodriguez (Montalban), Rizal", // [cite: 113]
                    masl = 720, // [cite: 113]
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate (3/9)", // [cite: 116]
                    hoursToSummit = "2–4 hours to the summit", // [cite: 116]
                    bestMonthsToHike = "November to February", // [cite: 120]
                    typeVolcano = "Forested mountain with rocky sections", // [cite: 116]
                    trekDurationDetails = "2–4 hours to the summit", // [cite: 116]
                    trailTypeDescription = "Forested paths with rocky and uneven sections", // [cite: 116]
                    sceneryDescription = "Forests, ridges, and expansive views of valleys and nearby peaks", // [cite: 117]
                    viewsDescription = "360-degree views of Sierra Madre, Montalban Valley, and other surrounding mountains", // [cite: 117]
                    wildlifeDescription = "Various bird species, insects, and some wild mammals", // [cite: 117]
                    featuresDescription = "A combination of forest trails, ridges, and rocky outcrops", // [cite: 117]
                    hikingSeasonDetails = "November to February (drier and cooler weather)", // [cite: 120]
                    introduction = "Mount Ayaas is located in Rodriguez (Montalban), Rizal, and rises to an elevation of 720 meters above sea level (MASL)[cite: 113]. It is a hidden gem for hikers looking for a peaceful, less crowded experience[cite: 113, 114]. The mountain offers a combination of forest trails, rocky ridges, and open areas that provide panoramic views of the Sierra Madre Mountains and the nearby valleys[cite: 114, 115]. Its accessibility makes it a great choice for day hikes and weekend outings[cite: 115].",
                    tagline = "Let's Hike to Mt. Ayaas",
                    mountainImageRef1 = "mt_ayaas_1",
                    mountainImageRef2 = "mt_ayaas_2",
                    mountainImageRef3 = "mt_ayaas_3",

                    hasSteepSections = true, // Implied by "rocky and uneven sections"
                    notableWildlife = "Birds, Insects, Wild Mammals", // [cite: 117]
                    isRocky = true, // [cite: 116]
                    isSlippery = true, // "The trail can be slippery after rainfall" [cite: 120]
                    isEstablishedTrail = true // Implied by "Main Trail"
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, description = "Camping is available on Mount Ayaas, especially in lower areas near the base and on flatter sections of the trail[cite: 118]. There are a few campsites with stunning views of the surrounding landscapes, perfect for those looking to spend the night and experience the tranquility of the mountains[cite: 118, 119]."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, name = "Near the summit", description = "Small flat areas perfect for tents"), // [cite: 119]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, name = "Lower sections", description = "Grassy fields and open spaces for a more relaxed camping experience"), // [cite: 119]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, name = "Water Sources", description = "Springs near the base and along the trail"), // [cite: 119]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, name = "Best For", description = "Beginner to intermediate hikers, family hikes, and campers") // [cite: 119]
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, name = "Main Trail", description = "The main route starting from Barangay San Isidro, Rodriguez"), // [cite: 119]
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, name = "Alternative Routes", description = "Some trails connect with other nearby mountains like Mount Pamitinan and Mount Binacayan") // [cite: 119]
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, category = "Registration Fee", description = "₱20–₱30 at the Barangay Hall"), // [cite: 119]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, category = "Guide Fee", description = "Optional (₱300–₱500), helpful for first-time hikers"), // [cite: 119]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, category = "Camping Fee", description = "₱20–₱50 depending on campsite location"), // [cite: 119]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, category = "Safety Tip", description = "The trail can be slippery after rainfall, so wear proper footwear"), // [cite: 120]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, category = "Safety Tip", description = "Bring sufficient water and snacks, especially if hiking for longer durations") // [cite: 120]
            ))

// --- Mt. Mabilog Data ---
            val mtMabilogId = "mtmblg001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtMabilogId,
                    mountainName = "Mt. Mabilog",
                    pictureReference = "mt_mabilog_main",
                    location = "San Pablo City, Laguna", // [cite: 121]
                    masl = 441, // [cite: 122]
                    difficultySummary = "Easy",
                    difficultyText = "2/9", // [cite: 122, 126]
                    hoursToSummit = "2-3 hours", // [cite: 126]
                    bestMonthsToHike = "Not explicitly stated, assume dry season (Nov-Feb).",
                    typeVolcano = "Not specified, likely hill/mountain", // Inferred
                    trekDurationDetails = "2-3 hours", // [cite: 126]
                    trailTypeDescription = "Loop or out-and-back, passing through farmlands and grasslands.", // [cite: 127]
                    sceneryDescription = "Panoramic views of the Seven Lakes of San Pablo, including Yambo and Pandin Lakes, and surrounding mountains like Banahaw, Cristobal, and Makiling", // [cite: 124, 127]
                    viewsDescription = "Amazing view overlooking the lakes (Yambo, Pandin most visible), Mt. Banahaw, Mt. Cristobal and Mt Makiling", // [cite: 124]
                    wildlifeDescription = "Not specified, likely typical grassland/farmland fauna.", // Inferred
                    featuresDescription = "Summit views of Seven Lakes, proximity to Mts. Banahaw, Cristobal, Makiling", // [cite: 121, 124]
                    hikingSeasonDetails = "Not explicitly stated, but dry season (Nov-Feb) is generally good for Philippine hiking.",
                    introduction = "Mt. Mabilog is nestled between the 7 lakes of San Pablo City, Laguna, and surrounded by the giants Mts. Banahaw, Cristobal and Makiling[cite: 121, 122]. With the height elevation of 441 MASL, this place is a good starting ground for the rookies for its 2/9 minor difficulty[cite: 122]. But though it's just a minor hike, there's still a challenge going to the top and the summit[cite: 123, 124].",
                    tagline = "Let's Hike to Mt. Mabilog",
                    mountainImageRef1 = "mt_mabilog_1",
                    mountainImageRef2 = "mt_mabilog_2",
                    mountainImageRef3 = "mt_mabilog_3",

                    hasSteepSections = false, // "Not that hard though" [cite: 124]
                    notableWildlife = "", // Not specified
                    isRocky = false, // Not specified as rocky
                    isSlippery = true, // "Wear sturdy, non-slip shoes" [cite: 132] (general advice, but implies potential)
                    isEstablishedTrail = false // "unmarked trails" [cite: 130]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, name = "Campsite Capacity", description = "Suitable for small groups; camping is allowed at the summit or near Yambo Lake"), // [cite: 128]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, name = "Amenities", description = "Basic facilities; bring your own camping gear"), // [cite: 128]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, name = "Permit/Fees", description = "No registration fee; guide fee is approximately ₱300-₱350 per group") // [cite: 129]
            ))
// No specific TrailEntity as it's "Loop or out-and-back" [cite: 127] and trails are "unmarked" [cite: 130]
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Registration", description = "Register at the jump-off point in Barangay Sulsuguin, Nagcarlan."), // [cite: 130]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Logbook Registration", description = "Not mandatory but recommended for safety"), // [cite: 130]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Courtesy Call", description = "Inform local officials or barangay staff of your hike"), // [cite: 130]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Guide Requirement", description = "Highly recommended due to unmarked trails and to support the local community"), // [cite: 130]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Trail Etiquette", description = "Stay on marked paths to prevent soil erosion"), // [cite: 131]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Waste Management", description = "Carry out all trash; no littering"), // [cite: 131]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Respect Nature", description = "Avoid disturbing wildlife and plants"), // [cite: 131]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Weather Awareness", description = "Check weather forecasts; avoid hiking during heavy rains"), // [cite: 132]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Proper Footwear", description = "Wear sturdy, non-slip shoes"), // [cite: 132]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Hydration", description = "Bring sufficient water; there are limited water sources along the trail"), // [cite: 133]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Sun Protection", description = "Use sunscreen and wear a hat") // [cite: 133]
            ))

// --- Mt. Kalisungan Data ---
            val mtKalisunganId = "mtklsgn001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtKalisunganId,
                    mountainName = "Mt. Kalisungan",
                    pictureReference = "mt_kalisungan_main",
                    location = "Calauan, Laguna", // [cite: 135]
                    masl = 760, // [cite: 139]
                    difficultySummary = "Easy to Moderate", // Inferred from 3/9 and description
                    difficultyText = "3/9", // [cite: 139]
                    hoursToSummit = "2.5 to 3 hours", // [cite: 139]
                    bestMonthsToHike = "Not explicitly stated, assume dry season (Nov-Feb).",
                    typeVolcano = "Not specified, likely mountain", // Inferred
                    trekDurationDetails = "2.5 to 3 hours", // [cite: 139]
                    trailTypeDescription = "Out-and-back, passing through community trails, fruit orchards, and grasslands", // [cite: 139]
                    sceneryDescription = "Panoramic views of Laguna's Seven Lakes, Mt. Makiling, Mt. Sembrano, Mt. Tagapo, and Mt. Banahaw", // [cite: 139]
                    viewsDescription = "Panoramic views of Laguna's Seven Lakes, Mt. Makiling, Mt. Sembrano, Mt. Tagapo, and Mt. Banahaw", // [cite: 139]
                    wildlifeDescription = "Not specified, likely typical grassland/orchard fauna.", // Inferred
                    featuresDescription = "Picturesque landscapes, serene atmosphere, views of Seven Lakes and surrounding mountains", // [cite: 134, 139]
                    hikingSeasonDetails = "Not explicitly stated, dry season (Nov-Feb) is generally advisable.",
                    introduction = "Nestled in the charming province of Laguna, Mt Kalisungan is a gem for hikers seeking an accessible yet rewarding adventure[cite: 134]. Known for its picturesque landscapes, serene atmosphere, and panoramic views, Mt Kalisungan hike is ideal for both beginners and seasoned trekkers[cite: 134]. What sets Mt Kalisungan apart is its strategic location in Calauan, Laguna, Philippines, making it a quick escape for nature lovers from the bustling city of Manila[cite: 135].",
                    tagline = "Let's Hike to Mt. Kalisungan",
                    mountainImageRef1 = "mt_kalisungan_1",
                    mountainImageRef2 = "mt_kalisungan_2",
                    mountainImageRef3 = "mt_kalisungan_3",

                    hasSteepSections = false, // Not specified as particularly steep
                    notableWildlife = "", // Not specified
                    isRocky = false, // Not specified as rocky
                    isSlippery = true, // "Wear sturdy, non-slip shoes" [cite: 145] (general advice, implies potential)
                    isEstablishedTrail = false // "unmarked trails" [cite: 142]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, name = "Campsite Capacity", description = "Suitable for small groups; camping is allowed at the summit"), // [cite: 140]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, name = "Amenities", description = "Basic facilities; bring your own camping gear"), // [cite: 140]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, name = "Permit/Fees", description = "Registration fee is ₱20 per person; guide fee is approximately ₱400 per group") // [cite: 141]
            ))
// No specific TrailEntity as it's "Out-and-back" [cite: 139] and trails are "unmarked" [cite: 142]
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Registration", description = "Register at the Barangay Lamot 2 Hall upon arrival"), // [cite: 141]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Logbook Registration", description = "Not mandatory but recommended for safety"), // [cite: 141]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Courtesy Call", description = "Inform local officials or barangay staff of your hike"), // [cite: 141]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Guide Requirement", description = "Highly recommended due to unmarked trails and to support the local community"), // [cite: 142]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Trail Etiquette", description = "Stay on marked paths to prevent soil erosion"), // [cite: 144]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Waste Management", description = "Carry out all trash; no littering"), // [cite: 144]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Respect Nature", description = "Avoid disturbing wildlife and plants"), // [cite: 144]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Weather Awareness", description = "Check weather forecasts; avoid hiking during heavy rains"), // [cite: 145]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Proper Footwear", description = "Wear sturdy, non-slip shoes"), // [cite: 145]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Hydration", description = "Bring sufficient water; there are limited water sources along the trail"), // [cite: 146]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Sun Protection", description = "Use sunscreen and wear a hat") // [cite: 146]
            ))

// --- Mt. Makiling (via UPLB) Data ---
            val mtMakilingId = "mtmkling001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtMakilingId,
                    mountainName = "Mt. Makiling (via UPLB)",
                    pictureReference = "mt_makiling_main",
                    location = "Los Baños, Laguna", // Inferred from UPLB College of Forestry [cite: 154]
                    masl = 1090, // [cite: 154]
                    difficultySummary = "Moderate to Challenging", // Inferred from 4/9 and description
                    difficultyText = "4/9", // [cite: 154]
                    hoursToSummit = "4-6 hours", // [cite: 153]
                    bestMonthsToHike = "Not explicitly stated, assume dry season (Nov-Feb).",
                    typeVolcano = "Not specified, likely dormant volcano (Maria Makiling legend context)", // Inferred
                    trekDurationDetails = "4-6 hours", // [cite: 153]
                    trailTypeDescription = "Out-and-back or loop, with the most popular route starting from the UPLB College of Forestry", // [cite: 154]
                    sceneryDescription = "Diverse ecosystems ranging from lowland dipterocarp forests to montane mossy forests, offering views of nearby towns and geothermal features", // [cite: 154]
                    viewsDescription = "Glimpse of Los Banos and the nearby towns, as well as of Laguna Lake", // [cite: 151]
                    wildlifeDescription = "Well-preserved forests teeming with wildlife, including plenty of limatik (leeches)", // [cite: 149]
                    featuresDescription = "Rugged terrain, untamed rainforests, narrow 'obstacle course' trails, mud springs, Makiling nursery", // [cite: 147, 150, 152, 153]
                    hikingSeasonDetails = "Not explicitly stated, dry season (Nov-Feb) generally preferred to avoid excessive leeches/mud.",
                    introduction = "Mt. Makiling is the perfect outing for those looking for the rush of a challenging hike, but are too busy for a two to three-day expedition[cite: 147]. The hike takes you through the rugged terrain and untamed rainforests of the famous Maria Makiling, all within a day and in close proximity to Manila[cite: 147, 148]. Mt. Makiling has plenty of surprises in store for hikers of every background[cite: 148].",
                    tagline = "Let's Hike Mt. Makiling",
                    mountainImageRef1 = "mt_makiling_1",
                    mountainImageRef2 = "mt_makiling_2",
                    mountainImageRef3 = "mt_makiling_3",

                    hasSteepSections = true, // "Rugged terrain", "obstacle courses" [cite: 147, 150]
                    notableWildlife = "Leeches (Limatik)", // [cite: 149]
                    isRocky = false, // Not primary feature, more "rugged terrain" [cite: 147]
                    isSlippery = true, // Implied by rainforest and mud springs [cite: 147, 153]
                    isEstablishedTrail = true // Popular route from UPLB [cite: 154]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, name = "Campsite Capacity", description = "Suitable for small to medium-sized groups"), // [cite: 154]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, name = "Amenities", description = "Basic facilities; bring your own camping gear"), // [cite: 155]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, name = "Permit/Fees - Entrance", description = "₱10 per person"), // [cite: 155]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, name = "Permit/Fees - Camping", description = "₱500 per group per night") // [cite: 155]
            ))
// No specific TrailEntity as it's "Out-and-back or loop" from UPLB [cite: 154]
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Registration", description = "Register at the Monitoring Station 1 upon arrival"), // [cite: 155]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Logbook Registration", description = "Mandatory for all visitors"), // [cite: 155]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Courtesy Call", description = "Inform local officials or barangay staff of your hike"), // [cite: 155]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Guide Requirement", description = "Highly recommended for safety and to support the local community"), // [cite: 155]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Trail Etiquette", description = "Stay on marked paths to prevent soil erosion"), // [cite: 157]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Waste Management", description = "Carry out all trash; no littering"), // [cite: 157]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Respect Nature", description = "Avoid disturbing wildlife and plants"), // [cite: 157]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Prohibited Items", description = "Liquor, dangerous drugs, firearms, and other harmful materials are strictly prohibited"), // [cite: 157]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Weather Awareness", description = "Check weather forecasts; avoid hiking during heavy rains"), // [cite: 158]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Proper Footwear", description = "Wear sturdy, non-slip shoes"), // [cite: 158]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Hydration", description = "Bring sufficient water; there are limited water sources along the trail"), // [cite: 159]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Sun Protection", description = "Use sunscreen and wear a hat") // [cite: 159]
            ))

// --- Mt. Sembrano Data ---
            val mtSembranoId = "mtsembrn001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtSembranoId,
                    mountainName = "Mt. Sembrano",
                    pictureReference = "mt_sembrano_main",
                    location = "Jalajala and Pililla, Rizal; Pakil, Laguna", // [cite: 162]
                    masl = 745, // [cite: 167]
                    difficultySummary = "Moderate", // Inferred from 3/9
                    difficultyText = "3/9", // [cite: 168]
                    hoursToSummit = "3-4 hours", // [cite: 168]
                    bestMonthsToHike = "Not explicitly stated, assume dry season (Nov-Feb).",
                    typeVolcano = "Volcano", // [cite: 160]
                    trekDurationDetails = "3-4 hours", // [cite: 168]
                    trailTypeDescription = "Out-and-back, passing through grasslands and forested sections", // [cite: 168]
                    sceneryDescription = "Panoramic views of Laguna de Bay, Talim Island, and surrounding mountains like Mt. Banahaw and Mt. Makiling", // [cite: 168]
                    viewsDescription = "Enchanting view of the largest freshwater lake in the country, the Laguna Lake", // [cite: 167]
                    wildlifeDescription = "Not specified, likely typical grassland/forest fauna.", // Inferred
                    featuresDescription = "Challenging trails to the summit, views of Laguna Lake", // [cite: 166, 167]
                    hikingSeasonDetails = "Not explicitly stated, dry season is generally advisable.",
                    introduction = "Mount Sembrano is a volcano located between Rizal and Laguna of the Calabarzon region in the Philippines[cite: 160, 161]. Mt. Sembrano is situated about 60 kilometres (37 mi) east by road from the capital city of Manila[cite: 161]. Mount Sembrano lies between the boundaries of the towns of Jalajala and Pililla in Rizal province and the town of Pakil in Laguna[cite: 162]. The mountain sits at the helm of Jalajala peninsula along the shore of Laguna de Bay and is surrounded by the lake on three sides[cite: 163].",
                    tagline = "Let's Hike Mt. Sembrano",
                    mountainImageRef1 = "mt_sembrano_1",
                    mountainImageRef2 = "mt_sembrano_2",
                    mountainImageRef3 = "mt_sembrano_3",

                    hasSteepSections = true, // "Challenging trails" [cite: 166]
                    notableWildlife = "", // Not specified
                    isRocky = false, // Not specified as primary feature
                    isSlippery = true, // "Wear sturdy, non-slip shoes" [cite: 173] (general advice)
                    isEstablishedTrail = true // Implied by popularity and defined trail type [cite: 165, 168]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, name = "Campsite Capacity", description = "Suitable for small to medium-sized groups"), // [cite: 169]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, name = "Amenities", description = "Basic facilities; bring your own camping gear"), // [cite: 169]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, name = "Permit/Fees - Registration", description = "₱60 per person"), // [cite: 170]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, name = "Permit/Fees - Guide (Day)", description = "₱600 per group for a day hike"), // [cite: 170]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, name = "Permit/Fees - Guide (Overnight)", description = "₱1,200 for an overnight hike") // [cite: 170]
            ))
// No specific TrailEntity as it's "Out-and-back" [cite: 168]
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Registration", description = "Register at the Barangay Malaya Hall upon arrival"), // [cite: 170]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Logbook Registration", description = "Mandatory for all visitors"), // [cite: 170]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Courtesy Call", description = "Inform local officials or barangay staff of your hike"), // [cite: 170]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Guide Requirement", description = "Highly recommended for safety and to support the local community"), // [cite: 170]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Trail Etiquette", description = "Stay on marked paths to prevent soil erosion"), // [cite: 172]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Waste Management", description = "Carry out all trash; no littering"), // [cite: 172]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Respect Nature", description = "Avoid disturbing wildlife and plants"), // [cite: 172]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Weather Awareness", description = "Check weather forecasts; avoid hiking during heavy rains"), // [cite: 173]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Proper Footwear", description = "Wear sturdy, non-slip shoes."), // [cite: 173]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Hydration", description = "Bring sufficient water; there are limited water sources along the trail."), // [cite: 174]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Sun Protection", description = "Use sunscreen and wear a hat.") // [cite: 175]
            ))

// --- Mt. Romelo Data ---
            val mtRomeloId = "mtrml001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtRomeloId,
                    mountainName = "Mt. Romelo",
                    pictureReference = "mt_romelo_main",
                    location = "Siniloan, Laguna", // [cite: 176]
                    masl = 300, // [cite: 177]
                    difficultySummary = "Easy", // Inferred from 2/9
                    difficultyText = "2/9", // [cite: 177]
                    hoursToSummit = "30-45 minutes to peak (4-6 hours round-trip for full experience)", // [cite: 182] for peak, [cite: 182] for round-trip.
                    bestMonthsToHike = "Dry season to avoid very muddy trails.", // Inferred from "very muddy during rainy season" [cite: 178]
                    typeVolcano = "Not specified, mountain", // Inferred
                    trekDurationDetails = "4-6 hours for a round-trip (including waterfalls)", // [cite: 182]
                    trailTypeDescription = "Out-and-back, passing through rainforest, fruit orchards, and river crossings", // [cite: 182]
                    sceneryDescription = "Features multiple waterfalls, including Buruwisan Falls, Lanzones Falls, Batya-Batya Falls, and Sampaloc Falls", // [cite: 182]
                    viewsDescription = "Waterfalls", // Main scenic element [cite: 182]
                    wildlifeDescription = "Leeches (\"limatik\") are common during the wet season", // [cite: 187]
                    featuresDescription = "Multiple waterfalls, muddy trail during rainy season", // [cite: 178, 182]
                    hikingSeasonDetails = "Dry season is better as the trail is very muddy during the rainy season[cite: 178].",
                    introduction = "Mt. Romelo is located in the heart of Siniloan, Laguna[cite: 176]. This mountain rises to 300 meters above sea level and has difficulty of 2/9[cite: 177]. This is a minor climb suitable for a day hike[cite: 177, 178]. The trail is very easy, however, it is very muddy during the rainy season[cite: 178]. Its jump off is in Brgy. Macatad, Upland Siniloan[cite: 179].",
                    tagline = "Let's Hike to Mt. Romelo & its Waterfalls",
                    mountainImageRef1 = "mt_romelo_1",
                    mountainImageRef2 = "mt_romelo_2",
                    mountainImageRef3 = "mt_romelo_3",

                    hasSteepSections = false, // "trail is very easy" [cite: 178]
                    notableWildlife = "Leeches (Limatik)", // [cite: 187]
                    isRocky = false, // Not specified as rocky, main issue is mud
                    isSlippery = true, // "very muddy during the rainy season" [cite: 178]
                    isEstablishedTrail = true // Implied by being a known hike with facilities
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, name = "Campsite Capacity", description = "Suitable for small groups; limited space available"), // [cite: 183]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, name = "Amenities", description = "Basic facilities; bring your own camping gear"), // [cite: 184]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, name = "Permit/Fees - Registration", description = "₱50 per person"), // [cite: 184]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, name = "Permit/Fees - Guide", description = "₱300-₱400 per group"), // [cite: 184]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, name = "Permit/Fees - Toilet/Shower", description = "₱20 per person") // [cite: 184]
            ))
// No specific TrailEntity as it's "Out-and-back" [cite: 182]
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Registration", description = "Register at the Barangay Macatad Hall upon arrival"), // [cite: 184]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Logbook Registration", description = "Mandatory for all visitors"), // [cite: 184]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Courtesy Call", description = "Inform local officials or barangay staff of your hike"), // [cite: 184]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Guide Requirement", description = "Required for safety and to support the local community"), // [cite: 184]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Trail Etiquette", description = "Stay on marked paths to prevent soil erosion"), // [cite: 186]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Waste Management", description = "Carry out all trash; no littering"), // [cite: 186]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Respect Nature", description = "Avoid disturbing wildlife and plants"), // [cite: 186]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Leech Presence", description = "Leeches (\"limatik\") are common during the wet season; use insect repellent and wear long sleeves and pants"), // [cite: 187]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Weather Awareness", description = "Check weather forecasts; avoid hiking during heavy rains"), // [cite: 188]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Proper Footwear", description = "Wear sturdy, non-slip shoes"), // [cite: 188]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Hydration", description = "Bring sufficient water; there are limited water sources along the trail"), // [cite: 189]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Sun Protection", description = "Use sunscreen and wear a hat") // [cite: 189]
            ))

// --- Mt. Tagapo Data ---
            val mtTagapoId = "mttgp001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtTagapoId,
                    mountainName = "Mt. Tagapo",
                    pictureReference = "mt_tagapo_main",
                    location = "Talim Island, Binangonan, Rizal (Laguna de Bay)", // [cite: 190, 192]
                    masl = 438, // [cite: 193]
                    difficultySummary = "Easy", // Inferred from 2/9
                    difficultyText = "2/9", // [cite: 199]
                    hoursToSummit = "1.5 to 2 hours", // [cite: 200]
                    bestMonthsToHike = "Dry season to avoid leeches and for clearer views.", // Inferred from leech presence [cite: 206]
                    typeVolcano = "Not specified, mountain on an island", // Inferred
                    trekDurationDetails = "1.5 to 2 hours", // [cite: 200]
                    trailTypeDescription = "Class 1-2, well-established paths through bamboo forests and grasslands", // [cite: 200]
                    sceneryDescription = "Panoramic views of Laguna de Bay, neighboring mountains, and the skylines of Makati and Ortigas", // [cite: 200]
                    viewsDescription = "Spectacular view of the Laguna Bay and its surrounding fishing villages", // [cite: 193]
                    wildlifeDescription = "Leeches (\"limatik\") are common during the wet season", // [cite: 206]
                    featuresDescription = "Located on an island, bamboo forests, views of Laguna de Bay and city skylines", // [cite: 191, 194, 200]
                    hikingSeasonDetails = "Dry season is preferable due to leech presence in wet season[cite: 206].",
                    introduction = "The highest peak of Talim Island at the heart of Laguna Bay is Mt. Tagapo[cite: 190]. Found at an island in the middle of a lake, the journey to Mt. Tagapo includes an hour of boat ride from Brgy. Janosa in Binangonan, Rizal to the island of Talim[cite: 191, 192]. Mt. Tagapo, most commonly known by its locals by its playful name; Mt. Susong Dalaga (Maiden's Breast Mountain) stands at 438 MASL and rewards its climbers with a spectacular view of the Laguna Bay and its surrounding fishing villages[cite: 193].",
                    tagline = "Let's Hike Mt. Tagapo",
                    mountainImageRef1 = "mt_tagapo_1",
                    mountainImageRef2 = "mt_tagapo_2",
                    mountainImageRef3 = "mt_tagapo_3",

                    hasSteepSections = false, // Class 1-2 trail [cite: 200]
                    notableWildlife = "Leeches (Limatik)", // [cite: 206]
                    isRocky = false, // Not specified as rocky
                    isSlippery = true, // General advice to wear non-slip shoes [cite: 207]
                    isEstablishedTrail = true, // "well-established paths" [cite: 200]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, name = "Campsite Capacity", description = "Overnight camping is permitted at designated sites near the summit, approximately 15 minutes away"), // [cite: 200]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, name = "Amenities", description = "Basic facilities; bring your own camping gear"), // [cite: 201]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, name = "Permit/Fees - Registration", description = "₱30 per person"), // [cite: 201]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, name = "Permit/Fees - Guide (Day)", description = "₱500 per group (day hike)"), // [cite: 202]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, name = "Permit/Fees - Guide (Overnight)", description = "₱1,000 per group (overnight)"), // [cite: 202]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, name = "Permit/Fees - Environmental", description = "₱40 per person") // [cite: 202]
            ))
// No specific TrailEntity as it's "Class 1-2, well-established paths" [cite: 200]
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Registration", description = "At the Barangay Janosa Hall upon arrival"), // [cite: 202]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Logbook Registration", description = "Mandatory for all visitors"), // [cite: 202]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Courtesy Call", description = "Inform local officials or barangay staff of your hike"), // [cite: 202]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Guide Requirement", description = "Required for safety and to support the local community"), // [cite: 202]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Last Boat Trip", description = "The last boat departs around 4:30-5:00 PM; plan your hike accordingly"), // [cite: 204]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Trail Etiquette", description = "Stay on marked paths to prevent soil erosion"), // [cite: 205]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Waste Management", description = "Carry out all trash; no littering"), // [cite: 205]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Respect Nature", description = "Avoid disturbing wildlife and plants"), // [cite: 205]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Leech Presence", description = "Leeches (\"limatik\") are common during the wet season; use insect repellent and wear long sleeves and pants"), // [cite: 206]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Weather Awareness", description = "Check weather forecasts; avoid hiking during heavy rains"), // [cite: 207]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Proper Footwear", description = "Wear sturdy, non-slip shoes"), // [cite: 207]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Hydration", description = "Bring sufficient water; there are limited water sources along the trail"), // [cite: 208]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Sun Protection", description = "Use sunscreen and wear a hat; the summit is exposed to direct sunlight") // [cite: 209]
            ))

// --- Mt. Arayat Data ---
            val mtArayatId = "mtaryt001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtArayatId,
                    mountainName = "Mt. Arayat",
                    pictureReference = "mt_arayat_main",
                    location = "Arayat, Pampanga (Jump-off: Brgy. San Juan Baño, Magalang, Pampanga)", // [cite: 210, 214]
                    masl = 1026, // [cite: 211]
                    difficultySummary = "Moderate to Challenging", // Inferred from 4/10 (closer to 4/9 in user's scale) and Class 3-4
                    difficultyText = "4/10", // [cite: 211] (Note: PDF uses /10, user format uses /9. Will keep PDF's rating)
                    hoursToSummit = "3-4 hours", // [cite: 211]
                    bestMonthsToHike = "Not explicitly stated, assume dry season (Nov-Feb).",
                    typeVolcano = "Extinct volcano", // [cite: 210]
                    trekDurationDetails = "3-4 hours", // [cite: 211]
                    trailTypeDescription = "Class 3-4; features rocky terrains and thorny vegetation", // [cite: 212]
                    sceneryDescription = "Offers panoramic views of Central Luzon, including Pampanga River, Zambales and Bataan mountains, and the Sierra Madre range", // [cite: 212]
                    viewsDescription = "Panoramic views of Central Luzon, Pampanga River, Zambales and Bataan mountains, Sierra Madre range", // [cite: 212]
                    wildlifeDescription = "Tales of fairies and mythical creatures; actual wildlife not detailed but likely forest species.", // [cite: 210]
                    featuresDescription = "Dominating presence in flat plains, forests, rocky terrains, thorny vegetation", // [cite: 210, 212]
                    hikingSeasonDetails = "Not specified, dry season generally advisable for clearer views and trail conditions.",
                    introduction = "Mt. Arayat is an extinct volcano located in the island of Luzon with no recorded historical eruption[cite: 210]. Originally famous for the tales of fairies and other mythical creatures that supposedly inhabit its forests, Mt. Arayat is now considered as one of the most popular hiking destinations in the Central Luzon region because of its dominating presence in the mostly flat plains in the area[cite: 210].",
                    tagline = "Let's Hike Mt. Arayat",
                    mountainImageRef1 = "mt_arayat_1",
                    mountainImageRef2 = "mt_arayat_2",
                    mountainImageRef3 = "mt_arayat_3",

                    hasSteepSections = true, // Class 3-4, rocky terrains [cite: 212]
                    notableWildlife = "Mythical Creatures (Folklore)", // [cite: 210]
                    isRocky = true, // [cite: 212]
                    isSlippery = true, // Rocky terrains can be slippery, general advice for footwear [cite: 218]
                    isEstablishedTrail = true // Implied by popularity and guide requirement [cite: 210, 214]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, name = "Campsite Capacity", description = "Overnight camping is permitted; however, facilities are basic"), // [cite: 213]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, name = "Amenities", description = "Limited; no water source along the trail or at the summit"), // [cite: 213]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, name = "Permit/Fees - Environmental", description = "₱30 per person"), // [cite: 213]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, name = "Permit/Fees - Guide (South Peak)", description = "₱700 for up to 5 people"), // [cite: 214]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, name = "Permit/Fees - Guide (Pinnacle)", description = "₱1,500 for up to 5 people"), // [cite: 214]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, name = "Permit/Fees - Guide (North Peak)", description = "₱1,750 for up to 5 people") // [cite: 214]
            ))
// No specific TrailEntity named beyond peak destinations.
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Registration", description = "At the jump-off point in Barangay San Juan Baño, Magalang, Pampanga"), // [cite: 214]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Logbook Registration", description = "Mandatory for all hikers"), // [cite: 214]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Courtesy Call", description = "Inform local officials or barangay staff of your hike"), // [cite: 214]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Guide Requirement", description = "Required for safety and to support the local community"), // [cite: 214]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "No Water Source", description = "Bring sufficient water; there are no water sources along the trail or at the summit"), // [cite: 216]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Trail Etiquette", description = "Stay on marked paths to prevent soil erosion"), // [cite: 217]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Waste Management", description = "Carry out all trash; no littering"), // [cite: 217]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Respect Nature", description = "Avoid disturbing wildlife and plants"), // [cite: 217]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Weather Awareness", description = "Check weather forecasts; avoid hiking during heavy rains"), // [cite: 218]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Proper Footwear", description = "Wear sturdy, non-slip shoes suitable for rocky terrains"), // [cite: 218]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Hydration", description = "Bring at least 2 liters of water for day hikes; 3 liters for overnight hikes"), // [cite: 219]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Sun Protection", description = "Use sunscreen, wear a hat, and dress appropriately for sun exposure") // [cite: 219]
            ))

// --- Mt. Tapulao (High Peak) Data ---
            val mtTapulaoId = "mttpl001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtTapulaoId,
                    mountainName = "Mt. Tapulao (High Peak)",
                    pictureReference = "mt_tapulao_main",
                    location = "Palauig, Zambales (Jump-off: Dampay, Brgy. Salaza)", // [cite: 220, 236]
                    masl = 2037, // [cite: 221] (PDF also says 2044+ MASL[cite: 230], using first mentioned)
                    difficultySummary = "Very Challenging", // Inferred from 8/10
                    difficultyText = "8/10", // [cite: 230] (Note: PDF uses /10)
                    hoursToSummit = "7-8 hours to ascend; descent is about to 4-5 hours", // [cite: 230]
                    bestMonthsToHike = "Not explicitly stated, cooler months (Nov-Feb) for less heat stress.",
                    typeVolcano = "Not specified, mountain (tallest in Central Luzon)", // [cite: 220]
                    trekDurationDetails = "7-8 hours to ascend; descent is about 4-5 hours", // [cite: 230]
                    trailTypeDescription = "Class 3-4; the trail follows an old mining road, transitioning through various ecosystems including grasslands, pine forests, and mossy forests.", // [cite: 231]
                    sceneryDescription = "Offers panoramic views of the Zambales mountains, Lingayen Gulf, and the South China Sea. The summit provides a 360-degree vista.", // [cite: 232]
                    viewsDescription = "Breathtaking views, panoramic views of Zambales mountains, Lingayen Gulf, South China Sea, 360-degree summit vista", // [cite: 222, 232]
                    wildlifeDescription = "Abundance of pine trees, mossy forest species. Specific fauna not detailed.", // [cite: 226, 228]
                    featuresDescription = "Tallest mountain in Central Luzon, beautiful ecosystem, endless and challenging rocky trail, pine trees, mossy forest", // [cite: 220, 222, 227, 228]
                    hikingSeasonDetails = "Temperature can be cooler than Baguio, especially at night[cite: 224]. Dry season likely best.",
                    introduction = "Mt. Tapulao is located in the municipality of Palauig, in the province of Zambales[cite: 220]. It is the tallest mountain in the Central Luzon region[cite: 220]. Mt. Tapulao has an elevation of 2,037 meters above sea level or 6,683 feet[cite: 221]. It also offers a beautiful ecosystem and breathtaking views[cite: 222].",
                    tagline = "Let's Hike Mt. Tapulao",
                    mountainImageRef1 = "mt_tapulao_1",
                    mountainImageRef2 = "mt_tapulao_2",
                    mountainImageRef3 = "mt_tapulao_3",

                    hasSteepSections = true, // "challenging rocky trail"[cite: 227], Class 3-4 [cite: 231]
                    notableWildlife = "Pine Trees", // [cite: 226]
                    isRocky = true, // "challenging rocky trail" [cite: 227]
                    isSlippery = true, // Rocky terrains can be slippery, general footwear advice [cite: 240]
                    isEstablishedTrail = true // Follows old mining road [cite: 231]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, name = "Campsite Capacity", description = "Suitable for large groups; however, it's advisable to coordinate with local authorities for group sizes"), // [cite: 234]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, name = "Amenities", description = "Basic facilities; no potable water sources along the trail or at the campsite"), // [cite: 235]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, name = "Permit/Fees - Registration", description = "₱30 per person"), // [cite: 235]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, name = "Permit/Fees - Guide", description = "₱700 per guide (1 guide per 5 hikers)"), // [cite: 235]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, name = "Permit/Fees - Porter (Optional)", description = "₱700") // [cite: 235]
            ))
// No specific TrailEntity named beyond the main trail characteristics.
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Registration", description = "Mandatory at the Barangay Hall in Dampay, Brgy. Salaza, Palauig, Zambales"), // [cite: 236]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Logbook Registration", description = "Required for all hikers"), // [cite: 236]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Courtesy Call", description = "Inform local officials or barangay staff of your hike"), // [cite: 236]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Guide Requirement", description = "Compulsory for safety and to support the local community"), // [cite: 236]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "No Water Source", description = "Bring sufficient water; there are no potable water sources along the trail or at the campsite"), // [cite: 238]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Trail Etiquette", description = "Stay on marked paths to prevent soil erosion"), // [cite: 239]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Waste Management", description = "Carry out all trash; no littering"), // [cite: 239]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Respect Nature", description = "Avoid disturbing wildlife and plants"), // [cite: 239]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Weather Awareness", description = "Check weather forecasts; avoid hiking during heavy rains"), // [cite: 240]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Proper Footwear", description = "Wear sturdy, non-slip shoes suitable for rocky terrains"), // [cite: 240]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Hydration", description = "Bring at least 2 liters of water for day hikes; 3 liters for overnight hikes"), // [cite: 241]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Sun Protection", description = "Use sunscreen, wear a hat, and dress appropriately for sun exposure") // [cite: 241]
            ))

// --- Mt. Damas Data ---
            val mtDamasId = "mtdms001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtDamasId,
                    mountainName = "Mt. Damas",
                    pictureReference = "mt_damas_main",
                    location = "Border of Mayantoc, Camiling, and San Clemente, Tarlac (Jump-off: Brgy. Papaac, Camiling)", // [cite: 242, 246]
                    masl = 685, // [cite: 244]
                    difficultySummary = "Challenging", // Inferred from 6/9
                    difficultyText = "6/9", // [cite: 244]
                    hoursToSummit = "4-6 hours", // [cite: 244]
                    bestMonthsToHike = "Not explicitly stated, assume dry season (Nov-Feb).",
                    typeVolcano = "Not specified, mountain", // Inferred
                    trekDurationDetails = "4-6 hours", // [cite: 244]
                    trailTypeDescription = "Trail class 1-4, involving steep ascents, river crossings, and some unmarked paths", // [cite: 244]
                    sceneryDescription = "Offers panoramic views of Tarlac and Pangasinan plains, Mt. Arayat, and a captivating sunrise amidst morning clouds", // [cite: 244]
                    viewsDescription = "Panoramic views of Tarlac and Pangasinan plains, Mt. Arayat, sunrise amidst morning clouds", // [cite: 244]
                    wildlifeDescription = "Not specified.", // Inferred
                    featuresDescription = "Steep ascents, river crossings, unmarked paths, views of plains and Mt. Arayat", // [cite: 244]
                    hikingSeasonDetails = "Not specified, dry season is generally advisable for river crossings and unmarked paths.",
                    introduction = "Mt. Damas is situated near the border of 3 Tarlac towns: Mayantoc to the southeast, Camiling to the north and San Clemente to the west[cite: 242]. The trek starts off at the Dueg resettlement - a relocation site established after Mt. Pinatubo eruption[cite: 243]. Although there were original settlers before the eruption, almost half of the population in the community were resettled from the various towns that were hardly hit by the eruption and the lahar flow[cite: 243].",
                    tagline = "Let's Hike Mt. Damas",
                    mountainImageRef1 = "mt_damas_1",
                    mountainImageRef2 = "mt_damas_2",
                    mountainImageRef3 = "mt_damas_3",

                    hasSteepSections = true, // "steep ascents" [cite: 244]
                    notableWildlife = "", // Not specified
                    isRocky = true, // Assumed with trail class up to 4 and general mountain terrain
                    isSlippery = true, // River crossings and steep ascents imply slippery conditions, footwear advice [cite: 251]
                    isEstablishedTrail = false // "some unmarked paths" [cite: 244]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, name = "Campsite Capacity", description = "Suitable for small to medium-sized groups"), // [cite: 245]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, name = "Amenities", description = "Basic facilities; no potable water sources along the trail or at the campsite"), // [cite: 245]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, name = "Permit/Fees - Registration", description = "₱10 per person at the barangay hall"), // [cite: 245]
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, name = "Permit/Fees - Guide", description = "₱500 per guide for a group not exceeding 10 persons") // [cite: 245]
            ))
// No specific TrailEntity named.
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Registration", description = "Mandatory at the Barangay Hall in Brgy. Papaac, Camiling, Tarlac"), // [cite: 246]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Logbook Registration", description = "Required for all hikers"), // [cite: 247]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Courtesy Call", description = "Inform local officials or barangay staff of your hike"), // [cite: 247]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Guide Requirement", description = "Compulsory for safety and to support the local community"), // [cite: 247]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "No Water Source", description = "Bring sufficient water; there are no potable water sources along the trail or at the campsite"), // [cite: 249]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Trail Etiquette", description = "Stay on marked paths to prevent soil erosion"), // [cite: 250]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Waste Management", description = "Carry out all trash; no littering"), // [cite: 250]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Respect Nature", description = "Avoid disturbing wildlife and plants"), // [cite: 250]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Weather Awareness", description = "Check weather forecasts; avoid hiking during heavy rains"), // [cite: 251]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Proper Footwear", description = "Wear sturdy, non-slip shoes suitable for rocky terrains"), // [cite: 251]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Hydration", description = "Bring at least 2 liters of water for day hikes; 3 liters for overnight hikes"), // [cite: 252]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Sun Protection", description = "Use sunscreen, wear a hat, and dress appropriately for sun exposure") // [cite: 252]
            ))

            // --- Mt. Balingkilat Data ---
            val mtBalingkilatId = "mtblngklt001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtBalingkilatId,
                    mountainName = "Mt. Balingkilat",
                    pictureReference = "mt_balingkilat_main",
                    location = "San Antonio and Subic, Zambales (Jump-off: Sitio Cawag)", //
                    masl = 1100, //
                    difficultySummary = "Challenging", // Inferred from 6/10
                    difficultyText = "6/10", // (Note: PDF uses /10)
                    hoursToSummit = "4-6 hours", //
                    bestMonthsToHike = "Not explicitly stated, dry season (Nov-Feb) for less sun exposure/heat.", // Dim-trekking strategy suggests avoiding intense sun
                    typeVolcano = "Not specified, mountain (Mountain of Thunder)", // Inferred
                    trekDurationDetails = "4-6 hours", //
                    trailTypeDescription = "Out-and-back trail with rocky slopes, open ridges, and some scrambling sections", //
                    sceneryDescription = "Offers panoramic views of Zambales coves, neighboring mountains like Mt. Cinco Picos, and the ocean", //
                    viewsDescription = "Breathtaking scene of the South China Sea and its coastline, coves like Anawangin, Talesayen, Nagsasa and Silanguin, other mountains and scenic ridges", //
                    wildlifeDescription = "Not specified.", //
                    featuresDescription = "Rocky, grassy, windy features, little tree cover, steep parts, views of coves and South China Sea", //
                    hikingSeasonDetails = "Intense exposure to the sun; dim-trekking (early morning or late afternoon) is the best strategy.",
                    introduction = "One of the highest peaks in the Zambales Coastal Mountains and Coves area, Mt. Balingkilat (known as the \"Mountain of Thunder\" in the native Aeta language) is an emerging hiking hotspot within San Antonio and Subic[cite: 253]. It was also once known as \"Pointed peak\" used by US Navy servicemen who used to be stationed in Subic Naval Base[cite: 254]. Mt. Balingkilat offers the same rocky, grassy and windy features like other western mountains of Luzon such as Mt. Cinco Picos[cite: 255].",
                    tagline = "Let's Hike Mt. Balingkilat",
                    mountainImageRef1 = "mt_balingkilat_1",
                    mountainImageRef2 = "mt_balingkilat_2",
                    mountainImageRef3 = "mt_balingkilat_3",

                    hasSteepSections = true, // "steep parts in the final legs", "scrambling sections"
                    notableWildlife = "", // Not specified
                    isRocky = true, //
                    isSlippery = true, // Rocky slopes and scrambling can be slippery
                    isEstablishedTrail = true // Implied by popularity and guide requirement
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, name = "Campsite Capacity", description = "Suitable for small to medium-sized groups"), //
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, name = "Amenities", description = "Basic facilities; no potable water sources along the trail or at the campsite [cite: 263]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, name = "Permit/Fees - Registration", description = "₱60 per person"), //
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, name = "Permit/Fees - Guide", description = "₱700 per guide") //
            ))
// No specific TrailEntity named beyond "Out-and-back trail".
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Registration", description = "Mandatory at the Subic Public Order and Safety Office"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Logbook Registration", description = "Required for all hikers"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Courtesy Call", description = "Inform local officials or barangay staff of your hike"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Guide Requirement", description = "Compulsory for safety and to support the local community"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "No Water Source", description = "Bring sufficient water; there are no potable water sources along the trail or at the campsite [cite: 265]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Trail Etiquette", description = "Stay on marked paths to prevent soil erosion"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Waste Management", description = "Carry out all trash; no littering [cite: 266]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Respect Nature", description = "Avoid disturbing wildlife and plants [cite: 267]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Weather Awareness", description = "Check weather forecasts; avoid hiking during heavy rains [cite: 268]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Proper Footwear", description = "Wear sturdy, non-slip shoes suitable for rocky terrains"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Hydration", description = "Bring at least 2.5 liters of water for day hikes; 3 liters for overnight hikes [cite: 269]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Sun Protection", description = "Use sunscreen, wear a hat, and dress appropriately for sun exposure") //
            ))

// --- Mt. Cinco Picos Data ---
            val mtCincoPicosId = "mtcncpc001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtCincoPicosId,
                    mountainName = "Mt. Cinco Picos",
                    pictureReference = "mt_cinco_picos_main",
                    location = "Subic, Zambales (Jump-off: Sitio Cawag)", //
                    masl = 1100, // (PDF says 881+ MASL in one spot, but 1100+ in detail section - using 1100+)
                    difficultySummary = "Moderate to Challenging", // Inferred from 5/10
                    difficultyText = "5/10", // (Note: PDF uses /10)
                    hoursToSummit = "4-6 hours", //
                    bestMonthsToHike = "Not explicitly stated, dry season (Nov-Feb) for less heat and better trail conditions.",
                    typeVolcano = "Not specified, mountain (Tatlong Tirad to Aetas)", // Inferred
                    trekDurationDetails = "4-6 hours", //
                    trailTypeDescription = "Out-and-back with exposed ridges and river crossings", //
                    sceneryDescription = "Offers views of Subic Bay, Silanguin Cove, and the rugged Zambales mountain range", //
                    viewsDescription = "Views of Subic Bay, Silanguin Cove, Zambales mountain range", //
                    wildlifeDescription = "Not specified.", //
                    featuresDescription = "Exposed ridges, river crossings, views of coves and Subic Bay", //
                    hikingSeasonDetails = "Not specified, dry season advisable for river crossings and exposed ridges.",
                    introduction = "Cinco Picos, also known as Tatlong Tirad to the Aetas, is a popular mountain located in Subic, Zambales[cite: 270]. The mountain was originally used as a training ground by the U.S. troops, who were stationed in what used to be the Subic Naval Base[cite: 271]. Today, it is one of the most sought-after hiking destinations in the region[cite: 272].",
                    tagline = "Let's Hike Mt. Cinco Picos",
                    mountainImageRef1 = "mt_cinco_picos_1",
                    mountainImageRef2 = "mt_cinco_picos_2",
                    mountainImageRef3 = "mt_cinco_picos_3",

                    hasSteepSections = true, // Implied by "rugged Zambales mountain range" and exposed ridges
                    notableWildlife = "", // Not specified
                    isRocky = true, // Implied by "rugged Zambales mountain range"
                    isSlippery = true, // River crossings and rocky terrain can be slippery
                    isEstablishedTrail = true // Implied by popularity and guide requirement
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, name = "Campsite Capacity", description = "Suitable for small to medium-sized groups"), //
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, name = "Amenities", description = "Basic facilities; no potable water sources along the trail or at the campsite [cite: 273]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, name = "Permit/Fees - Registration", description = "₱60 per person"), //
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, name = "Permit/Fees - Guide", description = "₱700-₱1,000 per guide (depending on group size)") //
            ))
// No specific TrailEntity named beyond "Out-and-back".
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Registration", description = "Mandatory at the Subic Police Station"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Logbook Registration", description = "Required for all hikers"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Courtesy Call", description = "Inform local officials or barangay staff of your hike"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Guide Requirement", description = "Compulsory for safety and to support the local community"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "No Water Source", description = "Bring sufficient water; there are no potable water sources along the trail or at the campsite [cite: 275]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Trail Etiquette", description = "Stay on marked paths to prevent soil erosion"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Waste Management", description = "Carry out all trash; no littering [cite: 276]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Respect Nature", description = "Avoid disturbing wildlife and plants"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Weather Awareness", description = "Check weather forecasts; avoid hiking during heavy rains [cite: 277]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Proper Footwear", description = "Wear sturdy, non-slip shoes suitable for rocky terrains"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Hydration", description = "Bring at least 2.5 liters of water for day hikes; 3 liters for overnight hikes. [cite: 278]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Sun Protection", description = "Use sunscreen, wear a hat, and dress appropriately for sun exposure. [cite: 279]")
            ))

// --- Mt. Cristobal Data ---
            val mtCristobalId = "mtcrstbl001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtCristobalId,
                    mountainName = "Mt. Cristobal",
                    pictureReference = "mt_cristobal_main",
                    location = "Dolores, Quezon (Jump-off: Brgy. Sta. Lucia)", //
                    masl = 1470, //
                    difficultySummary = "Moderate to Challenging", // Inferred from 4/9 and "Devil's Mountain" reputation
                    difficultyText = "4/9", //
                    hoursToSummit = "4-5 hours", //
                    bestMonthsToHike = "Not explicitly stated, dry season (Nov-Feb) generally preferred for river crossings and dense forests.",
                    typeVolcano = "Not specified, likely dormant volcano (often paired with Banahaw)", // Inferred
                    trekDurationDetails = "4-5 hours", //
                    trailTypeDescription = "Class 3 trail with steep ascents, dense forests, and occasional river crossings", //
                    sceneryDescription = "Dense mossy forests, ferns, and orchids; panoramic views of nearby mountains and Tayabas Bay [cite: 284]",
                    viewsDescription = "Panoramic views of nearby mountains and Tayabas Bay [cite: 284]",
                    wildlifeDescription = "Mosses, ferns, orchids. Stories of ghosts and extraterrestrial events[cite: 280, 282].",
                    featuresDescription = "Known as 'Devil's Mountain', dense forests, mosses, ferns, orchids, cool ambiance and weather[cite: 280, 281, 282, 283].",
                    hikingSeasonDetails = "Not specified, cool weather is a feature[cite: 283]. Dry season is generally advisable.",
                    introduction = "This is one of the famous mountains in Luzon because of the countless stories, rumors, and legends about \"ghosts\" and other extraterrestrial events[cite: 280]. As a result, the title \"devil's mountain\" was given to the pride of Quezon[cite: 281]. Aside from those, Mt. Cristobal also hides amazing terrains, surrounded by dense forest areas[cite: 282].",
                    tagline = "Let's Hike Mt. Cristobal (Devil's Mountain)",
                    mountainImageRef1 = "mt_cristobal_1",
                    mountainImageRef2 = "mt_cristobal_2",
                    mountainImageRef3 = "mt_cristobal_3",

                    hasSteepSections = true, // "steep ascents"
                    notableWildlife = "Ferns, Orchids", // Flora
                    isRocky = true, // Implied by Class 3 trail and steep ascents
                    isSlippery = true, // Dense forests, river crossings imply slippery conditions
                    isEstablishedTrail = true // Implied by its fame and defined trail class
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, name = "Campsite Capacity", description = "Suitable for small to medium-sized groups"), //
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, name = "Amenities", description = "Basic facilities; no potable water sources along the trail or at the campsite [cite: 285]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, name = "Permit/Fees", description = "No formal registration fees; however, donations are encouraged to support the local community [cite: 286]")
            ))
// No specific TrailEntity named beyond "Class 3 trail".
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Registration", description = "Not mandatory; however, it's advisable to inform local officials or barangay staff of your hike [cite: 287]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Logbook Registration", description = "Not required"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Courtesy Call", description = "Recommended to notify local authorities of your hiking plans"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Guide Requirement", description = "Not compulsory but advisable, especially for first-time hikers"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "No Water Source", description = "Bring sufficient water; there are no potable water sources along the trail or at the campsite"), // Based on [cite: 285]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Trail Etiquette", description = "Stay on marked paths to prevent soil erosion"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Waste Management", description = "Carry out all trash; no littering [cite: 291]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Respect Nature", description = "Avoid disturbing wildlife and plants"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Weather Awareness", description = "Check weather forecasts; avoid hiking during heavy rains [cite: 292]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Proper Footwear", description = "Wear sturdy, non-slip shoes suitable for rocky terrains"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Hydration", description = "Bring at least 2.5 liters of water for day hikes; 3 liters for overnight hikes [cite: 293]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Sun Protection", description = "Use sunscreen, wear a hat, and dress appropriately for sun exposure") //
            ))

// --- Mt. Malepunyo (Malipunyo) Data ---
            val mtMalepunyoId = "mtmlpny001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtMalepunyoId,
                    mountainName = "Mt. Malepunyo", // Also referred to as Malipunyo
                    pictureReference = "mt_malepunyo_main",
                    location = "Lipa city, Batangas (Jump-off: Brgy. Talisay)", //
                    masl = 1003, // PDF states 1002 MASL [cite: 294] and 1003+ MASL in details. Using 1003.
                    difficultySummary = "Moderate", // Inferred from 3/9
                    difficultyText = "3/9", //
                    hoursToSummit = "3-4 hours", //
                    bestMonthsToHike = "Not explicitly stated, dry season (Nov-Feb) is generally preferred.",
                    typeVolcano = "Not specified, part of Malepunyo Mountain Range", // Inferred
                    trekDurationDetails = "3-4 hours", //
                    trailTypeDescription = "Class 3 trail with steep ascents, dense forests, and occasional river crossings", //
                    sceneryDescription = "Verdant forests, mossy areas, and panoramic views of the surrounding provinces [cite: 297]",
                    viewsDescription = "Panoramic views of the surrounding provinces [cite: 297]",
                    wildlifeDescription = "Not specified, likely forest species.", //
                    featuresDescription = "Tallest in Batangas[cite: 294], part of Malepunyo Range, traverse trail possible with Mt. Manabu[cite: 294], verdant forests, mossy areas.",
                    hikingSeasonDetails = "Not specified, dry season generally advisable for river crossings.",
                    introduction = "The Malepunyo Mountain Range comprises two major mountains: The tallest in Batangas, Mt. Malipunyo, standing at a massive 1002MASL which is situated at Lipa city, Batangas and Mt. Manabu (755MASL) at Sto. Tomas, Batangas[cite: 294].",
                    tagline = "Let's Hike Mt. Malepunyo",
                    mountainImageRef1 = "mt_malepunyo_1",
                    mountainImageRef2 = "mt_malepunyo_2",
                    mountainImageRef3 = "mt_malepunyo_3",

                    hasSteepSections = true, // "steep ascents"
                    notableWildlife = "", // Not specified
                    isRocky = true, // Implied by Class 3 trail
                    isSlippery = true, // Dense forests, river crossings imply slippery conditions
                    isEstablishedTrail = true // Implied by defined trail class and traverse options
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, name = "Campsite Capacity - Peak 3", description = "Peak 3 can accommodate up to 50 people [cite: 298]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, name = "Campsite Capacity - Peak 2", description = "Peak 2 can accommodate 5-10 people [cite: 298]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, name = "Campsite Capacity - Peak 1", description = "Peak 1 is covered with tall trees and shrubs, offering limited space [cite: 298]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, name = "Amenities", description = "Basic facilities; no potable water sources along the trail or at the campsite [cite: 299]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, name = "Permit/Fees", description = "Registration at the Barangay Hall is required; fees are minimal, typically around ₱10-₱20. [cite: 300]")
            ))
// No specific TrailEntity named beyond "Class 3 trail".
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Registration", description = "Mandatory at the Barangay Hall before starting the hike"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Logbook Registration", description = "Required upon arrival at the Barangay Hall"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Courtesy Call", description = "Recommended to notify local authorities of your hiking plans"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Guide Requirement", description = "Not mandatory but advisable, especially for first-time hikers"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "No Water Source", description = "Bring sufficient water; there are no potable water sources along the trail or at the campsite"), // Based on [cite: 299]
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Trail Etiquette", description = "Stay on marked paths to prevent soil erosion"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Waste Management", description = "Carry out all trash; no littering [cite: 303]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Respect Nature", description = "Avoid disturbing wildlife and plants"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Weather Awareness", description = "Check weather forecasts; avoid hiking during heavy rains [cite: 304]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Proper Footwear", description = "Wear sturdy, non-slip shoes suitable for rocky terrains"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Hydration", description = "Bring at least 2.5 liters of water for day hikes; 3 liters for overnight hikes [cite: 305]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Sun Protection", description = "Use sunscreen, wear a hat, and dress appropriately for sun exposure") //
            ))

// --- Mt. Marami Data ---
            val mtMaramiId = "mtmrm001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtMaramiId,
                    mountainName = "Mt. Marami",
                    pictureReference = "mt_marami_main",
                    location = "Maragondon, Cavite (Jump-off: Brgy. Ramirez, Magallanes or Brgy. Talispungo, Maragondon)", //
                    masl = 405, //
                    difficultySummary = "Beginner-friendly with moderate length",
                    difficultyText = "4/9 (Beginner-friendly with moderate length and exposure)", //
                    hoursToSummit = "4-6 hours (round trip, depends on pace and weather)", //
                    bestMonthsToHike = "Dry season to avoid muddy trails and impassable river crossings.", // Inferred from "muddy during rainy season", "River crossings may be impassable"
                    typeVolcano = "Rocky mountain", //
                    trekDurationDetails = "4-6 hours (round trip, depends on pace and weather)", //
                    trailTypeDescription = "Out-and-back with multiple route options", //
                    sceneryDescription = "Open trails, grassy slopes, forest paths, river crossings, panoramic summit views", //
                    viewsDescription = "Sweeping views of Cavite and neighboring provinces from Silyang Bato", //
                    wildlifeDescription = "Not specified, likely typical grassland/forest fauna.", //
                    featuresDescription = "Iconic Silyang Bato (large rock structure resembling a chair), 'Labyrinth of Trails', multiple branching routes, open grasslands, forest trails, river crossings [cite: 308, 309]",
                    hikingSeasonDetails = "Trails can be muddy and slippery, especially during the rainy season. River crossings may be impassable after heavy rains[cite: 325, 326].",
                    introduction = "Mt. Marami is a rocky mountain located in Maragondon, Cavite, and is part of the Maragondon mountain range[cite: 307]. Often compared to Mt. Pico de Loro due to its proximity and rock formations, Mt. Marami is known for its iconic Silyang Bato a large rock structure resembling a chair-perched at the summit with sweeping views of Cavite and neighboring provinces[cite: 308]. Dubbed the \"Labyrinth of Trails\", the mountain features multiple branching routes, open grasslands, forest trails, and river crossings[cite: 309].",
                    tagline = "Let's Hike Mt. Marami (Silyang Bato)",
                    mountainImageRef1 = "mt_marami_1",
                    mountainImageRef2 = "mt_marami_2",
                    mountainImageRef3 = "mt_marami_3",

                    hasSteepSections = true, // "uphill assaults", "Summit has steep drop-offs"
                    notableWildlife = "", // Not specified
                    isRocky = true, // "rocky mountain", "jagged rocks"
                    isSlippery = true, // "muddy and slippery, especially during the rainy season [cite: 325]"
                    isEstablishedTrail = false // "Labyrinth of Trails", "poorly marked", "hikers have gotten lost"
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, description = "Camping is possible at several points along the trail, particularly in open grassy areas before the final summit ascent. A common campsite is near the Silyang Bato area, offering great views at sunrise. However, conditions are basic, and water sources are unreliable[cite: 313, 314]."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, name = "Campsite Capacity", description = "5-10 tents (scattered)"), //
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, name = "Amenities", description = "None (wilderness camping)"), //
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, name = "Permit/Fees", description = "Entrance fee is ₱65 per person. Guide fee is approximately ₱500 for 3-4 hikers (negotiable)[cite: 316].") // Combined fees from guidelines
            ))
// No specific TrailEntity named beyond "Out-and-back with multiple route options".
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Registration", description = "Hikers must register at the jump-off point in Brgy. Ramirez, Magallanes, Cavite or Brgy. Talispungo, Maragondon. Prior coordination with local guides is advised, especially due to the confusing trail system."), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Logbook Registration", description = "Register at the barangay hall; entrance fee is ₱65 per person."), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Courtesy Call", description = "Optional, but hikers are encouraged to inform barangay officials or locals of their itinerary."), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Guide Requirement", description = "Strongly recommended. The trail system is poorly marked, and hikers have gotten lost before. Guide fee is approximately ₱500 for 3-4 hikers (negotiable)[cite: 315, 316]."),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Environmental Concern", description = "Stick to established trails to avoid damaging surrounding flora."), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Environmental Concern", description = "Bring all trash down and use designated trash bags. [cite: 319]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Environmental Concern", description = "Avoid loud noises and blasting music to preserve the mountain's serene environment. [cite: 320]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Safety Tip", description = "Summit has steep drop-offs and jagged rocks—be cautious when taking photos at Silyang Bato. [cite: 321]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Safety Tip", description = "Bring 2-3L of water and sufficient snacks; there are limited refill areas. [cite: 322]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Safety Tip", description = "Cell signal is weak in most parts but available near the summit. [cite: 323]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Safety Tip", description = "Wear sturdy footwear, especially when the trail is muddy or waterlogged. [cite: 324]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Safety Tip", description = "Trails can be muddy and slippery, especially during the rainy season. [cite: 325]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Safety Tip", description = "River crossings may be impassable after heavy rains always assess conditions before proceeding. [cite: 326]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Safety Tip", description = "Wear sun protection and bring trekking poles to help with river crossings and uphill assaults. [cite: 327]")
            ))

// --- Mt. Lubog Data ---
            val mtLubogId = "mtlbg001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtLubogId,
                    mountainName = "Mt. Lubog",
                    pictureReference = "mt_lubog_main",
                    location = "Rodriguez, Rizal (near Bulacan border)", //
                    masl = 955, //
                    difficultySummary = "Beginner-friendly with some challenges",
                    difficultyText = "4/9 (Beginner-friendly but with some challenging sections and rough access road)", //
                    hoursToSummit = "3-4 hours (including sidetrip to Lubog Cave)", //
                    bestMonthsToHike = "Dry season to avoid very slippery access roads and trails.", // Inferred from "rough, slippery routes"
                    typeVolcano = "Limestone-capped mountain", //
                    trekDurationDetails = "3-4 hours (including sidetrip to Lubog Cave)", //
                    trailTypeDescription = "Out-and-back", //
                    sceneryDescription = "Tropical rainforest, limestone formations, panoramic mountain views", //
                    viewsDescription = "Panoramic views of the Sierra Madre mountains [cite: 335]",
                    wildlifeDescription = "Not specified, likely forest species.", //
                    featuresDescription = "Forested trails, scenic rock formations at the summit, Lubog Cave sidetrip, challenging habal-habal access road [cite: 330, 333, 334]",
                    hikingSeasonDetails = "Access road can be rough and slippery; habal-habal ride is 1-3 hours[cite: 334, 343].",
                    introduction = "Mt. Lubog is a limestone-capped mountain located in Rodriguez, Rizal, near the border of Bulacan[cite: 329]. It is part of the Sierra Madre mountain range and is surrounded by peaks such as Mt. Balagbag and Mt. Oriod[cite: 330]. Recently gaining popularity, Mt. Lubog offers forested trails and scenic rock formations at the summit[cite: 331]. Although previously undocumented and hard to access, it is now considered a hidden gem for hikers looking for off-the-beaten-path adventures[cite: 332].",
                    tagline = "Let's Hike Mt. Lubog",
                    mountainImageRef1 = "mt_lubog_1",
                    mountainImageRef2 = "mt_lubog_2",
                    mountainImageRef3 = "mt_lubog_3",

                    hasSteepSections = true, // "challenging sections", "steep, slippery sections" for habal-habal
                    notableWildlife = "", // Not specified
                    isRocky = true, // "limestone formations", "Slippery rocks"
                    isSlippery = true, // "Slippery rocks can pose a serious risk [cite: 348]", access road is slippery [cite: 334]
                    isEstablishedTrail = true // Implied by guide requirement and being a known hike
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, description = "Camping is optional but possible in a view deck near the registration area[cite: 337]. The campsite offers a simple, open space ideal for small groups who want to stay overnight or catch a sunrise hike[cite: 338]. No water source is available nearby, so campers must bring enough water[cite: 339]."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, name = "Campsite Capacity", description = "5-7 tents [cite: 339]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, name = "Amenities", description = "None (basic/primitive) [cite: 339]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, name = "Permit/Fees - Registration", description = "₱50 fee per person (at barangay hall)[cite: 341]. Habal-habal fare: ~₱500-₱700 per person (round-trip, negotiable)[cite: 345].")
            ))
// No specific TrailEntity named beyond "Out-and-back".
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Registration", description = "Prior arrangement with local guides or tourism officers is required to secure guides and transportation. [cite: 340]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Logbook Registration", description = "Register at the barangay hall (₱50 fee per person). [cite: 341]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Courtesy Call", description = "Check in with the nearby military detachment for safety and security protocols. [cite: 342]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Habal-Habal Transport", description = "After registration, hikers will ride habal-habal motorcycles to reach the trailhead. Expect a bumpy, 1-3 hour ride depending on road conditions. Hikers may be asked to disembark and walk during steep, slippery sections. Fare: ~₱500-₱700 per person (round-trip, negotiable). Tip: Wear sturdy shoes and waterproof your bags. [cite: 343, 344, 345]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Environmental Concern", description = "Expect to see signs of illegal logging; respectful behavior and adherence to Leave No Trace principles are highly encouraged. [cite: 346]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Environmental Concern", description = "Stay on marked trails to avoid further impact on the environment. [cite: 347]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Safety Tip", description = "Summits have exposed rock edges—practice caution while taking photos. Slippery rocks can pose a serious risk. [cite: 348]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Safety Tip", description = "Bring sun protection and enough hydration; there are no natural water sources on the trail. [cite: 349]")
            ))

// --- Mt. Labo Data ---
            val mtLaboId = "mtlabo001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtLaboId,
                    mountainName = "Mt. Labo",
                    pictureReference = "mt_labo_main",
                    location = "San Lorenzo Ruiz, Camarines Norte (Jump-off: Sitio Butan, Brgy. San Isidro)", //
                    masl = 1544, //
                    difficultySummary = "Major Climb",
                    difficultyText = "6/9 (Major Climb)", //
                    hoursToSummit = "8-10 hours to summit (2-3 days total)", //
                    bestMonthsToHike = "June-July for Rafflesia blooming[cite: 355]. Avoid rainy season due to flash flood risk[cite: 361].",
                    typeVolcano = "Not specified, towering peak", // Inferred
                    trekDurationDetails = "8-10 hours to summit (2-3 days total)", //
                    trailTypeDescription = "Dense forest, multiple rivers, unique flora (old geothermal exploration trails)", //
                    sceneryDescription = "Rich biodiversity, remote jungle trails, scenic vistas, Rafflesia manillana, rufous hornbill, Angelina Falls", //
                    viewsDescription = "Views of Mt. Mayon, Mt. Isarog, and Mt. Banahaw on clear days [cite: 354]",
                    wildlifeDescription = "Rafflesia manillana, rufous hornbill, various unique species, insects, jungle hazards [cite: 352, 361]",
                    featuresDescription = "Towering peak, rich biodiversity, remote jungle trails, Rafflesia manillana, Angelina Falls sidetrip, geothermal exploration trails [cite: 351, 352, 353, 355]",
                    hikingSeasonDetails = "Seasonal highlights include blooming rafflesias around June-July[cite: 355]. Flash flood risk during rainy season at river crossings[cite: 361].",
                    introduction = "Mt. Labo is a towering peak in San Lorenzo Ruiz, Camarines Norte, standing at 1544 MASL and spanning three municipalities[cite: 351]. It offers a challenging yet rewarding experience for hikers seeking rich biodiversity, remote jungle trails, and scenic vistas[cite: 352]. The mountain is home to various unique species including the Rafflesia manillana and rufous hornbill[cite: 352].",
                    tagline = "Let's Hike Mt. Labo",
                    mountainImageRef1 = "mt_labo_1",
                    mountainImageRef2 = "mt_labo_2",
                    mountainImageRef3 = "mt_labo_3",

                    hasSteepSections = true, // Implied by "Major Climb" and long trek
                    notableWildlife = "Rafflesia, Rufous Hornbill", //
                    isRocky = false, // Not specified as primary feature, more jungle/forest
                    isSlippery = true, // Multiple river crossings, jungle trails
                    isEstablishedTrail = true // "geothermal exploration trails", guide is legendary
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, description = "There is one main campsite around 800 MASL, located 9 km from the trailhead[cite: 358]. It is set in a small clearing at the base of Mt. Labo, surrounded by dense forest[cite: 359]. It can accommodate small groups. Water source is available approximately 500 meters away."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, name = "Main Campsite (800 MASL)", description = "Small clearing, can accommodate small groups, water source 500m away[cite: 358, 359].")
            ))
// No specific TrailEntity named beyond the description.
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, category = "Registration", description = "Logbook available at Brgy. San Isidro; no registration fee required. Coordinate with local guide, Tatay Anie (Angeles Malate) - legendary trail expert. Organized groups highly recommended due to remoteness and wildlife presence. [cite: 360]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, category = "Safety Tip", description = "4/10 summit safety index (low injury risk but not recommended solo)"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, category = "Safety Tip", description = "Presence of wildlife, insects, and jungle hazards - bring insect repellent and long clothing [cite: 361]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, category = "Safety Tip", description = "Cell signal (Globe > Smart) is available in most areas including summit"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, category = "Safety Tip", description = "Flash flood risk during rainy season - exercise caution at river crossings [cite: 361]")
            ))

// --- Mt. Pulag Data ---
            val mtPulagId = "mtplg001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtPulagId,
                    mountainName = "Mt. Pulag",
                    pictureReference = "mt_pulag_main",
                    location = "Benguet, Ifugao, and Nueva Vizcaya (Jump-off: DENR Visitor Center, Bokod, Benguet for Ambangeg)", //
                    masl = 2926, //
                    difficultySummary = "Minor to Moderate (Ambangeg Trail)",
                    difficultyText = "3/9 (Minor to Moderate Climb - Ambangeg Trail)", //
                    hoursToSummit = "4-5 hours (Ambangeg Trail)", //
                    bestMonthsToHike = "November to early March (peak season for clearest skies) [cite: 366]",
                    typeVolcano = "Not specified, highest in Luzon, part of National Park", // Inferred
                    trekDurationDetails = "4-5 hours (Ambangeg Trail)", //
                    trailTypeDescription = "Out-and-back via Ambangeg Trail (easiest route)", //
                    sceneryDescription = "Mossy forests, dwarf bamboo grasslands, panoramic sunrise views, and the famous sea of clouds [cite: 364]",
                    viewsDescription = "Famed 'sea of clouds' and breathtaking sunrise views [cite: 363]",
                    wildlifeDescription = "Dwarf bamboo grasslands, mossy forests, unique wildlife like the Luzon pygmy fruit bat [cite: 363]",
                    featuresDescription = "Third highest peak in PH, 'sea of clouds', sunrise views, dwarf bamboo grasslands, mossy forests, Mt. Pulag National Park [cite: 362, 363]",
                    hikingSeasonDetails = "Peak season is typically from November to early March when the skies are clearest and the temperature drops near freezing[cite: 366]. Temperature Range: 5°C to as low as 0°C during cold months[cite: 370].",
                    introduction = "Mt. Pulag, the third highest peak in the Philippines, towers at 2,926 MASL and straddles the borders of Benguet, Ifugao, and Nueva Vizcaya[cite: 362]. Known for its famed \"sea of clouds\" and breathtaking sunrise views, it is a bucket-list climb for both beginner and seasoned hikers[cite: 363]. The mountain is part of the Mt. Pulag National Park and boasts a rich biodiversity, home to dwarf bamboo grasslands, mossy forests, and unique wildlife like the Luzon pygmy fruit bat[cite: 363].",
                    tagline = "Let's Hike Mt. Pulag (Sea of Clouds)",
                    mountainImageRef1 = "mt_pulag_1",
                    mountainImageRef2 = "mt_pulag_2",
                    mountainImageRef3 = "mt_pulag_3",

                    hasSteepSections = false, // Ambangeg Trail is "gradual ascents" [cite: 364]
                    notableWildlife = "Luzon pygmy fruit bat", //
                    isRocky = false, // Not primary feature of Ambangeg
                    isSlippery = true, // General mountain conditions, especially mossy forests
                    isEstablishedTrail = true // "well-established" [cite: 364]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, name = "Main Campsites", description = "Camp 1 and Camp 2 along the Ambangeg Trail"), //
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, name = "Camp 2", description = "Most popular, located above the clouds; basic latrines available [cite: 368]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, name = "Capacity", description = "Suitable for large groups; Camp 2 accommodates most hikers"), //
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, name = "Water Source", description = "Limited; bring sufficient water or purifying tablets [cite: 369]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, name = "Temperature Range", description = "5°C to as low as 0°C during cold months [cite: 370]")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, name = "Ambangeg Trail", description = "Most popular and beginner-friendly route, well-established, gradual ascents, scenic views [cite: 364]")
                // Other trails like Akiki, Tawangan exist but Ambangeg is detailed for beginner-intermediate focus.
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Registration", description = "All hikers must register at the DENR Visitor Center in Bokod, Benguet. Medical certificate required (valid within 30 days). Attendance in the mandatory orientation is required. Registration is only open to those aged 10-60 years old."), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Fees - Entrance", description = "₱175/person (Local), USD 15 for Foreigners"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Fees - Environmental", description = "₱150/person"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Fees - Camping", description = "₱100/person"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Fees - Guide", description = "₱600 (1-5 persons), ₱120/person for groups above 5"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Fees - Porter", description = "₱750 per 15 kg load"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Guide Requirement", description = "Mandatory; no guide, no hike policy [cite: 371]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Booking Tip", description = "It is highly advised to book 1-2 months in advance, especially during peak season"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Leave No Trace", description = "Carry out all trash and avoid disturbing natural habitats"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Fire Safety", description = "No open fires allowed; use portable stoves [cite: 372]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Trail Use", description = "Stick to established trails; off-trail hiking is discouraged"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Cultural Respect", description = "Mt. Pulag is considered sacred by local tribes; observe respectful conduct at all times [cite: 373]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Safety Tip - Health", description = "Make sure you are fit for high-altitude hiking; secure a valid medical certificate [cite: 374]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Safety Tip - Cold Protection", description = "Wear thermal layers, gloves, and windproof jackets"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Safety Tip - Weather", description = "Temperature can drop near or below freezing; rain and fog are common [cite: 375]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Safety Tip - Altitude", description = "Pace yourself to avoid altitude sickness"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Safety Tip - Hydration/Nutrition", description = "Bring sufficient water and trail food; prepare for limited resources [cite: 376]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Safety Tip - Communication", description = "Cell signal is available in some sections (Globe generally better than Smart)") //
            ))

// --- Mt. Amuyao Data ---
            val mtAmuyaoId = "mtamy001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtAmuyaoId,
                    mountainName = "Mt. Amuyao",
                    pictureReference = "mt_amuyao_main",
                    location = "Ifugao and Mountain Province (Jump-off: Brgy. Nabuya, Ifugao)", //
                    masl = 2702, //
                    difficultySummary = "Major Climb",
                    difficultyText = "7/9 (Major Climb)", //
                    hoursToSummit = "9-12 hours to summit (2-3 days total)", //
                    bestMonthsToHike = "Dry season (Nov-April) for better weather and views.", // General Cordillera hiking advice
                    typeVolcano = "Not specified, majestic peak in Cordillera", // Inferred
                    trekDurationDetails = "9-12 hours to summit (2-3 days total)", //
                    trailTypeDescription = "Mixed Trail (Forest, Grasslands, Rocky Terrain) [cite: 393]", //
                    sceneryDescription = "Forest, grasslands, mountain views, rivers and streams [cite: 379, 380]",
                    viewsDescription = "Expansive views of surrounding mountains, including Mount Pulag and Mount Kalawitan [cite: 379]",
                    wildlifeDescription = "Wild animals including snakes[cite: 392]. General Cordillera biodiversity.",
                    featuresDescription = "Majestic peak in Cordillera, rugged terrain, picturesque mountain views, lush forests, mossy forests, grasslands, cultural significance [cite: 377, 378, 380, 381]",
                    hikingSeasonDetails = "Expect variable weather conditions, including rain and strong winds; be prepared for rapid changes in temperature[cite: 392].",
                    introduction = "Mount Amuyao is a majestic peak located in the Cordillera mountain range, straddling the provinces of Ifugao and Mountain Province[cite: 377]. With an elevation of 2,702 MASL, it offers a challenging and scenic climb for experienced hikers[cite: 378]. The trail provides a perfect mix of rugged terrain, picturesque mountain views, and lush forests[cite: 378].",
                    tagline = "Let's Hike Mt. Amuyao",
                    mountainImageRef1 = "mt_amuyao_1",
                    mountainImageRef2 = "mt_amuyao_2",
                    mountainImageRef3 = "mt_amuyao_3",

                    hasSteepSections = true, // "rugged terrain", "long stretches of steep terrain" [cite: 378, 392]
                    notableWildlife = "Snakes", //
                    isRocky = true, // "Rocky Terrain"[cite: 393], "rocky ridgelines" [cite: 393]
                    isSlippery = true, // "muddy and slippery, especially during the rainy season" [cite: 394]
                    isEstablishedTrail = true // Implied by guide recommendation and known campsites
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, name = "Main Campsite (around 2,400 MASL)", description = "Located roughly 6-8 hours from the trailhead. Offers a relatively open space with scenic views of surrounding mountain ranges. Can accommodate groups. [cite: 383, 384]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, name = "Water Source", description = "A reliable water source can be found along the trail before reaching the campsite, but hikers are advised to bring extra water. [cite: 385]")
            ))
// Trail info integrated into mountain description and details.
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Registration", description = "Logbook available at the trailhead in Brgy. Nabuya, Ifugao; minimal registration fee required. Coordinate with local guides to ensure safety and to navigate the terrain effectively. Organized groups are recommended due to the remote nature of the area. [cite: 387]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Waste Management", description = "Carry out all trash; there are no disposal facilities along the trail. [cite: 388]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Flora and Fauna Protection", description = "Avoid picking plants and respect wildlife. [cite: 389]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Erosion Prevention", description = "Stick to marked trails to prevent soil erosion. [cite: 390]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Water Conservation", description = "Do not wash in streams; filter or boil water before use. [cite: 391]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Fire Safety", description = "Control campfires and ensure they are fully extinguished. [cite: 391]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip - Summit Index", description = "6/10 summit safety index (moderate risk, physical conditioning is required)"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip - Weather", description = "Expect variable weather conditions, including rain and strong winds—be prepared for rapid changes in temperature [cite: 392]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip - Wildlife", description = "Presence of wild animals, including snakes—keep a safe distance and be cautious [cite: 392]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip - Signal", description = "Cell signal is limited in most areas, including near the summit"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip - Gear", description = "Bring proper gear for both high-altitude and wet conditions (waterproof clothing, sturdy footwear)"), //
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip - Terrain", description = "The trail involves long stretches of steep terrain—prepare for challenging ascents and descents [cite: 392]")
            ))

// --- Mt. Napulauan Data ---
            val mtNapulauanId = "mtnpuln001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtNapulauanId,
                    mountainName = "Mt. Napulauan",
                    pictureReference = "mt_napulauan_main",
                    location = "Hungduan, Ifugao", //
                    masl = 2642, //
                    difficultySummary = "Major Climb",
                    difficultyText = "8/9 (Major Climb)", //
                    hoursToSummit = "5-6 hours to summit (1-2 days)", //
                    bestMonthsToHike = "Dry season (Nov-April) for safer trails and clearer views.", // General Cordillera advice
                    typeVolcano = "Not specified, 'Whitened Grand Mountain'", // Inferred
                    trekDurationDetails = "5-6 hours to summit (1-2 days)", //
                    trailTypeDescription = "Mossy forest, steep ascent with challenging sections", //
                    sceneryDescription = "Stunning moss-covered trees, primeval-looking trails, bonsai-like summit, and views of rice terraces and cloud seas [cite: 399]",
                    viewsDescription = "Views of rice terraces and cloud seas, panoramic views from summit [cite: 399, 402]",
                    wildlifeDescription = "Leeches common in mossy forests[cite: 411]. Wild boars and musang may be encountered[cite: 413].",
                    featuresDescription = "15th highest peak in PH, part of Great Cordillera Traverse, expansive mossy forests, surreal landscapes, historically significant (Gen. Yamashita WW2) [cite: 396, 397, 399, 400]",
                    hikingSeasonDetails = "Prepare for cold temperatures at the summit (5-8°C) and possible rain[cite: 412].",
                    introduction = "Mt. Napulauan, also known as the \"Whitened Grand Mountain,\" stands at an impressive 2,642+ MASL in Hungduan, Ifugao[cite: 396]. As the 15th highest peak in the Philippines, it is part of the Great Cordillera Traverse[cite: 397]. The mountain offers three main trails: the Hungduan Trail, which is the most commonly used, the Hapao Trail, which passes through UNESCO-protected rice terraces, and the Balentimol Trail, known for its dangerous narrow paths and ravines[cite: 398].",
                    tagline = "Let's Hike Mt. Napulauan",
                    mountainImageRef1 = "mt_napulauan_1",
                    mountainImageRef2 = "mt_napulauan_2",
                    mountainImageRef3 = "mt_napulauan_3",

                    hasSteepSections = true, // "steep ascent with challenging sections"
                    notableWildlife = "Leeches, Wild Boars, Musang", //
                    isRocky = false, // Not primary feature, more mossy forest and steep paths
                    isSlippery = true, // "Steep and sometimes narrow paths", "Balentimol Trail...dangerous narrow paths" [cite: 398, 412]
                    isEstablishedTrail = true // Three main trails are known
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Ny-o Campsite (1,630 MASL)", description = "Located around 3 hours from the jump-off point, this campsite offers a break before heading deeper into the mossy forest. [cite: 401]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Summit Campsite (2,642 MASL)", description = "A small open area with bonsai-like trees, suitable for a dozen tents. The summit offers panoramic views but can be cold, with temperatures dropping to 5°C at night. [cite: 402, 403]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Water Sources", description = "Available at Ny-o campsite and near the summit; no consistent water source along the trail, so bring enough water. [cite: 406]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Cell Signal", description = "Available at Ny-o and summit campsites.") //
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Hungduan Trail", description = "Most commonly used. [cite: 398]"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Hapao Trail", description = "Passes through UNESCO-protected rice terraces. [cite: 398]"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Balentimol Trail", description = "Known for its dangerous narrow paths and ravines. [cite: 398]")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Registration", description = "Required at the municipal hall of Hungduan; no fee but optional souvenirs (₱150). [cite: 404]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Local Guide", description = "Recommended for safety and navigation (₱500/day for 5 hikers). [cite: 405]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Environmental - Waste", description = "Carry out all trash; no disposal facilities along the trail. [cite: 407]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Environmental - Flora/Fauna", description = "Avoid picking plants, especially berries like \"upang\" or orchids, and respect the ecosystem. [cite: 408]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Environmental - Erosion", description = "Stay on designated trails to prevent erosion, especially in steep sections. [cite: 409]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Environmental - Fire", description = "Minimize campfire use and ensure it is completely extinguished before leaving. [cite: 410]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Safety Tip - Leeches", description = "Common in mossy forests; wear protective clothing and bring leech repellent. [cite: 411]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Safety Tip - Weather", description = "Prepare for cold temperatures at the summit (5-8°C) and possible rain along the trail. [cite: 412]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Safety Tip - Trail Conditions", description = "Steep and sometimes narrow paths, especially on the Balentimol Trail; use caution during descent. [cite: 412]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Safety Tip - Wildlife", description = "Keep an eye out for wild boars and musang; avoid picking plants or disturbing wildlife. [cite: 413]")
            ))

            // --- Mt. Kalawitan Data ---
            val mtKalawitanId = "mtklwtn001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtKalawitanId,
                    mountainName = "Mt. Kalawitan",
                    pictureReference = "mt_kalawitan_main",
                    location = "Bontoc and Sabangan, Mountain Province (Jump-off: Barangay Talubin or Golden Farm Resort)", // [cite: 415, 429]
                    masl = 2714, // [cite: 415]
                    difficultySummary = "Major Climb",
                    difficultyText = "7/9 (Major Climb)", // [cite: 420]
                    hoursToSummit = "8-12 hours to summit (1-2 days)", // [cite: 420]
                    bestMonthsToHike = "Dry season (Nov-April) for better trail conditions and views.", // General Cordillera hiking advice
                    typeVolcano = "Not specified, tenth highest peak in PH", // [cite: 415]
                    trekDurationDetails = "8-12 hours to summit (1-2 days)", // [cite: 420]
                    trailTypeDescription = "out-and-back trail", // [cite: 420] (Details: pine and mossy forests, rugged terrain [cite: 416])
                    sceneryDescription = "Stunning pine and mossy forests, mist-covered peaks, dramatic views of surrounding valleys, and a rewarding summit with panoramic vistas of neighboring mountains and ridges. The summit offers a scenic view of the cloud sea.", // [cite: 421, 422]
                    viewsDescription = "Panoramic view at the summit, cloud sea[cite: 419, 422], views of neighboring mountains and ridges [cite: 421]",
                    wildlifeDescription = "Mountain leeches (limatik)[cite: 419]. Other specific fauna not detailed but likely Cordillera species.",
                    featuresDescription = "Tenth highest peak in PH[cite: 415], stunning pine forests, expansive mossy forests, rugged terrain[cite: 416], cloudscapes[cite: 417], historical stories[cite: 418].",
                    hikingSeasonDetails = "Prepare for cold temperatures at the summit (around 5°C) and possible rain. [cite: 428, 440]",
                    introduction = "Mt. Kalawitan, standing at 2,714 MASL, is the tenth highest peak in the Philippines. [cite: 415] Located in the towns of Bontoc and Sabangan, Mountain Province, Kalawitan is known for its stunning pine forests, expansive mossy forests, and rugged terrain. [cite: 415, 416] Its trails, particularly the Talubin Trail, offer an unforgettable experience, taking climbers through a variety of forest landscapes and rugged terrain, with opportunities to witness breathtaking cloudscapes and wildlife. [cite: 416, 417]",
                    tagline = "Let's Hike Mt. Kalawitan",
                    mountainImageRef1 = "mt_kalawitan_1",
                    mountainImageRef2 = "mt_kalawitan_2",
                    mountainImageRef3 = "mt_kalawitan_3",

                    hasSteepSections = true, // "rugged terrain"[cite: 416], "narrow and steep in parts" [cite: 441]
                    notableWildlife = "Leeches (Limatik)", // [cite: 419, 439]
                    isRocky = true, // "rugged terrain"[cite: 416], "rough paths" [cite: 419]
                    isSlippery = true, // "trail can be narrow and steep in parts...especially during rainy conditions" [cite: 441, 442]
                    isEstablishedTrail = true // Talubin Trail is preferred route [cite: 418]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, name = "Munna Camp (1,950 MASL)", description = "Located about 4 hours from the jump-off point, this campsite offers an opportunity to rest before heading into the mossy forest. [cite: 423] Commonly used as a rest stop. [cite: 424]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, name = "Emergency Camp (2,400 MASL)", description = "Situated in the mossy forest, this camp serves as a backup campsite for hikers needing to stop before reaching the summit. [cite: 425] Typically used if summit cannot be reached on first day. [cite: 426]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, name = "Summit Campsite (2,714 MASL)", description = "Limited space for camping, typically accommodating up to 6-8 tents. [cite: 427] Beautiful spot with mossy shrubs and scenic views, temperatures can drop to around 5°C at night. [cite: 427, 428]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, name = "Water Sources", description = "Available at Munna Camp, Emergency Camp, and near the summit. [cite: 431] Water may be scarce along some sections, bring extra. [cite: 432]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, name = "Cell Signal", description = "Available at Munna Camp and Summit Camp. [cite: 433]")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, name = "Talubin Trail", description = "Preferred route for most hikers, challenging yet scenic. [cite: 418, 419]")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Registration", description = "Required at the Barangay Talubin or Golden Farm Resort. [cite: 429] No mandatory fees, but optional donation or souvenir purchases may be encouraged (around ₱150). [cite: 430]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Local Guide", description = "Highly recommended for safety and navigation (₱500/day for up to 5 hikers). [cite: 431]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Environmental - Waste", description = "Carry out all trash. [cite: 434] No designated waste disposal areas along the trail. [cite: 434]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Environmental - Flora/Fauna", description = "Do not disturb or pick plants, especially rare orchids or berries, to help preserve the local ecosystem. [cite: 435]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Environmental - Erosion", description = "Stick to designated trails, especially in steeper sections, to prevent soil erosion. [cite: 436]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Environmental - Fire", description = "Minimize campfire use. Ensure all fires are completely extinguished before leaving. [cite: 437]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Safety Tip - Leeches", description = "Common in the mossy forests, particularly past Munna Camp. [cite: 439] Wear long pants, gaiters, or protective clothing, and carry leech repellent. [cite: 439, 440]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Safety Tip - Weather", description = "Prepare for cold temperatures at the summit (around 5°C) and possible rain throughout the hike. [cite: 440]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Safety Tip - Trail Conditions", description = "The trail can be narrow and steep in parts, particularly in the mossy forest section. [cite: 441] Use caution, especially during rainy conditions. [cite: 442]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Safety Tip - Wildlife", description = "Be mindful of wildlife, including wild boars and musang (civets), which may be encountered during the hike. [cite: 443]")
            ))

// --- Mt. Ugo Data ---
            val mtUgoId = "mtugo001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtUgoId,
                    mountainName = "Mt. Ugo",
                    pictureReference = "mt_ugo_main",
                    location = "Traversing Nueva Vizcaya and Benguet (Kayapa to Itogon)", // [cite: 444, 448]
                    masl = 2150, // [cite: 444]
                    difficultySummary = "Major Climb",
                    difficultyText = "5/9 (Major Climb)", // [cite: 449]
                    hoursToSummit = "10-12 hours total (typically 2 days, traverse)", // [cite: 449]
                    bestMonthsToHike = "Dry season (Nov-April) for clearer views and better trail conditions.", // General Cordillera advice
                    typeVolcano = "Not specified, prominent Cordillera peak", // [cite: 444]
                    trekDurationDetails = "10-12 hours total (typically 2 days, traverse)[cite: 449]. Trail Length: Train for endurance; this traverse is long and includes multiple ascents and descents. [cite: 450]",
                    trailTypeDescription = "Point-to-point traverse (Kayapa to Itogon)", // [cite: 449]
                    sceneryDescription = "Verdant pine forests, rolling ridgelines, sea of clouds, distant views of Mt. Pulag and other Cordillera ranges, summit marker, remote villages, and cool mountain breezes.", // [cite: 451]
                    viewsDescription = "Panoramic views of neighboring peaks including Mt. Pulag, Mt. Sto. Tomas, and Mt. Timbak[cite: 447]. One of the best summit views in the Cordillera region. [cite: 449]",
                    wildlifeDescription = "Occasional encounters with native fauna like birds or civets. [cite: 472]",
                    featuresDescription = "Known due to a 1987 plane crash[cite: 445], long-distance trails, pine forest scenery, expansive mountain views[cite: 445], rolling terrain, scenic ridgelines, remote villages, old forest paths[cite: 446].",
                    hikingSeasonDetails = "Be prepared for cold temperatures, wind, and rain. [cite: 469] Summit temps can drop to 10°C or lower. [cite: 470]",
                    introduction = "Mt. Ugo (2,150 MASL) is a prominent Cordillera peak traversing the provinces of Nueva Vizcaya and Benguet. [cite: 444] Once known primarily due to a tragic plane crash in 1987, it has since become a favorite among seasoned hikers seeking long-distance trails, pine forest scenery, and expansive mountain views. [cite: 445] The mountain features rolling terrain and scenic ridgelines, passing through remote villages and old forest paths. [cite: 446]",
                    tagline = "Let's Hike Mt. Ugo",
                    mountainImageRef1 = "mt_ugo_1",
                    mountainImageRef2 = "mt_ugo_2",
                    mountainImageRef3 = "mt_ugo_3",

                    hasSteepSections = true, // "multiple ascents and descents"[cite: 450], "Long, steep descents after the summit" [cite: 471]
                    notableWildlife = "Birds, Civets", // [cite: 472]
                    isRocky = false, // Not primary feature, more pine forests and rolling terrain
                    isSlippery = true, // Long, steep descents can be slippery
                    isEstablishedTrail = true // Kayapa to Itogon traverse is popular [cite: 448]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, name = "Indupit Elementary School (~1,700 MASL)", description = "A common campsite on the first day of the traverse trail. [cite: 453] Offers a spacious area for tents near the school grounds and access to water and basic shelter. [cite: 454] Provides interaction with the local community and a scenic view of nearby ridges. [cite: 455]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, name = "Domolpos Village (~1,850 MASL)", description = "Some hikers choose to camp here after the summit, particularly when taking a longer descent. [cite: 456] Offers modest accommodation options and basic supplies from locals. [cite: 456]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, name = "Summit Area (~2,150 MASL)", description = "The summit has limited space for tents but can be used for early morning breaks or quick photo stops. [cite: 457] Overnight stays are rare due to exposure and wind. [cite: 457]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, name = "Water Sources", description = "Available at villages like Indupit and Domolpos. [cite: 462] Bring enough water for long stretches between settlements. Water may need purification. [cite: 462]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, name = "Cell Signal", description = "Available intermittently along ridges and at the summit. [cite: 463] Stronger signal at Indupit campsite and parts of the descent to Tinongdan. [cite: 463]")
            ))
// Trail info integrated into mountain description.
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Registration", description = "Required. [cite: 458] Coordinate with local tourism offices or barangays in Kayapa (Nueva Vizcaya) and Tinongdan (Benguet). [cite: 459] Registration may be done via the tourism officer or arranged with the guide. Expect fees for environmental and guide services. [cite: 460]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Local Guide", description = "Highly recommended, especially for the traverse. Guide fee ranges from ₱500-₱1,000/day, depending on group size and arrangements. [cite: 461]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Environmental - Waste", description = "Carry out all trash; no trash disposal facilities along the trail. [cite: 464]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Environmental - Trail Respect", description = "Stick to established paths to avoid damaging vegetation and causing erosion. [cite: 465]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Environmental - Community", description = "Be respectful to local villagers and children you may meet along the way. [cite: 466] Avoid littering or disturbing local life. [cite: 466]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Environmental - Campfire", description = "Minimize or avoid campfires. [cite: 467] Use portable stoves and ensure any flames are fully extinguished before breaking camp. [cite: 467]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Safety Tip - Weather", description = "Be prepared for cold temperatures, wind, and rain. [cite: 469] Summit temps can drop to 10°C or lower, especially early in the morning. [cite: 470]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Safety Tip - Trail Conditions", description = "Long, steep descents after the summit can be tiring. Trekking poles recommended. [cite: 471]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Safety Tip - Wildlife", description = "Occasional encounters with native fauna like birds or civets. Respect wildlife and avoid feeding animals. [cite: 472]")
            ))

// --- Mt. Timbak Data ---
            val mtTimbakId = "mttmbk001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtTimbakId,
                    mountainName = "Mt. Timbak",
                    pictureReference = "mt_timbak_main",
                    location = "Atok, Benguet (Jump-off: KM 55, Halsema Highway)", // [cite: 473, 486]
                    masl = 2717, // [cite: 473]
                    difficultySummary = "Minor Climb (Easiest of top ten)",
                    difficultyText = "2/9 (Minor Climb)", // [cite: 478]
                    hoursToSummit = "1.5-2.5 hours to summit (1 day)", // [cite: 478]
                    bestMonthsToHike = "Dry season (Nov-April) for best views and less fog.", // General Cordillera advice
                    typeVolcano = "Not specified, ninth highest in PH", // [cite: 473]
                    trekDurationDetails = "1.5-2.5 hours to summit (1 day)", // [cite: 478]
                    trailTypeDescription = "Out-and-back trail via paved village road", // [cite: 478]
                    sceneryDescription = "Vegetable gardens, scenic village life, panoramic views of Cordillera peaks, sea of clouds, and cultural heritage sites including ancient burial caves (mummies).", // [cite: 479]
                    viewsDescription = "Panoramic views of nearby peaks, including Pulag and Tabayoc, as well as the famous sea of clouds. [cite: 477]",
                    wildlifeDescription = "Not specified, mainly agricultural and village setting.",
                    featuresDescription = "Ninth highest mountain in PH, third highest in Luzon[cite: 473], easiest climb among top ten peaks[cite: 474], mostly paved route accessible via Halsema Highway[cite: 475], part of Luzon 3-2-1 challenge[cite: 476], three crosses at summit ('mini-Calvary')[cite: 477], Timbak mummies[cite: 493].",
                    hikingSeasonDetails = "Cold temperatures, especially in early morning or late afternoon. [cite: 491] Optional side trip to Timbak mummies (₱40 fee). [cite: 493]",
                    introduction = "Mt. Timbak, standing at 2,717 MASL, is the ninth highest mountain in the Philippines and the third highest in Luzon. [cite: 473] Located in Atok, Benguet, it is widely known as the easiest climb among the country's top ten peaks-more of a scenic walk than a grueling trek. [cite: 474] Despite its elevation, the route is mostly paved and accessible via the Halsema Highway. [cite: 475]",
                    tagline = "Let's Hike Mt. Timbak",
                    mountainImageRef1 = "mt_timbak_1",
                    mountainImageRef2 = "mt_timbak_2",
                    mountainImageRef3 = "mt_timbak_3",

                    hasSteepSections = false, // "mostly paved village road", "scenic walk" [cite: 474, 478]
                    notableWildlife = "Mummies (Cultural)", // [cite: 479, 493]
                    isRocky = false, // "mostly paved" [cite: 475]
                    isSlippery = false, // Paved road generally not slippery unless mossy/wet
                    isEstablishedTrail = true // Paved village road [cite: 478]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, name = "Timbak Elementary School (Approx. 2,650 MASL)", description = "Often used as a staging area for the final ascent to the summit. [cite: 480] Flat areas nearby may be used for camping with local permission. [cite: 481]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, name = "Summit Area (2,717 MASL)", description = "Limited flat ground near the three crosses, suitable for short breaks or minimal overnight setups. [cite: 482] It is exposed and windy, so proper cold-weather gear is necessary. [cite: 482]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, name = "Water Sources", description = "None along the trail; bring sufficient water. [cite: 484]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, name = "Cell Signal", description = "Available throughout most of the trail and summit. [cite: 485]")
            ))
// Trail info integrated into mountain description.
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Registration", description = "No permits required. No formal registration process. [cite: 483]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Local Guide", description = "Not required, but can enhance the experience, especially when visiting the burial caves. [cite: 484]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Transportation", description = "From Baguio City, take buses bound for Sagada or Bontoc. [cite: 486] Ask to be dropped off at KM 55 (Atok), the jump-off point. [cite: 486]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Environmental - Waste", description = "Practice Leave No Trace. [cite: 487] Carry out all trash. [cite: 487]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Environmental - Cultural Respect", description = "Do not open or disturb the mummy burial sites. [cite: 488] Always ask for local guidance when visiting. [cite: 488]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Environmental - Trail Preservation", description = "Although paved, stick to the main road and avoid entering vegetable plots or private lands. [cite: 489]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Environmental - Fire Safety", description = "Open fires are not needed due to the short hike. Avoid lighting any unless necessary and permitted. [cite: 490]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Safety Tip - Weather", description = "Cold temperatures, especially in early morning or late afternoon. Bring appropriate layers. [cite: 491]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Safety Tip - Trail Confusion", description = "Some forks on the road; generally, keep to the left when uncertain, or ask locals for directions. [cite: 492]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Safety Tip - Road Traffic", description = "The trail shares the road with local vehicles. Stay aware when walking along the cemented road. [cite: 492]"), // Source 492 mentions "road"
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Optional Side Trips", description = "Timbak mummies can be visited with a ₱40 fee. Respect the site and local customs. [cite: 493]")
            ))

// --- Mt. Tabayoc Data ---
// Note: The PDF entry for Mt. Tabayoc (No. 37, pg 50-51) largely duplicates Mt. Timbak's information in its introduction and some details.
// The data below reflects what is written under the "Mt. Tabayoc" heading in the PDF.
            val mtTabayocId = "mttbyc001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtTabayocId,
                    mountainName = "Mt. Tabayoc",
                    pictureReference = "mt_tabayoc_main", // Assumed, not specified
                    location = "Atok, Benguet", // Inferred from context and similarity to Timbak's entry [cite: 496]
                    masl = 2717, // As stated under Mt. Tabayoc entry, identical to Timbak [cite: 499]
                    difficultySummary = "Minor Climb", // As stated under Mt. Tabayoc entry [cite: 499]
                    difficultyText = "2/9 (Minor Climb)", // As stated under Mt. Tabayoc entry [cite: 499]
                    hoursToSummit = "1.5-2.5 hours to summit (1 day)", // As stated under Mt. Tabayoc entry [cite: 499]
                    bestMonthsToHike = "Dry season (Nov-April) for best views and less fog.", // General Cordillera advice, as no specific info
                    typeVolcano = "Not specified", // Mt. Timbak (described in intro) is ninth highest [cite: 495]
                    trekDurationDetails = "1.5-2.5 hours to summit (1 day)", // As stated under Mt. Tabayoc entry [cite: 499]
                    trailTypeDescription = "Out-and-back trail via paved village road", // As stated under Mt. Tabayoc entry [cite: 499]
                    sceneryDescription = "Vegetable gardens, scenic village life, panoramic views of Cordillera peaks (including Mt. Pulag and Mt. Tabayoc), sea of clouds, and cultural heritage sites such as ancient burial caves (mummies).", // As stated under Mt. Tabayoc entry [cite: 499]
                    viewsDescription = "Stunning views of Mt. Pulag, Mt. Tabayoc, and the Cordillera mountain ranges.", // As stated under Mt. Tabayoc entry [cite: 497]
                    wildlifeDescription = "Not specified, mainly agricultural and village setting.", // Inferred
                    featuresDescription = "Part of Luzon 3-2-1 hiking challenge[cite: 498], gentle trail[cite: 496], summit with three crosses ('mini-Calvary')[cite: 497], views of Mt. Pulag and other peaks[cite: 497].",
                    hikingSeasonDetails = "Fog and sudden weather changes are common. [cite: 508] Early morning hikes recommended for better views. [cite: 509]",
                    introduction = "Mt. Timbak is the ninth highest mountain in the Philippines and the third highest in Luzon. [cite: 495] Despite its lofty elevation of 2,717 MASL, it is one of the easiest hikes among the country's high peaks. [cite: 495, 496] Located in Atok, Benguet, Mt. Timbak is often accessed via the Halsema Highway and is known for its gentle trail, which is mostly paved and leads through picturesque vegetable terraces and local villages. [cite: 496]", // This intro is under Mt. Tabayoc heading but describes Mt. Timbak.
                    tagline = "Let's Hike Mt. Tabayoc",
                    mountainImageRef1 = "mt_tabayoc_1", // Assumed
                    mountainImageRef2 = "mt_tabayoc_2", // Assumed
                    mountainImageRef3 = "mt_tabayoc_3", // Assumed

                    hasSteepSections = false, // "gentle trail" [cite: 496]
                    notableWildlife = "Mummies (Cultural)", // Mentioned in scenery [cite: 499]
                    isRocky = false, // "mostly paved" [cite: 496]
                    isSlippery = false, // Paved road generally not slippery
                    isEstablishedTrail = true // Paved village road [cite: 499]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, name = "Dayhike Destination", description = "Mt. Timbak is typically done as a dayhike and does not require camping. [cite: 500]"), // Note: States Timbak
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, name = "Nearby Lodging", description = "Homestays or transient accommodations may be found along the Halsema Highway or in nearby villages for those doing the Luzon 3-2-1 sequence. [cite: 501]")
            ))
// Trail info integrated.
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Permit", description = "Not strictly required for dayhikes, but coordinate with local barangay or tourism officers if necessary. [cite: 502]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Guide", description = "Optional due to the straightforward path, but hiring a local guide supports the community and enriches the cultural experience. [cite: 503]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Access Point", description = "Start from KM 55 along Halsema Highway; trail follows a gradual paved village road. [cite: 503]"), // Source 503 mentions path and access point
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Leave No Trace", description = "Always pack out trash; help maintain the clean and respectful atmosphere of this sacred and cultural site. [cite: 504]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Respect Sacred Grounds", description = "The summit crosses and nearby burial caves are cultural landmarks—avoid disruptive behavior or climbing on sacred structures. [cite: 505]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Local Support", description = "Buy produce or items from local stalls to support farmers and residents. [cite: 506]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Safety Tip - Altitude", description = "Despite the trail's ease, it's still a high-altitude hike—pace yourself and stay hydrated. [cite: 507]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Safety Tip - Weather", description = "Fog and sudden weather changes are common; wear layers and bring rain protection. [cite: 508]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Safety Tip - Navigation", description = "The trail is well-defined, but early morning hikes are recommended for better views and less fog. [cite: 509]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Safety Tip - Transport", description = "Accessible via bus or private vehicle along the Halsema Highway; minimal walking distance from the drop-off point to the trailhead. [cite: 510, 511]") // Source 511 specifies minimal walking distance
            ))

// --- Mt. Purgatory (Mangisi Range) Data ---
            val mtPurgatoryId = "mtprgtry001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtPurgatoryId,
                    mountainName = "Mt. Purgatory (Mangisi Range)",
                    pictureReference = "mt_purgatory_main",
                    location = "Bokod, Benguet (Jump-off: Japas Jump-off to Brgy. Ekip)", // [cite: 513, 517]
                    masl = 2329, // Highest point of traverse (Mt. Komkompol) [cite: 517]
                    difficultySummary = "Major Climb",
                    difficultyText = "6/9 (Major Climb)", // [cite: 517]
                    hoursToSummit = "9-11 hours (2-3 days for traverse)", // [cite: 517] (Note: Hours to specific Purgatory peak not isolated, this is for traverse)
                    bestMonthsToHike = "Dry season (Nov-April) for less fog and better trail conditions.", // General Cordillera advice
                    typeVolcano = "Not specified, multi-peak traverse in Cordillera", // [cite: 513]
                    trekDurationDetails = "9-11 hours (2-3 days)", // [cite: 517]
                    trailTypeDescription = "Point-to-point traverse (Japas Jump-off to Brgy. Ekip)", // [cite: 517]
                    sceneryDescription = "Expansive pine forests, extensive mossy forest, views of Cordillera peaks including Pulag and Timbak, historical relay station, traditional villages, and scenic ridgelines.", // [cite: 518]
                    viewsDescription = "Dramatic sights of Cordillera peaks including Mt. Pulag, Mt. Timbak, and Mt. Sto. Tomas. [cite: 516]",
                    wildlifeDescription = "Leeches are occasionally found in mossy segments. [cite: 537]",
                    featuresDescription = "Scenic and challenging multi-peak hike[cite: 513], winds through Mt. Pack, Mt. Purgatory, and Mt. Komkompol[cite: 514], name origin from American loggers[cite: 515], pine and mossy forests, ridge walks, old relay stations[cite: 516].",
                    hikingSeasonDetails = "Cold and foggy weather expected at Mangisi Village campsite. [cite: 520] Temperature can drop significantly in mossy forests and ridge areas. [cite: 533]",
                    introduction = "The Mt. Purgatory Traverse is a scenic and challenging multi-peak hike in Bokod, Benguet, nestled between the towering Cordillera giants of Mt. Pulag and Mt. Ugo. [cite: 513] Originally developed through the collaboration of the local government and hikers, the traverse winds through Mt. Pack, Mt. Purgatory, and Mt. Komkompol. [cite: 514] The name \"Purgatory\" originated from American loggers who likened the cold and foggy weather to a purgatorial experience. [cite: 515]",
                    tagline = "Let's Hike the Mt. Purgatory Traverse",
                    mountainImageRef1 = "mt_purgatory_1",
                    mountainImageRef2 = "mt_purgatory_2",
                    mountainImageRef3 = "mt_purgatory_3",

                    hasSteepSections = true, // "challenging multi-peak hike"[cite: 513], "prolonged trekking with steep, slippery sections" [cite: 534]
                    notableWildlife = "Leeches (Limatik)", // [cite: 537]
                    isRocky = false, // Not primary feature, more forest and ridges
                    isSlippery = true, // "steep, slippery sections" [cite: 534]
                    isEstablishedTrail = true // "Trails are generally established" [cite: 535]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, name = "Mangisi Village / Near Mt. Purgatory (~2,100 MASL)", description = "Used as an overnight site on 3-day itineraries. [cite: 519] Basic open ground and proximity to water. Cold and foggy weather expected. [cite: 520]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, name = "Bakian Elementary School (Traditional 2-Day Campsite)", description = "Equipped with a waiting shed. Offers a resting point with flat space. [cite: 521] Often used as a designated night stop. [cite: 521]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, name = "Other Campsites (Along Mossy Forest Trails)", description = "Clearings available between peaks, particularly near Aponan Junction. [cite: 522] Can be used for emergency or planned overnights with local coordination. [cite: 523]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, name = "Water Sources", description = "Available early in the hike (e.g., Kambingan area), but limited or none beyond mid-trail. Carry sufficient water. [cite: 526]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, name = "Cellphone Signal", description = "Intermittent. [cite: 527] Signal available at some ridges and campsites. [cite: 527]")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, name = "Mt. Purgatory Traverse", description = "Point-to-point from Japas Jump-off to Brgy. Ekip, passing Mt. Pack, Mt. Purgatory, and Mt. Komkompol. [cite: 514, 517]")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Registration", description = "Required at Bokod Municipal Hall (₱100 registration + ₱20 camping/night + ₱500 group donation to school if staying overnight). [cite: 524]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Local Guide", description = "Mandatory. Guide fee is ₱500/day for up to 7 hikers; add ₱100 per head up to 9 hikers. [cite: 525] Porters available at same rate (up to 25 kg capacity). [cite: 526]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Transportation - From Manila", description = "Take a bus to Baguio (₱460, 6-7 hrs). [cite: 528]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Transportation - From Baguio", description = "Rent a jeep to the jump-off (₱3,500-₱6,500 depending on group size), or take a bus bound for Nueva Vizcaya via Ambuklao and get off at Bokod. [cite: 529]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Environmental - Waste", description = "Strictly observe Leave No Trace. Carry out all garbage. [cite: 529]"), // Source 529 for Baguio transport, 530 for waste. Assuming citation error in doc and 530 applies to waste.
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Environmental - Trail", description = "Stay on designated paths. Do not damage mossy forest vegetation. [cite: 530]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Environmental - Fire", description = "Open flames discouraged. [cite: 531] Use portable stoves when cooking and follow safety precautions. [cite: 531]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Environmental - Cultural", description = "Respect local customs when passing through villages and near schools. [cite: 532]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Safety Tip - Cold Weather", description = "Bring layers. Temperature can drop significantly in mossy forests and ridge areas. [cite: 533]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Safety Tip - Endurance", description = "Ensure physical readiness for prolonged trekking with steep, slippery sections. [cite: 534]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Safety Tip - Trail Markers", description = "Trails are generally established, but always stay with the group and guide. [cite: 535]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Safety Tip - Jeep Access", description = "The final 4-5 km exit involves a rough road walk unless your jeep can enter this segment. [cite: 536]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Safety Tip - Limatik", description = "Leeches are occasionally found in mossy segments. Wear protective clothing. [cite: 537]")
            ))

// --- Mt. Sicapoo Data ---
            val mtSicapooId = "mtscpoo001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtSicapooId,
                    mountainName = "Mt. Sicapoo",
                    pictureReference = "mt_sicapoo_main",
                    location = "Ilocos Norte (Jump-off: Gasgas River, Brgy. Manalpac, Solsona to Solsona Dam)", // [cite: 538, 542, 557]
                    masl = 2354, // [cite: 538] (Also lists ~1873 for Simagaysay, ~1780 for Timarid [cite: 543])
                    difficultySummary = "Extremely Difficult (Luzon's most grueling)",
                    difficultyText = "9/9 (Major Climb)", // [cite: 543]
                    hoursToSummit = "16-18 hours (4-5 days for traverse)", // [cite: 543]
                    bestMonthsToHike = "Dry season to avoid flash floods in Gasgas River.", // [cite: 540, 560]
                    typeVolcano = "Not specified, highest peak in Ilocos Region", // [cite: 538]
                    trekDurationDetails = "16-18 hours (4-5 days)", // [cite: 543]
                    trailTypeDescription = "Point-to-point traverse (Gasgas River to Solsona Dam)", // [cite: 544]
                    sceneryDescription = "Treacherous river crossings, tropical and pine forests, mossy trails, steep ridgelines, summit rock formations like \"The Penguin,\" panoramic views of Ilocos and Apayao ranges, and World War II heritage site at One Degree Plateau.", // [cite: 544]
                    viewsDescription = "Panoramic views of Ilocos and Apayao ranges. [cite: 544] Views from Mt. Timarid and Mt. Simagaysay of vast grasslands. [cite: 542]",
                    wildlifeDescription = "Not specified, general forest/mountain fauna. Main concern is flash floods.",
                    featuresDescription = "Highest peak in Ilocos Norte and Ilocos Region ('Roof of Ilocos')[cite: 538], treacherous terrain, extreme remoteness[cite: 539], multiple Gasgas River crossings (over 20, flash flood hazard)[cite: 540], Benguet-like pine ridges, 'The Penguin' rock formation[cite: 541], passes Mt. Timarid and Mt. Simagaysay[cite: 542], considered Luzon's most grueling hike[cite: 543].",
                    hikingSeasonDetails = "Gasgas River crossings are riskiest section, avoid if heavy rains expected. [cite: 563] Cold and damp conditions, especially from Pakpako upward (temps below 10°C). [cite: 563]",
                    introduction = "Mt. Sicapoo, rising at 2,354 MASL, is the highest peak in Ilocos Norte and the entire Ilocos Region, earning the nickname \"Roof of Ilocos.\" [cite: 538] Known for its treacherous terrain and extreme remoteness, it is one of the most difficult climbs in Luzon. [cite: 539] The route starts with multiple crossings of the Gasgas River-over 20 in total-where flash floods are a serious hazard. [cite: 540]",
                    tagline = "Let's Hike Mt. Sicapoo (Roof of Ilocos)",
                    mountainImageRef1 = "mt_sicapoo_1",
                    mountainImageRef2 = "mt_sicapoo_2",
                    mountainImageRef3 = "mt_sicapoo_3",

                    hasSteepSections = true, // "treacherous terrain"[cite: 539], "steep ridgelines"[cite: 544], "Steep, trail-less ascents near summit" [cite: 564]
                    notableWildlife = "", // Not specified
                    isRocky = true, // "summit rock formations like 'The Penguin'" [cite: 544]
                    isSlippery = true, // "Treacherous river crossings"[cite: 544], "mossy trails" [cite: 544]
                    isEstablishedTrail = false // "trail-less ascents near summit"[cite: 564], but general route known by guides
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Saulay Campsite (~1,200 MASL)", description = "First-night camp after the river crossing. [cite: 545] Shaded forest site, near a water source. Cold, damp conditions—set up before dark. [cite: 546]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Pakpako Campsite (~1,800-1,900 MASL)", description = "Staging area for summit assault. Surrounded by mossy forest. [cite: 547] Coldest point of the trail—temps can drop below 10°C. [cite: 547]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Summit Area (~2,354 MASL)", description = "Small clearing near \"The Penguin.\" [cite: 548] Only suited for short breaks or emergency bivouac. Exposed, steep terrain—not ideal for camping. [cite: 549]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Other Options", description = "Summits of Mt. Timarid and Mt. Simagaysay have flat areas and limited water access. [cite: 550] One Degree Plateau offers vast open ground but is typically used near the exit. [cite: 551]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Water Sources", description = "Available at Saulay and Pakpako campsites. [cite: 554] Bring enough for dry segments and summit assault. [cite: 554]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Cell Signal", description = "Present (Smart and Globe) at most ridges and higher elevation points. [cite: 555] Limited in valleys and forests. [cite: 555]")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Sicapoo Traverse", description = "Point-to-point from Gasgas River to Solsona Dam, passing Mt. Timarid and Mt. Simagaysay. [cite: 542, 544]")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Registration", description = "Coordinate with One Degree Mountaineering Group (ODMG) prior to climb. They provide trail access support and climb advisories. [cite: 552] No official LGU registration as of now. [cite: 552]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Local Guide", description = "Mandatory. Guide fee: ₱500/day. [cite: 553] Contact ODMG via onedegreemg@ymail.com for guide recommendations and logistics help. [cite: 553]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Transportation", description = "From Manila, ride buses to Laoag (Maria de Leon, Fariñas, Florida Lines; ~₱600). [cite: 556] Then take local transport to Solsona. Chartering a jeep or tricycle to Brgy. Manalpac (Gasgas River) is required for the jump-off. [cite: 557]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Environmental - Waste", description = "Pack out all trash. [cite: 558] No garbage disposal on the trail. [cite: 558]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Environmental - Trail", description = "Stay on visible paths. Avoid damaging vegetation, especially in mossy forests. [cite: 559]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Environmental - River Safety", description = "Cross Gasgas River only when safe. Avoid afternoon crossings due to flash flood risk. [cite: 560]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Environmental - Fire Safety", description = "Campfires not recommended due to high forest density. Use portable stoves. [cite: 561]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Safety Tip - Flash Floods", description = "Gasgas River crossings are the riskiest section. Start early and avoid if heavy rains are expected. [cite: 563]"), // Source 563 for weather and flash floods
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Safety Tip - Weather", description = "Cold and damp, especially from Pakpako upward. Expect temperatures below 10°C. [cite: 563]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Safety Tip - Trail Hazards", description = "Steep, trail-less ascents near summit. [cite: 564] Use gloves, trekking poles, and secure footing. [cite: 564]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Safety Tip - Physical Fitness", description = "This is a 9/9 climb—endurance and experience are mandatory. [cite: 565]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Safety Tip - Emergency Exit", description = "If needed, the climb can be cut short via Timarid-Simagaysay route before summit. [cite: 566]")
            ))

// --- Mt. Tirad Peak (Tirad Pass) Data ---
            val mtTiradPeakId = "mttrdpk001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtTiradPeakId,
                    mountainName = "Mt. Tirad Peak (Tirad Pass)",
                    pictureReference = "mt_tirad_peak_main",
                    location = "Gregorio del Pilar, Ilocos Sur (Traverse to Quirino, possible extension to Sagada)", // [cite: 570]
                    masl = 1388, // MASL of Tirad Peak [cite: 569] (Shrine is ~1500[cite: 571], summit area ~1950 [cite: 573] - these seem to refer to campsite elevations, not the peak itself)
                    difficultySummary = "Major Climb",
                    difficultyText = "6/9 (Major Climb)", // [cite: 569]
                    hoursToSummit = "9-11 hours (2-3 days for traverse)", // [cite: 569]
                    bestMonthsToHike = "Dry season to avoid flash floods and for safer trail conditions.", // [cite: 588]
                    typeVolcano = "Not specified, historical mountain pass", // [cite: 567]
                    trekDurationDetails = "9-11 hours (2-3 days)", // [cite: 569]
                    trailTypeDescription = "Point-to-point traverse (Gregorio del Pilar to Quirino, possible extension to Sagada)", // [cite: 570]
                    sceneryDescription = "Historic Spanish-era mountain trail, del Pilar Shrine and Sniper's Knoll, panoramic views of Ilocos and Cordillera mountains, Old Spanish Trail, scenic campsites, Illengan Cave, and sweeping summit views from Tirad Peak.", // [cite: 570]
                    viewsDescription = "Panoramic views of the Ilocos region and the surrounding mountain ranges from Tirad Peak. [cite: 569]",
                    wildlifeDescription = "Not specified.",
                    featuresDescription = "Historically significant (Battle of Tirad Pass, Gen. Gregorio del Pilar's last stand)[cite: 567], del Pilar Shrine, Sniper's Knoll[cite: 570], rugged terrain[cite: 569], Old Spanish Trail, Illengan Cave[cite: 570].",
                    hikingSeasonDetails = "Flash floods can occur at Gasgas River crossings and lower parts of trail during rainy season. [cite: 588] Expect cool temperatures at higher elevations. [cite: 590]",
                    introduction = "Tirad Pass, located in the Cordillera Mountains, is historically significant as the site of the last stand of General Gregorio del Pilar and his men during the Philippine-American War in 1899. [cite: 567] The battle, which took place at the pass, is a symbol of bravery and sacrifice... [cite: 567] The area is surrounded by rich history and natural beauty, making it a unique destination for both history enthusiasts and adventurers. [cite: 568]",
                    tagline = "Let's Hike Tirad Peak (Tirad Pass)",
                    mountainImageRef1 = "mt_tirad_peak_1",
                    mountainImageRef2 = "mt_tirad_peak_2",
                    mountainImageRef3 = "mt_tirad_peak_3",

                    hasSteepSections = true, // "rugged terrain"[cite: 569], "trail is steep in some parts, especially near the summit" [cite: 591]
                    notableWildlife = "", // Not specified
                    isRocky = true, // "rugged terrain"[cite: 569], "loose rocks" [cite: 592]
                    isSlippery = true, // "slippery paths"[cite: 592], flash flood risk [cite: 588]
                    isEstablishedTrail = true // "Historic Spanish-era mountain trail", "Old Spanish Trail" [cite: 570]
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, name = "Tirad Pass Shrine (~1,500 MASL)", description = "Primary camping site near the historical shrine. [cite: 571] Offers cool weather, scenic views of the surrounding landscape, and historical significance. Good for an overnight stop. [cite: 572]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, name = "Summit Area (~1,950 MASL)", description = "Small clearing near the summit. Suitable for short breaks or emergency bivouac. [cite: 573] The exposed terrain and cold weather make it not ideal for extended camping. [cite: 574]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, name = "Water Sources", description = "Available at the shrine and some parts of the trail. [cite: 578] Bring enough water for the hike, especially during the dry sections and summit assault. [cite: 579]"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, name = "Cell Signal", description = "Limited to none at lower elevations, with occasional signal on higher ridges. [cite: 580]")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, name = "Tirad Pass Traverse", description = "Point-to-point from Gregorio del Pilar to Quirino, passing historical sites. [cite: 570]")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Registration", description = "Coordinate with the local tourism office for trail access and guides. [cite: 575] There is no official LGU registration, but coordination with the tourism officer is recommended. [cite: 575]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Local Guide", description = "Mandatory. Guide fee: ₱500/day. [cite: 576] Local guides can be arranged through the local tourism office or guide associations. [cite: 577]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Transportation", description = "From Manila, buses travel to Candon or nearby towns in Ilocos Sur. [cite: 581] From there, jeepneys or tricycles can be chartered to the jump-off point near the Tirad Pass Shrine. [cite: 582]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Environmental - Waste", description = "Pack out all trash. There are no garbage disposal facilities along the trail. [cite: 582]"), // Source 582 for transport, assume citation error for waste, general LNT applies
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Environmental - Trail", description = "Stay on marked paths. Avoid damaging vegetation, particularly in the mossy forest areas. [cite: 583]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Environmental - Historical Site", description = "Be respectful of the historical significance of the area. [cite: 585] Avoid disturbing memorials and keep noise levels to a minimum. [cite: 585]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Environmental - Fire Safety", description = "Campfires are not recommended due to the dense forest and high risk of fire. [cite: 587] Use portable stoves for cooking. [cite: 587]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Safety Tip - Flash Floods", description = "The Gasgas River crossings and lower parts of the trail can be prone to flash floods, especially during rainy season. [cite: 588] Be cautious and avoid crossing if the weather is bad. [cite: 589]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Safety Tip - Weather", description = "Expect cool temperatures, especially at higher elevations and the summit. Prepare for fluctuating weather conditions, and bring proper clothing. [cite: 590]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Safety Tip - Trail Hazards", description = "The trail is steep in some parts, especially near the summit. [cite: 591] There are sections with loose rocks and slippery paths. Use gloves and trekking poles for better grip and support. [cite: 592]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Safety Tip - Physical Fitness", description = "A challenging climb that requires good physical endurance and experience. Make sure you're prepared for the strenuous hike. [cite: 593]"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Safety Tip - Emergency Exit", description = "If necessary, an exit can be made via the lower sections of the trail before reaching the summit, though this should be done with the guidance of your local guide. [cite: 593]") // Source 593 for fitness and emergency exit
            ))


            checklistItemDao.insertAllItems(listOf(
                ChecklistItemEntity(itemId = "predef_001", name = "Sleeping Bags and Sleeping Mat", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_002", name = "Water Bottles (at least 2-3L)", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_003", name = "Headlamp or Flashlight (extra batteries)", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_004", name = "First Aid Kit", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_005", name = "Backpack with Rain Cover", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_006", name = "Proper Hiking Shoes", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_007", name = "Sun Protection (Sunscreen, Hat, Sunglasses)", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_008", name = "Rain Gear", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_009", name = "Trail Food/Snacks", isPreMade = true),
                ChecklistItemEntity(itemId = "predef_010", name = "Trash Bag ", isPreMade = true)
                // Add more common items
            ))

            Log.d("AppDatabaseCallback", "populateInitialData: FINISHED")
        }
    }
}
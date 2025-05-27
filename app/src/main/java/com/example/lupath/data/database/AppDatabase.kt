package com.example.lupath.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lupath.data.Converters
import com.example.lupath.data.database.dao.CampsiteDao
import com.example.lupath.data.database.dao.ChecklistItemDao
import com.example.lupath.data.database.dao.GuidelineDao
import com.example.lupath.data.database.dao.HikePlanDao
import com.example.lupath.data.database.dao.MountainDao
import com.example.lupath.data.database.dao.TrailDao
import com.example.lupath.data.database.entity.CampsiteEntity
import com.example.lupath.data.database.entity.ChecklistItemEntity
import com.example.lupath.data.database.entity.GuidelineEntity
import com.example.lupath.data.database.entity.HikePlanEntity
import com.example.lupath.data.database.entity.MountainEntity
import com.example.lupath.data.database.entity.TrailEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [
        MountainEntity::class, CampsiteEntity::class, TrailEntity::class,
        GuidelineEntity::class,
        ChecklistItemEntity::class,
        HikePlanEntity::class

    ],
    version = 9 ,
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
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(private val context: Context) : Callback() {
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
                    location = "Rodriguez (Montalban), Rizal",
                    masl = 723,
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate (3/9)",
                    hoursToSummit = "3–4 hours to the summit",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Mountain ridge with some rocky sections",
                    trekDurationDetails = "3–4 hours to the summit",
                    trailTypeDescription = "Forest trail with rocky sections and gradual inclines",
                    sceneryDescription = "Dense forests, ridges, and mountain landscapes",
                    viewsDescription = "Panoramic views of the Sierra Madre mountains and Wawa River",
                    wildlifeDescription = "Birds, insects, and native plants",
                    featuresDescription = "Ridges, forest trails, and stunning summit views",
                    hikingSeasonDetails = "November to February (cooler and drier conditions)",
                    introduction = "Mount Maynoba is located in Rodriguez (Montalban), Rizal, and " +
                            "stands at an elevation of about 723 meters above sea level (MASL). " +
                            " It's a popular mountain for both novice and intermediate " +
                            "hikers due to its manageable trails and beautiful views. " +
                            "The hike offers a mix of forested sections, open ridges, and rocky " +
                            "terrain, providing panoramic views of the Wawa River, Sierra Madre " +
                            "mountains, and nearby peaks. ",
                    tagline = "Let's Hike to Mt. Maynoba",
                    mountainImageRef1 = "mt_maynoba_1",
                    mountainImageRef2 = "mt_maynoba_2",
                    mountainImageRef3 = "mt_maynoba_3",

                    hasSteepSections = false,
                    notableWildlife = "Birds, Insects, Native Plants",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, description = "There are no formal campsites on Mount Maynoba, but the mountain has some flat areas " +
                        "near the summit and along the lower sections of the trail where hikers can pitch tents for a peaceful night under the stars. "),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, name = "Near the summit", description = "Flat spots perfect for tents and offering scenic views"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, name = "Water Sources", description = "Limited; carry at least 2–3 liters of water for the hike"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, name = "Best For", description = "Day hikers, beginner hikers, first-timers, overnight trips")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, name = "Main Trail", description = "The most common route from the jump-off point near Wawa River"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, name = "Alternative Trail", description = "A longer route from the nearby towns of San Isidro or Dulong Bayan")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, category = "Registration Fee", description = "₱20–₱50, payable at the barangay hall or jump-off"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, category = "Guide Fee", description = "Optional (₱300–₱500), helpful for first-time hikers"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, category = "Camping Fee", description = "₱20–₱50, depending on campsite availability"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, category = "Safety Tip", description = "The trail can be slippery after rainfall ensure proper footwear"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, category = "Safety Tip", description = "If camping, pack sufficient food and water, as supplies are limited"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaynobaId, category = "Best Season", description = "November to February (cooler and drier conditions)")
            ))

            // --- Mt. Daraitan Data ---
            val mtDaraitanId = "mtdrtn001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtDaraitanId,
                    mountainName = "Mt. Daraitan",
                    pictureReference = "mt_daraitan_main",
                    location = "Tanay, Rizal",
                    masl = 1130,
                    difficultySummary = "Moderate to Challenging",
                    difficultyText = "Moderate to Challenging (5/9)",
                    hoursToSummit = "3–5 hours to the summit",
                    bestMonthsToHike = "Not explicitly stated, but generally dry season (Nov-Feb) is preferred for most PH mountains.",
                    typeVolcano = "Limestone mountain with rich biodiversity",
                    trekDurationDetails = "3–5 hours to the summit",
                    trailTypeDescription = "Rocky and forested with sections of river crossing",
                    sceneryDescription = "Lush forests, limestone rock formations, valleys, and rivers",
                    viewsDescription = "Panoramic views of the Tanay plains, Sierra Madre mountains, and Daraitan River",
                    wildlifeDescription = "Rich biodiversity, including birds, butterflies, and native plants",
                    featuresDescription = "Limestone formations, river crossing, and panoramic views from the summit",
                    hikingSeasonDetails = "Not explicitly stated, assume dry season (November to February/March) for better conditions.",
                    introduction = "Mount Daraitan is a majestic peak located in Tanay, Rizal, rising " +
                            "to an elevation of about 1,130 meters above sea level (MASL). Known for " +
                            "its stunning limestone formations, lush forests, and the famous Daraitan " +
                            "River, this mountain has become a sought-after destination for hikers " +
                            "and nature lovers. The trail provides scenic views of the surrounding " +
                            "valleys and rivers, making it a great adventure for both novice and " +
                            "experienced hikers.",
                    tagline = "Let's Hike to Mt. Daraitan",
                    mountainImageRef1 = "mt_daraitan_1",
                    mountainImageRef2 = "mt_daraitan_2",
                    mountainImageRef3 = "mt_daraitan_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Butterflies, Native Plants",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, description = "Mount Daraitan offers some campsites near the base and lower sections of the trail, particularly near the " +
                        "Daraitan River. This river is a common spot for overnight stays, as it's both beautiful and tranquil, ideal for setting up camp."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, name = "Near Daraitan River", description = "Perfect for overnight stays with beautiful views of the river and mountains"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, name = "Near the summit", description = "Limited camping space due to steep terrain"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, name = "Water Sources", description = "Daraitan River, available at the jump-off point"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, name = "Best For", description = "Nature lovers, overnight campers, first-timers, and photography enthusiasts")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, name = "Main Trail", description = "The primary trail leading from the Daraitan Barangay Hall"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, name = "River Crossing", description = "Part of the trail involves crossing the Daraitan River")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, category = "Registration Fee", description = "₱20–₱50 at the Barangay Hall"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, category = "Guide Fee", description = "Required (₱500–₱800 depending on group size)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, category = "Camping Fee", description = "₱20–₱50 at river campsites"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, category = "Safety Tip", description = "Bring extra socks for crossing the river, as it can get slippery"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDaraitanId, category = "Safety Tip", description = "Ensure proper footwear for the rocky and steep sections")
            ))

            // --- Mt. Mapalad Data ---
            val mtMapaladId = "mtmpld001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtMapaladId,
                    mountainName = "Mt. Mapalad",
                    pictureReference = "mt_mapalad_main",
                    location = "Rodriguez (Montalban), Rizal",
                    masl = 500,
                    difficultySummary = "Easy to Moderate",
                    difficultyText = "Easy to Moderate (2/9 to 3/9)",
                    hoursToSummit = "2–3 hours to the summit",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Forested mountain with grassy ridges",
                    trekDurationDetails = "2–3 hours to the summit",
                    trailTypeDescription = "Forested paths and grassland ridges",
                    sceneryDescription = "Lush forests, ridges with expansive views, and nearby mountains",
                    viewsDescription = "Sierra Madre, Montalban valley, and nearby peaks",
                    wildlifeDescription = "Birds, insects, and various plant species",
                    featuresDescription = "Forest paths, grassy ridges, and sweeping views from the summit",
                    hikingSeasonDetails = "November to February (cooler and drier conditions)",
                    introduction = "Mount Mapalad is a relatively lesser-known mountain located in " +
                            "Rodriguez (Montalban), Rizal, with an elevation of around 500 meters " +
                            "above sea level (MASL). Known for its picturesque views of the Sierra " +
                            "Madre and Montalban Valley, it offers a refreshing escape for those " +
                            "looking to enjoy nature without the typical crowds. The trail features " +
                            "a mix of forests, grassy ridges, and small rocky sections, making it" +
                            " perfect for beginner to moderate hikers.",
                    tagline = "Let's Hike to Mt. Mapalad",
                    mountainImageRef1 = "mt_mapalad_1",
                    mountainImageRef2 = "mt_mapalad_2",
                    mountainImageRef3 = "mt_mapalad_3",

                    hasSteepSections = false,
                    notableWildlife = "Birds, Insects, Plant Species",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, description = "Camping on Mount Mapalad is possible, but it's best suited for those who prefer simple " +
                        "overnight stays with minimal facilities. The mountain offers some camping spots along the trail, particularly near the ridges and lower slopes."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, name = "Near the summit", description = "A few flat spots for tents with great views"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, name = "Lower sections", description = "Open grassy areas suitable for a basic camping setup"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, name = "Water Sources", description = "Wawa River and a few springs along the trail"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, name = "Best For", description = "Beginner hikers, family outings, and overnight campers")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, name = "Main Trail", description = "Starts from the Wawa River area or nearby barangays"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, name = "Alternative Trail", description = "From San Isidro, providing a longer route with more scenic views")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, category = "Registration Fee", description = "₱20–₱30 at the Barangay Hall"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, category = "Guide Fee", description = "Optional (₱300–₱500 per group), recommended for first-timers"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, category = "Camping Fee", description = "₱20–₱50, depending on the area"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, category = "Safety Tip", description = "Be aware of sudden weather changes—bring a raincoat or jacket"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, category = "Safety Tip", description = "Wear sturdy shoes for the grassy and occasionally slippery sections"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMapaladId, category = "Best Season", description = "November to February (cooler and drier conditions)")
            ))

            // --- Mt. Ayaas Data ---
            val mtAyaasId = "mtayaas001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtAyaasId,
                    mountainName = "Mt. Ayaas",
                    pictureReference = "mt_ayaas_main",
                    location = "Rodriguez (Montalban), Rizal",
                    masl = 720,
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate (3/9)",
                    hoursToSummit = "2–4 hours to the summit",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Forested mountain with rocky sections",
                    trekDurationDetails = "2–4 hours to the summit",
                    trailTypeDescription = "Forested paths with rocky and uneven sections",
                    sceneryDescription = "Forests, ridges, and expansive views of valleys and nearby peaks",
                    viewsDescription = "360-degree views of Sierra Madre, Montalban Valley, and other surrounding mountains",
                    wildlifeDescription = "Various bird species, insects, and some wild mammals",
                    featuresDescription = "A combination of forest trails, ridges, and rocky outcrops",
                    hikingSeasonDetails = "November to February (drier and cooler weather)",
                    introduction = "Mount Ayaas is located in Rodriguez (Montalban), Rizal, and rises " +
                            "to an elevation of 720 meters above sea level (MASL). It is a hidden gem " +
                            "for hikers looking for a peaceful, less crowded experience. The mountain " +
                            "offers a combination of forest trails, rocky ridges, and open areas that " +
                            "provide panoramic views of the Sierra Madre Mountains and the nearby " +
                            "valleys. Its accessibility makes it a great choice for day hikes and " +
                            "weekend outings.",
                    tagline = "Let's Hike to Mt. Ayaas",
                    mountainImageRef1 = "mt_ayaas_1",
                    mountainImageRef2 = "mt_ayaas_2",
                    mountainImageRef3 = "mt_ayaas_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Insects, Wild Mammals",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, description = "Camping is available on Mount Ayaas, especially in lower areas near the base and on flatter sections " +
                        "of the trail. There are a few campsites with stunning views of the surrounding landscapes, perfect for those looking to spend the night and experience the tranquility of the mountains."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, name = "Near the summit", description = "Small flat areas perfect for tents"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, name = "Lower sections", description = "Grassy fields and open spaces for a more relaxed camping experience"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, name = "Water Sources", description = "Springs near the base and along the trail"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, name = "Best For", description = "Beginner to intermediate hikers, family hikes, and campers")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, name = "Main Trail", description = "The main route starting from Barangay San Isidro, Rodriguez"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, name = "Alternative Routes", description = "Some trails connect with other nearby mountains like Mount Pamitinan and Mount Binacayan")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, category = "Registration Fee", description = "₱20–₱30 at the Barangay Hall"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, category = "Guide Fee", description = "Optional (₱300–₱500), helpful for first-time hikers"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, category = "Camping Fee", description = "₱20–₱50 depending on campsite location"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, category = "Safety Tip", description = "The trail can be slippery after rainfall, so wear proper footwear"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, category = "Safety Tip", description = "Bring sufficient water and snacks, especially if hiking for longer durations"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, category = "Best Season", description = "November to February (cooler and drier conditions)")
            ))

            // --- Mt. Mabilog Data ---
            val mtMabilogId = "mtmblg001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtMabilogId,
                    mountainName = "Mt. Mabilog",
                    pictureReference = "mt_mabilog_main",
                    location = "San Pablo City, Laguna",
                    masl = 441,
                    difficultySummary = "Easy",
                    difficultyText = "Easy (2/9)",
                    hoursToSummit = "2-3 hours",
                    bestMonthsToHike = "Best from November to February; avoid rainy season due to mud and leeches near trail\n",
                    typeVolcano = "Dormant volcano",
                    trekDurationDetails = "2-3 hours",
                    trailTypeDescription = "Loop or out-and-back, passing through farmlands and grasslands.",
                    sceneryDescription = "Panoramic views of the Seven Lakes of San Pablo, including " +
                            "Yambo and Pandin Lakes, and surrounding mountains like Banahaw, Cristobal, " +
                            "and Makiling",
                    viewsDescription = "Amazing view overlooking the lakes (Yambo, Pandin most visible), " +
                            "Mt. Banahaw, Mt. Cristobal and Mt Makiling",
                    wildlifeDescription = "Limited due to proximity to settlements, but some birds, lizards, and butterflies present\n",
                    featuresDescription = "Summit views of Seven Lakes, proximity to Mts. Banahaw, Cristobal, Makiling",
                    hikingSeasonDetails = "Best from November to February; avoid rainy season due to mud and leeches near trail\n",
                    introduction = "Mt. Mabilog is nestled between the 7 lakes of San Pablo City, Laguna, " +
                            "and surrounded by the giants Mts. Banahaw, Cristobal and Makiling. With " +
                            "the height elevation of 441 MASL, this place is a good starting ground " +
                            "for the rookies for its 2/9 minor difficulty. But though it's just a " +
                            "minor hike, there's still a challenge going to the top and the summit.",
                    tagline = "Let's Hike to Mt. Mabilog",
                    mountainImageRef1 = "mt_mabilog_1",
                    mountainImageRef2 = "mt_mabilog_2",
                    mountainImageRef3 = "mt_mabilog_3",

                    hasSteepSections = false,
                    notableWildlife = "Birds, Lizards, Butterflies",
                    isRocky = false,
                    isSlippery = true,
                    isEstablishedTrail = false
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAyaasId, description = "Mt. Mabilog is often visited as a day hike, but some choose to camp at the summit or along the lakesides below (e.g., Lake Pandin). " +
                        "The summit is grassy and can accommodate small groups for overnight stays, offering a cool breeze and sunrise views." ),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, name = "Summit:", description = "Small and exposed, best for good weather conditions"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, name = "Lake Pandin Lakeside:", description = "Accessible flat areas near jump-off"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, name = "Trek Time to Campsite:", description = "1.5 hours to summit; <30 minutes to lakeside"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, name = "Water Source:", description = "Available near Lake Pandin (not potable, treat before drinking)"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, name = "Best For", description = "Beginners, nature trekkers, campers who want lake and mountain experience\n")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, name = "Trail Option:", description = "Single established trail from Lake Pandin jump-off\n"),
            ))

            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Registration", description = "Register at the jump-off point in Barangay Sulsuguin, Nagcarlan."),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Guide Fee", description = "₱300–₱500 per group (local guides from Pandin community)\n"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Camping Fee", description = "₱50 at summit (optional); separate fees may apply for lakeside camps"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Safety Tip", description = "Trail can get slippery after rain—wear shoes with good traction"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Safety Tip", description = "Summit is exposed—avoid camping during thunderstorms"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Safety Tip", description = "Respect local communities and lake rules"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMabilogId, category = "Best Season", description = "Best from November to February; avoid rainy season due to mud and leeches near trail\n")
            ))

            // --- Mt. Kalisungan Data ---
            val mtKalisunganId = "mtklsgn001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtKalisunganId,
                    mountainName = "Mt. Kalisungan",
                    pictureReference = "mt_kalisungan_main",
                    location = "Calauan, Laguna (near Bay and Los Baños)",
                    masl = 760,
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate (4/9)",
                    hoursToSummit = "3-4 hours round trip",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Dormant hill/mountain (no volcanic activity)",
                    trekDurationDetails = "3-4 hours round trip",
                    trailTypeDescription = "Steep, direct assault trail with grassy and partially forested sections",
                    sceneryDescription = "Farmlands, open grasslands, Laguna towns, Mt. Makiling and the distant Banahaw-Cristobal range",
                    viewsDescription = "360-degree summit view of Laguna de Bay, Tagaytay Highlands, Mt. Makiling, and nearby lakes",
                    wildlifeDescription = "Common birds, small reptiles, butterflies; mostly disturbed due to farming activity",
                    featuresDescription = "Historical marker commemorating Filipino soldiers from WWII; summit cross and grassy rest area",
                    hikingSeasonDetails = "Best from November to February; slippery and leech-prone during the rainy months",
                    introduction = "Mount Kalisungan is a prominent standalone peak in Calauan, Laguna, " +
                            "known for its historical significance and scenic views of Laguna de Bay, " +
                            "Mt. Makiling, and the surrounding towns. It's one of the so-called \"Buntis\" " +
                            "or \"pregnant mountains\" of Laguna due to its rounded slopes. The trail " +
                            "consists of continuous ascents through grasslands and coconut plantations, " +
                            "making it a great leg workout for hikers.",
                    tagline = "Let's Hike to Mt. Kalisungan",
                    mountainImageRef1 = "mt_kalisungan_1",
                    mountainImageRef2 = "mt_kalisungan_2",
                    mountainImageRef3 = "mt_kalisungan_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Reptiles, Butterflies",
                    isRocky = false,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, description = "While Mt. Kalisungan is usually done as a day hike, the summit has a wide grassy area suitable for camping. " +
                        "There are also a few open clearings along the trail that can accommodate small groups."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, name = "Summit", description = "Wide grassy field, can accommodate tents for small to medium groups"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, name = "Mid-Trail Clearings", description = "Few shaded flat spots for rest or emergency overnight stays"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, name = "Trek Time to Campsite", description = "1.5-2.5 hours depending on pace"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, name = "Water Source", description = "None along the trail—bring at least 2-3 liters")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, name = "Brgy. Lamot or Brgy. Bambang Trail", description = "Single direct assault trail from Brgy. Lamot or Brgy. Bambang (most common route)") // [cite: 139, 140]
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Registration Fee", description = "₱20-P30 at the jump-off barangay hall"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Guide Fee", description = "₱500-P600 per group (required, especially for first-timers)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Camping Fee", description = "₱30-P50 for overnight stay at the summit"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Safety Tip", description = "Trail is steep and can be muddy—bring trekking poles if needed"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Safety Tip", description = "Summit is windy and exposed—secure tents well"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Safety Tip", description = "Start early to avoid the sun; the trail offers little shade"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalisunganId, category = "Hiking Season", description = "Best from November to February; slippery and leech-prone during the rainy months")
            ))

            // --- Mt. Makiling (via UPLB) Data ---
            val mtMakilingId = "mtmkling001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtMakilingId,
                    mountainName = "Mt. Makiling (via UPLB)",
                    pictureReference = "mt_makiling_main",
                    location = "Los Baños, Laguna",
                    masl = 1090,
                    difficultySummary = "Moderate to challenging",
                    difficultyText = "Moderate to challenging (6/9 on PH scale)",
                    hoursToSummit = "5-7 hours to Peak 2",
                    bestMonthsToHike = "November to March",
                    typeVolcano = "Dormant Stratovolcano",
                    trekDurationDetails = "5-7 hours to Peak 2 (10-12 hours round trip)",
                    trailTypeDescription = "Continuous forest trail with mud, roots, ropes, and steep ascents, especially near the summit",
                    sceneryDescription = "Dense rainforest, mossy trees, bamboo groves, ridge sections with limited views",
                    viewsDescription = "Peak 2 has limited views due to thick foliage, but ridge points offer glimpses of Laguna and Batangas plains",
                    wildlifeDescription = "Home to endemic flora and fauna including birds, frogs, " +
                            "orchids, and tree ferns; part of the Makiling Forest Reserve",
                    featuresDescription = "Known for the \"Peak 1 and 2\" summits, knife-edge ridges, " +
                            "sulfur vents (not active), and rich ecosystem used in biological studies",
                    hikingSeasonDetails = "Best from November to March; avoid June-October due to heavy rains and trail closure risk",
                    introduction = "Mount Makiling is a dormant stratovolcano rising majestically " +
                            "between the provinces of Laguna and Batangas. Rich in biodiversity and " +
                            "folklore, it is considered a protected forest reserve under the care of " +
                            "the University of the Philippines Los Baños (UPLB). The UPLB trail is " +
                            "the more established and educational route, offering a longer, forested " +
                            "hike that culminates in the summit called Peak 2.",
                    tagline = "Let's Hike to Mt. Makiling",
                    mountainImageRef1 = "mt_makiling_1",
                    mountainImageRef2 = "mt_makiling_2",
                    mountainImageRef3 = "mt_makiling_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Frogs, Orchids, Tree Ferns",
                    isRocky = false,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, description = "Camping is allowed at designated points with prior coordination. Most hikers do Mt. Makiling via UPLB as a day " +
                        "hike or a traverse, but there are flat areas near the summit and rest points along the trail."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, name = "Flat clearing near Peak 2", description = "Small groups can camp; expect wet and muddy conditions"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, name = "Agila Base (rest stop near mid-trail)", description = "Former ranger station and common rest point"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, name = "Trek Time to Campsite", description = "4-6 hours to Peak 2 depending on pace"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, name = "Water Source", description = "None along the trail-bring 3+ liters")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, name = "UPLB Trail", description = "Starts from the College of Forestry; possible to traverse to Sto. Tomas (Batangas side - MakTrav)")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Registration Fee", description = "P40-P50 at the Makiling Center for Mountain Ecosystems (MCME)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Guide Fee", description = "Optional but helpful for first-timers (P800-P1,200 for a group if arranged)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Camping Fee", description = "P30-P50 with prior coordination"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Permit", description = "Required for day hikes and overnights; coordinate with MCME or UPLB Forestry"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Safety Tip", description = "Trail can be extremely muddy-wear high-grip shoes"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Safety Tip", description = "Bring gloves and use ropes when ascending near summit"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Safety Tip", description = "Be alert for leeches especially in wet months"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMakilingId, category = "Hiking Season", description = "Best from November to March; avoid June-October due to heavy rains and trail closure risk")
            ))

            // --- Mt. Sembrano Data ---
            val mtSembranoId = "mtsembrano001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtSembranoId,
                    mountainName = "Mt. Sembrano",
                    pictureReference = "mt_sembrano_main",
                    location = "Pililla, Rizal / Jalajala, Rizal (bordering Laguna de Bay)",
                    masl = 745,
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate (4/9)",
                    hoursToSummit = "3-5 hours round trip",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Dormant mountain (formerly considered volcanic)",
                    trekDurationDetails = "3-5 hours round trip",
                    trailTypeDescription = "Single trail with a mix of rough dirt, rocks, grassy slopes, and short wooded segments",
                    sceneryDescription = "Rolling hills, lake views, coconut plantations, open ridges",
                    viewsDescription = "Laguna de Bay, Talim Island, Mt. Banahaw, Mt. Makiling, Metro Manila skyline on clear days",
                    wildlifeDescription = "Occasional birds, insects, goats, and native grasses",
                    featuresDescription = "Rock formations (e.g., \"Simba\" Rock), hot spring access nearby, wind-exposed summit ridge",
                    hikingSeasonDetails = "Best from November to February; avoid summer heat and rainy season for safety",
                    introduction = "Mount Sembrano is a scenic day hike located on the eastern side " +
                            "of Laguna de Bay, straddling the provinces of Rizal and Laguna. It was " +
                            "once believed to be a part of a volcanic system but is now classified " +
                            "as a dormant mountain. Its grassy summit offers panoramic views of " +
                            "Laguna de Bay, Talim Island, and the Sierra Madre range. With a mix of " +
                            "open trails and forest paths, it's a popular choice for beginners and t" +
                            "hose looking for a quick weekend climb near Manila.",
                    tagline = "Let's Hike to Mt. Sembrano",
                    mountainImageRef1 = "mt_sembrano_1",
                    mountainImageRef2 = "mt_sembrano_2",
                    mountainImageRef3 = "mt_sembrano_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Insects, Goats",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, description = "Mt. Sembrano's summit and a mid-trail area known as \"Kweba Campsite\" are commonly used for overnight stays. " +
                        "The trail is open and exposed, so camping is best done in dry weather."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, name = "Kweba Campsite (mid-trail)", description = "Shaded, flat; near rock formations and resting areas"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, name = "Summit Area", description = "Wide and grassy but exposed-great for sunrise and sunset views"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, name = "Trek Time to Campsite", description = "1.5-3 hours depending on pace"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, name = "Water Source", description = "Natural spring near trailhead; no water near summit-bring at least 2-3 liters")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, name = "Standard Trail", description = "Standard trail from Brgy. Malaya in Pililla, Rizal")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Registration Fee", description = "P30-P50 at the Brgy. Malaya registration area"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Guide Fee", description = "P500-P800 per group (optional for day hikes, recommended for overnights)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Camping Fee", description = "P30-P50 per person for overnight stays"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Safety Tip", description = "Summit is exposed-secure tents and avoid during thunderstorms"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Safety Tip", description = "Start early to avoid heat; trail has limited tree cover"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Safety Tip", description = "Be cautious of loose rocks and steep portions during descent"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSembranoId, category = "Hiking Season", description = "Best from November to February; avoid summer heat and rainy season for safety")
            ))

            // --- Mt. Romelo Data ---
            val mtRomeloId = "mtromelo001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtRomeloId,
                    mountainName = "Mt. Romelo",
                    pictureReference = "mt_romelo_main",
                    location = "Siniloan, Laguna",
                    masl = 300,
                    difficultySummary = "Easy to moderate",
                    difficultyText = "Easy to moderate (3/9)",
                    hoursToSummit = "2-4 hours round trip",
                    bestMonthsToHike = "December to March",
                    typeVolcano = "Low-lying mountain / hill (non-volcanic)",
                    trekDurationDetails = "2-4 hours round trip (longer if visiting multiple waterfalls)",
                    trailTypeDescription = "Muddy farm trails, river crossings, grassy and forest paths",
                    sceneryDescription = "Waterfalls, river streams, coconut plantations, lowland forests",
                    viewsDescription = "Limited summit view, but nice scenery along trails and near falls",
                    wildlifeDescription = "Frogs, insects, birds, and aquatic life near river systems",
                    featuresDescription = "Numerous waterfalls including Buruwisan, Lansones, Batya-Batya, and Sampaloc Falls; rope-assisted descents at some parts",
                    hikingSeasonDetails = "Best from December to March; avoid rainy season due to slippery trails and flash flood risk",
                    introduction = "Mount Romelo, located in Siniloan, Laguna, is a popular beginner-friendly " +
                            "hiking destination known not just for its summit, but for the multiple " +
                            "waterfalls scattered along its trail. Often dubbed the \"Falls Destination of Laguna,\" " +
                            "Mt. Romelo is an excellent choice for casual hikers and campers seeking " +
                            "both a scenic mountain trek and refreshing dips in waterfalls.",
                    tagline = "Let's Hike to Mt. Romelo",
                    mountainImageRef1 = "mt_romelo_1",
                    mountainImageRef2 = "mt_romelo_2",
                    mountainImageRef3 = "mt_romelo_3",

                    hasSteepSections = false,
                    notableWildlife = "Frogs, Insects, Birds",
                    isRocky = false,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, description = "Mt. Romelo has several camping areas near the waterfalls, particularly near Buruwisan Falls, the most iconic and " +
                        "frequented site. These areas are spacious, grassy, and shaded, making them ideal for overnight treks or weekend camping."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, name = "Buruwisan Falls Base", description = "Main campsite; wide and near water source"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, name = "Batya-Batya & Lansones Vicinity", description = "Quieter, smaller campsites for those venturing further"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, name = "Trek Time to Campsite", description = "1.5-2.5 hours"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, name = "Water Source", description = "Abundant-waterfalls and streams (though still advisable to filter or boil)")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, name = "Standard Trail", description = "Single established trail starting from Brgy. Macatad")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Registration Fee", description = "P50-P70 at the jump-off (includes environmental and maintenance fee)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Guide Fee", description = "P300-P500 per group (optional but useful for navigating waterfall paths)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Camping Fee", description = "P50-P100 depending on area and season"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Safety Tip", description = "Trail is very muddy especially after rain-wear waterproof footwear"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Safety Tip", description = "Exercise caution near waterfalls-some have slippery rocks and rope descents"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Safety Tip", description = "Watch your belongings; petty theft was previously reported in the area"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtRomeloId, category = "Hiking Season", description = "Best from December to March; avoid rainy season due to slippery trails and flash flood risk")
            ))

            // --- Mt. Tagapo Data ---
            val mtTagapoId = "mttagapo001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtTagapoId,
                    mountainName = "Mt. Tagapo",
                    pictureReference = "mt_tagapo_main",
                    location = "Talim Island, Binangonan, Rizal (in Laguna de Bay)",
                    masl = 438,
                    difficultySummary = "Easy to moderate",
                    difficultyText = "Easy to moderate (3/9)",
                    hoursToSummit = "2-3 hours round trip",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Hill-type peak (non-volcanic, sedimentary origin)",
                    trekDurationDetails = "2-3 hours round trip",
                    trailTypeDescription = "Gradual ascent through grasslands, bamboo groves, and open ridges",
                    sceneryDescription = "Lake views, island villages, grass fields, bamboo forests",
                    viewsDescription = "360-degree view of Laguna de Bay, Metro Manila skyline, Mt. Sembrano, Sierra Madre range",
                    wildlifeDescription = "Birds, insects, goats; trail passes through rural and farmed areas",
                    featuresDescription = "Distinct cone-shaped summit, remote island atmosphere, breezy ridgelines",
                    hikingSeasonDetails = "Best from November to February; very hot during summer and trail may be overgrown during rainy season",
                    introduction = "Mount Tagapo, locally known as \"Bundok ng Susong Dalaga\" due " +
                            "to its conical, breast-shaped summit, is the highest point on Talim Island, " +
                            "located at the center of Laguna de Bay. Accessible via a short boat ride " +
                            "from the mainland, Mt. Tagapo offers a unique island hiking experience " +
                            "with panoramic views of the largest lake in the Philippines. Its open " +
                            "grassland trail and wide summit make it ideal for beginners and scenic " +
                            "trekkers.",
                    tagline = "Let's Hike to Mt. Tagapo",
                    mountainImageRef1 = "mt_tagapo_1",
                    mountainImageRef2 = "mt_tagapo_2",
                    mountainImageRef3 = "mt_tagapo_3",

                    hasSteepSections = false,
                    notableWildlife = "Birds, Insects, Goats",
                    isRocky = false,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, description = "Camping is possible at the summit clearing, which is wide and grassy but fully exposed. There are also " +
                        "small flat areas near the trailhead that can serve as base camps for groups staying overnight on the island."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, name = "Summit Clearing", description = "Offers great sunrise/sunset views; flat but exposed to wind and heat"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, name = "Trailhead Area", description = "Near Brgy. Janosa; convenient for pre/post-hike base camp"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, name = "Trek Time to Campsite", description = "1-1.5 hours to summit"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, name = "Water Source", description = "None along trail-bring 2-3 liters; water is scarce on the island")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, name = "Standard Trail", description = "Single established trail from Brgy. Janosa")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Registration Fee", description = "P40-P50 at the barangay hall in Janosa"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Guide Fee", description = "P300-P500 per group (mandatory for first-timers)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Camping Fee", description = "P30-P50 per person"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Boat Fare", description = "P30-P50 one-way from Binangonan Port to Talim Island (public boat)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Safety Tip", description = "Summit is fully exposed-bring sun protection and secure tents if camping"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Safety Tip", description = "Trail is dry but can be slippery when muddy-use proper footwear"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Safety Tip", description = "Coordinate boat schedule ahead of time; last trip back to mainland may be early"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTagapoId, category = "Hiking Season", description = "Best from November to February; very hot during summer and trail may be overgrown during rainy season")
            ))

            // --- Mt. Arayat Data ---
            val mtArayatId = "mtarayat001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtArayatId,
                    mountainName = "Mt. Arayat",
                    pictureReference = "mt_arayat_main",
                    location = "Arayat and Magalang, Pampanga",
                    masl = 1026,
                    difficultySummary = "Challenging",
                    difficultyText = "Challenging (7/9)",
                    hoursToSummit = "6-9 hours round trip (via North Peak only)",
                    bestMonthsToHike = "November to March",
                    typeVolcano = "Extinct stratovolcano",
                    trekDurationDetails = "6-9 hours round trip (via North Peak only); 8-10 hours if traversing South to North Peak",
                    trailTypeDescription = "Steep forest trails, rocky ascents, knife-edge ridges (on traverse), dense vegetation",
                    sceneryDescription = "Lush tropical forests, mossy paths, scenic ridgelines",
                    viewsDescription = "Central Luzon plains, Mount Pinatubo, Mt. Balungao, Pampanga River; excellent summit views",
                    wildlifeDescription = "Home to native birds, lizards, wild boars, and rare plants; part of Mt. Arayat National Park",
                    featuresDescription = "Twin peaks (North and South), White Rock (a scenic viewpoint), and legendary folklore sites",
                    hikingSeasonDetails = "Best from November to March; trails are slippery and dangerous during heavy rains",
                    introduction = "Mount Arayat is an extinct stratovolcano located in the province " +
                            "of Pampanga, towering over the Central Luzon plains. Steeped in folklore " +
                            "and believed to be the home of the legendary Kapampangan god Sinukuan, " +
                            "this mountain features two major peaks and dense forest trails. Although " +
                            "it no longer exhibits volcanic activity, Mt. Arayat remains a challenging " +
                            "climb due to its rugged terrain and steep ascents.",
                    tagline = "Let's Hike to Mt. Arayat",
                    mountainImageRef1 = "mt_arayat_1",
                    mountainImageRef2 = "mt_arayat_2",
                    mountainImageRef3 = "mt_arayat_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Lizards, Wild Boars, Rare Plants",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, description = "Mt. Arayat is commonly done as a day hike, but there are campable areas especially on the Magalang side " +
                        "and along the ridgeline between the two peaks. Overnight traverses are also possible with proper permits."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, name = "White Rock View Deck (Magalang side)", description = "Scenic and flat; can accommodate small tents"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, name = "Between North and South Peaks", description = "Narrow but campable for overnight traverses"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, name = "Trek Time to Campsite", description = "4-6 hours depending on trail and pace"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, name = "Water Source", description = "None along the trail-bring at least 3 liters")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, name = "Magalang Trail (North Peak)", description = "Forested, gradual at start, then steep and technical"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, name = "Arayat Trail (South Peak)", description = "Steeper and more rugged; used for traverses")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Registration Fee", description = "P50-P100 (varies by trailhead, Magalang or Arayat side)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Guide Fee", description = "P1,000-P1,500 per group (required for traverses and overnight hikes)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Camping Fee", description = "P50-P100 depending on site and LGU policy"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Permit", description = "Required for traverses and overnight hikes; coordinate with DENR and local tourism offices"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Safety Tip", description = "Trails can be overgrown-wear long sleeves and be cautious of snakes"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Safety Tip", description = "North Peak has steep sections with loose rocks-use trekking poles"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Safety Tip", description = "Start early to avoid hiking in the dark on descent"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtArayatId, category = "Hiking Season", description = "Best from November to March; trails are slippery and dangerous during heavy rains")
            ))

            // --- Mt. Tapulao (High Peak) Data ---
            val mtTapulaoId = "mttapulao001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtTapulaoId,
                    mountainName = "Mt. Tapulao (High Peak)",
                    pictureReference = "mt_tapulao_main",
                    location = "Palauig, Zambales",
                    masl = 2037,
                    difficultySummary = "Challenging",
                    difficultyText = "Challenging (8/9)",
                    hoursToSummit = "12-15 hours round trip",
                    bestMonthsToHike = "November to March",
                    typeVolcano = "Non-active volcano / highland mountain",
                    trekDurationDetails = "12-15 hours round trip (overnight recommended)",
                    trailTypeDescription = "Mixed trail with forested paths, steep ascents, rocky and grassy ridges",
                    sceneryDescription = "Pine forests, mossy forest, wide open grasslands, panoramic summit views",
                    viewsDescription = "360-degree views of Zambales range, West Philippine Sea, and nearby provinces",
                    wildlifeDescription = "Native birds, small mammals, unique flora such as orchids and ferns",
                    featuresDescription = "Abandoned chromite mine near summit, camping grounds, cold mountain climate",
                    hikingSeasonDetails = "Best from November to March; avoid rainy season for safety and trail conditions",
                    introduction = "Mount Tapulao, also known as High Peak, is the highest mountain " +
                            "in Zambales province and part of the Zambales Mountain Range. It is a " +
                            "favorite for mountaineers due to its challenging trails, cold climate, " +
                            "and diverse landscapes ranging from pine forests to grasslands near the " +
                            "summit. The mountain is famous for its scenic rolling hills, open ridges, " +
                            "and the remnants of an abandoned chromite mine near the summit.",
                    tagline = "Let's Hike to Mt. Tapulao",
                    mountainImageRef1 = "mt_tapulao_1",
                    mountainImageRef2 = "mt_tapulao_2",
                    mountainImageRef3 = "mt_tapulao_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Mammals, Orchids, Ferns",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, description = "Several established campsites are located near the summit, including the Mine Camp and " +
                        "the Summit Camp. These sites have flat grassy areas ideal for pitching tents and enjoying cool mountain air."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, name = "Mine Camp", description = "Former mining site, flat and spacious, near summit"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, name = "Summit Camp", description = "Close to peak, excellent for sunrise views"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, name = "Trek Time to Campsite", description = "8-10 hours depending on pace"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, name = "Water Source", description = "Limited water sources-carry at least 3-4 liters; small spring near base camp but often seasonal")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, name = "Main Trail", description = "Main trail from Barangay Salaza (Palauig)")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Registration Fee", description = "P50-P100 at the DENR office or trailhead"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Guide Fee", description = "P1,000-P1,500 per group (recommended for safety)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Camping Fee", description = "P50 per person"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Safety Tip", description = "Prepare for cold weather, especially at night-bring warm clothing"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Safety Tip", description = "Trail can be slippery and steep; trekking poles recommended"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Safety Tip", description = "Limited mobile signal; inform someone before hiking"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTapulaoId, category = "Hiking Season", description = "Best from November to March; avoid rainy season for safety and trail conditions")
            ))

            // --- Mt. Damas Data ---
            val mtDamasId = "mtdamas001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtDamasId,
                    mountainName = "Mt. Damas",
                    pictureReference = "mt_damas_main",
                    location = "Zambales",
                    masl = 600,
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate (5/9)",
                    hoursToSummit = "4-6 hours round trip",
                    bestMonthsToHike = "November to April",
                    typeVolcano = "Non-volcanic mountain",
                    trekDurationDetails = "4-6 hours round trip",
                    trailTypeDescription = "Forest trails with moderate inclines, dirt paths, some rocky sections",
                    sceneryDescription = "Dense forest canopy, wildflowers, mountain and sea views from summit",
                    viewsDescription = "Zambales mountain range, South China Sea coastline, nearby lowlands",
                    wildlifeDescription = "Forest birds, butterflies, occasional small mammals",
                    featuresDescription = "Quiet summit, diverse flora, tranquil atmosphere",
                    hikingSeasonDetails = "Best during dry months from November to April; avoid rainy season for slippery trails and safety",
                    introduction = "Mount Damas is a lesser-known mountain located in Zambales, " +
                            "offering a quieter and more remote hiking experience compared to popular " +
                            "peaks in the region. It features forested trails, moderate elevation, " +
                            "and provides panoramic views of the surrounding mountain ranges and coastal " +
                            "areas. It's favored by hikers who prefer off-the-beaten-path adventures " +
                            "with minimal crowds.",
                    tagline = "Let's Hike to Mt. Damas",
                    mountainImageRef1 = "mt_damas_1",
                    mountainImageRef2 = "mt_damas_2",
                    mountainImageRef3 = "mt_damas_3",

                    hasSteepSections = false,
                    notableWildlife = "Birds, Butterflies, Mammals",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, description = "Mt. Damas has limited formal campsites but some flat areas near the summit and along " +
                        "the trail can be used for camping. It's best suited for day hikes but overnight stays are possible for experienced hikers who bring their own gear."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, name = "Summit Area", description = "Flat and open enough for small tents"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, name = "Mid-Trail Clearings", description = "Suitable for brief rest or bivouac"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, name = "Trek Time to Campsite", description = "3-4 hours depending on pace"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, name = "Water Source", description = "Scarce along trail; hikers should carry adequate water (at least 2-3 liters)")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, name = "Established Trail", description = "Single established trail from nearby barangay jump-off point")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Registration Fee", description = "P30-P50 at trailhead (local barangay)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Guide Fee", description = "P300-P500 per group (optional but recommended)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Camping Fee", description = "Usually free; coordinate with local officials"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Safety Tip", description = "Wear insect repellent; forest trails have mosquitoes and ticks"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Safety Tip", description = "Bring enough water; no reliable water source on the trail"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Safety Tip", description = "Inform locals or rangers of your plans, especially if camping overnight"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtDamasId, category = "Hiking Season", description = "Best during dry months from November to April; avoid rainy season for slippery trails and safety")
            ))

            // --- Mt. Balingkilat Data ---
            val mtBalingkilatId = "mtbalingkilat001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtBalingkilatId,
                    mountainName = "Mt. Balingkilat",
                    pictureReference = "mt_balingkilat_main",
                    location = "Zambales",
                    masl = 1027,
                    difficultySummary = "Moderate to challenging",
                    difficultyText = "Moderate to challenging (6/9)",
                    hoursToSummit = "6-8 hours round trip",
                    bestMonthsToHike = "November to April",
                    typeVolcano = "Non-volcanic mountain",
                    trekDurationDetails = "6-8 hours round trip",
                    trailTypeDescription = "Steep forested trails, rocky sections, mossy forest near summit",
                    sceneryDescription = "Dense mossy forests, diverse flora, panoramic views at summit",
                    viewsDescription = "Coastal plains of Zambales, mountain ridges, nearby sea",
                    wildlifeDescription = "Native birds, insects, small mammals, orchids, and ferns",
                    featuresDescription = "Mossy forest, steep knife-edge ridges, relatively undisturbed habitat",
                    hikingSeasonDetails = "Best during dry months, November to April; avoid heavy rains and typhoon season",
                    introduction = "Mount Balingkilat is a forested mountain located in the province " +
                            "of Zambales, known for its moderately challenging trails and rich " +
                            "biodiversity. The mountain is part of the Zambales Mountain Range and " +
                            "offers lush mossy forests, steep ascents, and rewarding summit views " +
                            "of nearby peaks and coastal plains.",
                    tagline = "Let's Hike to Mt. Balingkilat",
                    mountainImageRef1 = "mt_balingkilat_1",
                    mountainImageRef2 = "mt_balingkilat_2",
                    mountainImageRef3 = "mt_balingkilat_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Insects, Mammals, Orchids, Ferns",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, description = "Campsites are available near the summit area, with flat and shaded spaces suitable for tents. " +
                        "The mountain is less crowded, making it ideal for campers looking for a serene environment."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, name = "Summit Plateau", description = "Flat area ideal for camping with good ventilation"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, name = "Mid-Trail Clearings", description = "Small rest areas for short breaks"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, name = "Trek Time to Campsite", description = "4-6 hours depending on pace"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, name = "Water Source", description = "Limited; hikers should bring sufficient water (3+ liters)")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, name = "Main Trail", description = "Main trail from Barangay Bayabasan or nearby jump-off points")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Registration Fee", description = "P50 at trailhead or local barangay office"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Guide Fee", description = "P500-P800 per group (recommended for safety and navigation)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Camping Fee", description = "P50 per person"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Safety Tip", description = "Trail can be slippery during rainy season-wear appropriate footwear"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Safety Tip", description = "Carry warm clothing as mossy forest can be cold and humid"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Safety Tip", description = "Inform local officials about your hiking plans"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtBalingkilatId, category = "Hiking Season", description = "Best during dry months, November to April; avoid heavy rains and typhoon season")
            ))

            // --- Mt. Cinco Picos Data ---
            val mtCincoPicosId = "mtcincopicos001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtCincoPicosId,
                    mountainName = "Mt. Cinco Picos",
                    pictureReference = "mt_cinco_picos_main",
                    location = "Subic, Zambales",
                    masl = 1100,
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate (5/9)",
                    hoursToSummit = "4-6 hours",
                    bestMonthsToHike = "November to March",
                    typeVolcano = "Rugged coastal mountain",
                    trekDurationDetails = "4-6 hours",
                    trailTypeDescription = "Out-and-back with exposed ridges, dry sections, and river crossings",
                    sceneryDescription = "Views of Subic Bay, Silanguin Cove, and neighboring peaks",
                    viewsDescription = "Coastal vistas, rolling hills, and distant coves",
                    wildlifeDescription = "Tropical vegetation, occasional bird sightings, dryland flora",
                    featuresDescription = "Five visible peaks, river crossings, and ridge campsites",
                    hikingSeasonDetails = "Best from November to March; avoid rainy season for safety and comfort",
                    introduction = "Cinco Picos, also known as Tatlong Tirad to the local Aetas, is a prominent hiking destination in Subic, Zambales. Once part of the former U.S. Naval training grounds, it now attracts adventurers for its rugged terrain, scenic ridges, and proximity to coves and coastline. Its name refers to the five distinct peaks visible along the trail, with panoramic views of the Zambales mountains and coastal areas making it a worthwhile climb.",
                    tagline = "Let's Hike to Mt. Cinco Picos",
                    mountainImageRef1 = "mt_cinco_picos_1",
                    mountainImageRef2 = "mt_cinco_picos_2",
                    mountainImageRef3 = "mt_cinco_picos_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, description = "Cinco Picos has one main designated campsite on the ridge area suitable for small to medium-sized groups. " +
                        "It is exposed and lacks water sources, so hikers must bring sufficient supplies."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, name = "Ridge campsite", description = "Primary overnight area with scenic views, flat terrain"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, name = "Trek Time to Campsite", description = "3-4 hours depending on pace"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, name = "Water Source", description = "None-bring 2.5 to 3 liters per person"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, name = "Best For", description = "Intermediate hikers, overnight campers, coastal mountain explorers")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, name = "Standard Trail", description = "Standard out-and-back trail from Sitio Cawag")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Registration Fee", description = "P60 per person"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Guide Fee", description = "P700-P1,000 per group (mandatory)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Camping Fee", description = "Usually included in registration; confirm locally"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Safety Tip", description = "Check weather conditions-ridges are exposed during storms"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Safety Tip", description = "Wear durable shoes-trail includes rocky and slippery sections"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Safety Tip", description = "Bring sufficient hydration-no water sources on trail or at campsite"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Safety Tip", description = "Respect trail and local customs-logbook and courtesy calls required"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCincoPicosId, category = "Hiking Season", description = "Best from November to March; avoid rainy season for safety and comfort")
            ))

            // --- Mt. Cristobal Data ---
            val mtCristobalId = "mtcristobal001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtCristobalId,
                    mountainName = "Mt. Cristobal",
                    pictureReference = "mt_cristobal_main",
                    location = "Dolores, Quezon / Laguna border",
                    masl = 1470,
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate 4/9)",
                    hoursToSummit = "4-5 hours round trip",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Stratovolcano",
                    trekDurationDetails = "4-5 hours round trip",
                    trailTypeDescription = "Class 3 trail with steep ascents, dense forests, and occasional river crossings",
                    sceneryDescription = "Mossy forests, ferns, hanging orchids, panoramic views of nearby mountains and Tayabas Bay",
                    viewsDescription = "Views of Tayabas Bay and neighboring peaks",
                    wildlifeDescription = "Mosses, ferns, orchids, forest-dwelling flora and fauna",
                    featuresDescription = "Dense jungle atmosphere, cool weather, and eerie ambiance tied to local folklore",
                    hikingSeasonDetails = "Best during November to February (cooler and drier months). Avoid during rainy season due to slippery trails and reduced visibility.",
                    introduction = "Mt. Cristobal is one of the most talked-about mountains in Luzon " +
                            "due to legends and paranormal stories, earning it the nickname \"Devil's Mountain.\" " +
                            "Despite the eerie reputation, it is a scenic and well-preserved wildlife " +
                            "sanctuary with lush mossy forests, hanging orchids, and a cool, misty ambiance. " +
                            "The mountain sits near the border of Laguna and Quezon, offering a " +
                            "uniquely mystical yet enriching hiking experience.",
                    tagline = "Let's Hike to Mt. Cristobal",
                    mountainImageRef1 = "mt_cristobal_1",
                    mountainImageRef2 = "mt_cristobal_2",
                    mountainImageRef3 = "mt_cristobal_3",

                    hasSteepSections = true,
                    notableWildlife = "Orchids, Ferns",
                    isRocky = false,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, description = "Mt. Cristobal offers modest camping spots within forest clearings that can accommodate small to medium-sized groups. " +
                        "While facilities are minimal and water is unavailable, the peaceful setting and cool climate are ideal for overnight hikers seeking solitude."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, name = "Forest zones near summit", description = "Within forest zones near summit (exact location not specified)"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, name = "Campsite Capacity", description = "Small to medium groups"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, name = "Amenities", description = "Basic; no potable water sources"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, name = "Water Source", description = "None - bring at least 2.5-3 liters"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, name = "Best For", description = "Nature lovers, mystic trail seekers, overnight hikers")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, name = "Main Trail", description = "Steep, forested trail with river crossings and muddy sections")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Registration Fee", description = "No formal registration fee (donations encouraged)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Safety Tip", description = "Avoid ridges during storms (lightning risk)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Safety Tip", description = "Wear sturdy, non-slip shoes-trails can be muddy and slippery"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Safety Tip", description = "Bring sun protection and warm layers-weather can shift quickly"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Safety Tip", description = "Hydrate properly-bring at least 2.5-3 liters of water"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Leave No Trace", description = "Carry out all trash and preserve natural elements"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtCristobalId, category = "Hiking Season", description = "Best during November to February (cooler and drier months). Avoid during rainy " +
                        "season due to slippery trails and reduced visibility.")
            ))

            // --- Mt. Malepunyo Data ---
            val mtMalepunyoId = "mtmalepunyo001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtMalepunyoId,
                    mountainName = "Mt. Malepunyo",
                    pictureReference = "mt_malepunyo_main",
                    location = "Lipa City, Batangas",
                    masl = 1003,
                    difficultySummary = "Easy to moderate",
                    difficultyText = "Easy to moderate (3/9)",
                    hoursToSummit = "3-4 hours",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Andesitic Stratovolcano",
                    trekDurationDetails = "3-4 hours",
                    trailTypeDescription = "Class 3 trail with steep ascents, dense forests, and occasional river crossings",
                    sceneryDescription = "Verdant forests, mossy areas, and panoramic views of the surrounding provinces",
                    viewsDescription = "Overlooking Batangas lowlands and nearby mountain ranges",
                    wildlifeDescription = "Forest-dwelling birds, mosses, orchids, and small mammals",
                    featuresDescription = "Peaks 1, 2, and 3 with varied terrain; Peak 3 offers the widest campsite",
                    hikingSeasonDetails = "Best during November to February (cool and dry). Avoid hiking during rainy season due to slippery trails and possible river swells.",
                    introduction = "The Malepunyo Mountain Range is home to the tallest peak in " +
                            "Batangas-Mt. Malepunyo (also spelled Malipunyo), rising to over 1,000 MASL. " +
                            "It forms a scenic pair with nearby Mt. Manabu and offers a traversable " +
                            "route between the two. The mountain is enveloped by dense forests, mossy " +
                            "sections, and steep paths that challenge and delight hikers. Located in " +
                            "Lipa City, Mt. Malepunyo is accessible yet remote enough to provide a " +
                            "peaceful nature escape.",
                    tagline = "Let's Hike to Mt. Malepunyo",
                    mountainImageRef1 = "mt_malepunyo_1",
                    mountainImageRef2 = "mt_malepunyo_2",
                    mountainImageRef3 = "mt_malepunyo_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Mammals, Orchids",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, description = "Mt. Malepunyo features three key camping spots across its peaks. Peak 3 is the most suitable for " +
                        "large groups, while Peaks 1 and 2 offer smaller, shaded options for more private or compact overnight stays."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, name = "Peak 1", description = "Limited space, covered with trees and shrubs"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, name = "Peak 2", description = "5-10 people capacity"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, name = "Peak 3", description = "Up to 50 people capacity"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, name = "Amenities", description = "Basic; no potable water sources"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, name = "Water Source", description = "None - bring at least 2.5-3 liters"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, name = "Best For", description = "Beginners, forest lovers, groups, overnight hikers")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, name = "Main Trail", description = "Forested with occasional river crossings and mossy terrain"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, name = "Traverse Option", description = "Connects Mt. Malepunyo with Mt. Manabu for extended treks")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Registration Fee", description = "P10-P20 (at Barangay Hall)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Safety Tip", description = "Avoid ridges and forested sections during storms"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Safety Tip", description = "Wear sturdy, non-slip footwear for forest and rocky paths"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Safety Tip", description = "Bring sun protection and light rain gear-weather can be unpredictable"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Safety Tip", description = "Hydrate well-bring at least 2.5-3 liters of water"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Leave No Trace", description = "Take all trash back down and respect nature"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMalepunyoId, category = "Hiking Season", description = "Best during November to February (cool and dry). Avoid hiking during rainy " +
                        "season due to slippery trails and possible river swells.")
            ))

            // --- Mt. Marami Data ---
            val mtMaramiId = "mtmarami001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtMaramiId,
                    mountainName = "Mt. Marami",
                    pictureReference = "mt_marami_main",
                    location = "Maragondon, Cavite",
                    masl = 405,
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate 4/9 (Beginner-friendly but long and exposed)",
                    hoursToSummit = "4-6 hours round trip",
                    bestMonthsToHike = "November to February",
                    typeVolcano = "Dormant stratovolcano",
                    trekDurationDetails = "4-6 hours round trip",
                    trailTypeDescription = "Out-and-back with multiple route variations",
                    sceneryDescription = "Open grasslands, forest paths, river crossings, panoramic summit views",
                    viewsDescription = "Cavite lowlands, nearby mountain ranges, and coastal silhouettes on clear days",
                    wildlifeDescription = "Forest birds, butterflies, and river life (crabs, fish)",
                    featuresDescription = "Silyang Bato, rolling hills, and branching trails",
                    hikingSeasonDetails = "Best during November to February for cooler and drier conditions. Avoid hiking after heavy rains due to river levels and muddy terrain. Summer hikes are possible but bring sun protection and hike early to avoid midday heat.",
                    introduction = "Mt. Marami, located in Maragondon, Cavite, is a unique low-elevation " +
                            "hike known for its iconic Silyang Bato, a massive chair-shaped rock formation perched " +
                            "at the summit. Though only around 405 MASL, the trek is deceptively " +
                            "challenging due to its long trail, muddy conditions, and multiple route " +
                            "branches, earning it the nickname \"Labyrinth of Trails.\" It offers open " +
                            "landscapes, scenic ridges, and forest passages, attracting both beginners " +
                            "and seasoned hikers looking for a full-day adventure.",
                    tagline = "Let's Hike to Mt. Marami",
                    mountainImageRef1 = "mt_marami_1",
                    mountainImageRef2 = "mt_marami_2",
                    mountainImageRef3 = "mt_marami_3",

                    hasSteepSections = false,
                    notableWildlife = "Birds, Butterflies, Crabs, Fish",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = false
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, description = "Camping is permitted at grassy sections near the summit and along the final trail ridge. The area near Silyang Bato is the " +
                        "most popular spot, particularly for sunrise and sunset views. However, amenities are nonexistent, and water sources are unreliable or seasonal."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, name = "Near Silyang Bato / Open areas before summit", description = "Open areas before summit / near Silyang Bato"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, name = "Campsite Capacity", description = "5-10 tents (dispersed)"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, name = "Amenities", description = "None"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, name = "Trek Time to Campsite", description = "3.5-5 hours depending on pace"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, name = "Water Source", description = "Limited - bring 2-3 liters"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, name = "Best For", description = "Beginners looking for a challenge, overnight campers, sunrise photographers")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, name = "Main Route", description = "From Brgy. Ramirez, Magallanes. Note: Multiple offshoots and unmarked paths exist - guides are highly recommended"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, name = "Alternate Route", description = "From Brgy. Talispungo, Maragondon. Note: Multiple offshoots and unmarked paths exist - guides are highly recommended")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Registration Fee", description = "P65 per person"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Guide Fee", description = "P500 for 3-4 hikers (negotiable)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Safety Tip", description = "Silyang Bato has dangerous drop-offs - exercise extreme caution"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Safety Tip", description = "Trails are muddy and slippery in rainy season - use trekking poles"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Safety Tip", description = "River crossings can be impassable after heavy rains - do not force a crossing"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Safety Tip", description = "Bring 2-3 liters of water and high-energy snacks"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Safety Tip", description = "Weak signal throughout the trail; spotty reception near the summit"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Safety Tip", description = "Wear sun protection - trails have long exposed stretches"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Safety Tip", description = "Wear durable, waterproof hiking shoes or sandals with grip"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtMaramiId, category = "Hiking Season", description = "Best during November to February for cooler and drier conditions. Avoid hiking after heavy " +
                        "rains due to river levels and muddy terrain. Summer hikes are possible but bring sun protection and hike early to avoid midday heat.")
            ))

            // --- Mt. Lubog Data ---
            val mtLubogId = "mtlubog001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtLubogId,
                    mountainName = "Mt. Lubog",
                    pictureReference = "mt_lubog_main",
                    location = "Rodriguez, Rizal (near Bulacan border)",
                    masl = 955,
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate 4/9)",
                    hoursToSummit = "3-4 hours including sidetrip to Lubog Cave",
                    bestMonthsToHike = "December to February",
                    typeVolcano = "Limestone mountain",
                    trekDurationDetails = "3-4 hours including sidetrip to Lubog Cave",
                    trailTypeDescription = "Out-and-back",
                    sceneryDescription = "Tropical rainforest, limestone cliffs, and panoramic Sierra Madre views",
                    viewsDescription = "Exposed rock edges overlooking mountain ranges and forest canopy",
                    wildlifeDescription = "Tropical vegetation, occasional birds, cave-dwelling species",
                    featuresDescription = "Summit limestone outcrops, Lubog Cave sidetrip, rugged access road",
                    hikingSeasonDetails = "Best from December to February; trail access difficult during rainy months due to rough roads",
                    introduction = "Mt. Lubog is a limestone-capped mountain located in Rodriguez, " +
                            "Rizal, along the Sierra Madre mountain range. Once relatively unknown, " +
                            "it has gained attention for its raw, offbeat charm and the scenic views " +
                            "it offers from its summit. The trail features rainforest terrain and " +
                            "rocky ascents, with a short sidetrip to the Lubog Cave. Despite the long, " +
                            "rough motorcycle approach, this hike rewards adventurers with panoramic " +
                            "vistas of Rizal and Bulacan's rugged landscapes.",
                    tagline = "Let's Hike to Mt. Lubog",
                    mountainImageRef1 = "mt_lubog_1",
                    mountainImageRef2 = "mt_lubog_2",
                    mountainImageRef3 = "mt_lubog_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Cave-dwelling species",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, description = "While Mt. Lubog is typically a dayhike, there is an optional camping area near the registration site. " +
                        "It's a small, open clearing suited for small groups, offering early sunrise views and a quiet overnight stay."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, name = "View deck near registration area", description = "Open space suitable for pitching tents"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, name = "Trek Time to Campsite", description = "Less than 15 minutes from registration"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, name = "Water Source", description = "None-bring at least 2-3 liters per person"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, name = "Best For", description = "Dayhikers, beginner hikers seeking unique limestone landscapes, and those up for an adventure")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, name = "Standard Trail", description = "Standard out-and-back trail with optional sidetrip to Lubog Cave")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Registration Fee", description = "P50 per person at the barangay hall"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Guide Fee", description = "Coordinate with local tourism (rate varies); guide required"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Safety Tip", description = "Wear sturdy footwear-trail and rocks may be slippery"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Safety Tip", description = "Avoid summit edges during strong winds"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Safety Tip", description = "Bring sun protection and enough water-no refill points available"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Safety Tip", description = "Pack light but waterproof-habal-habal ride can get muddy"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLubogId, category = "Hiking Season", description = "Best from December to February; trail access difficult during rainy months due to rough roads")
            ))

            // --- Mt. Labo Data ---
            val mtLaboId = "mtlabo001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtLaboId,
                    mountainName = "Mt. Labo",
                    pictureReference = "mt_labo_main",
                    location = "San Lorenzo Ruiz, Camarines Norte",
                    masl = 1544,
                    difficultySummary = "Strenuous",
                    difficultyText = "Strenuous (6/9)",
                    hoursToSummit = "8-10 hours to summit",
                    bestMonthsToHike = "December to May",
                    typeVolcano = "Forested, andesitic compound volcano",
                    trekDurationDetails = "8-10 hours to summit (2-3 days total)",
                    trailTypeDescription = "Remote jungle trail, dense forest, multiple rivers",
                    sceneryDescription = "Dense rainforest, river crossings, rafflesia blooms (June-July), panoramic views of Mt. Mayon, Mt. Isarog, and Mt. Banahaw",
                    viewsDescription = "Mt. Mayon, Mt. Isarog, Mt. Banahaw on clear days",
                    wildlifeDescription = "Rafflesia manillana, rufous hornbill, tropical birds, forest-dwelling species",
                    featuresDescription = "Remote jungle trail, geothermal exploration route, rich flora and fauna, side trip to Angelina Falls",
                    hikingSeasonDetails = "Best during dry months (December to May); avoid rainy season due to slippery trails and river flooding",
                    introduction = "Mt. Labo is a towering peak in San Lorenzo Ruiz, Camarines Norte, " +
                            "standing at 1,544 MASL and spanning three municipalities. It offers a " +
                            "challenging yet rewarding experience for hikers seeking rich biodiversity, " +
                            "remote jungle trails, and scenic vistas. The mountain is home to various " +
                            "unique species including the Rafflesia manillana and rufous hornbill. " +
                            "Originally made accessible through old geothermal exploration trails, " +
                            "Mt. Labo now welcomes climbers via Sitio Butan, Brgy. San Isidro. " +
                            "The hike passes through dense forest, multiple rivers, and unique flora, " +
                            "culminating in views of Mt. Mayon, Mt. Isarog, and Mt. Banahaw on clear days. " +
                            "Seasonal highlights include blooming rafflesias around June-July and a " +
                            "side trip to Angelina Falls. The route, while long and remote, is immersed " +
                            "in wilderness and is ideal for those seeking a multiday nature expedition.",
                    tagline = "Let's Hike to Mt. Labo",
                    mountainImageRef1 = "mt_labo_1",
                    mountainImageRef2 = "mt_labo_2",
                    mountainImageRef3 = "mt_labo_3",

                    hasSteepSections = true,
                    notableWildlife = "Rafflesia manillana, Rufous Hornbill, Birds",
                    isRocky = false,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, description = "Mt. Labo has one main campsite around 800 MASL, located 9 km from the trailhead. It is set in a small clearing " +
                        "at the base of the mountain, surrounded by dense forest."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, name = "Main Campsite (800 MASL)", description = "800 MASL, 9 km from trailhead, small clearing, dense forest surround"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, name = "Campsite Capacity", description = "Small groups"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, name = "Amenities", description = "Basic; no developed facilities"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, name = "Water Source", description = "Available approximately 500 meters from campsite"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, name = "Best For", description = "Wilderness trekkers, nature explorers, multiday expedition hikers")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, name = "Main Trail", description = "Via Sitio Butan, Brgy. San Isidro - dense forest, river crossings, remote jungle")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, category = "Registration Fee", description = "No registration fee required"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, category = "Safety Tip", description = "Summit safety index: 4/10 - low injury risk but not recommended for solo hikers"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, category = "Safety Tip", description = "Bring insect repellent and wear long clothing to protect against jungle insects"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, category = "Safety Tip", description = "Be cautious of wildlife and flash flood risk during rainy season"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, category = "Safety Tip", description = "Cell signal (Globe > Smart) is available in most parts including the summit"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, category = "Leave No Trace", description = "Practice Leave No Trace principles and respect biodiversity"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtLaboId, category = "Hiking Season", description = "Best during dry months (December to May); avoid rainy season due to slippery trails and river flooding")
            ))

            // --- Mt. Pulag Data ---
            val mtPulagId = "mtpulag001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtPulagId,
                    mountainName = "Mt. Pulag",
                    pictureReference = "mt_pulag_main",
                    location = "Bokod, Benguet (bordering Ifugao and Nueva Vizcaya)",
                    masl = 2926,
                    difficultySummary = "Easy to Intermediate",
                    difficultyText = "Easy to Intermediate (3/9 to 7/9,)",
                    hoursToSummit = "4-5 hours (via Ambangeg Trail)",
                    bestMonthsToHike = "November to early March",
                    typeVolcano = "Dormant Volcano",
                    trekDurationDetails = "4-5 hours (via Ambangeg Trail)",
                    trailTypeDescription = "Out-and-back via Ambangeg Trail (easiest route)",
                    sceneryDescription = "Mossy forests, dwarf bamboo grasslands, panoramic sunrise views, and the famous sea of clouds",
                    viewsDescription = "Sea of clouds, sunrise vistas, distant Cordillera peaks",
                    wildlifeDescription = "Luzon pygmy fruit bat, endemic birds, mossy forest flora",
                    featuresDescription = "Cold climate, scenic campsites above the clouds, protected national park",
                    hikingSeasonDetails = "Best during November to early March (cool and clear conditions). Avoid during rainy months due to slippery trails and limited visibility.",
                    introduction = "Mt. Pulag, the third highest peak in the Philippines, towers at " +
                            "2,926 MASL and straddles the borders of Benguet, Ifugao, and Nueva Vizcaya. " +
                            "Known for its famed \"sea of clouds\" and breathtaking sunrise views, " +
                            "it is a bucket-list climb for both beginner and seasoned hikers. The " +
                            "mountain is part of the Mt. Pulag National Park and boasts a rich " +
                            "biodiversity, home to dwarf bamboo grasslands, mossy forests, and unique " +
                            "wildlife like the Luzon pygmy fruit bat. The Ambangeg Trail-the most " +
                            "popular and beginner-friendly route-is well-established and features " +
                            "gradual ascents, scenic views, and campgrounds above the clouds. Mt. Pulag " +
                            "is a protected area, and climbers are expected to adhere strictly to " +
                            "environmental rules and health requirements. Peak season is typically " +
                            "from November to early March when the skies are clearest and the " +
                            "temperature drops near freezing.",
                    tagline = "Let's Hike to Mt. Pulag",
                    mountainImageRef1 = "mt_pulag_1",
                    mountainImageRef2 = "mt_pulag_2",
                    mountainImageRef3 = "mt_pulag_3",

                    hasSteepSections = true,
                    notableWildlife = "Luzon pygmy fruit bat, Birds",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, description = "Mount Pulag offers several campsite options, including those near the ranger station and mid-trail. You can camp at the Babadak Ranger Station, " +
                        "which offers a more comfortable experience with access to amenities, or venture further up to Camp 2 for a more challenging, yet closer to the summit."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, name = "Camp 1 (Ambangeg Trail)", description = "Campsite along Ambangeg Trail"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, name = "Camp 2 (Ambangeg Trail)", description = "Most popular and widely used campsite along Ambangeg Trail. Suitable for large groups. Basic latrines available."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, name = "Water Source", description = "Limited; bring sufficient water or purification tablets"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, name = "Best For", description = "Beginner hikers, nature photographers, sunrise seekers")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, name = "Ambangeg Trail", description = "This trail is known for its gradual ascent and scenic views. It's considered the easiest and most popular choice, typically taking 3-4 hours to reach the summit."),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, name = "Akiki Trail", description = "This trail offers a more challenging climb, suitable for experienced hikers and those seeking a longer, more immersive experience. It can take multiple days to complete."),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, name = "Tawangan Trail", description = "This trail is known for its steep inclines and rocky terrain, requiring a good level of fitness. It is also considered a challenging route, similar to the Akiki Trail."),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, name = "Ambaguio Trail", description = "Starting from Nueva Vizcaya, this is the longest trail to the summit and can take up to three days to complete.")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Registration Fee", description = "P175/person (Local), USD 15 (Foreigners)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Guide Fee", description = "P600 (1-5 pax), P120/person for groups above 5"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Camping Fee", description = "P100/person"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Safety Tip", description = "Ensure medical clearance; altitude may pose health risks"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Safety Tip", description = "Bring cold-weather gear: thermal layers, gloves, windproof jackets"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Safety Tip", description = "Be weather-aware: temperature can drop below freezing"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Safety Tip", description = "Hydrate and eat well; bring trail food and at least 2.5 liters of water"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Safety Tip", description = "Cell signal is available in some areas (Globe generally better)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Safety Tip", description = "Save emergency contacts for DENR and local guides"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPulagId, category = "Hiking Season", description = "Best during November to early March (cool and clear conditions). Avoid during rainy months due to slippery trails and limited visibility.")
            ))

            // --- Mt. Amuyao Data ---
            val mtAmuyaoId = "mtamuyao001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtAmuyaoId,
                    mountainName = "Mt. Amuyao",
                    pictureReference = "mt_amuyao_main",
                    location = "Ifugao / Mountain Province",
                    masl = 2702,
                    difficultySummary = "Very Strenuous",
                    difficultyText = "Very Strenuous (7/9)",
                    hoursToSummit = "9-12 hours to summit",
                    bestMonthsToHike = "November to early March",
                    typeVolcano = "Prominent Mountain",
                    trekDurationDetails = "9-12 hours to summit (2-3 days total)",
                    trailTypeDescription = "Mixed trail (forest, grasslands, rocky terrain)",
                    sceneryDescription = "Forest, grasslands, mountain views, rivers and streams",
                    viewsDescription = "Panoramic views of Cordillera peaks including Mt. Pulag and Mt. Kalawitan",
                    wildlifeDescription = "Wildlife presence (including snakes)",
                    featuresDescription = "Remote wilderness, cultural significance, varied ecosystems",
                    hikingSeasonDetails = "Best during November to early March (cooler and drier months). Avoid during rainy season due to trail difficulty and reduced visibility.",
                    introduction = "Mount Amuyao is a majestic peak located in the Cordillera mountain " +
                            "range, straddling the provinces of Ifugao and Mountain Province. With " +
                            "an elevation of 2,702 MASL, it offers a challenging and scenic climb for " +
                            "experienced hikers. The trail provides a perfect mix of rugged terrain, " +
                            "picturesque mountain views, and lush forests. The summit is known for its " +
                            "expansive views of surrounding mountains, including Mount Pulag and Mount " +
                            "Kalawitan. The hike features various ecosystems, from mossy forests to grasslands, " +
                            "and it is a prime location for those seeking a remote and peaceful outdoor " +
                            "experience. Amuyao also holds cultural significance for the indigenous " +
                            "communities of the region, with nearby villages offering insights into " +
                            "traditional Cordilleran life.",
                    tagline = "Let's Hike to Mt. Amuyao",
                    mountainImageRef1 = "mt_amuyao_1",
                    mountainImageRef2 = "mt_amuyao_2",
                    mountainImageRef3 = "mt_amuyao_3",

                    hasSteepSections = true,
                    notableWildlife = "Snakes",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, description = "Mount Amuyao features one main campsite located around 2,400 MASL, typically used by overnight trekkers. " +
                        "It sits in a semi-open clearing with panoramic views of surrounding peaks, offering a cool and peaceful atmosphere ideal for rest and recovery. The campsite is commonly reached after 6-8 hours of trekking, making it " +
                        "a strategic spot for those doing a two-day hike. A water source is available along the trail before reaching the camp, though hikers are advised to filter or boil water."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, name = "Main Campsite (Approx. 2,400 MASL)", description = "Approx. 2,400 MASL, scenic forest clearing before summit"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, name = "Campsite Capacity", description = "Suitable for group camping"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, name = "Amenities", description = "Basic; no toilets or shelters"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, name = "Trek Time to Campsite", description = "6-8 hours"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, name = "Water Source", description = "Available along trail before camp (filter/boil recommended)"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, name = "Best For", description = "Experienced hikers, cultural trekkers, remote expedition seekers")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, name = "Main Trail", description = " Alternates between narrow forest paths, rocky ridgelines, and open grasslands. Includes steep and rugged sections, " +
                        "with occasional muddy and slippery terrain especially during the rainy season. Long, continuous ascents and descents require physical endurance and experience.")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Registration Fee", description = "Minimal registration fee at Brgy. Nabuya trailhead"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Guide Fee", description = "Mandatory, usually Php 2,500 for an overnight hike"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Registration", description = "Required at trailhead logbook. Courtesy Call recommended."),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Guide Requirement", description = "Strongly recommended due to remoteness and terrain"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip", description = "Summit Safety Index: 6/10 (moderate risk, physical conditioning required)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip", description = "Avoid ridges during storms (lightning risk)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip", description = "Wear sturdy, non-slip shoes-trails can be steep, muddy, and slippery"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip", description = "Bring sun protection and warm layers-weather can shift quickly"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip", description = "Hydrate properly-bring at least 2.5-3 liters of water"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Leave No Trace", description = "Carry out all trash and preserve natural elements"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip", description = "Expect changing conditions-rain, fog, and cold temperatures common"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip", description = "Watch for wildlife (e.g., snakes)-keep distance and remain cautious"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip", description = "Cell signal is limited in most areas, including near summit"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Safety Tip", description = "Bring waterproof gear and reliable footwear"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtAmuyaoId, category = "Hiking Season", description = "Best during November to early March (cooler and drier months). Avoid during rainy season due to trail difficulty and reduced visibility.")
            ))

            // --- Mt. Napulauan Data ---
            val mtNapulauanId = "mtnapulauan001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtNapulauanId,
                    mountainName = "Mt. Napulauan",
                    pictureReference = "mt_napulauan_main",
                    location = "Hungduan, Ifugao",
                    masl = 2642,
                    difficultySummary = "Very Strenuous",
                    difficultyText = "Very Strenuous (8/9)",
                    hoursToSummit = "5-6 hours to summit",
                    bestMonthsToHike = "November to March",
                    typeVolcano = "Highland Mountain",
                    trekDurationDetails = "5-6 hours to summit (1-2 days total)",
                    trailTypeDescription = "Mossy forest, steep ascent with challenging sections",
                    sceneryDescription = "Moss-covered trees, primeval forest trails, bonsai summit, rice terraces, cloud seas",
                    viewsDescription = "Rice terraces, mountain ranges, sea of clouds",
                    wildlifeDescription = "Wild boars, musang (civet), leeches, diverse mossy flora and orchids",
                    featuresDescription = "WWII historical site, unique mossy forest, narrow ridge trails",
                    hikingSeasonDetails = "Best during November to March (cool and dry months). Avoid during rainy season due to leeches, slippery terrain, and poor visibility.",
                    introduction = "Mt. Napulauan, also known as the \"Whitened Grand Mountain,\" " +
                            "stands at an impressive 2,642+ MASL in Hungduan, Ifugao. As the 15th " +
                            "highest peak in the Philippines, it is part of the Great Cordillera " +
                            "Traverse. The mountain offers three main trails: the Hungduan Trail, " +
                            "which is the most commonly used; the Hapao Trail, which passes through " +
                            "UNESCO-protected rice terraces; and the Balentimol Trail, known for its " +
                            "dangerous narrow paths and ravines. Mt. Napulauan is famous for its expansive " +
                            "mossy forests and surreal landscapes, offering hikers an otherworldly " +
                            "experience. It is also historically significant, associated with General " +
                            "Yamashita during World War II.",
                    tagline = "Let's Hike to Mt. Napulauan",
                    mountainImageRef1 = "mt_napulauan_1",
                    mountainImageRef2 = "mt_napulauan_2",
                    mountainImageRef3 = "mt_napulauan_3",

                    hasSteepSections = true,
                    notableWildlife = "Wild Boars, Musang, Leeches, Orchids",
                    isRocky = false,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, description = "Mt. Napulauan features two main campsites used by hikers. Ny-o Campsite (around 1,630 MASL) is a rest " +
                        "stop located approximately 3 hours from the jump-off point, offering a forest clearing where hikers can pitch tents. The Summit Campsite (at 2,642 MASL) is a small open space with bonsai-like trees and colder conditions, " +
                        "ideal for sunrise and sea-of-cloud viewing. Night temperatures can drop to 5°C so cold-weather gear is essential."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Ny-o Campsite", description = "Forest clearing at ~1,630 MASL. Trek time ~3 hours. Small groups capacity."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Summit Campsite", description = "Small open area with bonsai trees, cold temperatures at 2,642 MASL. Trek time ~5-6 hours. Up to a dozen tents capacity."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Amenities", description = "Basic; no toilets or shelters"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Water Source", description = "Available at Ny-o campsite and near summit; bring extra water"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Best For", description = "Experienced hikers, adventure seekers, nature lovers, Cordillera explorers")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Hungduan Trail", description = "Most commonly used; direct route through mossy forests and steep slopes"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Hapao Trail", description = "Passes through UNESCO-protected rice terraces; scenic but longer"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, name = "Balentimol Trail", description = "Technical trail with dangerous narrow ridges and ravines; for experienced hikers only")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Registration Fee", description = "None, but optional souvenir (P150) at Hungduan municipal hall"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Guide Fee", description = "P500/day for up to 5 hikers"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Registration", description = "Required at municipal hall of Hungduan. Logbook Registration at trailhead. Courtesy Call recommended."),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Guide Requirement", description = "Strongly recommended"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Safety Tip", description = "Avoid ridge lines during thunderstorms"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Safety Tip", description = "Leeches are common-wear leech protection and long clothing"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Safety Tip", description = "Weather can drop to 5-8°C-bring cold-weather gear"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Safety Tip", description = "Trail is steep and narrow in some areas descend cautiously"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Safety Tip", description = "Watch out for wild boars and musang-do not approach"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Safety Tip", description = "Stay on marked trails to prevent erosion and accidents"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Safety Tip", description = "Cell signal available at Ny-o and summit only"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Safety Tip", description = "Bring at least 2.5-3 liters of water-sources not always reliable"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtNapulauanId, category = "Hiking Season", description = "Best during November to March (cool and dry months). Avoid during rainy season due to leeches, slippery terrain, and poor visibility.")
            ))

            // --- Mt. Kalawitan Data ---
            val mtKalawitanId = "mtkalawitan001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtKalawitanId,
                    mountainName = "Mt. Kalawitan",
                    pictureReference = "mt_kalawitan_main",
                    location = "Bontoc and Sabangan, Mountain Province",
                    masl = 2714,
                    difficultySummary = "Very Strenuous",
                    difficultyText = "Very Strenuous (7/9)",
                    hoursToSummit = "8-12 hours to summit",
                    bestMonthsToHike = "November to March",
                    typeVolcano = "Granitic mountain",
                    trekDurationDetails = "8-12 hours to summit (1-2 days total)",
                    trailTypeDescription = "Out-and-back trail",
                    sceneryDescription = "Pine and mossy forests, mist-covered peaks, dramatic valley views, cloud sea at the summit",
                    viewsDescription = "Panoramic views of Mountain Province ridges and neighboring peaks",
                    wildlifeDescription = "Wild boars, civets (musang), mountain leeches (limatik), rare orchids",
                    featuresDescription = "Expansive mossy trails, cold summit nights, mix of forest types",
                    hikingSeasonDetails = "Best during November to March (dry season). Avoid during rainy months due to muddy, slippery trails and leech activity.",
                    introduction = "Mt. Kalawitan, standing at 2,714 MASL, is the tenth highest peak " +
                            "in the Philippines. Located in the towns of Bontoc and Sabangan, " +
                            "Mountain Province, Kalawitan is known for its stunning pine forests, " +
                            "expansive mossy forests, and rugged terrain. Its trails, particularly " +
                            "the Talubin Trail, offer an unforgettable experience, taking climbers " +
                            "through a variety of forest landscapes and rugged terrain, with opportunities " +
                            "to witness breathtaking cloudscapes and wildlife. The mountain is also " +
                            "steeped in history, with stories of its earlier exploration and its " +
                            "connection to local communities. The Talubin Trail is the preferred route " +
                            "for most hikers, taking adventurers on a challenging yet scenic hike. " +
                            "With mossy forests, rough paths, and occasional encounters with mountain " +
                            "leeches (limatik), this hike demands both physical endurance and mental " +
                            "resolve, but the reward is a panoramic view at the summit and the experience " +
                            "of a pristine mountain ecosystem.",
                    tagline = "Let's Hike to Mt. Kalawitan",
                    mountainImageRef1 = "mt_kalawitan_1",
                    mountainImageRef2 = "mt_kalawitan_2",
                    mountainImageRef3 = "mt_kalawitan_3",

                    hasSteepSections = true,
                    notableWildlife = "Wild Boars, Civets, Leeches, Orchids",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, description = "Mt. Kalawitan features three key campsites catering to different stages of the ascent. Munna Camp (1,950 MASL) is around 4 hours " +
                        "from the jump-off and offers a forest clearing ideal for meals and short rests before entering the mossy zone. Emergency Camp (2,400 MASL) sits deeper into the trail, surrounded by mossy forest, and is used when weather or time " +
                        "forces hikers to stop before the summit. The Summit Campsite (2,714 MASL) is a compact area with mossy shrubs, accommodating up to 6-8 tents, ideal for sunrise viewing and cloud sea photography. It gets cold at night, often reaching 5°C."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, name = "Munna Camp", description = "Pine forest zone at ~1,950 MASL. Trek time ~4 hours. Small groups capacity."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, name = "Emergency Camp", description = "Mossy forest at ~2,400 MASL. Trek time ~6-8 hours. Medium-sized groups capacity."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, name = "Summit Camp", description = "Limited flat area at the summit (2,714 MASL). Trek time ~8-12 hours. 6-8 tents capacity."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, name = "Amenities", description = "Basic; no established toilets or shelters"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, name = "Water Source", description = "Available at Munna, Emergency, and near the summit; bring 2.5-3 liters as backup"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, name = "Best For", description = "Experienced hikers, nature lovers, overnight campers, mossy forest enthusiasts")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, name = "Talubin Trail", description = "Primary route; passes through pine forests, mossy forests, and steep ascents")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Registration Fee", description = "None; optional donation or souvenir (approx. P150)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Guide Fee", description = "P500/day for up to 5 hikers"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Safety Tip", description = "Wear leech protection-limatik are common past Munna"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Safety Tip", description = "Cold weather gear essential-temperatures at summit can drop to 5°C"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Safety Tip", description = "Be cautious on narrow, steep trails especially during rain"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Safety Tip", description = "Respect wildlife-avoid close interaction with wild boars and civets"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Safety Tip", description = "Cell signal available at Munna and summit only"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Safety Tip", description = "Bring sufficient water-though available, supply is limited in parts"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtKalawitanId, category = "Hiking Season", description = "Best during November to March (dry season). Avoid during rainy months due to muddy, slippery " +
                        "trails and leech activity.")
            ))

            // --- Mt. Ugo Data ---
            val mtUgoId = "mtugo001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtUgoId,
                    mountainName = "Mt. Ugo",
                    pictureReference = "mt_ugo_main",
                    location = "Kayapa, Nueva Vizcaya to Itogon, Benguet",
                    masl = 2150,
                    difficultySummary = "Strenuous",
                    difficultyText = "Strenuous (5/9)",
                    hoursToSummit = "10-12 hours total",
                    bestMonthsToHike = "November to April",
                    typeVolcano = "Cordillera Central Mountain Range",
                    trekDurationDetails = "10-12 hours total (typically 2 days)",
                    trailTypeDescription = "Point-to-point traverse",
                    sceneryDescription = "Pine forests, ridgelines, cloud sea, distant Cordillera peaks, remote barangays",
                    viewsDescription = "Mt. Pulag, Mt. Timbak, Mt. Sto. Tomas, Agno River Valley",
                    wildlifeDescription = "Civets, birds, insects",
                    featuresDescription = "Plane crash site, summit marker, old forest trails, village encounters",
                    hikingSeasonDetails = "Best from November to April (dry season). Avoid during typhoon months or peak rainy season due to slippery trails and reduced visibility.",
                    introduction = "Mt. Ugo (2,150 MASL) is a prominent Cordillera peak traversing " +
                            "the provinces of Nueva Vizcaya and Benguet. Once known primarily due " +
                            "to a tragic plane crash in 1987, it has since become a favorite among " +
                            "seasoned hikers seeking long-distance trails, pine forest scenery, and " +
                            "expansive mountain views. The mountain features rolling terrain and " +
                            "scenic ridgelines, passing through remote villages and old forest paths. " +
                            "Its summit offers panoramic views of neighboring peaks including Mt. Pulag, " +
                            "Mt. Sto. Tomas, and Mt. Timbak. The Kayapa to Itogon traverse trail is the " +
                            "most popular route for hikers, stretching about 30 km. This multi-day " +
                            "trek demands endurance and preparation, but rewards hikers with diverse scenery, " +
                            "encounters with local communities, and one of the best summit views in " +
                            "the Cordillera region.",
                    tagline = "Let's Hike to Mt. Ugo",
                    mountainImageRef1 = "mt_ugo_1",
                    mountainImageRef2 = "mt_ugo_2",
                    mountainImageRef3 = "mt_ugo_3",

                    hasSteepSections = true,
                    notableWildlife = "Civets, Birds, Insects",
                    isRocky = false,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, description = "Mt. Ugo offers several practical camping options along the Kayapa-Itogon traverse. The most common is Indupit Elementary School (~1,700 MASL), a large, " +
                        "flat area ideal for tents and group overnights. It features nearby water access and a chance to interact with locals. Domolpos Village (~1,850 MASL) is another overnight option for those descending post-summit. It offers modest shelter, water, and " +
                        "basic supplies. The Summit Area (~2,150 MASL) can be used for short breaks and photo ops, but due to wind exposure, it's rarely used for overnight camping."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, name = "Indupit Elementary School", description = "Popular overnight site before summit, ~1,700 MASL. Large groups capacity. Trek time ~6-7 hours from Kayapa."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, name = "Domolpos Village", description = "Used on second night or descent, ~1,850 MASL. Medium groups capacity. Trek time ~2-3 hours from summit (descent)."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, name = "Summit Camp", description = "Open, windy-more for rest/photo ops than camping. 2-3 tents at most."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, name = "Amenities", description = "Indupit has water and shelter. Domolpos offers limited supplies from locals."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, name = "Water Source", description = "Available at Indupit, Domolpos, and streams along trail. Bring 2-3 liters; purify water when needed."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, name = "Best For", description = "Endurance hikers, cultural adventurers, Cordillera ridge lovers, long-distance trekkers")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, name = "Kayapa-Itogon Traverse", description = "30+ km trail passing through pine ridges, remote barangays, forest sections, and the summit"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, name = "Alternative Itogon Day Hike", description = "Possible from Tinongdan for experienced hikers only")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Registration Fee", description = "Required; coordinate with Kayapa and Tinongdan LGUs"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Guide Fee", description = "P500-P1,000/day depending on group size"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Camping Fee", description = "None officially, but donations welcome at school/villages"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Safety Tip", description = "Cold, windy summit-bring warm layers (can drop to 10°C)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Safety Tip", description = "Steep descents-use trekking poles and manage pacing"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Safety Tip", description = "Long distances-train and prepare for back-to-back hiking days"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Safety Tip", description = "Village etiquette-be polite and respectful when passing through"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Safety Tip", description = "Cell signal spotty-available at Indupit and some ridgelines"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Safety Tip", description = "Bring energy food-long hours between settlements"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtUgoId, category = "Hiking Season", description = "Best from November to April (dry season). Avoid during typhoon months or peak rainy season due to slippery trails and reduced visibility.")
            ))

            // --- Mt. Timbak Data ---
            val mtTimbakId = "mttimbak001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtTimbakId,
                    mountainName = "Mt. Timbak",
                    pictureReference = "mt_timbak_main",
                    location = "Atok, Benguet",
                    masl = 2717,
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate (4/9)",
                    hoursToSummit = "1.5-2.5 hours to summit",
                    bestMonthsToHike = "November to April",
                    typeVolcano = "Cordillera Central Mountain Range",
                    trekDurationDetails = "1.5-2.5 hours to summit",
                    trailTypeDescription = "Out-and-back via cemented road",
                    sceneryDescription = "Vegetable gardens, scenic Cordillera villages, panoramic views, burial caves",
                    viewsDescription = "Mt. Pulag, Mt. Tabayoc, Halsema ridges, sea of clouds",
                    wildlifeDescription = "Minimal sightings; mostly agricultural zone",
                    featuresDescription = "3 crosses at the summit, Timbak mummies (optional side trip), Luzon 3-2-1 trail component",
                    hikingSeasonDetails = "Open year-round. Best conditions from November to April. Rainy season (May to October) may make roads slippery and reduce visibility.",
                    introduction = "Mt. Timbak, standing at 2,717 MASL, is the ninth highest mountain " +
                            "in the Philippines and the third highest in Luzon. Located in Atok, Benguet, " +
                            "it is widely known as the easiest climb among the country's top ten peaks-more " +
                            "of a scenic walk than a grueling trek. Despite its elevation, the route " +
                            "is mostly paved and accessible via the Halsema Highway. The mountain is " +
                            "often part of the Luzon 3-2-1 challenge, along with Mt. Tabayoc and Mt. Pulag. " +
                            "The trail takes you past vegetable terraces, local villages, and finally " +
                            "to the summit marked by three crosses-a site affectionately dubbed the " +
                            "\"mini-Calvary\" by locals. From here, hikers are treated to panoramic " +
                            "views of nearby peaks, including Pulag and Tabayoc, as well as the famous " +
                            "sea of clouds that occasionally envelops the surrounding ranges.",
                    tagline = "Let's Hike to Mt. Timbak",
                    mountainImageRef1 = "mt_timbak_1",
                    mountainImageRef2 = "mt_timbak_2",
                    mountainImageRef3 = "mt_timbak_3",

                    hasSteepSections = true,
                    notableWildlife = "",
                    isRocky = false,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, description = "Though typically done as a dayhike, Mt. Timbak has two potential rest areas. Timbak Elementary School (~2,650 MASL) is often used as a base or staging area before " +
                        "the summit, with flat areas nearby for camping if permitted by locals. The Summit Area (~2,717 MASL), while limited in space, can accommodate short breaks or minimal overnight setups. However, due to its exposure, it is not an ideal campsite without proper gear."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, name = "Timbak Elementary School", description = "Rest stop or staging area, ~2,650 MASL. ~5-10 tents (with permission). Trek time ~1-1.5 hours from KM 55."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, name = "Summit (Three Crosses)", description = "Photo/rest point, ~2,717 MASL. 1-2 tents max (exposed and limited flat area). Trek time ~30-45 minutes from school area."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, name = "Amenities", description = "No formal amenities; possible access to village homes or school with prior arrangement"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, name = "Water Source", description = "None available on trail - bring at least 1-2 liters per person"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, name = "Best For", description = "Beginners, Luzon 3-2-1 climbers, cultural hikers, families")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, name = "KM 55 Jump-off (Halsema Highway)", description = "Standard entry point for the paved ascent"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, name = "Optional Side Trip", description = "Timbak mummies - ancient burial site near the village trail")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Registration Fee", description = "None required"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Guide Fee", description = "Not required but optional for cultural visits (P200-P300)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Camping Fee", description = "None; coordinate with locals if staying at school area"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Safety Tip", description = "Cold weather in early morning-wear layers"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Safety Tip", description = "Paved road can be steep and slippery when wet"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Safety Tip", description = "Watch for local vehicles-trail is a village access road"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Safety Tip", description = "Respect cultural sites-do not touch or disturb mummies"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Safety Tip", description = "Bring sun protection-trail is largely exposed during midday"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Safety Tip", description = "Side trail confusion-ask locals or keep left when unsure"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTimbakId, category = "Hiking Season", description = "Open year-round. Best conditions from November to April. Rainy season (May to October) may make roads slippery and reduce visibility.")
            ))

            // --- Mt. Tabayoc Data ---
            val mtTabayocId = "mttabayoc001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtTabayocId,
                    mountainName = "Mt. Tabayoc",
                    pictureReference = "mt_tabayoc_main",
                    location = "Kabayan, Benguet",
                    masl = 2842,
                    difficultySummary = "Moderate",
                    difficultyText = "Moderate (4/9)",
                    hoursToSummit = "2-4 hours to summit",
                    bestMonthsToHike = "November to May",
                    typeVolcano = "Mountain",
                    trekDurationDetails = "2-4 hours to summit",
                    trailTypeDescription = "Out-and-back trail",
                    sceneryDescription = "Mossy forests, tree tunnels, thick vegetation, and highland lakes nearby",
                    viewsDescription = "Limited at summit due to overgrowth, but nearby Lake Tabeo offers scenic views",
                    wildlifeDescription = "Birds, frogs, and insects; mossy forest biodiversity",
                    featuresDescription = "Part of Luzon 3-2-1 challenge, nearby four Lakes of Kabayan (including Lake Tabeo)",
                    hikingSeasonDetails = "Accessible year-round, but best from November to May (dry season). Trail becomes especially muddy and slippery from June to October.",
                    introduction = "Mt. Tabayoc, standing at 2,842 MASL, is the second highest " +
                            "mountain in the Philippines and the highest in Luzon after Mt. Pulag. " +
                            "Located in Kabayan, Benguet, it is part of the Mt. Pulag National Park " +
                            "and frequently included in the Luzon 3-2-1 challenge alongside " +
                            "Mt. Pulag and Mt. Timbak. Known for its dense mossy forests, cool " +
                            "climate, and remote, mystical ambiance, Mt. Tabayoc offers a unique " +
                            "trekking experience. The trail to the summit is relatively short but " +
                            "steep, muddy, and overgrown-earning it a reputation as a challenging " +
                            "yet worthwhile hike. The summit is often shrouded in fog and features " +
                            "thick vegetation, limiting the panoramic view but enhancing the mystique " +
                            "of the mountain.",
                    tagline = "Let's Hike to Mt. Tabayoc",
                    mountainImageRef1 = "mt_tabayoc_1",
                    mountainImageRef2 = "mt_tabayoc_2",
                    mountainImageRef3 = "mt_tabayoc_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds, Frogs, Insects",
                    isRocky = false,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, description = "Climbers usually stay overnight near Lake Tabeo, the main jump-off for Mt. Tabayoc. The lake area is flat, scenic, and commonly used as " +
                        "a base camp for both Mt. Tabayoc and side trips to the nearby lakes. The summit itself has no space for tents."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, name = "Lake Tabeo", description = "Popular camping site before summit climb (approx. 2,300 MASL). ~15-20 tents capacity. Basic restrooms nearby."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, name = "Summit", description = "No space for camping (2,842 MASL)."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, name = "Water Source", description = "Available at Lake Tabeo"),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, name = "Best For", description = "Intermediate hikers, Luzon 3-2-1 challengers, forest lovers, and those seeking mystical mossy scenery")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, name = "Lake Tabeo Jump-off", description = "Primary starting point for the hike"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, name = "Four Lakes Side Trip", description = "Lakes Tabeo, Ambulalakao, Incolos, and Letepngepos (optional exploration)")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Registration Fee", description = "Required; coordinate with DENR/Mt. Pulag National Park and local officials"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Guide Fee", description = "P500-P1,000 per group, required"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Camping Fee", description = "P50-P100 depending on arrangements"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Safety Tip", description = "Muddy trail: Wear proper footwear; expect slippery, steep sections"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Safety Tip", description = "Dense vegetation: Protect skin from scratches and insects"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Safety Tip", description = "Cold temperature: Night temps may drop below 10°C-bring appropriate clothing"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Safety Tip", description = "Limited visibility: Fog is frequent; stick with your guide"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Safety Tip", description = "Leeches possible: Especially during rainy season-prepare accordingly"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTabayocId, category = "Hiking Season", description = "Accessible year-round, but best from November to May (dry season). Trail becomes especially muddy and slippery from June to October.")
            ))

            // --- Mt. Purgatory (Mangisi Range) Data ---
            val mtPurgatoryId = "mtpurgatory001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtPurgatoryId,
                    mountainName = "Mt. Purgatory (Mangisi Range)",
                    pictureReference = "mt_purgatory_main",
                    location = "Bokod, Benguet",
                    masl = 2329,
                    difficultySummary = "Challenging (8/9)",
                    difficultyText = "Challenging (8/9)",
                    hoursToSummit = "9-11 hours (traverse)",
                    bestMonthsToHike = "November to May",
                    typeVolcano = "Major Climb",
                    trekDurationDetails = "9-11 hours (commonly done over 2-3 days)",
                    trailTypeDescription = "Point-to-point (Japas jump-off to Brgy. Ekip exit)",
                    sceneryDescription = "Pine and mossy forests, ridgelines, mountain villages, old relay station, Cordillera views (Pulag, Timbak, Sto. Tomas), cloud-covered peaks",
                    viewsDescription = "Scenic ridges, mossy tree tunnels, foggy valleys, panoramic mountain ranges",
                    wildlifeDescription = "Mossy forest flora and native birds",
                    featuresDescription = "Multi-peak challenge, historical significance, educational stopovers (Bakian Elementary School)",
                    hikingSeasonDetails = "Best hiked during the dry season (November to May). Trails become slippery and difficult in rainy months.",
                    introduction = "The Mt. Purgatory Traverse is a multi-peak mountain trail located " +
                            "in Bokod, Benguet, cutting through the remote highland wilderness between " +
                            "Mt. Pulag and Mt. Ugo. The trail takes hikers through the summits of " +
                            "Mt. Pack (2,290 MASL), Mt. Purgatory (2,080 MASL), and Mt. Komkompol (2,329 MASL). " +
                            "The route features both pine and mossy forests, scenic ridgelines, and " +
                            "occasional panoramic viewpoints of Cordillera peaks including Mt. Pulag, " +
                            "Mt. Timbak, and Mt. Sto. Tomas. The name \"Purgatory\" stems from American l" +
                            "oggers who likened the trail's cold and foggy weather to purgatory.",
                    tagline = "Let's Hike the Mt. Purgatory Traverse",
                    mountainImageRef1 = "mt_purgatory_1",
                    mountainImageRef2 = "mt_purgatory_2",
                    mountainImageRef3 = "mt_purgatory_3",

                    hasSteepSections = true,
                    notableWildlife = "Birds",
                    isRocky = false,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, description = "The Mt. Purgatory Traverse offers several campsites. Mangisi Village (~2,100 MASL) is a common overnight spot. Bakian Elementary School is the " +
                        "traditional 2-day trail campsite. Aponan Junction / Trail Clearings are used for informal or emergency camping."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, name = "Mangisi Village", description = "~2,100 MASL. Common overnight spot on 3-day itinerary. Access to water. Cold and fog-prone. 10-15 tents capacity. " +
                        "Trek time ~7-9 hours from Japas (additional 2-3 hrs from Bakian)."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, name = "Bakian Elementary School", description = "Traditional 2-day trail campsite. Flat ground, has a waiting shed. Near midpoint of trail. ~10 " +
                        "tents capacity. Trek time ~5-6 hours from Japas."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, name = "Aponan Junction / Trail Clearings", description = "Informal or emergency campsites along mossy forest sections. 2-4 tents capacity. Coordinate with guide."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, name = "Amenities", description = "Water near Mangisi and early trail. Waiting shed at Bakian. No restroom facilities; nature stop."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, name = "Water Source", description = "Available early in hike (Kambingan area). Scarce beyond mid-trail-carry ample water."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, name = "Best For", description = "Experienced hikers, mossy forest lovers, Cordillera long-distance trekkers")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, name = "Standard Traverse", description = "Japas jump-off to Brgy. Ekip"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, name = "Shorter Dayhike Options", description = "Possible exit via Mangakew or closer access routes (rarely used)")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Registration Fee", description = "P100 at Bokod Municipal Hall"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Camping Fee", description = "P20 per night"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "School Donation", description = "P500 per group if camping at Bakian Elementary"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Guide Fee", description = "P500/day (up to 7 pax), +P100 per extra hiker"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Safety Tip", description = "Cold Weather: Wear layers; mossy sections can get below 10°C"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Safety Tip", description = "Tiring Trail: Long hours of walking-train for endurance"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Safety Tip", description = "Trail Markers: Stick with guide; trail is mostly clear but fog can reduce visibility"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Safety Tip", description = "Exit Logistics: Final 4-5 km road may require jeepney pickup or walking"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Safety Tip", description = "Leeches (Limatik): Possible in damp mossy areas-wear leech socks or gaiters"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtPurgatoryId, category = "Hiking Season", description = "Best hiked during the dry season (November to May). Trails become slippery and difficult in rainy months.")
            ))

            // --- Mt. Sicapoo Data ---
            val mtSicapooId = "mtsicapoo001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtSicapooId,
                    mountainName = "Mt. Sicapoo",
                    pictureReference = "mt_sicapoo_main",
                    location = "Solsona, Ilocos Norte",
                    masl = 2354,
                    difficultySummary = "Extremely Challenging (9/9)",
                    difficultyText = "Extremely Challenging (9/9)",
                    hoursToSummit = "16-18 hours (traverse)",
                    bestMonthsToHike = "November to early May",
                    typeVolcano = "Block Mountain",
                    trekDurationDetails = "16-18 hours (commonly done over 4-5 days)",
                    trailTypeDescription = "Point-to-point (Gasgas River to Solsona Dam traverse)",
                    sceneryDescription = "Multiple river crossings, tropical and pine forests, steep ridges, mossy trails, summit spire \"The Penguin,\" One Degree Plateau, panoramic views of Ilocos and Apayao ranges",
                    viewsDescription = "Wide panoramas from Timarid and Simagaysay, dramatic rock formations, dense forest canopies",
                    wildlifeDescription = "",
                    featuresDescription = "Remote challenge, World War II site (One Degree Plateau), multi-peak route, summit rock icon",
                    hikingSeasonDetails = "Best climbed from November to early May. Avoid during rainy season due to flood and landslide risks.",
                    introduction = "Standing at 2,354 MASL, Mt. Sicapoo is the highest mountain in " +
                            "Ilocos Norte and the entire Ilocos Region, earning it the title " +
                            "\"Roof of Ilocos.\" This is considered one of the most difficult and " +
                            "remote hikes in Luzon, with over 20 river crossings, steep ascents " +
                            "through dense jungle and pine ridges, and a rugged trail culminating " +
                            "at a summit marked by a rock spire called \"The Penguin.\" The full " +
                            "traverse exits at Solsona Dam, passing other peaks such as Mt. Timarid and Mt. Simagaysay.",
                    tagline = "Let's Hike to Mt. Sicapoo",
                    mountainImageRef1 = "mt_sicapoo_1",
                    mountainImageRef2 = "mt_sicapoo_2",
                    mountainImageRef3 = "mt_sicapoo_3",

                    hasSteepSections = true,
                    notableWildlife = "",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, description = "Mt. Sicapoo offers several campsites along its traverse. Saulay Campsite is the first-night camp. Pakpako Campsite is used for the summit push. " +
                        "The Summit Area is for emergency bivouac only. Optional campsites exist at Mt. Timarid summit, Mt. Simagaysay summit, and One Degree Plateau."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Saulay Campsite", description = "~1,200 MASL. First-night camp after river segment. Cool forest site with water nearby. Wet and shaded. 10-12 tents capacity. Trek time Day 1: 6-8 hours."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Pakpako Campsite", description = "~1,800-1,900 MASL. Coldest campsite, used for summit push. Surrounded by mossy forest. Water source present. 8-10 tents capacity. Trek time Day 2: 4-6 hours."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Summit Area", description = "~2,354 MASL. Small clearing near \"The Penguin.\" Only for emergency bivouac-steep and exposed."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Optional: Mt. Timarid summit", description = "Limited space (2-3 tents)."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Optional: Mt. Simagaysay summit", description = "Has flat ground, limited water (2-3 tents)."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Optional: One Degree Plateau", description = "Vast grassland, near end of trail (20+ tents)."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Amenities", description = "No formal facilities. Natural water sources at Saulay and Pakpako. No toilets-LNT required."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Water Source", description = "Available at Saulay and Pakpako. None from summit to exit-carry 2-3L for summit day."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Best For", description = "Veteran climbers, challenge seekers, remote adventure enthusiasts")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Standard Traverse", description = "Gasgas River (Brgy. Manalpac) to Solsona Dam"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, name = "Shortened Traverse", description = "Exit before summit via Timarid-Simagaysay route (emergency exit)")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Registration Fee", description = "P500/day; coordinate with One Degree Mountaineering Group (ODMG)"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Guide Fee", description = "P500/day"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Camping Fee", description = "Mandatory"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Safety Tip", description = "Flash Flood Risk: Avoid Gasgas River crossings after noon or during heavy rains"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Safety Tip", description = "Cold Exposure: Temperatures drop below 10°C at night-wear thermal layers"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Safety Tip", description = "Technical Sections: Use gloves, trekking poles, and stay alert near summit and ridges"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Safety Tip", description = "Physical Readiness: Demands top endurance-train for long hours and river navigation"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Safety Tip", description = "Exit Strategy: If needed, shorten trek by exiting through Timarid-Simagaysay route"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtSicapooId, category = "Hiking Season", description = "Best climbed from November to early May. Avoid during rainy season due to flood and landslide risks.")
            ))

            // --- Mt. Tirad Peak (Tirad Pass) Data ---
            val mtTiradPeakId = "mttiradpeak001"
            mountainDao.insertAllMountains(listOf(
                MountainEntity(
                    mountainId = mtTiradPeakId,
                    mountainName = "Mt. Tirad Peak (Tirad Pass)",
                    pictureReference = "mt_tirad_peak_main",
                    location = "Gregorio del Pilar, Ilocos Sur",
                    masl = 1388,
                    difficultySummary = "Challenging",
                    difficultyText = "Very Strenuous 6/9 - Challenging",
                    hoursToSummit = "9-11 hours (traverse)",
                    bestMonthsToHike = "November to April",
                    typeVolcano = "Mountain Peak",
                    trekDurationDetails = "9-11 hours (commonly done in 2-3 days)",
                    trailTypeDescription = "Point-to-point traverse (Brgy. Gregorio del Pilar to Quirino; extended trail possible to Sagada)",
                    sceneryDescription = "Del Pilar Shrine, Old Spanish Trail, Sniper's Knoll, mossy and pine forests, Tirad Peak summit views, Illengan Cave",
                    viewsDescription = "Panoramas of Ilocos lowlands, Abra River basin, and Cordillera ranges",
                    wildlifeDescription = "",
                    featuresDescription = "Historical shrine, cultural trail, mossy forests, scenic ridge hike",
                    hikingSeasonDetails = "Best climbed November to April. Avoid rainy season due to landslides and possible flash floods in lower trail sections.",
                    introduction = "Mt. Tirad Peak, reaching 1,388 MASL, is a hike rich in both " +
                            "historical value and natural beauty. It was the site of General Gregorio " +
                            "del Pilar's heroic last stand during the Philippine-American War in 1899, " +
                            "defending Tirad Pass to delay American troops and aid President Emilio " +
                            "Aguinaldo's escape. The trail follows the historic Spanish-era route, " +
                            "passing the del Pilar Shrine, Sniper's Knoll, and other heritage features " +
                            "before leading to the summit. It combines cultural significance with " +
                            "rugged mountain scenery and panoramic vistas of Ilocos and Cordillera ranges.",
                    tagline = "Let's Hike to Mt. Tirad Peak",
                    mountainImageRef1 = "mt_tirad_peak_1",
                    mountainImageRef2 = "mt_tirad_peak_2",
                    mountainImageRef3 = "mt_tirad_peak_3",

                    hasSteepSections = true,
                    notableWildlife = "",
                    isRocky = true,
                    isSlippery = true,
                    isEstablishedTrail = true
                )
            ))
            campsiteDao.insertAllCampsites(listOf(
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, description = "The Mt. Tirad Peak trail offers limited but historically and scenically significant campsites. Most itineraries include an overnight at the Tirad Pass Shrine, " +
                        "which is both a cultural landmark and a practical resting area. While the summit has a small clearing, it is generally used only for short breaks or emergency bivouacs due to exposure and harsh weather conditions."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, name = "Tirad Pass Shrine", description = "~1,500 MASL. Historical site and primary campsite. Offers flat space, cool climate, scenic ridges, and access to " +
                        "water. Culturally significant location. ~8-10 tents capacity. Trek time Day 1: 4-6 hours."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, name = "Summit Area", description = "~1,950 MASL (Note: PDF says Tirad Peak is 1,388 MASL, shrine 1,500 MASL, then summit area 1,950 MASL which is " +
                        "inconsistent. Using 1388 as summit for this entry). Small exposed clearing-used for short breaks or emergencies only. Not recommended for overnight stays due to terrain and cold. 1-2 tents max (emergency only)."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, name = "Amenities", description = "Water source available near the shrine. No restrooms or shelters-observe Leave No Trace principles."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, name = "Water Source", description = "Available near Tirad Shrine and selected points along the trail. None at summit-bring enough for summit day."),
                CampsiteEntity(campsiteId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, name = "Best For", description = "History buffs, heritage hikers, Cordillera explorers")
            ))
            trailDao.insertAllTrails(listOf(
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, name = "Standard Trail", description = "Gregorio del Pilar to Quirino (or reverse)"),
                TrailEntity(trailId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, name = "Extension", description = "Possible connection to Sagada or adjacent trails in the Cordillera range")
            ))
            guidelineDao.insertAllGuidelines(listOf(
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Registration Fee", description = "No official LGU registration; coordination with local tourism office is highly recommended"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Guide Fee", description = "P500/day"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Camping Fee", description = "P200/day"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Safety Tip", description = "Weather Exposure: Expect cold, especially at summit and ridges. Prepare layers for sudden drops in temperature"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Safety Tip", description = "Trail Hazards: Steep terrain, loose rocks, and slippery sections near summit"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Safety Tip", description = "Fitness: Moderate to high endurance required; suitable for intermediate to experienced hikers"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Safety Tip", description = "Emergency Exit: Possible to turn back before summit with guide's assistance"),
                GuidelineEntity(guidelineId = UUID.randomUUID().toString(), mountainOwnerId = mtTiradPeakId, category = "Hiking Season", description = "Best climbed November to April. Avoid rainy season due to landslides and possible flash floods in lower trail sections.")
            ))

            // --- Predifined CheckList ---
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
            ))

            Log.d("AppDatabaseCallback", "populateInitialData: FINISHED")
        }
    }
}
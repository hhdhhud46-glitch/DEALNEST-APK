package com.example.dealnest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dealnest.model.*

@Database(
    entities = [
        User::class,
        Project::class,
        Proposal::class,
        Deal::class,
        DealMilestone::class,
        PortfolioProject::class,
        DesignerLeadAlertConfig::class,
        BlockedUser::class,
        Conversation::class,
        ChatMessage::class,
        Review::class,
        AppNotification::class,
        FavoriteItem::class,
        ReportItem::class,
        VerificationRequest::class,
        DesignerService::class,
        DisputeItem::class,
        PaymentTransaction::class
    ],
    version = 5,
    exportSchema = false
)
abstract class DealNestDatabase : RoomDatabase() {
    abstract fun dealNestDao(): DealNestDao

    companion object {
        @Volatile
        private var INSTANCE: DealNestDatabase? = null

        fun getDatabase(context: Context): DealNestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DealNestDatabase::class.java,
                    "dealnest_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

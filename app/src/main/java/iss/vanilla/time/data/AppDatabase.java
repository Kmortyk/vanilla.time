package iss.vanilla.time.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;

@Database(entities = {TimerPageEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TimerPageDao timerPageDao();

//    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) { /* none */ }
//    };

}


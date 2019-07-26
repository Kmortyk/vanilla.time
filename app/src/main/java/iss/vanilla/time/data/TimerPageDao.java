package iss.vanilla.time.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public abstract class TimerPageDao {

    @Query("SELECT * FROM timerpageentity")
    public abstract List<TimerPageEntity> getAll();

    @Query("SELECT * FROM timerpageentity WHERE pagePosition = :position")
    public abstract TimerPageEntity get(int position);

    @Query("SELECT COUNT(pagePosition) FROM timerpageentity")
    public abstract int getCount();

    @Transaction
    public void insertOrUpdate(TimerPageEntity entity) {
        long id = insert(entity);
        if (id == -1) {
            update(entity);
        }
    }

    @Query("DELETE FROM timerpageentity")
    public abstract void deleteAll();

    @Transaction
    void delete(TimerPageEntity entity) {
        deleteOneRow(entity);
        updatePositions(entity.pagePosition);
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract long insert(TimerPageEntity entity);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    abstract void update(TimerPageEntity entity);

    @Delete
    abstract void deleteOneRow(TimerPageEntity entity);

    @Query("UPDATE timerpageentity SET pagePosition = pagePosition+1 WHERE pagePosition > :pagePosition;")
    abstract void updatePositions(int pagePosition);

}

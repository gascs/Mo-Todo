package com.mo.todo.data.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.mo.todo.data.dao.MemoDao;
import com.mo.todo.data.dao.MemoDao_Impl;
import com.mo.todo.data.dao.TodoDao;
import com.mo.todo.data.dao.TodoDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile TodoDao _todoDao;

  private volatile MemoDao _memoDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `todos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `description` TEXT, `tag` TEXT NOT NULL, `priority` INTEGER NOT NULL, `reminderTime` INTEGER, `isCompleted` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `memos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL, `tag` TEXT NOT NULL, `color` INTEGER, `isStarred` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'ef10f6f5d0433d82fd022b427e24a902')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `todos`");
        db.execSQL("DROP TABLE IF EXISTS `memos`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsTodos = new HashMap<String, TableInfo.Column>(8);
        _columnsTodos.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodos.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodos.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodos.put("tag", new TableInfo.Column("tag", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodos.put("priority", new TableInfo.Column("priority", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodos.put("reminderTime", new TableInfo.Column("reminderTime", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodos.put("isCompleted", new TableInfo.Column("isCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodos.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTodos = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTodos = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTodos = new TableInfo("todos", _columnsTodos, _foreignKeysTodos, _indicesTodos);
        final TableInfo _existingTodos = TableInfo.read(db, "todos");
        if (!_infoTodos.equals(_existingTodos)) {
          return new RoomOpenHelper.ValidationResult(false, "todos(com.mo.todo.data.model.Todo).\n"
                  + " Expected:\n" + _infoTodos + "\n"
                  + " Found:\n" + _existingTodos);
        }
        final HashMap<String, TableInfo.Column> _columnsMemos = new HashMap<String, TableInfo.Column>(8);
        _columnsMemos.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemos.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemos.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemos.put("tag", new TableInfo.Column("tag", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemos.put("color", new TableInfo.Column("color", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemos.put("isStarred", new TableInfo.Column("isStarred", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemos.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMemos.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMemos = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMemos = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMemos = new TableInfo("memos", _columnsMemos, _foreignKeysMemos, _indicesMemos);
        final TableInfo _existingMemos = TableInfo.read(db, "memos");
        if (!_infoMemos.equals(_existingMemos)) {
          return new RoomOpenHelper.ValidationResult(false, "memos(com.mo.todo.data.model.Memo).\n"
                  + " Expected:\n" + _infoMemos + "\n"
                  + " Found:\n" + _existingMemos);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "ef10f6f5d0433d82fd022b427e24a902", "f87ea6bc528eee7dee091c8325be6c0d");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "todos","memos");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `todos`");
      _db.execSQL("DELETE FROM `memos`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(TodoDao.class, TodoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MemoDao.class, MemoDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public TodoDao todoDao() {
    if (_todoDao != null) {
      return _todoDao;
    } else {
      synchronized(this) {
        if(_todoDao == null) {
          _todoDao = new TodoDao_Impl(this);
        }
        return _todoDao;
      }
    }
  }

  @Override
  public MemoDao memoDao() {
    if (_memoDao != null) {
      return _memoDao;
    } else {
      synchronized(this) {
        if(_memoDao == null) {
          _memoDao = new MemoDao_Impl(this);
        }
        return _memoDao;
      }
    }
  }
}

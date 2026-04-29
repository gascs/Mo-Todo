package com.mo.todo.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.mo.todo.data.model.Todo;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class TodoDao_Impl implements TodoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Todo> __insertionAdapterOfTodo;

  private final EntityDeletionOrUpdateAdapter<Todo> __deletionAdapterOfTodo;

  private final EntityDeletionOrUpdateAdapter<Todo> __updateAdapterOfTodo;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTodoById;

  private final SharedSQLiteStatement __preparedStmtOfUpdateCompletion;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTagByTag;

  public TodoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTodo = new EntityInsertionAdapter<Todo>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `todos` (`id`,`title`,`description`,`tag`,`priority`,`reminderTime`,`isCompleted`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Todo entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        if (entity.getDescription() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDescription());
        }
        statement.bindString(4, entity.getTag());
        statement.bindLong(5, entity.getPriority());
        if (entity.getReminderTime() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getReminderTime());
        }
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfTodo = new EntityDeletionOrUpdateAdapter<Todo>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `todos` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Todo entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTodo = new EntityDeletionOrUpdateAdapter<Todo>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `todos` SET `id` = ?,`title` = ?,`description` = ?,`tag` = ?,`priority` = ?,`reminderTime` = ?,`isCompleted` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Todo entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        if (entity.getDescription() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDescription());
        }
        statement.bindString(4, entity.getTag());
        statement.bindLong(5, entity.getPriority());
        if (entity.getReminderTime() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getReminderTime());
        }
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteTodoById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM todos WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateCompletion = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE todos SET isCompleted = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateTagByTag = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE todos SET tag = ? WHERE tag = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertTodo(final Todo todo, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfTodo.insertAndReturnId(todo);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTodo(final Todo todo, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfTodo.handle(todo);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTodo(final Todo todo, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfTodo.handle(todo);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteTodoById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTodoById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteTodoById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCompletion(final long id, final boolean completed,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateCompletion.acquire();
        int _argIndex = 1;
        final int _tmp = completed ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateCompletion.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTagByTag(final String oldTag, final String newTag,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTagByTag.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, newTag);
        _argIndex = 2;
        _stmt.bindString(_argIndex, oldTag);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateTagByTag.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Todo>> getAllTodos() {
    final String _sql = "SELECT * FROM todos ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"todos"}, new Callable<List<Todo>>() {
      @Override
      @NonNull
      public List<Todo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Todo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Todo(_tmpId,_tmpTitle,_tmpDescription,_tmpTag,_tmpPriority,_tmpReminderTime,_tmpIsCompleted,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllTodosSuspend(final Continuation<? super List<Todo>> $completion) {
    final String _sql = "SELECT * FROM todos ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Todo>>() {
      @Override
      @NonNull
      public List<Todo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Todo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Todo(_tmpId,_tmpTitle,_tmpDescription,_tmpTag,_tmpPriority,_tmpReminderTime,_tmpIsCompleted,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Todo>> getTodosByCompletion(final boolean completed) {
    final String _sql = "SELECT * FROM todos WHERE isCompleted = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final int _tmp = completed ? 1 : 0;
    _statement.bindLong(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"todos"}, new Callable<List<Todo>>() {
      @Override
      @NonNull
      public List<Todo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Todo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsCompleted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Todo(_tmpId,_tmpTitle,_tmpDescription,_tmpTag,_tmpPriority,_tmpReminderTime,_tmpIsCompleted,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Todo>> getTodosByTagAndCompletion(final String tag, final boolean completed) {
    final String _sql = "SELECT * FROM todos WHERE tag = ? AND isCompleted = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, tag);
    _argIndex = 2;
    final int _tmp = completed ? 1 : 0;
    _statement.bindLong(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"todos"}, new Callable<List<Todo>>() {
      @Override
      @NonNull
      public List<Todo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Todo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsCompleted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp_1 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Todo(_tmpId,_tmpTitle,_tmpDescription,_tmpTag,_tmpPriority,_tmpReminderTime,_tmpIsCompleted,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Todo>> searchTodos(final String query) {
    final String _sql = "SELECT * FROM todos WHERE title LIKE '%' || ? || '%' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"todos"}, new Callable<List<Todo>>() {
      @Override
      @NonNull
      public List<Todo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Todo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Todo(_tmpId,_tmpTitle,_tmpDescription,_tmpTag,_tmpPriority,_tmpReminderTime,_tmpIsCompleted,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Todo>> getCompletedTodosByTag(final String tag) {
    final String _sql = "SELECT * FROM todos WHERE isCompleted = 1 AND (tag = ? OR ? = 'all') ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, tag);
    _argIndex = 2;
    _statement.bindString(_argIndex, tag);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"todos"}, new Callable<List<Todo>>() {
      @Override
      @NonNull
      public List<Todo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Todo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Todo(_tmpId,_tmpTitle,_tmpDescription,_tmpTag,_tmpPriority,_tmpReminderTime,_tmpIsCompleted,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Todo>> getActiveTodosByTag(final String tag) {
    final String _sql = "SELECT * FROM todos WHERE isCompleted = 0 AND (tag = ? OR ? = 'all') ORDER BY priority DESC, createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, tag);
    _argIndex = 2;
    _statement.bindString(_argIndex, tag);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"todos"}, new Callable<List<Todo>>() {
      @Override
      @NonNull
      public List<Todo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Todo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Todo(_tmpId,_tmpTitle,_tmpDescription,_tmpTag,_tmpPriority,_tmpReminderTime,_tmpIsCompleted,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTodoById(final long id, final Continuation<? super Todo> $completion) {
    final String _sql = "SELECT * FROM todos WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Todo>() {
      @Override
      @Nullable
      public Todo call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final Todo _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new Todo(_tmpId,_tmpTitle,_tmpDescription,_tmpTag,_tmpPriority,_tmpReminderTime,_tmpIsCompleted,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getUpcomingReminders(final long now, final long threshold,
      final Continuation<? super List<Todo>> $completion) {
    final String _sql = "SELECT * FROM todos WHERE reminderTime IS NOT NULL AND reminderTime > ? AND reminderTime < ? AND isCompleted = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, now);
    _argIndex = 2;
    _statement.bindLong(_argIndex, threshold);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Todo>>() {
      @Override
      @NonNull
      public List<Todo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfTag = CursorUtil.getColumnIndexOrThrow(_cursor, "tag");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfReminderTime = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Todo> _result = new ArrayList<Todo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Todo _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpTag;
            _tmpTag = _cursor.getString(_cursorIndexOfTag);
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final Long _tmpReminderTime;
            if (_cursor.isNull(_cursorIndexOfReminderTime)) {
              _tmpReminderTime = null;
            } else {
              _tmpReminderTime = _cursor.getLong(_cursorIndexOfReminderTime);
            }
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Todo(_tmpId,_tmpTitle,_tmpDescription,_tmpTag,_tmpPriority,_tmpReminderTime,_tmpIsCompleted,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

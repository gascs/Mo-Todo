package com.mo.todo.di;

import com.mo.todo.data.dao.TodoDao;
import com.mo.todo.data.database.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DatabaseModule_ProvideTodoDaoFactory implements Factory<TodoDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideTodoDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public TodoDao get() {
    return provideTodoDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideTodoDaoFactory create(
      javax.inject.Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideTodoDaoFactory(Providers.asDaggerProvider(databaseProvider));
  }

  public static DatabaseModule_ProvideTodoDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideTodoDaoFactory(databaseProvider);
  }

  public static TodoDao provideTodoDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideTodoDao(database));
  }
}

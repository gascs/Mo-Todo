package com.mo.todo.di;

import com.mo.todo.data.dao.MemoDao;
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
public final class DatabaseModule_ProvideMemoDaoFactory implements Factory<MemoDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideMemoDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public MemoDao get() {
    return provideMemoDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideMemoDaoFactory create(
      javax.inject.Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideMemoDaoFactory(Providers.asDaggerProvider(databaseProvider));
  }

  public static DatabaseModule_ProvideMemoDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideMemoDaoFactory(databaseProvider);
  }

  public static MemoDao provideMemoDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideMemoDao(database));
  }
}

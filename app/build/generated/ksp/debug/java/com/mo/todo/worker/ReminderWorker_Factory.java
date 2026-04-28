package com.mo.todo.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.mo.todo.data.database.AppDatabase;
import dagger.internal.DaggerGenerated;
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
public final class ReminderWorker_Factory {
  private final Provider<AppDatabase> databaseProvider;

  public ReminderWorker_Factory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  public ReminderWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, databaseProvider.get());
  }

  public static ReminderWorker_Factory create(javax.inject.Provider<AppDatabase> databaseProvider) {
    return new ReminderWorker_Factory(Providers.asDaggerProvider(databaseProvider));
  }

  public static ReminderWorker_Factory create(Provider<AppDatabase> databaseProvider) {
    return new ReminderWorker_Factory(databaseProvider);
  }

  public static ReminderWorker newInstance(Context context, WorkerParameters workerParams,
      AppDatabase database) {
    return new ReminderWorker(context, workerParams, database);
  }
}

package com.mo.todo.worker;

import com.mo.todo.data.database.AppDatabase;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class BootReceiver_MembersInjector implements MembersInjector<BootReceiver> {
  private final Provider<AppDatabase> databaseProvider;

  public BootReceiver_MembersInjector(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  public static MembersInjector<BootReceiver> create(Provider<AppDatabase> databaseProvider) {
    return new BootReceiver_MembersInjector(databaseProvider);
  }

  public static MembersInjector<BootReceiver> create(
      javax.inject.Provider<AppDatabase> databaseProvider) {
    return new BootReceiver_MembersInjector(Providers.asDaggerProvider(databaseProvider));
  }

  @Override
  public void injectMembers(BootReceiver instance) {
    injectDatabase(instance, databaseProvider.get());
  }

  @InjectedFieldSignature("com.mo.todo.worker.BootReceiver.database")
  public static void injectDatabase(BootReceiver instance, AppDatabase database) {
    instance.database = database;
  }
}

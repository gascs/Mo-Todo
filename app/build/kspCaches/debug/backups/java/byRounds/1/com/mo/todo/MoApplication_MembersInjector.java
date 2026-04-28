package com.mo.todo;

import androidx.hilt.work.HiltWorkerFactory;
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
public final class MoApplication_MembersInjector implements MembersInjector<MoApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public MoApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<MoApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new MoApplication_MembersInjector(workerFactoryProvider);
  }

  public static MembersInjector<MoApplication> create(
      javax.inject.Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new MoApplication_MembersInjector(Providers.asDaggerProvider(workerFactoryProvider));
  }

  @Override
  public void injectMembers(MoApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.mo.todo.MoApplication.workerFactory")
  public static void injectWorkerFactory(MoApplication instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}

package com.mo.todo.di;

import com.mo.todo.data.dao.MemoDao;
import com.mo.todo.repository.MemoRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class RepositoryModule_ProvideMemoRepositoryFactory implements Factory<MemoRepository> {
  private final Provider<MemoDao> memoDaoProvider;

  public RepositoryModule_ProvideMemoRepositoryFactory(Provider<MemoDao> memoDaoProvider) {
    this.memoDaoProvider = memoDaoProvider;
  }

  @Override
  public MemoRepository get() {
    return provideMemoRepository(memoDaoProvider.get());
  }

  public static RepositoryModule_ProvideMemoRepositoryFactory create(
      javax.inject.Provider<MemoDao> memoDaoProvider) {
    return new RepositoryModule_ProvideMemoRepositoryFactory(Providers.asDaggerProvider(memoDaoProvider));
  }

  public static RepositoryModule_ProvideMemoRepositoryFactory create(
      Provider<MemoDao> memoDaoProvider) {
    return new RepositoryModule_ProvideMemoRepositoryFactory(memoDaoProvider);
  }

  public static MemoRepository provideMemoRepository(MemoDao memoDao) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideMemoRepository(memoDao));
  }
}

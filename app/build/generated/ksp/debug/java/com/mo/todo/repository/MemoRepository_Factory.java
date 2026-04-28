package com.mo.todo.repository;

import com.mo.todo.data.dao.MemoDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class MemoRepository_Factory implements Factory<MemoRepository> {
  private final Provider<MemoDao> memoDaoProvider;

  public MemoRepository_Factory(Provider<MemoDao> memoDaoProvider) {
    this.memoDaoProvider = memoDaoProvider;
  }

  @Override
  public MemoRepository get() {
    return newInstance(memoDaoProvider.get());
  }

  public static MemoRepository_Factory create(javax.inject.Provider<MemoDao> memoDaoProvider) {
    return new MemoRepository_Factory(Providers.asDaggerProvider(memoDaoProvider));
  }

  public static MemoRepository_Factory create(Provider<MemoDao> memoDaoProvider) {
    return new MemoRepository_Factory(memoDaoProvider);
  }

  public static MemoRepository newInstance(MemoDao memoDao) {
    return new MemoRepository(memoDao);
  }
}

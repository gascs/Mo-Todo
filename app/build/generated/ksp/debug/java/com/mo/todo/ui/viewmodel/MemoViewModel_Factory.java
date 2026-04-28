package com.mo.todo.ui.viewmodel;

import com.mo.todo.repository.MemoRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class MemoViewModel_Factory implements Factory<MemoViewModel> {
  private final Provider<MemoRepository> memoRepositoryProvider;

  public MemoViewModel_Factory(Provider<MemoRepository> memoRepositoryProvider) {
    this.memoRepositoryProvider = memoRepositoryProvider;
  }

  @Override
  public MemoViewModel get() {
    return newInstance(memoRepositoryProvider.get());
  }

  public static MemoViewModel_Factory create(
      javax.inject.Provider<MemoRepository> memoRepositoryProvider) {
    return new MemoViewModel_Factory(Providers.asDaggerProvider(memoRepositoryProvider));
  }

  public static MemoViewModel_Factory create(Provider<MemoRepository> memoRepositoryProvider) {
    return new MemoViewModel_Factory(memoRepositoryProvider);
  }

  public static MemoViewModel newInstance(MemoRepository memoRepository) {
    return new MemoViewModel(memoRepository);
  }
}

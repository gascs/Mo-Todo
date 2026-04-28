package com.mo.todo.di;

import com.mo.todo.data.dao.TodoDao;
import com.mo.todo.repository.TodoRepository;
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
public final class RepositoryModule_ProvideTodoRepositoryFactory implements Factory<TodoRepository> {
  private final Provider<TodoDao> todoDaoProvider;

  public RepositoryModule_ProvideTodoRepositoryFactory(Provider<TodoDao> todoDaoProvider) {
    this.todoDaoProvider = todoDaoProvider;
  }

  @Override
  public TodoRepository get() {
    return provideTodoRepository(todoDaoProvider.get());
  }

  public static RepositoryModule_ProvideTodoRepositoryFactory create(
      javax.inject.Provider<TodoDao> todoDaoProvider) {
    return new RepositoryModule_ProvideTodoRepositoryFactory(Providers.asDaggerProvider(todoDaoProvider));
  }

  public static RepositoryModule_ProvideTodoRepositoryFactory create(
      Provider<TodoDao> todoDaoProvider) {
    return new RepositoryModule_ProvideTodoRepositoryFactory(todoDaoProvider);
  }

  public static TodoRepository provideTodoRepository(TodoDao todoDao) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideTodoRepository(todoDao));
  }
}

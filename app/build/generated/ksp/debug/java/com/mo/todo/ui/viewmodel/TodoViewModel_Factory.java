package com.mo.todo.ui.viewmodel;

import android.app.Application;
import com.mo.todo.repository.TodoRepository;
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
public final class TodoViewModel_Factory implements Factory<TodoViewModel> {
  private final Provider<TodoRepository> todoRepositoryProvider;

  private final Provider<Application> applicationProvider;

  public TodoViewModel_Factory(Provider<TodoRepository> todoRepositoryProvider,
      Provider<Application> applicationProvider) {
    this.todoRepositoryProvider = todoRepositoryProvider;
    this.applicationProvider = applicationProvider;
  }

  @Override
  public TodoViewModel get() {
    return newInstance(todoRepositoryProvider.get(), applicationProvider.get());
  }

  public static TodoViewModel_Factory create(
      javax.inject.Provider<TodoRepository> todoRepositoryProvider,
      javax.inject.Provider<Application> applicationProvider) {
    return new TodoViewModel_Factory(Providers.asDaggerProvider(todoRepositoryProvider), Providers.asDaggerProvider(applicationProvider));
  }

  public static TodoViewModel_Factory create(Provider<TodoRepository> todoRepositoryProvider,
      Provider<Application> applicationProvider) {
    return new TodoViewModel_Factory(todoRepositoryProvider, applicationProvider);
  }

  public static TodoViewModel newInstance(TodoRepository todoRepository, Application application) {
    return new TodoViewModel(todoRepository, application);
  }
}

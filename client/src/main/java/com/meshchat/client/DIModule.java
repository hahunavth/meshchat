package com.meshchat.client;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.meshchat.client.binding.IDataSource;
import com.meshchat.client.binding.ITCPService;
import com.meshchat.client.model.DataStore;
import com.meshchat.client.net.client.TCPNativeClient;
import com.meshchat.client.viewmodels.*;
import com.meshchat.client.viewmodels.interfaces.*;
import com.meshchat.client.views.base.INavigation;
import com.meshchat.client.views.navigation.StackNavigation;

/**
 * Dependency injection using IOC Google Guice
 * define bindings
 */
public class DIModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(BaseViewModel.class).to(MessageViewModel.class);
        // singleton
        bind(DataStore.class).annotatedWith(IDataSource.class).toInstance(new DataStore());
        bind(TCPNativeClient.class).annotatedWith(ITCPService.class).toInstance(new TCPNativeClient("127.0.0.1", 9000));
        bind(new TypeLiteral<INavigation<StackNavigation.WINDOW_LIST>>() {}).to(StackNavigation.class).asEagerSingleton();
        // view models
        bind(IChatViewModel.class).to(ChatViewModel.class);
        bind(ICreateConvViewModel.class).to(CreateConvViewModel.class);
        bind(IDialogViewModel.class).to(DialogViewModel.class);
        bind(ILoginViewModel.class).to(LoginViewModel.class);
        bind(IMessageViewModel.class).to(MessageViewModel.class);
        bind(ISearchUserViewModel.class).to(SearchUserViewModel.class);
        bind(ISignUpViewModel.class).to(SignUpViewModel.class);
        bind(IUserProfileViewModel.class).to(UserProfileViewModel.class);
        bind(IPaginateViewModel.class).to(PaginateViewModel.class);
        bind(IConvInfoViewModel.class).to(ConvInfoViewModel.class);
    }
}

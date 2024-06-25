package at.aau.anti_mon.client.game;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import at.aau.anti_mon.client.utilities.UserManager;

public class UserProxy implements InvocationHandler {
    private final UserManager userManager;

    public UserProxy(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        User appUser = userManager.getAppUser();

        if (method.getName().startsWith("set")) {
            Method targetMethod = appUser.getClass().getMethod(method.getName(), method.getParameterTypes());
            return targetMethod.invoke(appUser, args);
        } else if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
            Method targetMethod = appUser.getClass().getMethod(method.getName());
            return targetMethod.invoke(appUser);
        }

        return null;
    }

    public static IUser create(UserManager userManager) {
        return (IUser) Proxy.newProxyInstance(
                IUser.class.getClassLoader(),
                new Class<?>[]{IUser.class},
                new UserProxy(userManager)
        );
    }
}

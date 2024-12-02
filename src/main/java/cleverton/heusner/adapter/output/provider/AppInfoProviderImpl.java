package cleverton.heusner.adapter.output.provider;

import cleverton.heusner.port.output.AppInfoProvider;
import jakarta.enterprise.context.Dependent;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Dependent
public class AppInfoProviderImpl implements AppInfoProvider {

    private final String appName;

    public AppInfoProviderImpl(@ConfigProperty(name = "quarkus.application.name") String appName) {
        this.appName = appName;
    }

    @Override
    public String getAppName() {
        return appName;
    }
}
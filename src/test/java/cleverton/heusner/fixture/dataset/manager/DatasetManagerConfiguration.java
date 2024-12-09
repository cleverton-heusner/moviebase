package cleverton.heusner.fixture.dataset.manager;

import cleverton.heusner.fixture.dataset.cleaner.DatasetCleaner;
import cleverton.heusner.fixture.dataset.creator.DatasetCreator;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;

@Dependent
public class DatasetManagerConfiguration {

    @Produces
    public DatasetManager dataSource(final DatasetCreator datasetCreator,
                                     final DatasetCleaner datasetCleaner) {
        return new DatasetManager(datasetCreator, datasetCleaner);
    }
}
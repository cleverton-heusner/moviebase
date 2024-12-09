package cleverton.heusner.fixture.dataset.manager;

import cleverton.heusner.fixture.dataset.cleaner.DatasetCleaner;
import cleverton.heusner.fixture.dataset.creator.DatasetCreator;

public class DatasetManager {

    private final DatasetCreator datasetCreator;
    private final DatasetCleaner datasetCleaner;

    public DatasetManager(final DatasetCreator datasetCreator,
                          final DatasetCleaner datasetCleaner) {
        this.datasetCreator = datasetCreator;
        this.datasetCleaner = datasetCleaner;
    }

    public final void create(final Object... entities) {
        datasetCreator.create(entities);
    }

    public void clean(final Class<?> entityClass) {
        datasetCleaner.clean(entityClass);
    }
}
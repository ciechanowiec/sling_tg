package eu.ciechanowiec.sling.telegram.api;

import eu.ciechanowiec.sling.rocket.jcr.path.JCRPath;

/**
 * {@link JCRPath} where data related with a given {@link TGBot} is persisted.
 */
@FunctionalInterface
public interface TGBotHome extends JCRPath {

}

package de.shop.util.concurrency;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

/**
 * @author <a href="mailto:Juergen.Zimmermann@HS-Karlsruhe.de">J&uuml;rgen Zimmermann</a>
 */
@Dependent
public class ManagedExecutorServiceProducer {
	@Resource
	@Produces
	private ManagedExecutorService managedExecutorService;
	
	// fuer zeitlich versetzte oder periodische Ausfuehrungen
	//@Resource
	//@Produces
	//private ManagedScheduledExecutorService managedScheduledExecutorService;
}

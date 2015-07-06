package org.cld.webconf;

import java.util.List;

import javax.jws.WebService;

import org.cld.datastore.entity.SiteConf;

@WebService(endpointInterface = "org.cld.webconf.CrawlConfWS", serviceName = "crawlconf")
public class CrawlConfWSImpl implements CrawlConfWS{

	@Override
	public List<SiteConf> getSiteConf() {
		return ConfServlet.getCConf().getDefaultDsm().getSiteConf(null, true, SiteConf.STATUS_DEPLOYED);
	}

}

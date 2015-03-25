package org.cld.webconf;

import java.util.List;

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.cld.datastore.entity.SiteConf;

@WebService
@Path("/crawlconf")
@Produces("application/json")
@Consumes("application/json")
public interface CrawlConfWS {
	@GET
   	@Path("/siteconfs/")
    public List<SiteConf> getSiteConf();
}

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.falcon.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.falcon.FalconWebException;
import org.apache.falcon.monitors.Dimension;
import org.apache.falcon.monitors.Monitored;

/**
 * Entity management operations as REST API for feed and process.
 */
@Path("entities")
public class SchedulableEntityManager extends AbstractSchedulableEntityManager {

    @GET
    @Path("status/{type}/{entity}")
    @Produces({MediaType.TEXT_XML, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Monitored(event = "status")
    @Override
    public APIResult getStatus(@Dimension("entityType") @PathParam("type") String type,
                               @Dimension("entityName") @PathParam("entity") String entity,
                               @Dimension("colo") @QueryParam("colo") final String colo,
                               @Dimension("showScheduler") @QueryParam("showScheduler") final Boolean showScheduler) {
        return super.getStatus(type, entity, colo, showScheduler);
    }

    @GET
    @Path("sla-alert/{type}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_XML})
    @Monitored(event = "feed-sla-misses")
    public SchedulableEntityInstanceResult getFeedSLAMissPendingAlerts(
            @Dimension("entityType") @PathParam("type") String entityType,
            @Dimension("entityName") @QueryParam("name") String entityName,
            @Dimension("start") @QueryParam("start") String start,
            @Dimension("end") @QueryParam("end") String end,
            @Dimension("colo") @QueryParam("colo") final String colo) {
        try {
            validateSlaParams(entityType, entityName, start, end, colo);
        } catch (Exception e) {
            throw FalconWebException.newAPIException(e);
        }
        return super.getFeedSLAMissPendingAlerts(entityName, start, end, colo);
    }

    @GET
    @Path("dependencies/{type}/{entity}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Monitored(event = "dependencies")
    @Override
    public EntityList getDependencies(@Dimension("entityType") @PathParam("type") String type,
                                      @Dimension("entityName") @PathParam("entity") String entity) {
        return super.getDependencies(type, entity);
    }

    //SUSPEND CHECKSTYLE CHECK ParameterNumberCheck
    @GET
    @Path("list{type : (/[^/]+)?}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Monitored(event = "list")
    @Override
    public EntityList getEntityList(@Dimension("type") @PathParam("type") String type,
                                    @DefaultValue("") @QueryParam("fields") String fields,
                                    @DefaultValue("") @QueryParam("nameseq") String nameSubsequence,
                                    @DefaultValue("") @QueryParam("tagkeys") String tagKeywords,
                                    @DefaultValue("") @QueryParam("tags") String tags,
                                    @DefaultValue("") @QueryParam("filterBy") String filterBy,
                                    @DefaultValue("") @QueryParam("orderBy") String orderBy,
                                    @DefaultValue("asc") @QueryParam("sortOrder") String sortOrder,
                                    @DefaultValue("0") @QueryParam("offset") Integer offset,
                                    @QueryParam("numResults") Integer resultsPerPage,
                                    @DefaultValue("") @QueryParam("doAs") String doAsUser) {
        if (StringUtils.isNotEmpty(type)) {
            type = type.substring(1);
        }
        resultsPerPage = resultsPerPage == null ? getDefaultResultsPerPage() : resultsPerPage;
        return super.getEntityList(fields, nameSubsequence, tagKeywords, type, tags, filterBy,
                orderBy, sortOrder, offset, resultsPerPage, doAsUser);
    }

    @GET
    @Path("summary/{type}")
    @Produces({MediaType.TEXT_XML, MediaType.APPLICATION_JSON})
    @Monitored(event = "summary")
    @Override
    public EntitySummaryResult getEntitySummary(
            @Dimension("type") @PathParam("type") String type,
            @Dimension("cluster") @QueryParam("cluster") String cluster,
            @DefaultValue("") @QueryParam("start") String startStr,
            @DefaultValue("") @QueryParam("end") String endStr,
            @DefaultValue("") @QueryParam("fields") String fields,
            @DefaultValue("") @QueryParam("filterBy") String entityFilter,
            @DefaultValue("") @QueryParam("tags")  String entityTags,
            @DefaultValue("") @QueryParam("orderBy") String entityOrderBy,
            @DefaultValue("asc") @QueryParam("sortOrder") String entitySortOrder,
            @DefaultValue("0") @QueryParam("offset") Integer entityOffset,
            @DefaultValue("10") @QueryParam("numResults") Integer numEntities,
            @DefaultValue("7") @QueryParam("numInstances") Integer numInstanceResults,
            @DefaultValue("") @QueryParam("doAs") final String doAsUser) {
        return super.getEntitySummary(type, cluster, startStr, endStr, fields, entityFilter, entityTags,
                entityOrderBy, entitySortOrder, entityOffset, numEntities, numInstanceResults, doAsUser);
    }
    //RESUME CHECKSTYLE CHECK ParameterNumberCheck

    @GET
    @Path("definition/{type}/{entity}")
    @Produces({MediaType.TEXT_XML, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Monitored(event = "definition")
    @Override
    public String getEntityDefinition(@Dimension("type") @PathParam("type") String type,
                                      @Dimension("entity") @PathParam("entity") String entityName) {
        return super.getEntityDefinition(type, entityName);
    }

    @POST
    @Path("schedule/{type}/{entity}")
    @Produces({MediaType.TEXT_XML, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Monitored(event = "schedule")
    @Override
    public APIResult schedule(@Context HttpServletRequest request,
                              @Dimension("entityType") @PathParam("type") String type,
                              @Dimension("entityName") @PathParam("entity") String entity,
                              @Dimension("colo") @QueryParam("colo") String colo,
                              @QueryParam("skipDryRun") Boolean skipDryRun,
                              @QueryParam("properties") String properties) {
        return super.schedule(request, type, entity, colo, skipDryRun, properties);
    }

    @POST
    @Path("suspend/{type}/{entity}")
    @Produces({MediaType.TEXT_XML, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Monitored(event = "suspend")
    @Override
    public APIResult suspend(@Context HttpServletRequest request,
                             @Dimension("entityType") @PathParam("type") String type,
                             @Dimension("entityName") @PathParam("entity") String entity,
                             @Dimension("colo") @QueryParam("colo") String colo) {
        return super.suspend(request, type, entity, colo);
    }

    @POST
    @Path("resume/{type}/{entity}")
    @Produces({MediaType.TEXT_XML, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Monitored(event = "resume")
    @Override
    public APIResult resume(@Context HttpServletRequest request,
                            @Dimension("entityType") @PathParam("type") String type,
                            @Dimension("entityName") @PathParam("entity") String entity,
                            @Dimension("colo") @QueryParam("colo") String colo) {
        return super.resume(request, type, entity, colo);
    }

    @POST
    @Path("validate/{type}")
    @Consumes({MediaType.TEXT_XML, MediaType.TEXT_PLAIN})
    @Produces({MediaType.TEXT_XML, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Monitored(event = "validate")
    @Override
    public APIResult validate(@Context HttpServletRequest request, @PathParam("type") String type,
                              @QueryParam("skipDryRun") Boolean skipDryRun) {
        return super.validate(request, type, skipDryRun);
    }

    @POST
    @Path("touch/{type}/{entity}")
    @Produces({MediaType.TEXT_XML, MediaType.TEXT_PLAIN})
    @Monitored(event = "touch")
    @Override
    public APIResult touch(@Dimension("entityType") @PathParam("type") String type,
                           @Dimension("entityName") @PathParam("entity") String entityName,
                           @Dimension("colo") @QueryParam("colo") String colo,
                           @QueryParam("skipDryRun") Boolean skipDryRun) {
        return super.touch(type, entityName, colo, skipDryRun);
    }

    @GET
    @Path("lookup/{type}/")
    @Produces(MediaType.APPLICATION_JSON)
    @Monitored(event = "reverse-lookup")
    public FeedLookupResult reverseLookup(
            @Dimension("type") @PathParam("type") String type,
            @Dimension("path") @QueryParam("path") String instancePath) {
        return super.reverseLookup(type, instancePath);
    }

}

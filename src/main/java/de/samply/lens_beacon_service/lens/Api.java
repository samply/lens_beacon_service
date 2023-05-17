package de.samply.lens_beacon_service.lens;

import de.samply.lens_beacon_service.query.QueryService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

/**
 * An API that talks directly with a Lens GUI instance or anything else that can generate queries in the
 * AST (tree) format. It executes the queries, and returns results based around serialized FHIR measure
 * reports.
 */

@Path("/query")
@Slf4j
public class Api {
  @Context
  private UriInfo uriInfo;

  private QueryService queryService = new QueryService();

  /**
   * Converts the incoming AST query into a form that Beacon understands,
   * runs the query against one or more Beacon sites and returns a serialized
   * list of SiteResult objects, one per Beacon site.
   *
   * @param astNode query, in AST format.
   * @return Serialized list of SiteResult objects.
   */
  @POST
  @Path("/ast")
  @Consumes({MediaType.APPLICATION_JSON})
  @APIResponses({
      @APIResponse(responseCode = "201", description = "Created"),
      @APIResponse(responseCode = "500", description = "Internal Server Error")
  })
  @Operation(summary = "Retrieve count of individuals matching the supplied query")
  public Response postAstQuery(
      @Parameter(
          name = "queryContainer",
          description = "Structured query and the target as a query container object.",
          schema = @Schema(implementation = AstNode.class))
      AstNode astNode) {
    log.info("postAstQuery: entered");
    if (astNode == null) {
      return Response.status(BAD_REQUEST)
          .entity("Missing payload. Expected a query container encoded in JSON.")
          .build();
    }
    try {
      String jsonResult = queryService.runQuery(astNode);

      return addCorsHeaders(Response.ok(jsonResult)).header("Access-Control-Expose-Headers", "Location").build();
    } catch (Exception e) {
      log.error("Error while creating a structured query.", e);
      return Response.status(INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  /**
   * CORS preflight for AST query.
   *
   * @return Response 200 when query is released or 500 when an error has occurred
   */
  @OPTIONS
  @Path("/ast")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
  @APIResponses({
      @APIResponse(responseCode = "200", description = "Ok, when query is released")
  })
  @Operation(summary = "Preflight OPTIONS for CORS for individuals count query")
  public Response optionsAstQuery() {
    return createPreflightCorsResponse(HttpMethod.POST, "Origin, Accept, Content-Type");
  }

  private Response.ResponseBuilder addCorsHeaders(Response.ResponseBuilder builder) {
    return builder
        .header("Access-Control-Allow-Origin", "*")
        .header("Cache-Control", "no-cache");
  }

  private Response createPreflightCorsResponse(String allowedMethod, String allowedHeaders) {
    return Response.noContent()
        .header("Access-Control-Allow-Origin", "*")
        .header("Access-Control-Allow-Methods", allowedMethod)
        .header("Access-Control-Allow-Headers", allowedHeaders)
        .build();
  }
}

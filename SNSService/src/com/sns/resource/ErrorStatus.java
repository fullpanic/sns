package com.sns.resource;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.service.StatusService;

import com.sns.resource.orm.RespData;

/**
 * process illegal request
 * @author fullpanic
 *
 */
public class ErrorStatus extends StatusService {
    
    @Override
    public Representation getRepresentation(Status status, Request request, Response response) {
        RespData data = new RespData(status);
        return data.getBody();
    }
}

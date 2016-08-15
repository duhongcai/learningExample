package com.yile.learning.resource.learning;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.mvc.Viewable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.rabbitframework.commons.utils.FileUtils;
import com.rabbitframework.web.resources.RabbitContextResource;

@Component
@Path("/")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UploadResource extends RabbitContextResource {
	private static final Logger logger = LoggerFactory.getLogger(UploadResource.class);

	@GET
	@Path("upload")
	public Viewable upload(@Context HttpServletRequest request) {
		String realPath = request.getServletContext().getRealPath("/");
		logger.info("realPath,{}", realPath);
		return new Viewable("/upload.jsp");
	}

	@POST
	@Path("uploadImage")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_HTML)
	public Object uploadImage(FormDataMultiPart form, @FormDataParam("username") String username,
			@Context HttpServletResponse response, @Context HttpServletRequest request) throws IOException {
		FormDataBodyPart filePart = form.getField("file");
		InputStream is = filePart.getValueAs(InputStream.class);
		FormDataContentDisposition formDataContentDisposition = filePart.getFormDataContentDisposition();
		String realPath = request.getServletContext().getRealPath("/");
		String filePath = realPath + File.separator + "images" + File.separator + System.currentTimeMillis() + "_"
				+ formDataContentDisposition.getFileName();
		FileUtils.copyInputStreamToFile(is, new File(filePath));
		return Response.ok("success").build();
	}
}

package hr.vsite.mentor.servlet.rest.resources;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import hr.vsite.mentor.course.Course;
import hr.vsite.mentor.lecture.Lecture;
import hr.vsite.mentor.lecture.LectureFilter;
import hr.vsite.mentor.lecture.LectureManager;
import hr.vsite.mentor.user.User;

@Path("lectures")
public class LectureResource {
	
	@Inject
	public LectureResource(Provider<LectureManager> lectureProvider) {
		this.lectureProvider = lectureProvider;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public List<Lecture> list(
		@QueryParam("title") String title,
		@QueryParam("author") User author,
		@QueryParam("course") Course course,
		@QueryParam("count") Integer count,
		@QueryParam("offset") Integer offset
	) {		
		LectureFilter filter = new LectureFilter();
		filter.setTitle(title);
		filter.setAuthor(author);
		filter.setCourse(course);
		
		return lectureProvider.get().list(filter, count, offset);	
	}
   
	@GET
	@Path("{lectureId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Lecture findById(@PathParam("lectureId") Lecture lectureId) {

		if (lectureId == null)
			throw new NotFoundException();

		return lectureId;

	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response insert(Lecture lecture){
		
		return Response.status(201).entity(lectureProvider.get().insert(lecture)).build();
	}
	
	@PUT
	@Path("{lectureId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response update(@PathParam("lectureId")UUID lectureId, Lecture lecture){
		
		lectureProvider.get().update(lectureId, lecture);
		return Response.status(200).entity(lectureProvider.get().findById(lectureId)).build();
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response delete(Lecture lecture){
		
		return Response.status(200).entity(lectureProvider.get().delete(lecture)).build();
	}


	private final Provider<LectureManager> lectureProvider;
}
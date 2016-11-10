package hr.vsite.mentor.course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hr.vsite.mentor.user.UserManager;

public class CourseManager {

	@Inject
	public CourseManager(Provider<UserManager> userProvider, Provider<Connection> connProvider) {
		this.userProvider = userProvider;
		this.connProvider = connProvider;
	}

	/** Returns <code>Course</code> with corresponding id, or <code>null</code> if such course does not exist. */
	public Course findById(UUID id) {

		try (PreparedStatement statement = connProvider.get().prepareStatement("SELECT * FROM courses WHERE course_id = ?")) {
			statement.setObject(1, id);
			try (ResultSet resultSet = statement.executeQuery()) {
	        	return resultSet.next() ? fromResultSet(resultSet) : null;
	        }
		} catch (Exception e) {
			throw new RuntimeException("Unable to find course with id " + id, e);
		}
	
	}
	
	/** Returns all courses known to application, unpaged. If there is a possibility for large number of courses,
	 * use {@link #list(CourseFilter, Integer, Integer)} */
	public List<Course> list() {
		return list(new CourseFilter(), null, null);
	}
	
	/** Returns courses that match given criteria, unpaged. If there is a possibility for large number of courses,
	 * use {@link #list(CourseFilter, Integer, Integer)} */
	public List<Course> list(CourseFilter filter) {
		return list(filter, null, null);
	}
	
	/** Returns courses that match given criteria, paged. */
	public List<Course> list(CourseFilter filter, Integer count, Integer offset) {

		List<Course> courses = new ArrayList<>(count != null ? count : 10);
		
		StringBuilder queryBuilder = new StringBuilder(1000);
		queryBuilder.append("SELECT * FROM courses WHERE true");
		if (filter.getTitle() != null)
			queryBuilder.append(" AND lower(course_title) LIKE '%' || lower(?) || '%'");
		if (filter.getDescription() != null)
			queryBuilder.append(" AND lower(course_description) LIKE '%' || lower(?) || '%'");
		if (filter.getAuthor() != null)
			queryBuilder.append(" AND lower(author_id) = lower(?)");
		if (count != null)
			queryBuilder.append(" LIMIT ?");
		if (offset != null)
			queryBuilder.append(" OFFSET ?");
		
		try (PreparedStatement statement = connProvider.get().prepareStatement(queryBuilder.toString())) {
			int index = 0;
			if (filter.getTitle() != null)
			    statement.setString(++index, filter.getTitle());
			if (filter.getDescription() != null)
			    statement.setString(++index, filter.getDescription());
			if (filter.getAuthor() != null)
			    statement.setString(++index, String.class.cast(filter.getAuthor().getId()));
			if (count != null)
				statement.setInt(++index, count);
			if (offset != null)
				statement.setInt(++index, offset);
	        try (ResultSet resultSet = statement.executeQuery()) {
	        	while(resultSet.next())
	        		courses.add(fromResultSet(resultSet));
	        }
		} catch (SQLException e) {
			throw new RuntimeException("Unable to list courses", e);
		}
		
	    return courses;
	    
	}
	
	/** Parses given ResultSet and extract {@link Course} from it.
	 * If ResultSet had <code>NULL</code> in <code>course_id</code> column, <code>null</code> is returned. */
	public Course fromResultSet(ResultSet resultSet) {
		
		Course course = new Course();
		
		try {
			course.setId(UUID.class.cast(resultSet.getObject("course_id")));
			if (resultSet.wasNull())
				return null;
			course.setTitle(resultSet.getString("course_title"));
			course.setDescription(resultSet.getString("course_description"));
			course.setAuthor(userProvider.get().findById(UUID.class.cast(resultSet.getObject("author_id"))));
		} catch (SQLException e) {
			throw new RuntimeException("Unable to resolve course from result set", e);
		}
		
		return course;
		
	}
	
	private static final Logger Log = LoggerFactory.getLogger(CourseManager.class);

	private final Provider<UserManager> userProvider;
	private final Provider<Connection> connProvider;
	
}
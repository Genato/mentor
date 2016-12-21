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
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hr.vsite.mentor.user.UserManager;

public class CourseManager {

	@Inject
	public CourseManager(Provider<UserManager> userProvider, Provider<Connection> connProvider) {
		this.userProvider = userProvider;
		this.connProvider = connProvider;
	}

	/**
	 * Inserts new <code>Course</code> in database. Returns <code>Course</code>
	 * if successful.
	 */
	public Course insert(Course course) {

		String msg = String.format("User: %s, Creating Course: %s", userProvider.get().me(), course.toString());
		Log.trace(msg);
		if (course.getTitle() == null || course.getDescription() == null || course.getAuthor() == null) {
			throw new BadRequestException("Error: Missing Parameter(s), " + msg);
		}
		String query = "INSERT INTO courses VALUES (DEFAULT, ?, ?, ?)";
		try (PreparedStatement statement = connProvider.get().prepareStatement(query,
				PreparedStatement.RETURN_GENERATED_KEYS)) {
			int index = 0;
			statement.setString(++index, course.getTitle());
			statement.setString(++index, course.getDescription());
			statement.setObject(++index, course.getAuthor().getId());
			statement.executeUpdate();
			ResultSet result = statement.getGeneratedKeys();
			if (result.next()) {
				course.setId(UUID.class.cast(result.getObject(1)));
				Log.info("User: {}, Created Course: {}", userProvider.get().me(), course.toString());
				return course;
			}
			else
				throw new InternalServerErrorException("Error: Unable To Create Course, " + msg);
		} catch (SQLException e) {
			if(e.getSQLState().equals("23503")) // Error Name: foreign_key_violation
				throw new BadRequestException(msg, e);
			throw new InternalServerErrorException("Error: Unable To Create Course, " + msg, e);
		}

	}

	/**
	 * Updates existing <code>Course</code> in database. Returns <code>Course</code>
	 * if successful.
	 */
	public Course update(UUID idToUpdate, Course newValues) {
		
		String msg = String.format("User: %s, Updating Course ID: %s With: %s", userProvider.get().me(), idToUpdate.toString(), newValues.toString());
		Log.trace(msg);
		if(!idToUpdate.equals(newValues.getId()))
			throw new BadRequestException("Error: ID Conflict, " + msg);
		newValues.setId(idToUpdate);
		if (newValues.getTitle() == null || newValues.getDescription() == null || newValues.getAuthor() == null) {
			throw new BadRequestException("Error: Missing Parameter(s), " + msg);
		}
		
		if(findById(newValues.getId()) == null) {
			Log.info("Aborted - no such course exists in database");
			throw new NotFoundException("Error: No Such Course, " + msg);
		}
		
		String query = "UPDATE courses SET course_title=?, course_description=?, author_id=? WHERE course_id=?";
		try (PreparedStatement statement = connProvider.get().prepareStatement(query)) {
			int index = 0;
			statement.setString(++index, newValues.getTitle());
			statement.setString(++index, newValues.getDescription());
			statement.setObject(++index, newValues.getAuthor().getId());
			statement.setObject(++index, newValues.getId());
			if(statement.executeUpdate() == 1) {
				Log.info("User: {}, Updated Course: {}", userProvider.get().me(), newValues.toString());
				return newValues;
			}
			else
				throw new InternalServerErrorException("Error: Unable To Update Course, " + msg);
		} catch (SQLException e) {
			throw new InternalServerErrorException("Error: Unable To Update Course, " + msg, e);
		}
	}
	
	/**
	 * Deletes a <code>Course</code> from database.
	 */
	public Course delete(Course course) {
		
		if(findById(course.getId()) == null) {
			throw new NotFoundException("Course " + course.toString() + " Not Found");
		}
		String query = "BEGIN; DELETE FROM course_lectures WHERE course_id=?; DELETE FROM courses WHERE course_id=?; COMMIT;";
		try (PreparedStatement statement = connProvider.get().prepareStatement(query)) {
			int index = 0;
			statement.setObject(++index, course.getId());
			statement.setObject(++index, course.getId());
			statement.executeUpdate();
			Log.info("User: {} Deleted Course: {}", userProvider.get().me(), course.toString());
			return course;
		} catch (SQLException e) {
			throw new InternalServerErrorException("Unable To Delete Course " + course.toString() + " By User " + userProvider.get().me(), e);
		}
	}

	/**
	 * Returns <code>Course</code> with corresponding id, or <code>null</code>
	 * if such course does not exist.
	 */
	public Course findById(UUID id) {

		try (PreparedStatement statement = connProvider.get()
				.prepareStatement("SELECT * FROM courses WHERE course_id = ?")) {
			statement.setObject(1, id);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next() ? fromResultSet(resultSet) : null;
			}
		} catch (Exception e) {
			throw new NotFoundException("Unable to find course with id " + id, e);
		}

	}

	/**
	 * Returns all courses known to application, unpaged. If there is a
	 * possibility for large number of courses, use
	 * {@link #list(CourseFilter, Integer, Integer)}
	 */
	public List<Course> list() {
		return list(new CourseFilter(), null, null);
	}

	/**
	 * Returns courses that match given criteria, unpaged. If there is a
	 * possibility for large number of courses, use
	 * {@link #list(CourseFilter, Integer, Integer)}
	 */
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
			queryBuilder.append(" AND author_id=?");
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
				statement.setObject(++index, filter.getAuthor().getId());
			if (count != null)
				statement.setInt(++index, count);
			if (offset != null)
				statement.setInt(++index, offset);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next())
					courses.add(fromResultSet(resultSet));
			}
		} catch (SQLException e) {
			throw new InternalServerErrorException("Unable to list courses", e);
		}

		return courses;

	}

	/**
	 * Parses given ResultSet and extract {@link Course} from it. If ResultSet
	 * had <code>NULL</code> in <code>course_id</code> column, <code>null</code>
	 * is returned.
	 */
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
			throw new InternalServerErrorException("Unable to resolve course from result set", e);
		}

		return course;

	}

	private static final Logger Log = LoggerFactory.getLogger(CourseManager.class);

	private final Provider<UserManager> userProvider;
	private final Provider<Connection> connProvider;

}

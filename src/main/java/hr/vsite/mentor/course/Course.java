package hr.vsite.mentor.course;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import hr.vsite.mentor.user.User;

public class Course {
	
	@JsonProperty
	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	@JsonProperty
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	@JsonProperty
	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	@JsonProperty
	public User getAuthor() { return author; }
	public void setAuthor(User author) { this.author = author; }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Course other = (Course) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return id + "/" + title;
	}
	
	private UUID id;
	private String title;
	private String description;
	private User author;

}

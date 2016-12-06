package hr.vsite.mentor.user;

import java.nio.file.Path;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.gwt.core.shared.GwtIncompatible;

import hr.vsite.mentor.MentorConfiguration;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public class User {

	@GwtIncompatible
	public static Path getDataFolder() {
		return MentorConfiguration.get().getSubDataPath("user");
	}

	@JsonProperty
	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	@JsonProperty
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	@JsonProperty
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

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
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@GwtIncompatible
	public Path getPhotoPath() {
		return getDataFolder().resolve(id + ".jpg");
	}

	private UUID id;
	private String email;
	private String name;
	
}

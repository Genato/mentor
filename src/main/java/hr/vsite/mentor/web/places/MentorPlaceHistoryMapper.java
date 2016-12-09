package hr.vsite.mentor.web.places;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({
	ClassroomPlace.Tokenizer.class,
	CoursePlace.Tokenizer.class,
	LecturePlace.Tokenizer.class
})
public interface MentorPlaceHistoryMapper extends PlaceHistoryMapper {

}

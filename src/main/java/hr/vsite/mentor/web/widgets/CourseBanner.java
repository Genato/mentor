package hr.vsite.mentor.web.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.HeadingSize;
import gwt.material.design.client.constants.HideOn;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.html.Heading;

import hr.vsite.mentor.course.Course;

public class CourseBanner extends MaterialPanel {

	public interface Resources extends ClientBundle {
		@Source("CourseBanner.gss")
		public Style style();
	}

	@CssResource.ImportedWithPrefix("CourseBanner")
	public interface Style extends CssResource {
		String view();
		String title();
		String image();
		String caption();
		String text();
	}
	
	public CourseBanner() {
		this(null);
	}
	
	public CourseBanner(Course course) {
	
		setBackgroundColor(Color.WHITE);
		setShadow(1);
		addStyleName(res.style().view());
		
		MaterialRow titleRow = new MaterialRow();
			MaterialColumn titleColumn = new MaterialColumn();
			titleColumn.setGrid("s12 m8");
				title = new Heading(HeadingSize.H1);
				title.addStyleName(res.style().title());
				title.setTextColor(Color.BLUE_GREY);
			titleColumn.add(title);
		titleRow.add(titleColumn);
			MaterialColumn lecturerImageColumn = new MaterialColumn();
			lecturerImageColumn.setGrid("m4");
			lecturerImageColumn.setHideOn(HideOn.HIDE_ON_SMALL_DOWN);
				lecturerImage = new MaterialImage();
				lecturerImage.addStyleName(res.style().image());
				lecturerImage.setCircle(true);
			lecturerImageColumn.add(lecturerImage);
		titleRow.add(lecturerImageColumn);
		add(titleRow);
		
		MaterialRow lecturerRow = new MaterialRow();
			MaterialColumn lecturerCaptionColumn = new MaterialColumn();
			lecturerCaptionColumn.setGrid("s12 m4 l3");
				MaterialLabel lecturerCaption = new MaterialLabel("Predavač:");
				lecturerCaption.setTextColor(Color.GREY_LIGHTEN_1);
				lecturerCaption.addStyleName(res.style().caption());
				lecturerCaption.addStyleName("flow-text");
			lecturerCaptionColumn.add(lecturerCaption);
		lecturerRow.add(lecturerCaptionColumn);
			MaterialColumn lecturerNameColumn = new MaterialColumn();
			lecturerNameColumn.setGrid("s12 m8 l9");
				lecturerName = new MaterialLabel();
				lecturerName.setTextColor(Color.GREY_DARKEN_1);
				lecturerName.addStyleName("flow-text");
			lecturerNameColumn.add(lecturerName);
		lecturerRow.add(lecturerNameColumn);
		add(lecturerRow);
		
		MaterialRow descriptionRow = new MaterialRow();
			MaterialColumn descriptionCaptionColumn = new MaterialColumn();
			descriptionCaptionColumn.setGrid("s12 m4 l3");
				MaterialLabel descriptionCaption = new MaterialLabel("Opis:");
				descriptionCaption.setTextColor(Color.GREY_LIGHTEN_1);
				descriptionCaption.addStyleName(res.style().caption());
				descriptionCaption.addStyleName("flow-text");
			descriptionCaptionColumn.add(descriptionCaption);
		descriptionRow.add(descriptionCaptionColumn);
			MaterialColumn descriptionColumn = new MaterialColumn();
			descriptionColumn.setGrid("s12 m8 l9");
				description = new MaterialLabel();
				description.setTextColor(Color.GREY_DARKEN_1);
				description.addStyleName("flow-text");
			descriptionColumn.add(description);
		descriptionRow.add(descriptionColumn);
		add(descriptionRow);

		if (course != null)
			setCourse(course);

	}

	public Course getCourse() { return course; }
	public void setCourse(Course course) {
		this.course = course;
		title.setText(course.getTitle());
		lecturerImage.setUrl(GWT.getHostPageBaseURL() + "api/users/" + course.getAuthor().getId() + "/photo");
		lecturerName.setText(course.getAuthor().getName());
		description.setText(course.getDescription());
	}

	private final MaterialImage lecturerImage;
	private final Heading title;
	private final MaterialLabel lecturerName;
	private final MaterialLabel description;
	private Course course = null;
	
	private static final Resources res = GWT.create(Resources.class);
	static {
		res.style().ensureInjected();
	}

}
